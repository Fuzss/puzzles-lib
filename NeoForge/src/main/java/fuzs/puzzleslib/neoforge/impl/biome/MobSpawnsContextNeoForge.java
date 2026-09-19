package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.common.api.biome.v1.MobSpawnsContext;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.EnvironmentAttributeMapBuilder;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public record MobSpawnsContextNeoForge(MobSpawnSettingsBuilder context,
                                       EnvironmentAttributeMapBuilder attributes) implements MobSpawnsContext {

    @Override
    public void setCreatureGenerationProbability(float probability) {
        this.attributes.set(EnvironmentAttributes.CREATURE_WORLD_GEN_SPAWN_PROBABILITY, probability);
    }

    @Override
    public void addSpawn(MobCategory mobCategory, int weight, MobSpawnSettings.SpawnerData spawnerData) {
        this.context.addSpawn(spawnerData.type(), weight, spawnerData.count());
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        for (MobCategory mobCategory : this.context.getSpawnerTypes()) {
            for (Weighted<MobSpawnSettings.SpawnerData> spawnerData : this.context.getSpawner(mobCategory).getList()) {
                if (filter.test(mobCategory, spawnerData.value())) {
                    mutableBoolean.setTrue();
                    break;
                }
            }

            this.context.getSpawner(mobCategory).removeIf((Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
                return filter.test(mobCategory, spawnerData.value());
            });
        }

        return mutableBoolean.isTrue();
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobSpawnCost(entityType, charge, energyBudget);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        if (this.getSpawnCost(entityType) != null) {
            this.context.removeSpawnCost(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Weighted<MobSpawnSettings.SpawnerData>> getSpawnerData(MobCategory mobCategory) {
        return this.context.getSpawner(mobCategory).getList();
    }

    @Override
    public Set<EntityType<?>> getEntityTypesWithSpawnCost() {
        return this.context.getEntityTypes();
    }

    @Override
    public MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.context.getCost(entityType);
    }

    @Override
    public float getCreatureGenerationProbability() {
        EnvironmentAttribute<Float> attribute = EnvironmentAttributes.CREATURE_WORLD_GEN_SPAWN_PROBABILITY;
        EnvironmentAttributeMap.Entry<Float, ?> entry = this.attributes.get(attribute);
        return entry != null ? entry.applyModifier(attribute.defaultValue()) : attribute.defaultValue();
    }
}
