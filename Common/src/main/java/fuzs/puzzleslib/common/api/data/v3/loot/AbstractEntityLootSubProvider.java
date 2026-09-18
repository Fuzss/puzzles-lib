package fuzs.puzzleslib.common.api.data.v3.loot;

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

public abstract class AbstractEntityLootSubProvider extends EntityLootSubProvider {

    public AbstractEntityLootSubProvider(LootTableSubProvider.Context output) {
        super(FeatureFlags.REGISTRY.allFlags(), output);
    }

    @Override
    public abstract void generate();

    @Override
    public void run() {
        this.generate();
        Set<ResourceKey<LootTable>> seen = new HashSet<>();
        String modId = ((NamedLootContext) this.output).getModId();

        BuiltInRegistries.ENTITY_TYPE.listElements().forEach((Holder.Reference<EntityType<?>> holder) -> {
            EntityType<?> entityType = holder.value();
            Optional<ResourceKey<LootTable>> defaultLootTable = entityType.getDefaultLootTable();
            if (defaultLootTable.isPresent()) {
                Map<ResourceKey<LootTable>, LootTable.Builder> builders = this.map.remove(entityType);
                if (builders == null || !builders.containsKey(defaultLootTable.get())) {
                    // Only our own loot tables are required to be generated here.
                    // Everything else is provided by vanilla or other mods and is simply skipped.
                    if (defaultLootTable.get().identifier().getNamespace().equals(modId)) {
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
