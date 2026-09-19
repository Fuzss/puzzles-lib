package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.EffectsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Objects;
import java.util.Optional;

public record EffectsContextFabricImpl(BiomeModificationContext.EffectsContext context,
                                       BiomeSpecialEffects biome) implements EffectsContext {
    @Override
    public void setWaterColor(int waterColor) {
        this.context.setWaterColor(waterColor);
    }

    @Override
    public int getWaterColor() {
        return this.biome.waterColor();
    }

    @Override
    public void setFoliageColorOverride(Optional<Integer> foliageColorOverride) {
        this.context.setFoliageColorOverride(foliageColorOverride);
    }

    @Override
    public Optional<Integer> getFoliageColorOverride() {
        return this.biome.foliageColorOverride();
    }

    @Override
    public void setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride) {
        this.context.setDryFoliageColorOverride(dryFoliageColorOverride);
    }

    @Override
    public Optional<Integer> getDryFoliageColorOverride() {
        return this.biome.dryFoliageColorOverride();
    }

    @Override
    public void setGrassColorOverride(Optional<Integer> grassColorOverride) {
        this.context.setGrassColorOverride(grassColorOverride);
    }

    @Override
    public Optional<Integer> getGrassColorOverride() {
        return this.biome.grassColorOverride();
    }

    @Override
    public void setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
        Objects.requireNonNull(grassColorModifier, "grass color modifier is null");
        this.context.setGrassColorModifier(grassColorModifier);
    }

    @Override
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
        return this.biome.grassColorModifier();
    }
}
