package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.Iterables;
import fuzs.puzzleslib.common.api.biome.v1.GenerationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public record GenerationContextFabric(BiomeGenerationSettings generationSettings,
                                      BiomeModificationContext.GenerationSettingsContext context) implements GenerationContext {

    @Override
    public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        return this.context.removeFeature(step, featureKey);
    }

    @Override
    public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        this.context.addFeature(step, featureKey);
    }

    @Override
    public void addCarver(ResourceKey<WorldCarver> carverKey) {
        this.context.addCarver(carverKey);
    }

    @Override
    public boolean removeCarver(ResourceKey<WorldCarver> carverKey) {
        return this.context.removeCarver(carverKey);
    }

    @Override
    public Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage) {
        List<HolderSet<PlacedFeature>> featureSteps = this.generationSettings.features();
        if (stage.ordinal() >= featureSteps.size()) return List.of();
        return Iterables.unmodifiableIterable(featureSteps.get(stage.ordinal()));
    }

    @Override
    public Iterable<Holder<WorldCarver>> getCarvers() {
        return Iterables.unmodifiableIterable(this.generationSettings.getCarvers());
    }
}
