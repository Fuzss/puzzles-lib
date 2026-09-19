package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Context for modifying the generation settings of a biome.
 *
 * @see BiomeGenerationSettings
 * @see BiomeGenerationSettings.PlainBuilder
 */
public interface GenerationContext {
    /**
     * Add a feature to a generation step.
     *
     * @param step    the generation step
     * @param feature the placed feature to add
     * @see BiomeGenerationSettings.PlainBuilder#addFeature(GenerationStep.Decoration, Holder)
     */
    void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    /**
     * Remove a feature from a generation step.
     *
     * @param step    the generation step
     * @param feature the placed feature to remove
     * @return whether the feature was removed
     */
    boolean removeFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    /**
     * Remove a feature from all generation steps.
     *
     * @param feature the placed feature to remove
     * @return whether the feature was removed
     */
    boolean removeFeature(Holder<PlacedFeature> feature);

    /**
     * Get all features of a generation step.
     *
     * @param step the generation step
     * @return the features of the generation step
     *
     * @see BiomeGenerationSettings#features()
     */
    Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration step);

    /**
     * Check whether a generation step contains a feature.
     *
     * @param step    the generation step
     * @param feature the placed feature to check
     * @return whether the feature is present
     *
     * @see BiomeGenerationSettings#hasFeature(PlacedFeature)
     */
    boolean hasFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    /**
     * Check whether any generation step contains a feature.
     *
     * @param feature the placed feature to check
     * @return whether the feature is present
     *
     * @see BiomeGenerationSettings#hasFeature(PlacedFeature)
     */
    boolean hasFeature(Holder<PlacedFeature> feature);

    /**
     * Add a carver to the biome.
     *
     * @param carver the carver to add
     * @see BiomeGenerationSettings.PlainBuilder#addCarver(Holder)
     */
    void addCarver(Holder<WorldCarver> carver);

    /**
     * Remove a carver from the biome.
     *
     * @param carver the carver to remove
     * @return whether the carver was removed
     */
    boolean removeCarver(Holder<WorldCarver> carver);

    /**
     * Get all carvers of the biome.
     *
     * @return the carvers of the biome
     *
     * @see BiomeGenerationSettings#getCarvers()
     */
    Iterable<Holder<WorldCarver>> getCarvers();

    /**
     * Check whether the biome contains a carver.
     *
     * @param carver the carver to check
     * @return whether the carver is present
     *
     * @see BiomeGenerationSettings#getCarvers()
     */
    boolean hasCarver(Holder<WorldCarver> carver);
}
