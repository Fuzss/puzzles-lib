package fuzs.puzzleslib.common.api.biome.v2;

import fuzs.puzzleslib.common.api.biome.v2.context.MobSpawnsContext;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.commons.lang3.math.Fraction;

import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;

/**
 * A builder for configuring mob spawner data in biome spawn settings.
 */
public final class SpawnerDataBuilder {
    private final MobSpawnsContext context;
    private final EntityType<?> originalEntityType;
    private IntUnaryOperator weightMapper = IntUnaryOperator.identity();
    private ToIntFunction<MobSpawnSettings.SpawnerData> minCountMapper = (MobSpawnSettings.SpawnerData spawnerData) -> {
        return spawnerData.count().minInclusive();
    };
    private ToIntFunction<MobSpawnSettings.SpawnerData> maxCountMapper = (MobSpawnSettings.SpawnerData spawnerData) -> {
        return spawnerData.count().maxInclusive();
    };

    private SpawnerDataBuilder(MobSpawnsContext context, EntityType<?> originalEntityType) {
        Objects.requireNonNull(context, "context is null");
        Objects.requireNonNull(originalEntityType, "entity type is null");
        this.context = context;
        this.originalEntityType = originalEntityType;
    }

    /**
     * Creates a new spawner data builder.
     *
     * @param context    context for mob spawn settings
     * @param entityType the entity type to configure spawning for
     * @return the builder
     */
    public static SpawnerDataBuilder create(MobSpawnsContext context, EntityType<?> entityType) {
        return new SpawnerDataBuilder(context, entityType);
    }

    /**
     * @param weight spawn weight for the entity
     * @return the builder
     */
    public SpawnerDataBuilder setWeight(int weight) {
        return this.setWeight((int oldWeight) -> {
            return weight;
        });
    }

    /**
     * @param weight fractional spawn weight for the entity
     * @return the builder
     */
    public SpawnerDataBuilder setWeight(Fraction weight) {
        Objects.requireNonNull(weight, "weight is null");
        return this.setWeight((int oldWeight) -> {
            return weight.multiplyBy(Fraction.getFraction(oldWeight, 1)).intValue();
        });
    }

    /**
     * @param weight custom weight mapping function
     * @return the builder
     */
    public SpawnerDataBuilder setWeight(IntUnaryOperator weight) {
        Objects.requireNonNull(weight, "weight is null");
        this.weightMapper = (int oldWeight) -> {
            return Math.max(1, weight.applyAsInt(oldWeight));
        };
        return this;
    }

    /**
     * @param minCount minimum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMinCount(int minCount) {
        return this.setMinCount((MobSpawnSettings.SpawnerData spawnerData) -> {
            return minCount;
        });
    }

    /**
     * @param minCount fractional minimum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMinCount(Fraction minCount) {
        Objects.requireNonNull(minCount, "min count is null");
        return this.setMinCount((MobSpawnSettings.SpawnerData spawnerData) -> {
            return minCount.multiplyBy(Fraction.getFraction(spawnerData.count().minInclusive(), 1)).intValue();
        });
    }

    /**
     * @param minCount custom minimum count mapping function
     * @return the builder
     */
    public SpawnerDataBuilder setMinCount(ToIntFunction<MobSpawnSettings.SpawnerData> minCount) {
        Objects.requireNonNull(minCount, "min count is null");
        this.minCountMapper = (MobSpawnSettings.SpawnerData spawnerData) -> {
            return Math.max(1, minCount.applyAsInt(spawnerData));
        };
        return this;
    }

    /**
     * @param maxCount maximum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMaxCount(int maxCount) {
        return this.setMaxCount((MobSpawnSettings.SpawnerData spawnerData) -> {
            return maxCount;
        });
    }

    /**
     * @param maxCount fractional maximum spawn count
     * @return the builder
     */
    public SpawnerDataBuilder setMaxCount(Fraction maxCount) {
        Objects.requireNonNull(maxCount, "max count is null");
        return this.setMaxCount((MobSpawnSettings.SpawnerData spawnerData) -> {
            return maxCount.multiplyBy(Fraction.getFraction(spawnerData.count().maxInclusive(), 1)).intValue();
        });
    }

    /**
     * @param maxCount custom maximum count mapping function
     * @return the builder
     */
    public SpawnerDataBuilder setMaxCount(ToIntFunction<MobSpawnSettings.SpawnerData> maxCount) {
        Objects.requireNonNull(maxCount, "max count is null");
        this.maxCountMapper = (MobSpawnSettings.SpawnerData spawnerData) -> {
            return Math.max(1, maxCount.applyAsInt(spawnerData));
        };
        return this;
    }

    /**
     * Applies spawner data to the specified entity type.
     *
     * @param entityType the entity type to apply spawner data to
     */
    public void apply(EntityType<?> entityType) {
        Weighted<MobSpawnSettings.SpawnerData> spawn = this.context.getSpawn(this.originalEntityType);
        if (spawn != null) {
            int weight = this.weightMapper.applyAsInt(spawn.weight());
            int minCount = this.minCountMapper.applyAsInt(spawn.value());
            int maxCount = this.maxCountMapper.applyAsInt(spawn.value());
            this.context.addSpawn(entityType, Math.min(minCount, maxCount), maxCount, weight);
        }

        MobSpawnSettings.MobSpawnCost cost = this.context.getSpawnCost(this.originalEntityType);
        if (cost != null) {
            // Just add this with the same values as the vanilla mob.
            // The spawn data weight is what matters most.
            this.context.addSpawnCost(entityType, cost.energyBudget(), cost.charge());
        }
    }
}
