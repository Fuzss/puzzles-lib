package fuzs.puzzleslib.common.api.data.v3.loot;

import net.minecraft.data.loot.LootTableSubProvider;

public abstract class AbstractLootSubProvider implements LootTableSubProvider {
    protected final LootTableSubProvider.Context output;

    public AbstractLootSubProvider(LootTableSubProvider.Context output) {
        this.output = output;
    }

    @Override
    public final void run() {
        this.generate();
    }

    public abstract void generate();
}
