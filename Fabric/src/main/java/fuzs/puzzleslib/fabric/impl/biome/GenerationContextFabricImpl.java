package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.GenerationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record GenerationContextFabricImpl(BiomeModificationContext.GenerationSettingsContext context) implements GenerationContext {
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
        return this.context.getFeatures(step);
    }

    @Override
    public boolean hasFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        return this.context.hasFeature(step, feature);
    }

    @Override
    public boolean hasFeature(Holder<PlacedFeature> feature) {
        return this.context.hasFeature(feature);
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
        return this.context.getCarvers();
    }

    @Override
    public boolean hasCarver(Holder<WorldCarver> carver) {
        return this.context.hasCarver(carver);
    }
}
