package fuzs.puzzleslib.common.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.BiomeModifier;
import fuzs.puzzleslib.common.api.biome.v2.context.*;

public record BiomeContext(AttributesContext attributes,
                           ClimateContext climate,
                           EffectsContext effects,
                           GenerationContext generation,
                           MobSpawnsContext mobSpawns) implements BiomeModifier.Context {

}
