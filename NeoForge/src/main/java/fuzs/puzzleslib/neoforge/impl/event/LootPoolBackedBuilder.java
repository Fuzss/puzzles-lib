package fuzs.puzzleslib.neoforge.impl.event;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

import java.util.ArrayList;
import java.util.List;

public final class LootPoolBackedBuilder extends LootPool.Builder {
    private final LootPool pool;

    public LootPoolBackedBuilder(LootPool pool) {
        this.pool = pool;
    }

    @Override
    public LootPool.Builder setRolls(Holder<ContextIntProvider> rolls) {
        this.pool.rolls = rolls;
        return this;
    }

    @Override
    public LootPool.Builder setBonusRolls(Holder<ContextFloatProvider> bonusRolls) {
        this.pool.bonusRolls = bonusRolls;
        return this;
    }

    @Override
    public LootPool.Builder add(LootPoolEntryContainer.Builder<?> entriesBuilder) {
        if (!(this.pool.entries instanceof ArrayList<LootPoolEntryContainer>)) {
            this.pool.entries = new ArrayList<>(this.pool.entries);
        }

        this.pool.entries.add(entriesBuilder.build());
        return this;
    }

    @Override
    public LootPool.Builder when(Holder<LootItemCondition> condition) {
        List<Holder<LootItemCondition>> conditions = this.pool.condition.map((Holder<LootItemCondition> holder) -> {
            return new ArrayList<>(List.of(holder));
        }).orElseGet(ArrayList::new);
        conditions.add(condition);
        this.pool.condition = ConditionUserBuilder.buildCondition(conditions);
        return this;
    }

    @Override
    public LootPool.Builder apply(Holder<LootItemFunction> function) {
        List<Holder<LootItemFunction>> functions = this.pool.modifier.map((Holder<LootItemFunction> holder) -> {
            return new ArrayList<>(List.of(holder));
        }).orElseGet(ArrayList::new);
        functions.add(function);
        this.pool.modifier = FunctionUserBuilder.buildFunction(functions);
        return this;
    }

    @Override
    public LootPool build() {
        return this.pool;
    }
}
