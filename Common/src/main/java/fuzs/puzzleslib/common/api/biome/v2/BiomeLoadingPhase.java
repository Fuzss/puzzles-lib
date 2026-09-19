package fuzs.puzzleslib.common.api.biome.v2;

import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;

/**
 * The phase of a biome modification, which determines the order in which modifications from various mods are applied.
 * <p>
 * Biomes are modified in the order the constants are declared in, so every phase can rely on all modifications from the
 * preceding phases already having been applied:
 * <ol>
 *     <li>{@link #ADD}</li>
 *     <li>{@link #REMOVE}</li>
 *     <li>{@link #MODIFY}</li>
 *     <li>{@link #POST}</li>
 * </ol>
 *
 * @see BiomeTransformer
 * @see BiomeModificationsContext
 */
public enum BiomeLoadingPhase {
    /**
     * For enriching biomes by adding to them, without relying on other modifications or removing existing content.
     * <p>
     * Examples are new ores, new vegetation, and new structures.
     */
    ADD,

    /**
     * For removing content from biomes.
     * <p>
     * Examples are removing iron ore from plains, or removing ghasts.
     */
    REMOVE,

    /**
     * For replacing existing biome content with modified content.
     * <p>
     * An example is replacing mineshafts with biome-specific mineshafts.
     */
    MODIFY,

    /**
     * For wide-reaching post-processing of biomes.
     * <p>
     * This is intended for modifications that other mods rely upon, e.g. a mod that allows modpack authors to customize
     * world generation by changing biome properties.
     */
    POST
}
