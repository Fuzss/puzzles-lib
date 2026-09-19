package fuzs.puzzleslib.common.api.core.v1.context;

import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.BiomeTransformer;

/**
 * Register modifications to biomes loaded from the current data pack.
 */
public interface BiomeModificationsContext {

    /**
     * Register a biome transformation to be applied to all biomes matching the given selector.
     *
     * @param loadingPhase the loading phase, which determines the order in which transformations are applied
     * @param selector     the selector for deciding which biomes the transformer should be applied to
     * @param transformer  the transformer that modifies the selected biomes
     */
    void registerBiomeModification(BiomeLoadingPhase loadingPhase, BiomeSelector selector, BiomeTransformer transformer);
}
