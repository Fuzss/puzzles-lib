package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Context for modifying the mob spawn settings of a biome.
 *
 * @see MobSpawnSettings
 * @see MobSpawnSettings.Builder
 */
public interface MobSpawnsContext {
    /**
     * Get all spawns of a mob category.
     *
     * @param mobCategory the mob category
     * @return the spawns of the mob category
     *
     * @see MobSpawnSettings#getMobsToSpawn(MobCategory)
     */
    List<Weighted<MobSpawnSettings.SpawnerData>> getSpawns(MobCategory mobCategory);

    /**
     * Remove all spawns of a mob category.
     *
     * @param mobCategory the mob category
     * @return whether any spawns were removed
     *
     * @see MobSpawnSettings.Builder#noSpawns(MobCategory)
     */
    boolean removeSpawns(MobCategory mobCategory);

    /**
     * Get the spawn of an entity type.
     *
     * @param entityType the entity type
     * @return the spawn of the entity type, or null if there is none
     */
    @Nullable Weighted<MobSpawnSettings.SpawnerData> getSpawn(EntityType<?> entityType);

    /**
     * Add a spawn for an entity type with a random spawn count between a minimum and a maximum.
     *
     * @param entityType the entity type
     * @param weight     the spawn weight
     * @param minCount   the minimum spawn count
     * @param maxCount   the maximum spawn count
     * @see MobSpawnSettings.Builder#addSpawn(EntityType, int, int, int)
     */
    void addSpawn(EntityType<?> entityType, int weight, int minCount, int maxCount);

    /**
     * Add a spawn for an entity type with the given spawn count.
     *
     * @param entityType the entity type
     * @param weight     the spawn weight
     * @param count      the spawn count
     * @see MobSpawnSettings.Builder#addSpawn(EntityType, int, IntProvider)
     */
    void addSpawn(EntityType<?> entityType, int weight, IntProvider count);

    /**
     * Remove all spawns of an entity type.
     *
     * @param entityType the entity type
     * @return whether any spawns were removed
     */
    boolean removeSpawn(EntityType<?> entityType);

    /**
     * Get all spawns of the biome, grouped by mob category.
     *
     * @return the spawns of the biome
     *
     * @see MobSpawnSettings#spawnsByCategory
     */
    Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> getSpawns();

    /**
     * Get the spawn cost of an entity type.
     *
     * @param entityType the entity type
     * @return the spawn cost of the entity type, or null if there is none
     *
     * @see MobSpawnSettings#getMobSpawnCost(EntityType)
     */
    MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType);

    /**
     * Set the spawn cost of an entity type.
     *
     * @param entityType   the entity type
     * @param energyBudget the energy budget
     * @param charge       the charge
     * @see MobSpawnSettings.Builder#addMobSpawnCost(EntityType, double, double)
     */
    void addSpawnCost(EntityType<?> entityType, double energyBudget, double charge);

    /**
     * Remove the spawn cost of an entity type.
     *
     * @param entityType the entity type
     * @return whether a spawn cost was removed
     */
    boolean removeSpawnCost(EntityType<?> entityType);

    /**
     * Get all spawn costs of the biome.
     *
     * @return the spawn costs of the biome
     *
     * @see MobSpawnSettings#allSpawnCosts()
     */
    Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getSpawnCosts();
}
