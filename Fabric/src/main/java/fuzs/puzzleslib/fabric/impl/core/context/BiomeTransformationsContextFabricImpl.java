package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.BiomeTransformer;
import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeTransformationsContext;
import fuzs.puzzleslib.common.impl.biome.BiomeTransformerContextImpl;
import fuzs.puzzleslib.fabric.impl.biome.*;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.resources.Identifier;

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
        this.modification.add(modificationPhase, (BiomeSelectionContext selectionContext) -> {
            return selector.test(selectionContext.getRegistryAccess(), selectionContext.getBiomeHolder());
        }, (BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) -> {
            BiomeTransformer.Context context = buildTransformerContext(modificationContext);
            transformer.accept(selectionContext.getRegistryAccess(), selectionContext.getBiomeHolder(), context);
        });
    }

    private static BiomeTransformer.Context buildTransformerContext(BiomeModificationContext context) {
        AttributesContext attributes = new AttributesContextFabricImpl(context.getAttributes());
        ClimateContext climate = new ClimateContextFabricImpl(context.getWeather());
        EffectsContext specialEffects = new EffectsContextFabricImpl(context.getEffects());
        GenerationContext generation = new GenerationContextFabricImpl(context.getGenerationSettings());
        MobSpawnsContext mobSpawns = new MobSpawnsContextFabricImpl(context.getMobSpawnSettings());
        return new BiomeTransformerContextImpl(attributes, climate, specialEffects, generation, mobSpawns);
    }
}
