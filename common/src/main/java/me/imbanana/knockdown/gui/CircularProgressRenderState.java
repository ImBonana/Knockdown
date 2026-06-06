package me.imbanana.knockdown.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.imbanana.knockdown.render.ModRenderPipeLines;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

import net.minecraft.util.ARGB;

public record CircularProgressRenderState(
        Matrix3x2fc pose,
        int x, int y, int size,
        float progress,
        float thickness,
        int color,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

    public CircularProgressRenderState(
            Matrix3x2fc pose, int x, int y, int size,
            float progress, float thickness, int color,
            @Nullable ScreenRectangle scissorArea
    ) {
        this(pose, x, y, size, progress, thickness, color,
                scissorArea,
                computeBounds(x, y, size, pose, scissorArea));
    }

    @Override
    public RenderPipeline pipeline() {
        return ModRenderPipeLines.CIRCULAR_PROGRESS;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public void buildVertices(VertexConsumer buf) {
        int r = ARGB.red(color);
        int g = ARGB.green(color);
        int b = ARGB.blue(color);
        int a = ARGB.alpha(color);

        int pu = (int)(progress  * 32767);
        int pv = (int)(thickness * 32767);

        int x1 = x + size;
        int y1 = y + size;

        buf.addVertexWith2DPose(pose, x,  y1).setColor(r,g,b,a).setUv(0f,1f).setUv2(pu,pv);
        buf.addVertexWith2DPose(pose, x1, y1).setColor(r,g,b,a).setUv(1f,1f).setUv2(pu,pv);
        buf.addVertexWith2DPose(pose, x1, y).setColor(r,g,b,a).setUv(1f,0f).setUv2(pu,pv);
        buf.addVertexWith2DPose(pose, x,  y).setColor(r,g,b,a).setUv(0f,0f).setUv2(pu,pv);
    }

    @Nullable
    private static ScreenRectangle computeBounds(
            int x, int y, int size, Matrix3x2fc pose,
            @Nullable ScreenRectangle scissor
    ) {
        ScreenRectangle bounds = new ScreenRectangle(x, y, size, size).transformMaxBounds(pose);
        return scissor != null ? scissor.intersection(bounds) : bounds;
    }
}