package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Optional;

/**
 * Context for modifying the special effects of a biome.
 *
 * @see BiomeSpecialEffects
 * @see BiomeSpecialEffects.Builder
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface EffectsContext {
    /**
     * Set the water color of the biome.
     *
     * @param waterColor the water color
     * @see BiomeSpecialEffects#waterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    void setWaterColor(int waterColor);

    /**
     * Get the water color of the biome.
     *
     * @return the water color
     *
     * @see BiomeSpecialEffects#waterColor()
     * @see BiomeSpecialEffects.Builder#waterColor(int)
     */
    int getWaterColor();

    /**
     * Set the foliage color override of the biome.
     *
     * @param foliageColorOverride the foliage color override or empty to clear it
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    void setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    /**
     * Get the foliage color override of the biome.
     *
     * @return the foliage color override, or empty if there is none
     *
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    Optional<Integer> getFoliageColorOverride();

    /**
     * Set the foliage color override of the biome.
     *
     * @param foliageColorOverride the foliage color override
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void setFoliageColorOverride(int foliageColorOverride) {
        this.setFoliageColorOverride(Optional.of(foliageColorOverride));
    }

    /**
     * Clear the foliage color override of the biome.
     *
     * @see BiomeSpecialEffects#foliageColorOverride()
     * @see BiomeSpecialEffects.Builder#foliageColorOverride(int)
     */
    default void clearFoliageColorOverride() {
        this.setFoliageColorOverride(Optional.empty());
    }

    /**
     * Set the dry foliage color override of the biome.
     *
     * @param dryFoliageColorOverride the dry foliage color override or empty to clear it
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    void setDryFoliageColorOverride(Optional<Integer> dryFoliageColorOverride);

    /**
     * Get the dry foliage color override of the biome.
     *
     * @return the dry foliage color override, or empty if there is none
     *
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    Optional<Integer> getDryFoliageColorOverride();

    /**
     * Set the dry foliage color override of the biome.
     *
     * @param dryFoliageColorOverride the dry foliage color override
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    default void setDryFoliageColorOverride(int dryFoliageColorOverride) {
        this.setDryFoliageColorOverride(Optional.of(dryFoliageColorOverride));
    }

    /**
     * Clear the dry foliage color override of the biome.
     *
     * @see BiomeSpecialEffects#dryFoliageColorOverride()
     * @see BiomeSpecialEffects.Builder#dryFoliageColorOverride(int)
     */
    default void clearDryFoliageColorOverride() {
        this.setDryFoliageColorOverride(Optional.empty());
    }

    /**
     * Set the grass color override of the biome.
     *
     * @param grassColorOverride the grass color override or empty to clear it
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    void setGrassColorOverride(Optional<Integer> grassColorOverride);

    /**
     * Get the grass color override of the biome.
     *
     * @return the grass color override, or empty if there is none
     *
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    Optional<Integer> getGrassColorOverride();

    /**
     * Set the grass color override of the biome.
     *
     * @param grassColorOverride the grass color override
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void setGrassColorOverride(int grassColorOverride) {
        this.setGrassColorOverride(Optional.of(grassColorOverride));
    }

    /**
     * Clear the grass color override of the biome.
     *
     * @see BiomeSpecialEffects#grassColorOverride()
     * @see BiomeSpecialEffects.Builder#grassColorOverride(int)
     */
    default void clearGrassColorOverride() {
        this.setGrassColorOverride(Optional.empty());
    }

    /**
     * Set the grass color modifier of the biome.
     *
     * @param grassColorModifier the grass color modifier
     * @see BiomeSpecialEffects#grassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    void setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);

    /**
     * Get the grass color modifier of the biome.
     *
     * @return the grass color modifier
     *
     * @see BiomeSpecialEffects#grassColorModifier()
     * @see BiomeSpecialEffects.Builder#grassColorModifier(BiomeSpecialEffects.GrassColorModifier)
     */
    BiomeSpecialEffects.GrassColorModifier getGrassColorModifier();
}
