package me.imbanana.knockdown.neoforge.client;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.gui.KnockdownOverlay;
import me.imbanana.knockdown.keymapping.ModKeyMapping;
import me.imbanana.knockdown.neoforge.client.gui.NeoForgeKnockdownOverlay;
import me.imbanana.knockdown.neoforge.network.NeoForgeModNetwork;
import me.imbanana.knockdown.network.ModNetwork;
import me.imbanana.knockdown.render.ModRenderPipeLines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = KnockdownMod.MOD_ID, dist = Dist.CLIENT)
public class KnockdownModNeoForgeClient {
    public KnockdownModNeoForgeClient(IEventBus modBus) {
        modBus.addListener(this::overlayRegistryEvent);
        modBus.addListener(this::registerPipelinesEvent);
        modBus.addListener(this::registerKeyMapping);

        NeoForge.EVENT_BUS.addListener(this::onClientTick);

        NeoForgeModNetwork.registerClient();
    }

    private void overlayRegistryEvent(RegisterGuiLayersEvent event) {
        event.registerBelowAll(KnockdownOverlay.ID, new NeoForgeKnockdownOverlay());
    }

    private void registerPipelinesEvent(RegisterRenderPipelinesEvent event) {
        ModRenderPipeLines.registerPipelines(event::registerPipeline);
    }

    private void registerKeyMapping(RegisterKeyMappingsEvent event) {
        ModKeyMapping.register(keyMapping -> {
            event.register(keyMapping);
            return keyMapping;
        });
    }

    private void onClientTick(ClientTickEvent.Post event) {
        ModKeyMapping.tick(Minecraft.getInstance());
    }
}
