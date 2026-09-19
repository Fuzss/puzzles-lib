package fuzs.puzzleslib.common.api.client.data.v2.models;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.common.impl.client.data.BlockStateOutputImpl;
import fuzs.puzzleslib.common.impl.client.data.ItemModelOutputImpl;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.Holder;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * A base implementation of {@link DataProvider} for generating block state, block model, and item model definitions for
 * the mod.
 * <p>
 * Subclasses register their models by overriding {@link #addBlockModels(BlockModelGenerators)} and
 * {@link #addItemModels(ItemModelGenerators)}. Block set families can be handled automatically via
 * {@link #generateForBlocks(BlockModelGenerators, BlockSetFamily)} and
 * {@link #generateForItems(ItemModelGenerators, BlockSetFamily, Map)}.
 */
public abstract class AbstractModelProvider implements DataProvider {
    /**
     * The default item model providers for the common wooden {@link BlockSetVariant BlockSetVariants}, used by
     * {@link #generateForItems(ItemModelGenerators, BlockSetFamily, Map)}.
     *
     * @see #generateForItems(ItemModelGenerators, BlockSetFamily, Map)
     */
    public static final Map<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>> VARIANT_WOOD_ITEM_PROVIDERS = ImmutableMap.<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>>builder()
            .put(BlockSetVariant.BOAT, (ItemModelGenerators generators, Item item) -> {
                generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            })
            .put(BlockSetVariant.CHEST_BOAT, (ItemModelGenerators generators, Item item) -> {
                generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            })
            .build();

    private final String modId;
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private final Set<Object> skipValidation = new HashSet<>();

    /**
     * @param context the data provider context
     */
    public AbstractModelProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    /**
     * @param modId      the mod id used for determining which content must be generated
     * @param packOutput the pack output
     */
    public AbstractModelProvider(String modId, PackOutput packOutput) {
        this.modId = modId;
        this.blockStatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    /**
     * Creates the default variant model providers for wooden block set families, covering the variants that are not
     * handled by {@link BlockModelGenerators#family(Block)}.
     *
     * @param family the block set family the providers operate on
     * @return the variant providers mapped by block set variant
     *
     * @see #generateForBlocks(BlockModelGenerators, BlockSetFamily, Map)
     */
    public static Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> createVariantWoodBlockProviders(BlockSetFamily family) {
        return ImmutableMap.<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>>builder()
                .put(BlockSetVariant.LOG, (BlockModelGenerators generators, Block block) -> {
                    generators.woodProvider(block).logWithHorizontal(block);
                })
                .put(BlockSetVariant.WOOD, (BlockModelGenerators generators, Block block) -> {
                    generators.woodProvider(family.getBlockVariants().get(BlockSetVariant.LOG).value()).wood(block);
                })
                .put(BlockSetVariant.STRIPPED_LOG, (BlockModelGenerators generators, Block block) -> {
                    generators.woodProvider(block).logWithHorizontal(block);
                })
                .put(BlockSetVariant.STRIPPED_WOOD, (BlockModelGenerators generators, Block block) -> {
                    generators.woodProvider(family.getBlockVariants().get(BlockSetVariant.STRIPPED_LOG).value())
                            .wood(block);
                })
                .put(BlockSetVariant.SHELF, (BlockModelGenerators generators, Block block) -> {
                    generators.createShelf(block, family.getBlockVariants().get(BlockSetVariant.STRIPPED_LOG).value());
                })
                .build();
    }

    /**
     * Runs the provider, generating and saving all block state, block model, and item model definition files.
     *
     * @param output the cached output to write to
     * @return a future that completes once all files have been saved
     */
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        BlockStateOutputImpl blockStateOutput = new BlockStateOutputImpl(this::isValid);
        ItemModelOutputImpl itemModelOutput = new ItemModelOutputImpl(this::isValid);
        ModelProvider.SimpleModelCollector modelOutput = new ModelProvider.SimpleModelCollector();
        this.addBlockModels(this.setupBlockModelGenerators(new BlockModelGenerators(blockStateOutput,
                itemModelOutput,
                modelOutput)));
        this.addItemModels(new ItemModelGenerators(itemModelOutput, modelOutput));
        blockStateOutput.validate();
        itemModelOutput.finalizeAndValidate();
        return CompletableFuture.allOf(blockStateOutput.save(output, this.blockStatePathProvider),
                modelOutput.save(output, this.modelPathProvider),
                itemModelOutput.save(output, this.itemInfoPathProvider));
    }

    private BlockModelGenerators setupBlockModelGenerators(BlockModelGenerators generators) {
        // Make all these mutable, so it is possible to add our own entries.
        if (!(BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR instanceof ArrayList<?>)) {
            BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR = new ArrayList<>(BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR);
        }

        if (!(BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS instanceof HashMap<?, ?>)) {
            BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS = new HashMap<>(BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS);
        }

        if (!(BlockModelGenerators.TEXTURED_MODELS instanceof HashMap<?, ?>)) {
            BlockModelGenerators.TEXTURED_MODELS = new HashMap<>(BlockModelGenerators.TEXTURED_MODELS);
        }

        return generators;
    }

    private <T> boolean isValid(Holder.Reference<T> holder) {
        if (!this.skipAllValidation()) {
            if (holder.key().identifier().getNamespace().equals(this.modId)) {
                return !this.skipValidation.contains(holder.value());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Adds block state and block model definitions to the given generators.
     * <p>
     * Called by {@link #run(CachedOutput)} before {@link #addItemModels(ItemModelGenerators)}.
     *
     * @param generators the block model generators
     */
    public void addBlockModels(BlockModelGenerators generators) {
        // NO-OP
    }

    /**
     * Adds item model definitions to the given generators.
     * <p>
     * Called by {@link #run(CachedOutput)} after {@link #addBlockModels(BlockModelGenerators)}.
     *
     * @param generators the item model generators
     */
    public void addItemModels(ItemModelGenerators generators) {
        // NO-OP
    }

    /**
     * Generates block state and block model definitions for all blocks of the given block set family, without any
     * additional variant providers.
     *
     * @param generators the block model generators
     * @param family     the block set family
     * @see #generateForBlocks(BlockModelGenerators, BlockSetFamily, Map)
     * @see BlockModelGenerators#family(Block)
     */
    public void generateForBlocks(BlockModelGenerators generators, BlockSetFamily family) {
        this.generateForBlocks(generators, family, Collections.emptyMap());
    }

    /**
     * Generates block state and block model definitions for all blocks of the given block set family, using the given
     * variant providers for individual block variants.
     * <p>
     * Similar to a method patched in by NeoForge.
     *
     * @param generators the block model generators
     * @param family     the block set family
     * @param variants   the variant providers mapped by block set variant
     * @see BlockModelGenerators#family(Block)
     */
    public void generateForBlocks(BlockModelGenerators generators, BlockSetFamily family, Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> variants) {
        BlockFamily blockFamily = family.getBlockFamily();
        if (blockFamily.shouldGenerateModel()) {
            Block baseBlock = family.getBaseBlock().value();
            TexturedModel model = BlockModelGenerators.TEXTURED_MODELS.getOrDefault(baseBlock,
                    TexturedModel.CUBE.get(baseBlock));
            BlockModelGenerators.BlockFamilyProvider familyProvider = generators.new BlockFamilyProvider(model.getMapping());
            familyProvider.fullBlock = BlockModelGenerators.plainModel(model.getTemplate()
                    .getDefaultModelLocation(blockFamily.getBaseBlock()));
            familyProvider.generateFor(blockFamily);
            family.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
                BiConsumer<BlockModelGenerators, Block> modelProvider = variants.get(variant);
                if (modelProvider != null) {
                    modelProvider.accept(generators, holder.value());
                }
            });
        }
    }

    /**
     * Generates item model definitions for all items of the given block set family, using the given variant providers
     * for individual item variants.
     *
     * @param generators the item model generators
     * @param family     the block set family
     * @param variants   the variant providers mapped by block set variant
     * @see #VARIANT_WOOD_ITEM_PROVIDERS
     */
    public void generateForItems(ItemModelGenerators generators, BlockSetFamily family, Map<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>> variants) {
        BlockFamily blockFamily = family.getBlockFamily();
        if (blockFamily.shouldGenerateModel()) {
            family.getItemVariants().forEach((BlockSetVariant variant, Holder.Reference<Item> holder) -> {
                BiConsumer<ItemModelGenerators, Item> modelProvider = variants.get(variant);
                if (modelProvider != null) {
                    modelProvider.accept(generators, holder.value());
                }
            });
        }
    }

    /**
     * Whether to skip validating that all blocks and items of the mod have model definitions, which is useful when only
     * a subset of the mod's content is generated by this provider.
     *
     * @return whether all validation is skipped
     */
    protected boolean skipAllValidation() {
        return false;
    }

    /**
     * Excludes the given block from validation, so it may be registered without a generated model definition.
     *
     * @param block the block to skip
     */
    protected final void skipBlockValidation(Block block) {
        this.skipValidation.add(block);
    }

    /**
     * Excludes the given item from validation, so it may be registered without a generated model definition.
     *
     * @param item the item to skip
     */
    protected final void skipItemValidation(Item item) {
        this.skipValidation.add(item);
    }

    /**
     * @return the name of this provider
     */
    @Override
    public final String getName() {
        return "Model Definitions";
    }
}
