package fuzs.puzzleslib.common.api.core.v1.context;

import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.BiomeTransformer;

/**
 * Allows for registering modifications (including additions and removals) to biomes loaded from the current data pack.
 */
public interface BiomeModificationsContext {

    /**
     * Add a modification to this context.
     *
     * @param loadingPhase the loading phase, useful to separate additions and removals
     * @param selector     the selection context for the current biome
     * @param transformer  the modification context
     */
    void registerBiomeModification(BiomeLoadingPhase loadingPhase, BiomeSelector selector, BiomeTransformer transformer);
}
