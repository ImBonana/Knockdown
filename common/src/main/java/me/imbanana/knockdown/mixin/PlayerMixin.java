package me.imbanana.knockdown.mixin;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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


    //!! For some reason NeoForge don't like this, And because of that NeoForge is crashing without any error :/
    // TODO: Replace this with data attachment
    @Unique
    private static final EntityDataAccessor<Boolean> KNOCKED_DOWN = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Component> DEATH_MESSAGE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPONENT);
    
    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "defineSynchedData",
            at = @At("TAIL")
    )
    private void addSynchedData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(KNOCKED_DOWN, false);
        entityData.define(DEATH_MESSAGE, Component.empty());
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void writeAdditionalPlayerData(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("knocked_down", this.isKnockedDown());
        output.store("death_message", ComponentSerialization.CODEC, this.getDeathMessage());
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void readAdditionalPlayerData(ValueInput input, CallbackInfo ci) {
        boolean isCurrentlyKnockedDown = input.getBooleanOr("knocked_down", false);
        if (isCurrentlyKnockedDown) knockdown();
        this.setDeathMessage(input.read("death_message", ComponentSerialization.CODEC).orElse(CommonComponents.EMPTY));
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
    }

    @Override
    public boolean isKnockedDown() {
        return this.entityData.get(KNOCKED_DOWN);
    }

    public void setKnockedDown(boolean value) {
        this.entityData.set(KNOCKED_DOWN, value);
    }

    public Component getDeathMessage() {
        return this.entityData.get(DEATH_MESSAGE);
    }

    @Override
    public void setDeathMessage(Component message) {
        this.entityData.set(DEATH_MESSAGE, message);
    }

    @Override
    public boolean shouldKnockdown() {
        return true;
    }
}
