package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.biome.v1.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.fabric.impl.biome.ClimateContextFabric;
import fuzs.puzzleslib.fabric.impl.biome.GenerationContextFabric;
import fuzs.puzzleslib.fabric.impl.biome.MobSpawnsContextFabric;
import fuzs.puzzleslib.fabric.impl.biome.EffectsContextFabric;
import fuzs.puzzleslib.fabric.mixin.accessor.BiomeSelectionContextImplFabricAccessor;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class BiomeModificationsContextFabricImpl implements BiomeModificationsContext {
    private static final Map<BiomeLoadingPhase, ModificationPhase> BIOME_PHASE_CONVERSIONS = Maps.immutableEnumMap(
            ImmutableMap.of(BiomeLoadingPhase.ADD,
                    ModificationPhase.ADDITIONS,
                    BiomeLoadingPhase.REMOVE,
                    ModificationPhase.REMOVALS,
                    BiomeLoadingPhase.MODIFY,
                    ModificationPhase.REPLACEMENTS,
                    BiomeLoadingPhase.POST,
                    ModificationPhase.POST_PROCESSING));

    private final BiomeModification biomeModification;

    public BiomeModificationsContextFabricImpl(String modId) {
        this.biomeModification = BiomeModifications.create(Identifier.fromNamespaceAndPath(modId, "biome_modifiers"));
    }

    @Override
    public void registerBiomeModification(BiomeLoadingPhase loadingPhase, BiomeSelector selector, Consumer<BiomeContext> biomeModifier) {
        Objects.requireNonNull(loadingPhase, "biome loading phase is null");
        Objects.requireNonNull(selector, "biome selector is null");
        Objects.requireNonNull(biomeModifier, "biome modifier is null");
        ModificationPhase modificationPhase = BIOME_PHASE_CONVERSIONS.get(loadingPhase);
        Objects.requireNonNull(modificationPhase, "modification phase is null");
        this.biomeModification.add(modificationPhase,
                (BiomeSelectionContext context) -> selector.test(BiomeSelectionContextImplFabricAccessor.class.cast(
                        context).puzzleslib$getDynamicRegistries(), context.getBiomeHolder()),
                (BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) -> {
                    biomeModifier.accept(createModificationContext(modificationContext,
                            selectionContext.getBiomeHolder()));
                });
    }

    private static BiomeContext createModificationContext(BiomeModificationContext context, Holder<Biome> biome) {
        ClimateContext climate = new ClimateContextFabric(biome.value().climateSettings, context.getWeather());
        EffectsContext specialEffects = new EffectsContextFabric(biome.value().getSpecialEffects(),
                context.getEffects());
        GenerationContext generation = new GenerationContextFabric(biome.value()
                .getGenerationSettings(), context.getGenerationSettings());
        MobSpawnsContext mobSpawns = new MobSpawnsContextFabric(biome.value().getAttributes(),
                context.getMobSpawnSettings());
        return new BiomeContext(biome, climate, specialEffects, generation, mobSpawns);
    }
}
