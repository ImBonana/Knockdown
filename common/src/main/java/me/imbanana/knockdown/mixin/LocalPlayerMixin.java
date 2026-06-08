package me.imbanana.knockdown.mixin;

import me.imbanana.knockdown.network.ModNetwork;
import me.imbanana.knockdown.network.c2s.KnockdownActionPayloadC2S;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends PlayerMixin {
    protected LocalPlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void setFastBleedOut(boolean value) {
        super.setFastBleedOut(value);
        ModNetwork.sendToServer(new KnockdownActionPayloadC2S(value ? KnockdownActionPayloadC2S.Action.START_FASTER_BLEED_OUT : KnockdownActionPayloadC2S.Action.END_FASTER_BLEED_OUT));
    }
}
