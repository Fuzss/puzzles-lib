package fuzs.puzzleslib.fabric.impl.biome;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.common.api.biome.v1.MobSpawnsContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public record MobSpawnsContextFabric(EnvironmentAttributeMap attributes,
                                     BiomeModificationContext.MobSpawnSettingsContext context) implements MobSpawnsContext {

    @Override
    public void setCreatureGenerationProbability(float probability) {
        this.context.setCreatureGenerationProbability(probability);
    }

    @Override
    public void addSpawn(MobCategory mobCategory, int weight, MobSpawnSettings.SpawnerData spawnerData) {
        this.context.addSpawn(spawnerData.type().getCategory(), spawnerData, weight);
    }

    @Override
    public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter) {
        return this.context.removeSpawns(filter);
    }

    @Override
    public void setSpawnCost(EntityType<?> entityType, double energyBudget, double charge) {
        this.context.addMobCharge(entityType, charge, energyBudget);
    }

    @Override
    public boolean clearSpawnCost(EntityType<?> entityType) {
        if (this.getSpawnCost(entityType) != null) {
            this.context.clearMobCharge(entityType);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Weighted<MobSpawnSettings.SpawnerData>> getSpawnerData(MobCategory mobCategory) {
        return this.context.getMobs(mobCategory);
    }

    @Override
    public Set<EntityType<?>> getEntityTypesWithSpawnCost() {
        return BuiltInRegistries.ENTITY_TYPE.stream()
                .filter((EntityType<?> entityType) -> this.getSpawnCost(entityType) != null)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public MobSpawnSettings.@Nullable MobSpawnCost getSpawnCost(EntityType<?> entityType) {
        return this.getAttributeValue(EnvironmentAttributes.NATURAL_MOB_SPAWNS).getMobSpawnCost(entityType);
    }

    @Override
    public float getCreatureGenerationProbability() {
        return this.getAttributeValue(EnvironmentAttributes.CREATURE_WORLD_GEN_SPAWN_PROBABILITY);
    }

    private <T> T getAttributeValue(EnvironmentAttribute<T> attribute) {
        EnvironmentAttributeMap.Entry<T, ?> entry = this.attributes.get(attribute);
        return entry != null ? entry.applyModifier(attribute.defaultValue()) : attribute.defaultValue();
    }
}
