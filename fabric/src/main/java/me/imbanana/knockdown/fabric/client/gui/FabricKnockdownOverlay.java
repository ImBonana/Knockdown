package me.imbanana.knockdown.fabric.client.gui;

import me.imbanana.knockdown.gui.KnockdownOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FabricKnockdownOverlay extends KnockdownOverlay implements HudElement {
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        super.extractRenderState(graphics, deltaTracker);
    }
}
