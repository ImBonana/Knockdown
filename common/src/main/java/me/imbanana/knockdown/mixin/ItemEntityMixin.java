package me.imbanana.knockdown.mixin;

import me.imbanana.knockdown.util.IKnockdownable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements TraceableEntity {
    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "playerTouch",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelInteraction(Player player, CallbackInfo ci) {
        if (this.level().isClientSide()) return;
        if (((IKnockdownable) player).isKnockedDown()) ci.cancel();
    }
}
