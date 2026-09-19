package fuzs.puzzleslib.common.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.BiomeTransformer;
import fuzs.puzzleslib.common.api.biome.v2.context.*;

public record BiomeTransformerContextImpl(AttributesContext attributes,
                                          ClimateContext climate,
                                          EffectsContext effects,
                                          GenerationContext generation,
                                          MobSpawnsContext mobSpawns) implements BiomeTransformer.Context {

}
