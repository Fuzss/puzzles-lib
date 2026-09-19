package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.common.api.client.core.v1.context.ItemModelsContext;
import fuzs.puzzleslib.neoforge.impl.core.context.AbstractNeoForgeContext;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.*;

import java.util.Objects;

public final class ItemModelsContextNeoForgeImpl extends AbstractNeoForgeContext implements ItemModelsContext {

    @Override
    public void registerItemModel(Identifier modelId, MapCodec<? extends ItemModel.Unbaked> codec) {
        Objects.requireNonNull(modelId, "identifier is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterItemModelsEvent.class, (RegisterItemModelsEvent event) -> {
            event.register(modelId, codec);
        });
    }

    @Override
    public void registerSpecialModelRenderer(Identifier modelId, MapCodec<? extends SpecialModelRenderer.Unbaked<?>> codec) {
        Objects.requireNonNull(modelId, "identifier is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterSpecialModelRendererEvent.class, (RegisterSpecialModelRendererEvent event) -> {
            event.register(modelId, codec);
        });
    }

    @Override
    public void registerItemTintSource(Identifier tintSourceId, MapCodec<? extends ItemTintSource> codec) {
        Objects.requireNonNull(tintSourceId, "identifier is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterColorHandlersEvent.ItemTintSources.class,
                (RegisterColorHandlersEvent.ItemTintSources event) -> {
                    event.register(tintSourceId, codec);
                });
    }

    @Override
    public void registerSelectItemModelProperty(Identifier propertyId, SelectItemModelProperty.Type<?, ?> type) {
        Objects.requireNonNull(propertyId, "identifier is null");
        Objects.requireNonNull(type, "type is null");
        this.registerForEvent(RegisterSelectItemModelPropertyEvent.class,
                (RegisterSelectItemModelPropertyEvent event) -> {
                    event.register(propertyId, type);
                });
    }

    @Override
    public void registerConditionalItemModelProperty(Identifier propertyId, MapCodec<? extends ConditionalItemModelProperty> codec) {
        Objects.requireNonNull(propertyId, "identifier is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterConditionalItemModelPropertyEvent.class,
                (RegisterConditionalItemModelPropertyEvent event) -> {
                    event.register(propertyId, codec);
                });
    }

    @Override
    public void registerRangeSelectItemModelProperty(Identifier propertyId, MapCodec<? extends RangeSelectItemModelProperty> codec) {
        Objects.requireNonNull(propertyId, "identifier is null");
        Objects.requireNonNull(codec, "codec is null");
        this.registerForEvent(RegisterRangeSelectItemModelPropertyEvent.class,
                (RegisterRangeSelectItemModelPropertyEvent event) -> {
                    event.register(propertyId, codec);
                });
    }
}
