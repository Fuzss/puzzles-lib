package fuzs.puzzleslib.neoforge.impl.event;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LootTableBackedBuilder extends LootTable.Builder {
    private final LootTable table;

    public LootTableBackedBuilder(LootTable table) {
        this.table = table;
    }

    @Override
    public LootTable.Builder withPool(LootPool.Builder pool) {
        if (!(this.table.pools instanceof ArrayList<LootPool>)) {
            this.table.pools = new ArrayList<>(this.table.pools);
        }

        this.table.pools.add(pool.build());
        return this;
    }

    @Override
    public LootTable.Builder setParamSet(ContextKeySet paramSet) {
        this.table.paramSet = paramSet;
        return this;
    }

    @Override
    public LootTable.Builder setRandomSequence(Identifier key) {
        this.table.randomSequence = Optional.of(key);
        return this;
    }

    @Override
    public LootTable.Builder apply(Holder<LootItemFunction> function) {
        List<Holder<LootItemFunction>> functions = this.table.modifier.map((Holder<LootItemFunction> holder) -> {
            return new ArrayList<>(List.of(holder));
        }).orElseGet(ArrayList::new);
        functions.add(function);
        this.table.modifier = FunctionUserBuilder.buildFunction(functions);
        return this;
    }

    @Override
    public LootTable build() {
        return this.table;
    }
}
