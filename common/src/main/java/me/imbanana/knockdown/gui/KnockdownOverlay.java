package me.imbanana.knockdown.gui;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.mixin.accessor.GuiGraphicsExtractorAccessor;
import me.imbanana.knockdown.util.IKnockdownable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;

public abstract class KnockdownOverlay {
    public static final Identifier ID = KnockdownMod.idOf("overlay");

    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        IKnockdownable player = (IKnockdownable) minecraft.player;
        if (player == null) return;
        if (!player.isKnockedDown()) return;


        GuiGraphicsExtractorAccessor accessor = (GuiGraphicsExtractorAccessor) graphics;

        int width = 45;

        int x = (graphics.guiWidth() - width) / 2 - 1;
        int y = graphics.guiHeight() - width - 40;

        float progress = player.getTicksLeft() / (float) player.getMaxTicks();

        accessor.getGuiRenderState().addGuiElement(
                new CircularProgressRenderState(
                        new Matrix3x2f(graphics.pose()),
                        x, y, width,
                        progress,
                        0.1f,
                        0xFFFF5500, // ARGB color
                        accessor.getScissorStack().peek()
                )
        );

        graphics.centeredText(minecraft.font, "%.2fs".formatted(player.getTicksLeft() / 20f), x + (width / 2), y + (width / 2) - minecraft.font.lineHeight / 2, 0xFFFFFFFF);
    }
}
