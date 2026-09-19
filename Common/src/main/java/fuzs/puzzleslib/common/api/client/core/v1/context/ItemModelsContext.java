package fuzs.puzzleslib.common.api.client.core.v1.context;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

/**
 * Register codecs for handling custom item model types and properties.
 */
public interface ItemModelsContext {

    /**
     * Register a codec for a custom {@link ItemModel.Unbaked} type.
     *
     * @param modelId the identifier
     * @param codec   the corresponding codec for the type
     */
    void registerItemModel(Identifier modelId, MapCodec<? extends ItemModel.Unbaked> codec);

    /**
     * Register a codec for a custom {@link SpecialModelRenderer.Unbaked} type.
     *
     * @param modelId the identifier
     * @param codec   the corresponding codec for the type
     */
    void registerSpecialModelRenderer(Identifier modelId, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> codec);

    /**
     * Register a codec for a custom {@link ItemTintSource} type.
     *
     * @param tintSourceId the identifier
     * @param codec        the corresponding codec for the type
     */
    void registerItemTintSource(Identifier tintSourceId, MapCodec<? extends ItemTintSource> codec);

    /**
     * Register a type for a custom {@link SelectItemModelProperty} implementation.
     *
     * @param propertyId the identifier
     * @param type       the corresponding codec for the type
     */
    void registerSelectItemModelProperty(Identifier propertyId, SelectItemModelProperty.Type<?, ?> type);

    /**
     * Register a codec for a custom {@link ConditionalItemModelProperty} type.
     *
     * @param propertyId the identifier
     * @param codec      the corresponding codec for the type
     */
    void registerConditionalItemModelProperty(Identifier propertyId, MapCodec<? extends ConditionalItemModelProperty> codec);

    /**
     * Register a codec for a custom {@link RangeSelectItemModelProperty} type.
     *
     * @param propertyId the identifier
     * @param codec      the corresponding codec for the type
     */
    void registerRangeSelectItemModelProperty(Identifier propertyId, MapCodec<? extends RangeSelectItemModelProperty> codec);
}
