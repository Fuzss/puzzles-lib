package fuzs.puzzleslib.common.api.client.data.v2.models;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * A utility for creating {@link ModelTemplate} and {@link TextureMapping} instances for modded blocks and items.
 */
public final class ModelTemplateHelper {

    private ModelTemplateHelper() {
        // NO-OP
    }

    /**
     * Creates a block model template for the given id, without a model suffix.
     *
     * @param identifier    the block model id
     * @param requiredSlots the required texture slots
     * @return the block model template
     *
     * @see #createBlockModelTemplate(Identifier, String, TextureSlot...)
     */
    public static ModelTemplate createBlockModelTemplate(Identifier identifier, TextureSlot... requiredSlots) {
        return createBlockModelTemplate(identifier, "", requiredSlots);
    }

    /**
     * Creates a block model template for the given id and model suffix.
     *
     * @param identifier    the block model id
     * @param suffix        the model suffix
     * @param requiredSlots the required texture slots
     * @return the block model template
     *
     * @see net.minecraft.client.data.models.model.ModelTemplates#create(String, String, TextureSlot...)
     */
    public static ModelTemplate createBlockModelTemplate(Identifier identifier, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(ModelLocationHelper.getBlockModel(identifier)),
                Optional.of(suffix),
                requiredSlots);
    }

    /**
     * Creates an item model template for the given id, without a model suffix.
     *
     * @param identifier    the item model id
     * @param requiredSlots the required texture slots
     * @return the item model template
     *
     * @see #createItemModelTemplate(Identifier, String, TextureSlot...)
     */
    public static ModelTemplate createItemModelTemplate(Identifier identifier, TextureSlot... requiredSlots) {
        return createItemModelTemplate(identifier, "", requiredSlots);
    }

    /**
     * Creates an item model template for the given id and model suffix.
     *
     * @param identifier    the item model id
     * @param suffix        the model suffix
     * @param requiredSlots the required texture slots
     * @return the item model template
     *
     * @see net.minecraft.client.data.models.model.ModelTemplates#createItem(String, String, TextureSlot...)
     */
    public static ModelTemplate createItemModelTemplate(Identifier identifier, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(ModelLocationHelper.getItemModel(identifier)),
                Optional.of(suffix),
                requiredSlots);
    }

    /**
     * Creates a texture mapping using the given block's texture for both the base texture and the particle texture.
     *
     * @param block the block
     * @return the texture mapping
     *
     * @see #createParticleTextureMapping(Block, String)
     */
    public static TextureMapping createParticleTextureMapping(Block block) {
        return createParticleTextureMapping(block, "");
    }

    /**
     * Creates a texture mapping using the given block's texture for both the base texture and the particle texture,
     * with the given suffix appended to the texture path.
     *
     * @param block  the block
     * @param suffix the texture suffix
     * @return the texture mapping
     */
    public static TextureMapping createParticleTextureMapping(Block block, String suffix) {
        Material material = TextureMapping.getBlockTexture(block, suffix);
        return new TextureMapping().put(TextureSlot.TEXTURE, material).put(TextureSlot.PARTICLE, material);
    }

    /**
     * Creates a texture mapping with the given texture slot set to the given block's texture.
     *
     * @param textureSlot the texture slot
     * @param block       the block
     * @return the texture mapping
     *
     * @see TextureMapping#singleSlot(TextureSlot, Material)
     */
    public static TextureMapping createSingleSlotMapping(TextureSlot textureSlot, Block block) {
        return TextureMapping.singleSlot(textureSlot, TextureMapping.getBlockTexture(block));
    }
}
