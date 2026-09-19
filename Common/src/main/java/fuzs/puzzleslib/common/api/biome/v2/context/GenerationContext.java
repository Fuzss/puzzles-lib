package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * @see BiomeGenerationSettings
 * @see BiomeGenerationSettings.PlainBuilder
 */
public interface GenerationContext {
    /**
     * @see BiomeGenerationSettings.PlainBuilder#addFeature(GenerationStep.Decoration, Holder)
     */
    void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    boolean removeFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    boolean removeFeature(Holder<PlacedFeature> feature);

    /**
     * @see BiomeGenerationSettings#features()
     */
    Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration step);

    /**
     * @see BiomeGenerationSettings#hasFeature(PlacedFeature)
     */
    boolean hasFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    /**
     * @see BiomeGenerationSettings#hasFeature(PlacedFeature)
     */
    boolean hasFeature(Holder<PlacedFeature> feature);

    /**
     * @see BiomeGenerationSettings.PlainBuilder#addCarver(Holder)
     */
    void addCarver(Holder<WorldCarver> carver);

    boolean removeCarver(Holder<WorldCarver> carver);

    /**
     * @see BiomeGenerationSettings#getCarvers()
     */
    Iterable<Holder<WorldCarver>> getCarvers();

    /**
     * @see BiomeGenerationSettings#getCarvers()
     */
    boolean hasCarver(Holder<WorldCarver> carver);
}
