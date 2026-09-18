package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.common.api.data.v3.loot.NamedLootContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link NamedLootContext} implementation delegating to the vanilla context provided by the loot table
 * provider, adding the mod id on top.
 */
record NamedLootContextImpl(String modId,
                            LootTableSubProvider.Context context) implements NamedLootContext {

    @Override
    public String getModId() {
        return this.modId;
    }

    @Override
    public Holder.Reference<LootTable> accept(ResourceKey<LootTable> key, LootTable.Builder value) {
        return this.context.accept(key, value);
    }

    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
        return this.context.lookup(key);
    }

    @Override
    @Deprecated
    public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
        return this.context.listContextElements(key);
    }

    @Override
    public <S> Optional<HolderLookup<S>> holderLookup(ResourceKey<? extends Registry<? extends S>> registry) {
        return this.context.holderLookup(registry);
    }

    @Override
    public LootTableSubProvider.Context withConditions(List<ICondition> conditions) {
        // re-wrap the result, otherwise the mod id would be lost
        return new NamedLootContextImpl(this.modId, this.context.withConditions(conditions));
    }
}
