package me.imbanana.knockdown.mixin;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.data.ModDamageTypes;
import me.imbanana.knockdown.data.ModDataAttachments;
import me.imbanana.knockdown.network.ModNetwork;
import me.imbanana.knockdown.network.s2c.KnockdownSyncPayloadS2C;
import me.imbanana.knockdown.util.IKnockdownable;
import me.imbanana.knockdown.util.KnockdownData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar implements ContainerUser, IKnockdownable {
    @Shadow
    protected abstract void removeEntitiesOnShoulder();


    @Shadow
    public abstract void die(DamageSource source);

    @Shadow
    public abstract boolean isLocalPlayer();

    @Unique
    private int knockdownTicksLeft = 0;

    @Unique
    private boolean fastBleedOut = false;

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void injectTick(CallbackInfo ci) {
        if (knockdownTicksLeft > 0 && this.isKnockedDown()) {
            KnockdownMod.LOGGER.info(String.valueOf(this.isUsingItem()));
            knockdownTicksLeft -= this.getBleedOutSpeed();
        }

        if (!this.isLocalPlayer() && this.isKnockedDown() && knockdownTicksLeft <= 0 && this.isAlive()) {
            this.setHealth(0);
            this.die(new DamageSource(
                    this.level()
                            .registryAccess()
                            .lookupOrThrow(Registries.DAMAGE_TYPE)
                            .get(ModDamageTypes.BLEED_OUT.identifier())
                            .orElseThrow()
            ));
        }

        if (this.isLocalPlayer()) {
            if (knockdownTicksLeft <= 0 && this.isKnockedDown() && this.isAlive()) {
                knockdownTicksLeft = this.getMaxTicks();
            }
        }
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void writeAdditionalPlayerData(ValueOutput output, CallbackInfo ci) {
        output.putInt("knockdown_ticks_left", this.getTicksLeft());
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readAdditionalPlayerData(ValueInput input, CallbackInfo ci) {
        this.knockdownTicksLeft = input.getIntOr("knockdown_ticks_left", 0);
    }

    @Inject(
            method = "getDesiredPose",
            at = @At("HEAD"),
            cancellable = true
    )
    private void updatePlayerPose(CallbackInfoReturnable<Pose> cir) {
        if (this.isKnockedDown()) cir.setReturnValue(Pose.SWIMMING);
    }

    @Override
    public void knockdown() {
        AttributeInstance maxHealthAttribute = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute != null) maxHealthAttribute.addOrUpdateTransientModifier(new AttributeModifier(KnockdownMod.idOf("knockdown_max_health"), 1 - maxHealthAttribute.getValue(), AttributeModifier.Operation.ADD_VALUE));

        AttributeInstance jumpStrengthAttribute = this.getAttribute(Attributes.JUMP_STRENGTH);
        if (jumpStrengthAttribute != null) jumpStrengthAttribute.addOrUpdateTransientModifier(new AttributeModifier(KnockdownMod.idOf("knockdown_jump_strength"), -jumpStrengthAttribute.getValue(), AttributeModifier.Operation.ADD_VALUE));

        this.setHealth(1);
        this.setPose(Pose.SWIMMING);
        this.setKnockedDown(true);
        this.clearFire();
        this.setTicksFrozen(0);
        this.removeEntitiesOnShoulder();
        this.knockdownTicksLeft = this.getMaxTicks();
    }

    @Override
    public boolean isKnockedDown() {
        return ModDataAttachments.KNOCKDOWN.getOrSet(this, KnockdownData.DEFAULT).isKnockedDown();
    }

    public void setKnockedDown(boolean value) {
        ModDataAttachments.KNOCKDOWN.modify(this, knockdownData -> knockdownData.setKnockedDown(value), KnockdownData.DEFAULT);
    }

    public Component getDeathMessage() {
        return ModDataAttachments.KNOCKDOWN.getOrSet(this, KnockdownData.DEFAULT).deathMessage();
    }

    @Override
    public void setDeathMessage(Component message) {
        ModDataAttachments.KNOCKDOWN.modify(this, knockdownData -> knockdownData.setDeathMessage(message), KnockdownData.DEFAULT);
    }

    @Override
    public boolean shouldKnockdown() {
        return true;
    }

    @Override
    public int getMaxTicks() {
        return 20 * 40;
    }

    @Override
    public int getTicksLeft() {
        return this.knockdownTicksLeft;
    }

    @Override
    public void setTicksLeft(int value) {
        this.knockdownTicksLeft = value;
    }

    @Override
    public int getBleedOutSpeed() {
        return this.fastBleedOut ? 4 : 1;
    }

    @Override
    public void setFastBleedOut(boolean value) {
        this.fastBleedOut = value;
    }

    @Override
    public boolean isBleedingOutFast() {
        return this.fastBleedOut;
    }

    @Override
    public void syncTicksLeft() {

    }
}
