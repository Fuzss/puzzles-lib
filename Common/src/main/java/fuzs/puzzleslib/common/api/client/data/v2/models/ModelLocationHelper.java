package fuzs.puzzleslib.common.api.client.data.v2.models;

import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * A utility for managing model and texture locations for blocks and items.
 */
public final class ModelLocationHelper {

    private ModelLocationHelper() {
        // NO-OP
    }

    /**
     * @param block the block
     * @return the default block model location, prefixed with {@code block/}
     *
     * @see ModelLocationUtils#getModelLocation(Block)
     */
    public static Identifier getBlockModel(Block block) {
        return ModelLocationUtils.getModelLocation(block);
    }

    /**
     * @param block  the block
     * @param suffix the block model suffix
     * @return the block model location, prefixed with {@code block/}
     *
     * @see ModelLocationUtils#getModelLocation(Block, String)
     */
    public static Identifier getBlockModel(Block block, String suffix) {
        return ModelLocationUtils.getModelLocation(block, suffix);
    }

    /**
     * @param id the block id
     * @return the default block model location, prefixed with {@code block/}
     *
     * @see ModelLocationUtils#decorateBlockModelLocation(String)
     */
    public static Identifier getBlockModel(Identifier id) {
        return id.withPrefix("block/");
    }

    /**
     * @param id     the block id
     * @param suffix the block model suffix
     * @return the block model location, prefixed with {@code block/}
     *
     * @see ModelLocationUtils#decorateBlockModelLocation(String)
     */
    public static Identifier getBlockModel(Identifier id, String suffix) {
        return getBlockModel(id).withSuffix(suffix);
    }

    /**
     * @param block the block
     * @return the default block texture location, prefixed with {@code block/}
     *
     * @see TextureMapping#getBlockTexture(Block)
     */
    public static Material getBlockTexture(Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    /**
     * @param block  the block
     * @param suffix the block texture suffix
     * @return the block texture location, prefixed with {@code block/}
     *
     * @see TextureMapping#getBlockTexture(Block, String)
     */
    public static Material getBlockTexture(Block block, String suffix) {
        return TextureMapping.getBlockTexture(block, suffix);
    }

    /**
     * @param id the block id
     * @return the default block texture location, prefixed with {@code block/}
     *
     * @see TextureMapping#getBlockTexture(Identifier, String)
     */
    public static Material getBlockTexture(Identifier id) {
        return new Material(id.withPrefix("block/"));
    }

    /**
     * @param id     the block id
     * @param suffix the block texture suffix
     * @return the block texture location, prefixed with {@code block/}
     *
     * @see TextureMapping#getBlockTexture(Identifier, String)
     */
    public static Material getBlockTexture(Identifier id, String suffix) {
        return new Material(id.withPath((String path) -> "block/" + path + suffix));
    }

    /**
     * @param block the block
     * @return the block registry key
     */
    public static Identifier getBlockLocation(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    /**
     * @param block  the block
     * @param suffix the block key suffix
     * @return the block registry key
     */
    public static Identifier getBlockLocation(Block block, String suffix) {
        return getBlockLocation(block).withSuffix(suffix);
    }

    /**
     * @param block the block
     * @return the block registry key name
     */
    public static String getBlockName(Block block) {
        return getBlockLocation(block).getPath();
    }

    /**
     * @param item the item
     * @return the default item model location, prefixed with {@code item/}
     *
     * @see ModelLocationUtils#getModelLocation(Item)
     */
    public static Identifier getItemModel(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    /**
     * @param item   the item
     * @param suffix the item model suffix
     * @return the item model location, prefixed with {@code item/}
     *
     * @see ModelLocationUtils#getModelLocation(Item, String)
     */
    public static Identifier getItemModel(Item item, String suffix) {
        return ModelLocationUtils.getModelLocation(item, suffix);
    }

    /**
     * @param id the item id
     * @return the default item model location, prefixed with {@code item/}
     *
     * @see ModelLocationUtils#decorateItemModelLocation(String)
     */
    public static Identifier getItemModel(Identifier id) {
        return id.withPrefix("item/");
    }

    /**
     * @param id     the item id
     * @param suffix the item model suffix
     * @return the item model location, prefixed with {@code item/}
     *
     * @see ModelLocationUtils#decorateItemModelLocation(String)
     */
    public static Identifier getItemModel(Identifier id, String suffix) {
        return getItemModel(id).withSuffix(suffix);
    }

    /**
     * @param item the item
     * @return the default item texture location, prefixed with {@code item/}
     *
     * @see TextureMapping#getItemTexture(Item)
     */
    public static Material getItemTexture(Item item) {
        return TextureMapping.getItemTexture(item);
    }

    /**
     * @param item   the item
     * @param suffix the item texture suffix
     * @return the item texture location, prefixed with {@code item/}
     *
     * @see TextureMapping#getItemTexture(Item, String)
     */
    public static Material getItemTexture(Item item, String suffix) {
        return TextureMapping.getItemTexture(item, suffix);
    }

    /**
     * @param id the item id
     * @return the default item texture location, prefixed with {@code item/}
     *
     * @see TextureMapping#getItemTexture(Item)
     */
    public static Material getItemTexture(Identifier id) {
        return new Material(id.withPrefix("item/"));
    }

    /**
     * @param id     the item id
     * @param suffix the item texture suffix
     * @return the item texture location, prefixed with {@code item/}
     *
     * @see TextureMapping#getItemTexture(Item, String)
     */
    public static Material getItemTexture(Identifier id, String suffix) {
        return new Material(id.withPath((String path) -> "item/" + path + suffix));
    }

    /**
     * @param item the item
     * @return the item registry key
     */
    public static Identifier getItemLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    /**
     * @param item   the item
     * @param suffix the item key suffix
     * @return the item registry key
     */
    public static Identifier getItemLocation(Item item, String suffix) {
        return getItemLocation(item).withSuffix(suffix);
    }

    /**
     * @param item the item
     * @return the item registry key name
     */
    public static String getItemName(Item item) {
        return getItemLocation(item).getPath();
    }
}
