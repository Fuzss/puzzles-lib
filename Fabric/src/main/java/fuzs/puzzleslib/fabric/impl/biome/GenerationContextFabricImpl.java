package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.Iterables;
import fuzs.puzzleslib.common.api.biome.v2.context.GenerationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public record GenerationContextFabricImpl(BiomeModificationContext.GenerationSettingsContext context,
                                          BiomeGenerationSettings biome) implements GenerationContext {
    @Override
    public void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        this.context.addFeature(step, feature.unwrapKey().orElseThrow());
    }

    @Override
    public boolean removeFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        return this.context.removeFeature(step, feature.unwrapKey().orElseThrow());
    }

    @Override
    public boolean removeFeature(Holder<PlacedFeature> feature) {
        return this.context.removeFeature(feature.unwrapKey().orElseThrow());
    }

    @Override
    public Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration step) {
        List<HolderSet<PlacedFeature>> featureSteps = this.biome.features();
        if (step.ordinal() >= featureSteps.size()) {
            return List.of();
        } else {
            return Iterables.unmodifiableIterable(featureSteps.get(step.ordinal()));
        }
    }

    @Override
    public boolean hasFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        List<HolderSet<PlacedFeature>> featureSteps = this.biome.features();
        if (step.ordinal() >= featureSteps.size()) {
            return false;
        } else {
            return featureSteps.get(step.ordinal()).contains(feature);
        }
    }

    @Override
    public boolean hasFeature(Holder<PlacedFeature> feature) {
        return this.biome.hasFeature(feature.value());
    }

    @Override
    public void addCarver(Holder<WorldCarver> carver) {
        this.context.addCarver(carver.unwrapKey().orElseThrow());
    }

    @Override
    public boolean removeCarver(Holder<WorldCarver> carver) {
        return this.context.removeCarver(carver.unwrapKey().orElseThrow());
    }

    @Override
    public Iterable<Holder<WorldCarver>> getCarvers() {
        return Iterables.unmodifiableIterable(this.biome.getCarvers());
    }

    @Override
    public boolean hasCarver(Holder<WorldCarver> carver) {
        for (Holder<WorldCarver> holder : this.biome.getCarvers()) {
            if (holder.value() == carver.value()) {
                return true;
            }
        }

        return false;
    }
}
