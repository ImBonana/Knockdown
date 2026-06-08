package me.imbanana.knockdown.fabric.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.imbanana.knockdown.fabric.client.gui.FabricKnockdownOverlay;
import me.imbanana.knockdown.fabric.network.FabricModNetwork;
import me.imbanana.knockdown.gui.KnockdownOverlay;
import me.imbanana.knockdown.keymapping.ModKeyMapping;
import me.imbanana.knockdown.render.ModRenderPipeLines;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.impl.client.rendering.FabricRenderPipelineInternals;
import net.minecraft.client.renderer.RenderPipelines;

public final class KnockdownModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.addFirst(KnockdownOverlay.ID, new FabricKnockdownOverlay());
        ModRenderPipeLines.registerPipelines(RenderPipelines::register);

        FabricModNetwork.registerClient();
        ModKeyMapping.register(KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyMapping::tick);
    }
}
