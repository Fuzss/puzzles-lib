package fuzs.puzzleslib.common.api.biome.v2;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @see MobSpawnSettings
 * @see MobSpawnSettings.Builder
 */
public interface MobSpawnsContext {
    /**
     * @see MobSpawnSettings#getMobsToSpawn(MobCategory)
     */
    List<Weighted<MobSpawnSettings.SpawnerData>> getSpawns(MobCategory mobCategory);

    /**
     * @see MobSpawnSettings.Builder#noSpawns(MobCategory)
     */
    boolean removeSpawns(MobCategory mobCategory);

    @Nullable Weighted<MobSpawnSettings.SpawnerData> getSpawn(EntityType<?> entityType);

    /**
     * @see MobSpawnSettings.Builder#addSpawn(EntityType, int, int, int)
     */
    void addSpawn(EntityType<?> entityType, int weight, int minCount, int maxCount);

    /**
     * @see MobSpawnSettings.Builder#addSpawn(EntityType, int, IntProvider)
     */
    void addSpawn(EntityType<?> entityType, int weight, IntProvider count);

    boolean removeSpawn(EntityType<?> entityType);

    /**
     * @see MobSpawnSettings#spawnsByCategory
     */
    Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> getSpawns();

    /**
     * @see MobSpawnSettings#getMobSpawnCost(EntityType)
     */
    MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType);

    /**
     * @see MobSpawnSettings.Builder#addMobSpawnCost(EntityType, double, double)
     */
    void addSpawnCost(EntityType<?> entityType, double energyBudget, double charge);

    boolean removeSpawnCost(EntityType<?> entityType);

    /**
     * @see MobSpawnSettings#allSpawnCosts()
     */
    Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getSpawnCosts();
}
