package fuzs.puzzleslib.common.api.biome.v2;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Optional;

/**
 * @see BiomeSpecialEffects
 * @see BiomeSpecialEffects.Builder
 */
public interface EffectsContext {
    /**
     * @see BiomeSpecialEffects#waterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    void setWaterColor(int waterColor);

    /**
     * @see BiomeSpecialEffects#waterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    int getWaterColor();

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    void setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    Optional<Integer> getFoliageColorOverride();

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void setFoliageColorOverride(int foliageColorOverride) {
        this.setFoliageColorOverride(Optional.of(foliageColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void clearFoliageColorOverride() {
        this.setFoliageColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    void setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride);

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    Optional<Integer> getDryFoliageColorOverride();

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    default void setDryFoliageColorOverride(int dryFoliageColorOverride) {
        this.setDryFoliageColorOverride(Optional.of(dryFoliageColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    default void clearDryFoliageColorOverride() {
        this.setDryFoliageColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    void setGrassColorOverride(Optional<Integer> grassColorOverride);

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    Optional<Integer> getGrassColorOverride();

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void setGrassColorOverride(int grassColorOverride) {
        this.setGrassColorOverride(Optional.of(grassColorOverride));
    }

    /**
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void clearGrassColorOverride() {
        this.setGrassColorOverride(Optional.empty());
    }

    /**
     * @see BiomeSpecialEffects#grassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    void setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);

    /**
     * @see BiomeSpecialEffects#grassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    BiomeSpecialEffects.GrassColorModifier getGrassColorModifier();
}
