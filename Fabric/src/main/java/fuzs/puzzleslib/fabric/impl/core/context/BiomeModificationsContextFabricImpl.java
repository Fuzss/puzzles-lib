package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.common.api.biome.v2.BiomeLoadingPhase;
import fuzs.puzzleslib.common.api.biome.v2.BiomeModifier;
import fuzs.puzzleslib.common.api.biome.v2.BiomeSelector;
import fuzs.puzzleslib.common.api.biome.v2.context.*;
import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.common.impl.biome.BiomeContext;
import fuzs.puzzleslib.fabric.impl.biome.*;
import fuzs.puzzleslib.fabric.mixin.accessor.BiomeSelectionContextImplFabricAccessor;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Objects;

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
    public void registerBiomeModification(BiomeLoadingPhase loadingPhase, BiomeSelector selector, BiomeModifier modifier) {
        Objects.requireNonNull(loadingPhase, "loading phase is null");
        Objects.requireNonNull(selector, "selector is null");
        Objects.requireNonNull(modifier, "modifier is null");
        ModificationPhase modificationPhase = BIOME_PHASE_CONVERSIONS.get(loadingPhase);
        Objects.requireNonNull(modificationPhase, "modification phase is null");
        this.biomeModification.add(modificationPhase, (BiomeSelectionContext context) -> {
            RegistryAccess registryAccess = BiomeSelectionContextImplFabricAccessor.class.cast(context)
                    .puzzleslib$getDynamicRegistries();
            return selector.test(registryAccess, context.getBiomeHolder());
        }, (BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) -> {
            RegistryAccess registryAccess = BiomeSelectionContextImplFabricAccessor.class.cast(selectionContext)
                    .puzzleslib$getDynamicRegistries();
            BiomeContext biomeContext = createModificationContext(modificationContext,
                    selectionContext.getBiomeHolder());
            modifier.accept(registryAccess, selectionContext.getBiomeHolder(), biomeContext);
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
        return new BiomeContext(attributes, climate, specialEffects, generation, mobSpawns);
    }
}
