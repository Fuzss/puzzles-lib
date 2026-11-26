package fuzs.puzzleslib.common.api.client.core.v1.context;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.oit.OitPipelineSet;

/**
 * Register new render pipelines to {@link net.minecraft.client.renderer.RenderPipelines}.
 */
public interface RenderPipelinesContext {
    /**
     * @param pipeline the render pipeline
     */
    void registerPipeline(RenderPipeline pipeline);

    /**
     * @param pipeline the render pipeline
     */
    void registerOptionalPipeline(RenderPipeline pipeline);

    /**
     * @param pipelineSet the OIT pipeline set
     */
    void registerOitPipelineSet(OitPipelineSet pipelineSet);
}
