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
     */
    public static Identifier getBlockModel(Block block) {
        return ModelLocationUtils.getModelLocation(block);
    }

    /**
     * @param block  the block
     * @param suffix the block model suffix
     * @return the block model location, prefixed with {@code block/}
     */
    public static Identifier getBlockModel(Block block, String suffix) {
        return ModelLocationUtils.getModelLocation(block, suffix);
    }

    /**
     * @param identifier the block location
     * @return the default block model location, prefixed with {@code block/}
     */
    public static Identifier getBlockModel(Identifier identifier) {
        return identifier.withPrefix("block/");
    }

    /**
     * @param identifier the block location
     * @param suffix     the block model suffix
     * @return the block model location, prefixed with {@code block/}
     */
    public static Identifier getBlockModel(Identifier identifier, String suffix) {
        return getBlockModel(identifier).withSuffix(suffix);
    }

    /**
     * @param block the block
     * @return the default block texture location, prefixed with {@code block/}
     */
    public static Material getBlockTexture(Block block) {
        return TextureMapping.getBlockTexture(block);
    }

    /**
     * @param block  the block
     * @param suffix the block texture suffix
     * @return the block texture location, prefixed with {@code block/}
     */
    public static Material getBlockTexture(Block block, String suffix) {
        return TextureMapping.getBlockTexture(block, suffix);
    }

    /**
     * @param identifier the block location
     * @return the default block texture location, prefixed with {@code block/}
     */
    public static Material getBlockTexture(Identifier identifier) {
        return new Material(identifier.withPrefix("block/"));
    }

    /**
     * @param identifier the block location
     * @param suffix     the block texture suffix
     * @return the block texture location, prefixed with {@code block/}
     */
    public static Material getBlockTexture(Identifier identifier, String suffix) {
        return new Material(identifier.withPath((String path) -> "block/" + path + suffix));
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
     */
    public static Identifier getItemModel(Item item) {
        return ModelLocationUtils.getModelLocation(item);
    }

    /**
     * @param item   the item
     * @param suffix the item model suffix
     * @return the item model location, prefixed with {@code item/}
     */
    public static Identifier getItemModel(Item item, String suffix) {
        return ModelLocationUtils.getModelLocation(item, suffix);
    }

    /**
     * @param identifier the item location
     * @return the default item model location, prefixed with {@code item/}
     */
    public static Identifier getItemModel(Identifier identifier) {
        return identifier.withPrefix("item/");
    }

    /**
     * @param identifier the item location
     * @param suffix     the item model suffix
     * @return the item model location, prefixed with {@code item/}
     */
    public static Identifier getItemModel(Identifier identifier, String suffix) {
        return getItemModel(identifier).withSuffix(suffix);
    }

    /**
     * @param item the item
     * @return the default item texture location, prefixed with {@code item/}
     */
    public static Material getItemTexture(Item item) {
        return TextureMapping.getItemTexture(item);
    }

    /**
     * @param item   the item
     * @param suffix the item texture suffix
     * @return the item texture location, prefixed with {@code item/}
     */
    public static Material getItemTexture(Item item, String suffix) {
        return TextureMapping.getItemTexture(item, suffix);
    }

    /**
     * @param identifier the item location
     * @return the default item texture location, prefixed with {@code item/}
     */
    public static Material getItemTexture(Identifier identifier) {
        return new Material(identifier.withPrefix("item/"));
    }

    /**
     * @param identifier the item location
     * @param suffix     the item texture suffix
     * @return the item texture location, prefixed with {@code item/}
     */
    public static Material getItemTexture(Identifier identifier, String suffix) {
        return new Material(identifier.withPath((String path) -> "item/" + path + suffix));
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
