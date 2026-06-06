package me.imbanana.knockdown.neoforge.client.gui;

import me.imbanana.knockdown.gui.KnockdownOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.neoforge.client.gui.GuiLayer;

public class NeoForgeKnockdownOverlay extends KnockdownOverlay implements GuiLayer {
    @Override
    public void render(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
        this.extractRenderState(guiGraphicsExtractor, deltaTracker);
    }
}
