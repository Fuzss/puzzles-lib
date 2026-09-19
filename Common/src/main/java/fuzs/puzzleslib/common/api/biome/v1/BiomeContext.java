package fuzs.puzzleslib.common.api.biome.v1;

import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

/**
 * Context containing all biome-related information passed in {@link BiomeModificationsContext}.
 *
 * @param climate    the modification context for the biome weather properties
 * @param effects     the modification context for the biome effects
 * @param generation the modification context for the biome generation settings
 * @param mobSpawns   the modification context for the biome spawn settings
 */
public record BiomeContext(Holder<Biome> biome,
                           ClimateContext climate,
                           EffectsContext effects,
                           GenerationContext generation,
                           MobSpawnsContext mobSpawns) {

}
