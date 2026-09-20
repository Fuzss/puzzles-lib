package fuzs.puzzleslib.common.api.client.data.v3.models;

import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * An extension of {@link ItemModelOutput} that allows registering item models by their {@link Identifier} in addition
 * to registering them via the {@link Item} they belong to.
 */
public interface CustomItemModelOutput extends ItemModelOutput {

    /**
     * @see ItemModelOutput#accept(Item, ItemModel.Unbaked, ClientItem.Properties)
     */
    @Override
    void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties);

    /**
     * Registers the given model for the item with the given id, using the default {@link ClientItem.Properties}.
     *
     * @param modelId the item id
     * @param model   the item model
     */
    default void accept(Identifier modelId, ItemModel.Unbaked model) {
        this.accept(modelId, model, ClientItem.Properties.DEFAULT);
    }

    /**
     * Registers the given model and properties for the item with the given id.
     *
     * @param modelId    the item id
     * @param model      the item model
     * @param properties the additional client item properties
     */
    void accept(Identifier modelId, ItemModel.Unbaked model, ClientItem.Properties properties);
}
