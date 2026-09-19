package fuzs.puzzleslib.common.api.biome.v2;

import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BiomeTransformer {
    void accept(RegistryAccess registryAccess, Holder<Biome> biome, Context context);

    /**
     * Context containing all biome-related information passed in {@link BiomeModificationsContext}.
     *
     * @param climate    the modification context for the biome weather properties
     * @param effects    the modification context for the biome effects
     * @param generation the modification context for the biome generation settings
     * @param mobSpawns  the modification context for the biome spawn settings
     */
    interface Context {
        AttributesContext attributes();

        ClimateContext climate();

        EffectsContext effects();

        GenerationContext generation();

        MobSpawnsContext mobSpawns();
    }
}
