package fuzs.puzzleslib.common.api.biome.v2;

import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;

/**
 * Modifies a biome in the current data pack.
 * <p>
 * A transformer is registered together with a {@link BiomeSelector} and a {@link BiomeLoadingPhase} in
 * {@link BiomeModificationsContext}, which decide the biomes the transformer is applied to and when it is applied.
 *
 * @see BiomeSelector
 * @see BiomeLoadingPhase
 * @see BiomeModificationsContext
 */
@FunctionalInterface
public interface BiomeTransformer {
    /**
     * Applies this transformer to a biome.
     *
     * @param registryAccess the registry access of the current level
     * @param biome          the holder of the biome being transformed
     * @param context        the context for modifying the biome
     */
    void accept(RegistryAccess registryAccess, Holder<Biome> biome, Context context);

    /**
     * Context containing all biome-related information passed in {@link BiomeModificationsContext}.
     */
    interface Context {
        /**
         * @return the context for modifying the biome attributes
         */
        AttributesContext attributes();

        /**
         * @return the context for modifying the biome climate settings
         */
        ClimateContext climate();

        /**
         * @return the context for modifying the biome effects
         */
        EffectsContext effects();

        /**
         * @return the context for modifying the biome generation settings
         */
        GenerationContext generation();

        /**
         * @return the context for modifying the biome mob spawn settings
         */
        MobSpawnsContext mobSpawns();
    }
}
