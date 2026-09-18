package fuzs.puzzleslib.common.api.data.v3.loot;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A base implementation of {@link BlockLootSubProvider} for generating block loot tables.
 * <p>
 * Unlike vanilla, only loot tables belonging to the generating mod are required to be generated, meaning loot table ids
 * whose namespace matches the mod id. Loot tables for blocks of vanilla or other mods can still be added and are
 * generated as well, all remaining blocks are skipped.
 */
public abstract class AbstractBlockLootSubProvider extends BlockLootSubProvider {
    /**
     * The default variant providers used by {@link #generateFor(BlockSetFamily)}.
     *
     * @see #generateFor(BlockSetFamily, Map)
     */
    public static final Map<BlockSetVariant, BiConsumer<AbstractBlockLootSubProvider, Block>> VARIANT_PROVIDERS = ImmutableMap.<BlockSetVariant, BiConsumer<AbstractBlockLootSubProvider, Block>>builder()
            .put(BlockSetVariant.CHISELED, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.CRACKED, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.CUT, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.MOSAIC, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.POLISHED, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.BRICKS, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.COBBLED, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.TILES, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.PILLAR, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.LOG, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.WOOD, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.STRIPPED_LOG, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.STRIPPED_WOOD, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.STAIRS, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.SLAB, (AbstractBlockLootSubProvider provider, Block block) -> {
                provider.add(block, provider::createSlabItemTable);
            })
            .put(BlockSetVariant.WALL, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.FENCE, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.FENCE_GATE, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.DOOR, (AbstractBlockLootSubProvider provider, Block block) -> {
                provider.add(block, provider::createDoorTable);
            })
            .put(BlockSetVariant.TRAPDOOR, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.BUTTON, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.PRESSURE_PLATE, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.SIGN, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.HANGING_SIGN, BlockLootSubProvider::dropSelf)
            .put(BlockSetVariant.SHELF, BlockLootSubProvider::dropSelf)
            .build();

    /**
     * @param output the context used for registering generated loot tables
     */
    public AbstractBlockLootSubProvider(LootTableSubProvider.Context output) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), output);
    }

    /**
     * Adds all loot tables of this provider via the various {@code add} methods inherited from
     * {@link BlockLootSubProvider}, which are then emitted to the reloadable registry by {@link #run()}.
     */
    @Override
    public abstract void generate();

    @Override
    public void run() {
        this.generate();
        Set<ResourceKey<LootTable>> seen = new HashSet<>();
        String modId = ((NamedLootContext) this.output).getModId();

        for (Block block : BuiltInRegistries.BLOCK) {
            block.getLootTable().ifPresent((ResourceKey<LootTable> lootTable) -> {
                if (seen.add(lootTable)) {
                    LootTable.Builder builder = this.map.remove(lootTable);
                    if (builder == null) {
                        // Only our own loot tables are required to be generated here.
                        // Everything else is provided by vanilla or other mods and is simply skipped.
                        if (lootTable.identifier().getNamespace().equals(modId)) {
                            throw new IllegalStateException(String.format(Locale.ROOT,
                                    "Missing loot table '%s' for '%s'",
                                    lootTable.identifier(),
                                    BuiltInRegistries.BLOCK.getKey(block)));
                        }
                    } else {
                        this.output.accept(lootTable, builder);
                    }
                }
            });
        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    /**
     * Adds a loot table for the given block that drops nothing.
     *
     * @param block the block to add a loot table for
     */
    public void dropNothing(Block block) {
        this.add(block, noDrop());
    }

    /**
     * Adds a loot table for the given block that drops itself while copying over the custom name of the block entity.
     *
     * @param block the block to add a loot table for
     */
    public void dropNameable(Block block) {
        this.add(block, this::createNameableBlockEntityTable);
    }

    /**
     * Creates a loot table for the given block that preserves the note block sound and custom name of the block entity,
     * similar to the drops of vanilla heads.
     *
     * @param block the block to create a loot table for
     * @return the created loot table builder
     */
    public LootTable.Builder createHeadDrop(Block block) {
        // The explosion condition is not applied on purpose; all vanilla heads are explosion-resistant.
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                        .include(DataComponents.NOTE_BLOCK_SOUND)
                                        .include(DataComponents.CUSTOM_NAME)))
                        .unwrap());
    }

    /**
     * Generates loot tables for all blocks of the given block set family using {@link #VARIANT_PROVIDERS}.
     *
     * @param blockSetFamily the block set family
     */
    public final void generateFor(BlockSetFamily blockSetFamily) {
        this.generateFor(blockSetFamily, VARIANT_PROVIDERS);
    }

    /**
     * Generates loot tables for all blocks of the given block set family using the given variant providers.
     *
     * @param blockSetFamily the block set family
     * @param variants       the variant providers to apply
     */
    public final void generateFor(BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<AbstractBlockLootSubProvider, Block>> variants) {
        blockSetFamily.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> block) -> {
            BiConsumer<AbstractBlockLootSubProvider, Block> provider = variants.get(variant);
            if (provider != null) {
                provider.accept(this, block.value());
            }
        });
    }
}
