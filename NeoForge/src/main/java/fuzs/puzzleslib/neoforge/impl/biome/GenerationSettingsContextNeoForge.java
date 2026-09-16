package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.common.api.biome.v1.GenerationSettingsContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.Collections;

public record GenerationSettingsContextNeoForge(Registry<WorldCarver> carvers,
                                                Registry<PlacedFeature> features,
                                                BiomeGenerationSettingsBuilder context) implements GenerationSettingsContext {

    public GenerationSettingsContextNeoForge(RegistryAccess registryAccess, BiomeGenerationSettingsBuilder context) {
        this(registryAccess.lookupOrThrow(Registries.CARVER),
                registryAccess.lookupOrThrow(Registries.PLACED_FEATURE),
                context);
    }

    @Override
    public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        PlacedFeature feature = this.features.getValueOrThrow(featureKey);
        return this.context.getFeatures(step).removeIf(featureHolder -> featureHolder.value() == feature);
    }

    @Override
    public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        this.context.addFeature(step, this.features.getOrThrow(featureKey));
    }

    @Override
    public void addCarver(ResourceKey<WorldCarver> carverKey) {
        this.context.addCarver(this.carvers.getOrThrow(carverKey));
    }

    @Override
    public boolean removeCarver(ResourceKey<WorldCarver> carverKey) {
        WorldCarver carver = this.carvers.getValueOrThrow(carverKey);
        return this.context.getCarvers().removeIf(carverHolder -> carverHolder.value() == carver);
    }

    @Override
    public Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage) {
        // immutable just as Fabric, as biome modifications happen after biomes are built over there where everything is already immutable
        return Collections.unmodifiableList(this.context.getFeatures(stage));
    }

    @Override
    public Iterable<Holder<WorldCarver>> getCarvers() {
        // immutable just as Fabric, as biome modifications happen after biomes are built over there where everything is already immutable
        return Collections.unmodifiableList(this.context.getCarvers());
    }
}
