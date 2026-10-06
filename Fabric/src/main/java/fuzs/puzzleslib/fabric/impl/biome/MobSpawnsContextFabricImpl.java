package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.MobSpawnsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MobSpawnsContextFabricImpl(BiomeModificationContext.MobSpawnSettingsContext context) implements MobSpawnsContext {
    @Override
    public List<Weighted<MobSpawnSettings.SpawnerData>> getSpawns(MobCategory mobCategory) {
        return this.context.getMobs(mobCategory);
    }

    @Override
    public boolean removeSpawns(MobCategory mobCategory) {
        if (!this.context.getMobs(mobCategory).isEmpty()) {
            this.context.clearSpawns(mobCategory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable Weighted<MobSpawnSettings.SpawnerData> getSpawn(EntityType<?> entityType) {
        return this.context.getMobs(entityType.getCategory())
                .stream()
                .filter((Weighted<MobSpawnSettings.SpawnerData> spawn) -> spawn.value().type() == entityType)
                .findFirst()
                .orElse(null);
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
        return this.context.removeSpawnsOfEntityType(entityType);
    }

    @Override
    public Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> getSpawns() {
        return this.context.getMobs();
    }

    @Override
    public MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.context.getMobCharge(entityType);
    }

    @Override
    public void addSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobCharge(entityType, charge, energyBudget);
    }

    @Override
    public boolean removeSpawnCost(EntityType<?> entityType) {
        if (this.context.getMobCharge(entityType) != null) {
            this.context.clearMobCharge(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getSpawnCosts() {
        return this.context.getMobCharges();
    }
}
