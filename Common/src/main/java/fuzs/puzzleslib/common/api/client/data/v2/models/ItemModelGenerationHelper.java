package fuzs.puzzleslib.common.api.client.data.v2.models;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility for generating item models, offering additional overloads and convenience methods on top of
 * {@link ItemModelGenerators}.
 */
public final class ItemModelGenerationHelper {
    /**
     * The {@link ModelTemplate} for goat horn item models.
     */
    public static final ModelTemplate HORN = ModelTemplateHelper.createItemModelTemplate(Identifier.withDefaultNamespace(
            "goat_horn"), TextureSlot.LAYER0);
    /**
     * The {@link ModelTemplate} for tooting goat horn item models.
     */
    public static final ModelTemplate TOOTING_HORN = ModelTemplateHelper.createItemModelTemplate(Identifier.withDefaultNamespace(
            "tooting_goat_horn"), TextureSlot.LAYER0);
    /**
     * The {@link ModelTemplate} for shield item models.
     */
    public static final ModelTemplate SHIELD_MODEL_TEMPLATE = ModelTemplateHelper.createItemModelTemplate(Identifier.withDefaultNamespace(
            "shield"), TextureSlot.PARTICLE);
    /**
     * The {@link ModelTemplate} for blocking shield item models.
     */
    public static final ModelTemplate SHIELD_BLOCKING_MODEL_TEMPLATE = ModelTemplateHelper.createItemModelTemplate(
            Identifier.withDefaultNamespace("shield_blocking"),
            TextureSlot.PARTICLE);

    private ItemModelGenerationHelper() {
        // NO-OP
    }

