package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.common.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Strippable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class GameplayContentContextNeoForgeImpl implements GameplayContentContext {
    private final Map<Holder<Block>, Flammable> flammables = new LinkedHashMap<>();
    private final DataMapBuilder<Holder<Block>, Holder<Block>> strippables;
    private final DataMapBuilder<Holder<Block>, Holder<Block>> oxidizables;
    private final DataMapBuilder<Holder<Block>, Holder<Block>> waxables;
    private final IEventBus eventBus;

    public GameplayContentContextNeoForgeImpl(String modId, IEventBus eventBus) {
        this.eventBus = eventBus;
        this.strippables = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.STRIPPABLES,
                Function.identity(),
                (Holder<Block> holder) -> new Strippable(holder.value()));
        this.oxidizables = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.OXIDIZABLES,
                Function.identity(),
                (Holder<Block> holder) -> new Oxidizable(holder.value()));
        this.waxables = new DataMapBuilder<>(modId,
                NeoForgeDataMaps.WAXABLES,
                Function.identity(),
                (Holder<Block> holder) -> new Waxable(holder.value()));
    }

    @Override
    public void registerFlammable(Holder<Block> flammableBlock, int encouragement, int flammability) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is non-positive");
        Preconditions.checkArgument(flammability > 0, "flammability is non-positive");
        Objects.requireNonNull(flammableBlock, "flammable block is null");
        if (this.flammables.isEmpty()) {
            this.eventBus.addListener((final FMLCommonSetupEvent event) -> {
                event.enqueueWork(() -> {
                    this.flammables.forEach((Holder<Block> holder, Flammable flammable) -> {
                        ((FireBlock) Blocks.FIRE).setFlammable(holder.value(),
                                flammable.encouragement(),
                                flammable.flammability());
                    });
                });
            });
        }

        this.flammables.put(flammableBlock, new Flammable(encouragement, flammability));
    }

    @Override
    public void registerStrippable(Holder<Block> unstrippedBlock, Holder<Block> strippedBlock) {
        Objects.requireNonNull(unstrippedBlock, "unstripped block is null");
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        this.strippables.register(unstrippedBlock, strippedBlock);
    }

    @Override
    public void registerFlattenable(Holder<Block> unflattenedBlock, Holder<Block> flattenedBlock) {
        Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
        Objects.requireNonNull(flattenedBlock, "flattened block is null");
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerTillable(Holder<Block> untilledBlock, Holder<Block> tilledBlock) {
        Objects.requireNonNull(untilledBlock, "untilled block is null");
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerOxidizable(Holder<Block> unoxidizedBlock, Holder<Block> oxidizedBlock) {
        Objects.requireNonNull(unoxidizedBlock, "unoxidized block is null");
        Objects.requireNonNull(oxidizedBlock, "oxidized block is null");
        this.oxidizables.register(unoxidizedBlock, oxidizedBlock);
    }

    @Override
    public void registerWaxable(Holder<Block> unwaxedBlock, Holder<Block> waxedBlock) {
        Objects.requireNonNull(unwaxedBlock, "unwaxed block is null");
        Objects.requireNonNull(waxedBlock, "waxed block is null");
        this.waxables.register(unwaxedBlock, waxedBlock);
    }

    private static class DataMapBuilder<K, V> {
        private final Map<K, V> values = new LinkedHashMap<>();
        private final String modId;
        private final DataProviderContext.Factory factory;

        public <R, T> DataMapBuilder(String modId, DataMapType<R, T> dataMapType, Function<K, Holder<R>> keyConverter, Function<V, T> valueConverter) {
            this.modId = modId;
            this.factory = (DataProviderContext context) -> {
                return new DataMapProvider(context.getPackOutput(), context.getRegistries()) {
                    @Override
                    protected void gather(HolderLookup.Provider registries) {
                        Builder<T, R> builder = this.builder(dataMapType);
                        DataMapBuilder.this.values.forEach((K key, V value) -> {
                            builder.add(keyConverter.apply(key), valueConverter.apply(value), false);
                        });
                    }

                    @Override
                    public String getName() {
                        return super.getName() + " for " + ResourceKey.create(dataMapType.registryKey(),
                                dataMapType.id());
                    }
                };
            };
        }

        public void register(K key, V value) {
            if (this.values.isEmpty()) {
                DataProviderBuilder.of(this.modId, this.factory);
            }

            this.values.put(key, value);
        }
    }

    private record Flammable(int encouragement, int flammability) {

    }
}
