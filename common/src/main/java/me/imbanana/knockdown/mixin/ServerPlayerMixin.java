package me.imbanana.knockdown.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements IKnockdownable {
    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    protected abstract void tellNeutralMobsThatIDied();

    @Shadow
    public abstract ServerLevel level();

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(
            method = "die",
            at = @At("HEAD"),
            cancellable = true
    )
    private void knockdownOnDeath(DamageSource source, CallbackInfo ci) {
        if (!this.shouldKnockdown()) return;

        boolean showDeathMessage = this.level().getGameRules().get(GameRules.SHOW_DEATH_MESSAGES);

        if (this.isKnockedDown()) {

            if (showDeathMessage) {
                Component deathMessage = this.getDeathMessage().copy();
                this.connection.send(
                        new ClientboundPlayerCombatKillPacket(this.getId(), deathMessage),
                        PacketSendListener.exceptionallySend(
                                () -> {
                                    String truncatedDeathMessage = deathMessage.getString(256);
                                    Component explanation = Component.translatable(
                                            "death.attack.message_too_long", Component.literal(truncatedDeathMessage).withStyle(ChatFormatting.YELLOW)
                                    );
                                    Component fakeDeathMessage = Component.translatable("death.attack.even_more_magic", this.getDisplayName())
                                            .withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(explanation)));
                                    return new ClientboundPlayerCombatKillPacket(this.getId(), fakeDeathMessage);
                                }
                        )
                );

                this.displayDeathMessage(Component.translatable("death.attack.bleed_out", this.getDisplayName()));
            }

            return;
        }

        if (source.typeHolder().is(DamageTypes.GENERIC_KILL)) return;

        this.knockdown();

        if (showDeathMessage) {
            Component deathMessage = this.getCombatTracker().getDeathMessage();
            this.setDeathMessage(deathMessage);

            this.displayDeathMessage(deathMessage);
        } else {
            this.setDeathMessage(CommonComponents.EMPTY);
        }

        if (this.level().getGameRules().get(GameRules.FORGIVE_DEAD_PLAYERS)) {
            this.tellNeutralMobsThatIDied();
        }

        this.level().getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this, ScoreAccess::increment);
        LivingEntity killer = this.getKillCredit();
        if (killer != null) {
            this.awardStat(Stats.ENTITY_KILLED_BY.get(killer.getType()));
            killer.awardKillScore(this, source);
            this.createWitherRose(killer);
        }

        ci.cancel();
    }

    @Definition(id = "showDeathMessage", local = @Local(type = boolean.class, name = "showDeathMessage"))
    @Expression("showDeathMessage")
    @ModifyExpressionValue(
            method = "die",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean hideDeathMessage(boolean original) {
        return !this.shouldKnockdown();
    }

    @Definition(id = "killer", local = @Local(type = LivingEntity.class, name = "killer"))
    @Expression("killer != null")
    @ModifyExpressionValue(
            method = "die",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean disableKillerStats(boolean original) {
        return !this.shouldKnockdown();
    }

    @WrapWithCondition(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private boolean disableDeathScreen(ServerGamePacketListenerImpl instance, Packet packet) {
        return !this.shouldKnockdown();
    }

    @Unique
    private void displayDeathMessage(Component deathMessage) {
        Team team = this.getTeam();
        if (team == null || team.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
            this.server.getPlayerList().broadcastSystemMessage(deathMessage, false);
        } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
            this.server.getPlayerList().broadcastSystemToTeam(this, deathMessage);
        } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
            this.server.getPlayerList().broadcastSystemToAllExceptTeam(this, deathMessage);
        }
    }

    @Override
    public boolean shouldKnockdown() {
        return !this.isSpectator() && !this.isCreative() && !this.isInvulnerable();
    }
}
