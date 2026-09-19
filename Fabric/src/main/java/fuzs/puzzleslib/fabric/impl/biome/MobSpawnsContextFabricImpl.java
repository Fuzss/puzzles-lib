package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.biome.v2.MobSpawnsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record MobSpawnsContextFabricImpl(BiomeModificationContext.MobSpawnSettingsContext context,
                                         EnvironmentAttributeMap biome) implements MobSpawnsContext {
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
        IntProvider count;
        if (minCount == maxCount) {
            count = new ConstantInt(minCount);
        } else {
            count = new UniformInt(minCount, maxCount);
        }

        this.context.addSpawn(entityType.getCategory(), new MobSpawnSettings.SpawnerData(entityType, count), weight);
    }

    @Override
    public void addSpawn(EntityType<?> entityType, int weight, IntProvider count) {
        this.context.addSpawn(entityType.getCategory(), new MobSpawnSettings.SpawnerData(entityType, count), weight);
    }

    @Override
    public boolean removeSpawn(EntityType<?> entityType) {
        return this.context.removeSpawnsOfEntityType(entityType);
    }

    @Override
    public Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> getSpawns() {
        ImmutableMap.Builder<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> builder = ImmutableMap.builder();
        for (MobCategory mobCategory : MobCategory.values()) {
            List<Weighted<MobSpawnSettings.SpawnerData>> spawns = this.context.getMobs(mobCategory);
            if (!spawns.isEmpty()) {
                builder.put(mobCategory, spawns);
            }
        }

        return builder.build();
    }

    @Override
    public MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.getMobSpawns().getMobSpawnCost(entityType);
    }

    @Override
    public void addSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobCharge(entityType, charge, energyBudget);
    }

    @Override
    public boolean removeSpawnCost(EntityType<?> entityType) {
        if (this.getMobSpawns().getMobSpawnCost(entityType) != null) {
            this.context.clearMobCharge(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getSpawnCosts() {
        // TODO check if this is a mutable type, it should not be
        return this.getMobSpawns().allSpawnCosts();
    }

    private MobSpawnSettings getMobSpawns() {
        return this.biome.applyModifier(EnvironmentAttributes.NATURAL_MOB_SPAWNS,
                EnvironmentAttributes.NATURAL_MOB_SPAWNS.defaultValue());
    }
}
