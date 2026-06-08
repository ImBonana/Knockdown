package me.imbanana.knockdown.fabric;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.data.ModDataAttachments;
import me.imbanana.knockdown.fabric.data.FabricModDataAttachment;
import me.imbanana.knockdown.fabric.network.FabricModNetwork;
import me.imbanana.knockdown.util.IKnockdownable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.entity.Entity;

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
    }
}
