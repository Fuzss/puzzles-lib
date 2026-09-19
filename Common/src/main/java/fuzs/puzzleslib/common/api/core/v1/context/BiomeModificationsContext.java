package fuzs.puzzleslib.common.api.core.v1.context;

import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeModifier;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;

/**
 * Allows for registering modifications (including additions and removals) to biomes loaded from the current data pack.
 */
public interface BiomeModificationsContext {

    /**
     * Add a modification to this context.
     *
     * @param loadingPhase  the loading phase, useful to separate additions and removals
     * @param selector      the selection context for current biome
     * @param biomeModifier the modification context
     */
    void registerBiomeModification(BiomeLoadingPhase loadingPhase, BiomeSelector selector, BiomeModifier modifier);
}
