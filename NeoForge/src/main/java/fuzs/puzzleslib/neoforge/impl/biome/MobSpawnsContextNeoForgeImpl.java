package fuzs.puzzleslib.neoforge.impl.biome;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.biome.v2.context.MobSpawnsContext;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MobSpawnsContextNeoForgeImpl(MobSpawnSettingsBuilder context) implements MobSpawnsContext {
    @Override
    public List<Weighted<MobSpawnSettings.SpawnerData>> getSpawns(MobCategory mobCategory) {
        WeightedList.Builder<MobSpawnSettings.SpawnerData> spawns = this.context.getSpawner(mobCategory);
        return spawns != null ? spawns.getList() : List.of();
    }

    @Override
    public boolean removeSpawns(MobCategory mobCategory) {
        WeightedList.Builder<MobSpawnSettings.SpawnerData> spawns = this.context.getSpawner(mobCategory);
        if (spawns != null && !spawns.getList().isEmpty()) {
            this.context.noSpawns(mobCategory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable Weighted<MobSpawnSettings.SpawnerData> getSpawn(EntityType<?> entityType) {
        WeightedList.Builder<MobSpawnSettings.SpawnerData> spawns = this.context.getSpawner(entityType.getCategory());
        if (spawns != null) {
            return spawns.getList()
                    .stream()
                    .filter((Weighted<MobSpawnSettings.SpawnerData> spawn) -> spawn.value().type() == entityType)
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public void addSpawn(EntityType<?> entityType, int weight, int minCount, int maxCount) {
        this.context.addSpawn(entityType, weight, minCount, maxCount);
    }

    @Override
    public void addSpawn(EntityType<?> entityType, int weight, IntProvider count) {
        this.context.addSpawn(entityType, weight, count);
    }

    @Override
    public boolean removeSpawn(EntityType<?> entityType) {
        MutableBoolean anyRemoved = new MutableBoolean();
        this.context.removeSpawns((Weighted<MobSpawnSettings.SpawnerData> spawn) -> {
            if (spawn.value().type() == entityType) {
                anyRemoved.setTrue();
                return true;
            } else {
                return false;
            }
        });
        return anyRemoved.isTrue();
    }

    @Override
    public Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> getSpawns() {
        ImmutableMap.Builder<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> builder = ImmutableMap.builder();
        for (MobCategory mobCategory : this.context.getSpawnerTypes()) {
            WeightedList.Builder<MobSpawnSettings.SpawnerData> weightedSpawns = this.context.getSpawner(mobCategory);
            if (weightedSpawns != null) {
                List<Weighted<MobSpawnSettings.SpawnerData>> spawns = weightedSpawns.getList();
                if (!spawns.isEmpty()) {
                    builder.put(mobCategory, spawns);
                }
            }
        }

        return builder.build();
    }

    @Override
    public MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.context.getCost(entityType);
    }

    @Override
    public void addSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobSpawnCost(entityType, charge, energyBudget);
    }

    @Override
    public boolean removeSpawnCost(EntityType<?> entityType) {
        if (this.context.getEntityTypes().contains(entityType)) {
            this.context.removeSpawnCost(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getSpawnCosts() {
        ImmutableMap.Builder<EntityType<?>, MobSpawnSettings.MobSpawnCost> spawnCosts = ImmutableMap.builder();
        for (EntityType<?> entityType : this.context.getEntityTypes()) {
            MobSpawnSettings.MobSpawnCost spawnCost = this.context.getCost(entityType);
            if (spawnCost != null) {
                spawnCosts.put(entityType, spawnCost);
            }
        }

        return spawnCosts.build();
    }
}