    /**
     * Generates a flat item model for the given item and registers it with the item model generators.
     *
     * @param item                the item
     * @param modelTemplate       the model template
     * @param itemModelGenerators the item model generators
     * @see ItemModelGenerators#generateFlatItem(Item, ModelTemplate)
     */
    public static void generateFlatItem(Item item, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item, modelTemplate, itemModelGenerators.modelOutput)));
    }

    /**
     * Creates a flat item model for the given item, using the item's own texture as the base layer.
     *
     * @param item          the item
     * @param modelTemplate the model template
     * @param modelOutput   the model output
     * @return the model id
     *
     * @see ItemModelGenerators#createFlatItemModel(Item, ModelTemplate)
     */
    public static Identifier createFlatItemModel(Item item, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(item, item, modelTemplate, modelOutput);
    }

    /**
     * Generates a flat item model for the given item and registers it with the item model generators, using another
     * item's texture as the base layer.
     *
     * @param item                the item
     * @param layerItem           the item the base layer texture is taken from
     * @param modelTemplate       the model template
     * @param itemModelGenerators the item model generators
     * @see ItemModelGenerators#generateFlatItem(Item, Item, ModelTemplate)
     */
    public static void generateFlatItem(Item item, Item layerItem, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item,
                        layerItem,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    /**
     * Creates a flat item model for the given item, using another item's texture as the base layer.
     *
     * @param item          the item
     * @param layerItem     the item the base layer texture is taken from
     * @param modelTemplate the model template
     * @param modelOutput   the model output
     * @return the model id
     *
     * @see ItemModelGenerators#createFlatItemModel(Item, Item, ModelTemplate)
     */
    public static Identifier createFlatItemModel(Item item, Item layerItem, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(item, ModelLocationHelper.getItemTexture(layerItem), modelTemplate, modelOutput);
    }

    /**
     * Generates a flat item model for the given item and registers it with the item model generators, using the given
     * texture as the base layer.
     *
     * @param item                the item
     * @param layer0Location      the base layer texture
     * @param modelTemplate       the model template
     * @param itemModelGenerators the item model generators
     * @see ItemModelGenerators#generateFlatItem(Item, Identifier, ModelTemplate)
     */
    public static void generateFlatItem(Item item, Material layer0Location, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createFlatItemModel(item,
                        layer0Location,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    /**
     * Creates a flat item model for the given item, using the given texture as the base layer.
     *
     * @param item           the item
     * @param layer0Location the base layer texture
     * @param modelTemplate  the model template
     * @param modelOutput    the model output
     * @return the model id
     *
     * @see #createFlatItemModel(Identifier, Material, ModelTemplate, BiConsumer)
     */
    public static Identifier createFlatItemModel(Item item, Material layer0Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(ModelLocationHelper.getItemModel(item), layer0Location, modelTemplate, modelOutput);
    }

    /**
     * Creates a flat item model for the item with the given id, using that id as the base layer texture.
     *
     * @param identifier    the item id
     * @param modelTemplate the model template
     * @param modelOutput   the model output
     * @return the model id
     *
     * @see #createFlatItemModel(Identifier, Material, ModelTemplate, BiConsumer)
     */
    public static Identifier createFlatItemModel(Identifier identifier, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createFlatItemModel(identifier, new Material(identifier), modelTemplate, modelOutput);
    }

    /**
     * Creates a flat item model for the item with the given id, using the given texture as the base layer.
     *
     * @param identifier     the item id
     * @param layer0Location the base layer texture
     * @param modelTemplate  the model template
     * @param modelOutput    the model output
     * @return the model id
     *
     * @see ModelTemplate#create(Identifier, TextureMapping, BiConsumer)
     */
    public static Identifier createFlatItemModel(Identifier identifier, Material layer0Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return modelTemplate.create(identifier, TextureMapping.layer0(layer0Location), modelOutput);
    }

    /**
     * Generates a layered item model for the given item and registers it with the item model generators.
     *
     * @param item                the item
     * @param layer0Location      the base layer texture
     * @param layer1Location      the overlay layer texture
     * @param modelTemplate       the model template
     * @param itemModelGenerators the item model generators
     * @see #createLayeredItemModel(Item, Material, Material, ModelTemplate, BiConsumer)
     */
    public static void generateLayeredItem(Item item, Material layer0Location, Material layer1Location, ModelTemplate modelTemplate, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.plainModel(createLayeredItemModel(item,
                        layer0Location,
                        layer1Location,
                        modelTemplate,
                        itemModelGenerators.modelOutput)));
    }

    /**
     * Creates a layered item model for the given item.
     *
     * @param item           the item
     * @param layer0Location the base layer texture
     * @param layer1Location the overlay layer texture
     * @param modelTemplate  the model template
     * @param modelOutput    the model output
     * @return the model id
     *
     * @see ItemModelGenerators#generateLayeredItem(Item, Material, Material)
     */
    public static Identifier createLayeredItemModel(Item item, Material layer0Location, Material layer1Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return createLayeredItemModel(ModelLocationHelper.getItemModel(item),
                layer0Location,
                layer1Location,
                modelTemplate,
                modelOutput);
    }

    /**
     * Creates a layered item model for the item with the given id.
     *
     * @param identifier     the item id
     * @param layer0Location the base layer texture
     * @param layer1Location the overlay layer texture
     * @param modelTemplate  the model template
     * @param modelOutput    the model output
     * @return the model id
     *
     * @see ItemModelGenerators#generateLayeredItem(Identifier, Material, Material)
     */
    public static Identifier createLayeredItemModel(Identifier identifier, Material layer0Location, Material layer1Location, ModelTemplate modelTemplate, BiConsumer<Identifier, ModelInstance> modelOutput) {
        return modelTemplate.create(identifier, TextureMapping.layered(layer0Location, layer1Location), modelOutput);
    }

    /**
     * Generates bow item models, including the pulling animation variants, for the given item.
     *
     * @param item                the item
     * @param itemModelGenerators the item model generators
     * @see ItemModelGenerators#generateBow(Item)
     */
    public static void generateBow(Item item, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.createFlatItemModel(item, ModelTemplates.BOW);
        itemModelGenerators.generateBow(item);
    }

    /**
     * Generates goat horn item models, including the tooting variant, for the given item.
     *
     * @param item                the item
     * @param itemModelGenerators the item model generators
     * @see ItemModelGenerators#generateGoatHorn(Item)
     */
    public static void generateHorn(Item item, ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked normal = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, HORN));
        ItemModel.Unbaked tooting = ItemModelUtils.plainModel(createFlatItemModel(ModelLocationHelper.getItemModel(item,
                "_tooting"), ModelLocationHelper.getItemTexture(item), TOOTING_HORN, itemModelGenerators.modelOutput));
        itemModelGenerators.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), tooting, normal);
    }

    /**
     * Generates block state and item model definitions for the given head block and its wall variant.
     *
     * @param headBlock            the standing head block
     * @param wallHeadBlock        the wall head block
     * @param type                 the skull type
     * @param blockModelGenerators the block model generators
     * @see BlockModelGenerators#createHead(Block, Block, SkullBlock.Type, Identifier)
     */
    public static void generateHead(Block headBlock, Block wallHeadBlock, SkullBlock.Type type, BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createHead(headBlock,
                wallHeadBlock,
                type,
                ModelLocationUtils.decorateItemModelLocation("template_skull"));
    }

    /**
     * Generates shield item models, including the blocking variant, for the given item.
     *
     * @param item                 the item
     * @param particleBlock        the block the particle texture is taken from
     * @param specialModelSupplier the supplier of the shield special model
     * @param itemModelGenerators  the item model generators
     * @see ItemModelGenerators#generateShield(Item)
     */
    public static void generateShield(Item item, Block particleBlock, Supplier<SpecialModelRenderer.Unbaked<?>> specialModelSupplier, ItemModelGenerators itemModelGenerators) {
        Identifier normalModel = SHIELD_MODEL_TEMPLATE.create(ModelLocationHelper.getItemModel(item),
                TextureMapping.particle(particleBlock),
                itemModelGenerators.modelOutput);
        Identifier blockingModel = SHIELD_BLOCKING_MODEL_TEMPLATE.create(ModelLocationHelper.getItemModel(item,
                "_blocking"), TextureMapping.particle(particleBlock), itemModelGenerators.modelOutput);
        ItemModel.Unbaked normal = ItemModelUtils.specialModel(normalModel, specialModelSupplier.get());
        ItemModel.Unbaked blocking = ItemModelUtils.specialModel(blockingModel, specialModelSupplier.get());
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.conditional(ShieldSpecialRenderer.DEFAULT_TRANSFORMATION,
                        ItemModelUtils.isUsingItem(),
                        blocking,
                        normal));
    }

    /**
     * Generates block state and item model definitions for a chest block with a particle-only block model.
     *
     * @param chestBlock             the chest block
     * @param particleBlock          the block the particle texture is taken from
     * @param itemTexture            the item texture
     * @param useGiftTexture         whether to add a Christmas gift variant
     * @param unbakedRendererFactory the factory for the special model renderer
     * @param blockModelGenerators   the block model generators
     * @see BlockModelGenerators#createChest(Block, Block, Identifier, boolean)
     */
    public static void generateChest(Block chestBlock, Block particleBlock, Identifier itemTexture, boolean useGiftTexture, Function<Identifier, SpecialModelRenderer.Unbaked<?>> unbakedRendererFactory, BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(chestBlock, particleBlock);
        Item chestItem = chestBlock.asItem();
        Identifier itemModelBase = ModelTemplates.CHEST_INVENTORY.create(chestItem,
                TextureMapping.particle(particleBlock),
                blockModelGenerators.modelOutput);
        ItemModel.Unbaked baseModel = ItemModelUtils.specialModel(itemModelBase,
                unbakedRendererFactory.apply(itemTexture));
        if (useGiftTexture) {
            ItemModel.Unbaked giftModel = ItemModelUtils.specialModel(itemModelBase,
                    new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.CHRISTMAS.single()));
            blockModelGenerators.itemModelOutput.accept(chestItem, ItemModelUtils.isXmas(giftModel, baseModel));
        } else {
            blockModelGenerators.itemModelOutput.accept(chestItem, baseModel);
        }
    }
}
