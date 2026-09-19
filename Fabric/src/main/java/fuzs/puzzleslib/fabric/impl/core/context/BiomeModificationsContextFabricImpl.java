package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.biome.v1.BiomeContext;
import fuzs.puzzleslib.common.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v1.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.fabric.impl.biome.*;
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
        AttributesContext attributes = new AttributesContextFabricImpl(context.getAttributes(),
                biome.value().getAttributes());
        ClimateContext climate = new ClimateContextFabricImpl(context.getWeather(), biome.value().climateSettings);
        EffectsContext specialEffects = new EffectsContextFabricImpl(context.getEffects(),
                biome.value().getSpecialEffects());
        GenerationContext generation = new GenerationContextFabricImpl(context.getGenerationSettings(),
                biome.value().getGenerationSettings());
        MobSpawnsContext mobSpawns = new MobSpawnsContextFabricImpl(context.getMobSpawnSettings(),
                biome.value().getAttributes());
        return new BiomeContext(biome, attributes, climate, specialEffects, generation, mobSpawns);
    }
}
