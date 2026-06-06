package me.imbanana.knockdown.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.imbanana.knockdown.KnockdownMod;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.Consumer;

public class ModRenderPipeLines {
    public static final RenderPipeline CIRCULAR_PROGRESS = RenderPipeline.builder()
            .withLocation(KnockdownMod.idOf("pipeline/circular_progress"))
            .withFragmentShader(KnockdownMod.idOf("core/circular_progress"))
            .withVertexShader(KnockdownMod.idOf("core/circular_progress"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection",        UniformType.UNIFORM_BUFFER)
            .withCull(false)
            .build();

    public static void registerPipelines(Consumer<RenderPipeline> registry) {
        registry.accept(CIRCULAR_PROGRESS);
    }
}
