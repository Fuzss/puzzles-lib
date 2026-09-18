package fuzs.puzzleslib.common.api.data.v3.loot;

import fuzs.puzzleslib.common.impl.data.DataGenerationScopes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A base implementation of {@link EntityLootSubProvider} for generating entity loot tables.
 * <p>
 * Unlike vanilla, only loot tables belonging to the generating mod are required to be generated, meaning default loot
 * table ids whose namespace matches the mod id. Loot tables for entities of vanilla or other mods can still be added
 * and are generated as well, all remaining entities are skipped.
 */
public abstract class AbstractEntityLootSubProvider extends EntityLootSubProvider {
    private final String modId;

    /**
     * @param output the context used for registering generated loot tables
     */
    public AbstractEntityLootSubProvider(LootTableSubProvider.Context output) {
        super(FeatureFlags.REGISTRY.allFlags(), output);
        this.modId = DataGenerationScopes.MOD_ID.get();
    }

    /**
     * Adds all loot tables of this provider via the various {@code add} methods inherited from
     * {@link EntityLootSubProvider}, which are then emitted to the reloadable registry by {@link #run()}.
     */
    @Override
    public abstract void generate();

    @Override
    public void run() {
        this.generate();
        Set<ResourceKey<LootTable>> seen = new HashSet<>();

        BuiltInRegistries.ENTITY_TYPE.listElements().forEach((Holder.Reference<EntityType<?>> holder) -> {
            EntityType<?> entityType = holder.value();
            Optional<ResourceKey<LootTable>> defaultLootTable = entityType.getDefaultLootTable();
            if (defaultLootTable.isPresent()) {
                Map<ResourceKey<LootTable>, LootTable.Builder> builders = this.map.remove(entityType);
                if (builders == null || !builders.containsKey(defaultLootTable.get())) {
                    // Only our own loot tables are required to be generated here.
                    // Everything else is provided by vanilla or other mods and is simply skipped.
                    if (defaultLootTable.get().identifier().getNamespace().equals(this.modId)) {
                        throw new IllegalStateException(String.format(Locale.ROOT,
                                "Missing loot table '%s' for '%s'",
                                defaultLootTable.get(),
                                holder.key().identifier()));
                    }
                }

                if (builders != null) {
                    builders.forEach((ResourceKey<LootTable> id, LootTable.Builder builder) -> {
                        if (!seen.add(id)) {
                            throw new IllegalStateException(String.format(Locale.ROOT,
                                    "Duplicate loot table '%s' for '%s'",
                                    id,
                                    holder.key().identifier()));
                        }

                        this.output.accept(id, builder);
                    });
                }
            } else {
                Map<ResourceKey<LootTable>, LootTable.Builder> builders = this.map.remove(entityType);
                if (builders != null) {
                    throw new IllegalStateException(String.format(Locale.ROOT,
                            "Weird loot table(s) '%s' for '%s', not a LivingEntity so should not have loot",
                            builders.keySet()
                                    .stream()
                                    .map(ResourceKey::identifier)
                                    .map(Identifier::toString)
                                    .collect(Collectors.joining(",")),
                            holder.key().identifier()));
                }
            }
        });

        if (!this.map.isEmpty()) {
            throw new IllegalStateException(
                    "Created loot tables for entities not supported by data pack: " + this.map.keySet());
        }
    }
}
