package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.GenerationContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.Collections;

public record GenerationContextNeoForgeImpl(BiomeGenerationSettingsBuilder context) implements GenerationContext {
    @Override
    public void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        this.context.addFeature(step, feature);
    }

    @Override
    public boolean removeFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        return this.context.getFeatures(step).removeIf((Holder<PlacedFeature> holder) -> {
            return holder.value() == feature.value();
        });
    }

    @Override
    public boolean removeFeature(Holder<PlacedFeature> feature) {
        boolean anyRemoved = false;
        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            if (this.removeFeature(step, feature)) {
                anyRemoved = true;
            }
        }

        return anyRemoved;
    }

    @Override
    public Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration step) {
        return Collections.unmodifiableList(this.context.getFeatures(step));
    }

    @Override
    public boolean hasFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        return this.context.getFeatures(step).contains(feature);
    }

    @Override
    public boolean hasFeature(Holder<PlacedFeature> feature) {
        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            if (this.hasFeature(step, feature)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addCarver(Holder<WorldCarver> carver) {
        this.context.addCarver(carver);
    }

    @Override
    public boolean removeCarver(Holder<WorldCarver> carver) {
        return this.context.getCarvers().removeIf((Holder<WorldCarver> holder) -> {
            return holder.value() == carver.value();
        });
    }

    @Override
    public Iterable<Holder<WorldCarver>> getCarvers() {
        return Collections.unmodifiableList(this.context.getCarvers());
    }

    @Override
    public boolean hasCarver(Holder<WorldCarver> carver) {
        return this.context.getCarvers().contains(carver);
    }
}
