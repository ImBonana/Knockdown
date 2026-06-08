package me.imbanana.knockdown.fabric;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.fabric.data.FabricModDataAttachment;
import me.imbanana.knockdown.fabric.network.FabricModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.world.InteractionResult;

public final class KnockdownModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KnockdownMod.init();
        FabricModDataAttachment.register();
        FabricModNetwork.registerC2S();
        FabricModNetwork.registerServer();

        ServerPlayerEvents.JOIN.register(serverPlayer -> {
            ((IKnockdownable) serverPlayer).syncTicksLeft();
        });

        disableInteractionEvents();
    }

    private void disableInteractionEvents() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> ((IKnockdownable) player).isKnockedDown() ? InteractionResult.FAIL : InteractionResult.PASS);
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> ((IKnockdownable) player).isKnockedDown() ? InteractionResult.FAIL : InteractionResult.PASS);
        UseItemCallback.EVENT.register((player, level, hand) -> ((IKnockdownable) player).isKnockedDown() ? InteractionResult.FAIL : InteractionResult.PASS);
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> !((IKnockdownable) player).isKnockedDown());
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> ((IKnockdownable) player).isKnockedDown() ? InteractionResult.FAIL : InteractionResult.PASS);
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> ((IKnockdownable) player).isKnockedDown() ? InteractionResult.FAIL : InteractionResult.PASS);
    }
}
