package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.BiomeTransformer;
import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeTransformationsContext;
import fuzs.puzzleslib.common.impl.biome.BiomeTransformerContextImpl;
import fuzs.puzzleslib.fabric.impl.biome.*;
import fuzs.puzzleslib.fabric.mixin.accessor.BiomeFabricAccessor;
import fuzs.puzzleslib.fabric.mixin.accessor.BiomeSelectionContextImplFabricAccessor;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Objects;

public final class BiomeTransformationsContextFabricImpl implements BiomeTransformationsContext {
    private static final Map<BiomeLoadingPhase, ModificationPhase> MODIFICATION_PHASES = Maps.immutableEnumMap(Map.of(
            BiomeLoadingPhase.ADD,
            ModificationPhase.ADDITIONS,
            BiomeLoadingPhase.REMOVE,
            ModificationPhase.REMOVALS,
            BiomeLoadingPhase.MODIFY,
            ModificationPhase.REPLACEMENTS,
            BiomeLoadingPhase.POST,
            ModificationPhase.POST_PROCESSING));

    private final BiomeModification modification;

    public BiomeTransformationsContextFabricImpl(String modId) {
        this.modification = BiomeModifications.create(Identifier.fromNamespaceAndPath(modId, "transformers"));
    }

    @Override
    public void registerBiomeTransformation(BiomeLoadingPhase loadingPhase, BiomeSelector selector, BiomeTransformer transformer) {
        Objects.requireNonNull(loadingPhase, "loading phase is null");
        Objects.requireNonNull(selector, "selector is null");
        Objects.requireNonNull(transformer, "transformer is null");
        ModificationPhase modificationPhase = MODIFICATION_PHASES.get(loadingPhase);
        Objects.requireNonNull(modificationPhase, "modification phase is null");
        this.modification.add(modificationPhase, (BiomeSelectionContext context) -> {
            RegistryAccess registryAccess = BiomeSelectionContextImplFabricAccessor.class.cast(context)
                    .puzzleslib$getDynamicRegistries();
            return selector.test(registryAccess, context.getBiomeHolder());
        }, (BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) -> {
            RegistryAccess registryAccess = BiomeSelectionContextImplFabricAccessor.class.cast(selectionContext)
                    .puzzleslib$getDynamicRegistries();
            BiomeTransformer.Context context = buildTransformerContext(modificationContext,
                    selectionContext.getBiomeHolder());
            transformer.accept(registryAccess, selectionContext.getBiomeHolder(), context);
        });
    }

    private static BiomeTransformer.Context buildTransformerContext(BiomeModificationContext context, Holder<Biome> biome) {
        AttributesContext attributes = new AttributesContextFabricImpl(context.getAttributes(),
                biome.value().getAttributes());
        Biome.ClimateSettings climateSettings = BiomeFabricAccessor.class.cast(biome.value())
                .puzzleslib$getClimateSettings();
        ClimateContext climate = new ClimateContextFabricImpl(context.getWeather(), climateSettings);
        EffectsContext specialEffects = new EffectsContextFabricImpl(context.getEffects(),
                biome.value().getSpecialEffects());
        GenerationContext generation = new GenerationContextFabricImpl(context.getGenerationSettings(),
                biome.value().getGenerationSettings());
        MobSpawnsContext mobSpawns = new MobSpawnsContextFabricImpl(context.getMobSpawnSettings(),
                biome.value().getAttributes());
        return new BiomeTransformerContextImpl(attributes, climate, specialEffects, generation, mobSpawns);
    }
}
