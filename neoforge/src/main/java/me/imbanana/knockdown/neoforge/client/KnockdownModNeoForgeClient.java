package me.imbanana.knockdown.neoforge.client;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.gui.KnockdownOverlay;
import me.imbanana.knockdown.neoforge.client.gui.NeoForgeKnockdownOverlay;
import me.imbanana.knockdown.render.ModRenderPipeLines;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@Mod(value = KnockdownMod.MOD_ID, dist = Dist.CLIENT)
public class KnockdownModNeoForgeClient {
    public KnockdownModNeoForgeClient(IEventBus modBus) {
        modBus.addListener(this::overlayRegistryEvent);
        modBus.addListener(this::registerPipelinesEvent);
    }

    private void overlayRegistryEvent(RegisterGuiLayersEvent event) {
        event.registerBelowAll(KnockdownOverlay.ID, new NeoForgeKnockdownOverlay());
    }

    private void registerPipelinesEvent(RegisterRenderPipelinesEvent event) {
        ModRenderPipeLines.registerPipelines(event::registerPipeline);
    }
}
