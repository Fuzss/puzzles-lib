package fuzs.puzzleslib.common.api.data.v3.loot;

import net.minecraft.data.loot.LootTableSubProvider;

/**
 * A base implementation of {@link LootTableSubProvider} for loot tables that are not tied to blocks or entity types,
 * like chests, fishing, gifts, and archaeology.
 * <p>
 * Subclasses implement {@link #generate()} and register loot tables directly via {@link #output}, mirroring the vanilla
 * providers. The generated loot data is validated globally when the reloadable registry is built.
 */
public abstract class AbstractLootSubProvider implements LootTableSubProvider {
    /**
     * The context used for registering generated loot tables.
     */
    protected final LootTableSubProvider.Context output;

    /**
     * @param output the context used for registering generated loot tables
     */
    public AbstractLootSubProvider(LootTableSubProvider.Context output) {
        this.output = output;
    }

    @Override
    public final void run() {
        this.generate();
    }

    /**
     * Registers all loot tables of this provider via {@link #output}.
     */
    public abstract void generate();
}
