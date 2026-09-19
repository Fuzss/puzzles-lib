package fuzs.puzzleslib.common.api.biome.v1;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * @see net.minecraft.world.level.biome.BiomeGenerationSettings
 * @see net.minecraft.world.level.biome.BiomeGenerationSettings.PlainBuilder
 */
public interface GenerationContext {
    /**
     * Removes a feature from one of this biomes generation steps, and returns if any features were removed.
     */
    boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey);

    /**
     * Removes a feature from all of this biomes generation steps, and returns if any features were removed.
     */
    default boolean removeFeature(ResourceKey<PlacedFeature> featureKey) {
        boolean anyFound = false;

        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            if (this.removeFeature(step, featureKey)) {
                anyFound = true;
            }
        }

        return anyFound;
    }

    /**
     * Adds a feature to one of this biomes generation steps, identified by the placed feature's registry key.
     */
    void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey);

    /**
     * Adds a carver to one of this biomes generation steps.
     */
    void addCarver(ResourceKey<WorldCarver> carverKey);

    /**
     * Removes all carvers with the given key from one of this biomes generation steps.
     *
     * @return True if any carvers were removed.
     */
    boolean removeCarver(ResourceKey<WorldCarver> carverKey);

    /**
     * @param stage decoration stage
     * @return all features registered for the given <code>stage</code>
     */
    Iterable<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration stage);

    /**
     * @return all carvers registered for the given <code>stage</code>
     */
    Iterable<Holder<WorldCarver>> getCarvers();
}
