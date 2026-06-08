package me.imbanana.knockdown.gui;

import me.imbanana.knockdown.KnockdownMod;
import me.imbanana.knockdown.keymapping.ModKeyMapping;
import me.imbanana.knockdown.mixin.accessor.GuiGraphicsExtractorAccessor;
import me.imbanana.knockdown.util.IKnockdownable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;

public abstract class KnockdownOverlay {
    public static final Identifier ID = KnockdownMod.idOf("overlay");

    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        IKnockdownable player = (IKnockdownable) minecraft.player;
        if (player == null) return;
        if (!player.isKnockedDown()) return;

        int progressBarWidth = 45;
        int progressBarX = (graphics.guiWidth() - progressBarWidth) / 2 - 1;
        int progressBarY = graphics.guiHeight() - progressBarWidth - 40;
        float progress = player.getTicksLeft() / (float) player.getMaxTicks();

        this.renderProgressBar(player, graphics, progressBarX, progressBarY, progressBarWidth, progress);

        int progressMargin = 5;

        Component skipText = Component.literal("SKIP");
        Component holdText = Component.literal("HOLD");

        Component skipTextButton = Component.translatable("overlay.knockdown.button.skip", ModKeyMapping.fastBleedOutKey.getTranslatedKeyMessage());
        Component holdTextButton = Component.translatable("overlay.knockdown.button.hold", ModKeyMapping.waitForHelpKey.getTranslatedKeyMessage());

        int textY = progressBarY + (progressBarWidth - minecraft.font.lineHeight) / 2;
        int textColor = 0xFFFFFFFF;
        int textColorActive = 0xFF00FF00;

        graphics.text(minecraft.font, skipText, progressBarX + progressBarWidth + progressMargin, textY, player.isBleedingOutFast() ? textColorActive : textColor);
        graphics.text(minecraft.font, skipTextButton, progressBarX + progressBarWidth + progressMargin, textY + minecraft.font.lineHeight, player.isBleedingOutFast() ? textColorActive : textColor);
        graphics.text(minecraft.font, holdText, progressBarX - progressMargin - minecraft.font.width(holdText), textY, player.isWaitingForHelp() ? textColorActive : textColor);
        graphics.text(minecraft.font, holdTextButton, progressBarX - progressMargin - minecraft.font.width(holdTextButton), textY + minecraft.font.lineHeight, player.isWaitingForHelp() ? textColorActive : textColor);
    }

    private void renderProgressBar(IKnockdownable player, GuiGraphicsExtractor graphics, int x, int y, int width, float progress) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphicsExtractorAccessor accessor = (GuiGraphicsExtractorAccessor) graphics;

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
