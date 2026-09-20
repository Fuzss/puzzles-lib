package fuzs.puzzleslib.common.impl.client.data;

import fuzs.puzzleslib.common.api.client.data.v3.models.CustomItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public final class ItemModelOutputImpl extends ModelProvider.ItemInfoCollector implements CustomItemModelOutput {
    private final Map<Identifier, ClientItem> additionalItemInfos = new HashMap<>();
    private final Predicate<Holder.Reference<Item>> itemFilter;

    public ItemModelOutputImpl(Predicate<Holder.Reference<Item>> itemFilter) {
        this.itemFilter = itemFilter;
    }

    @Override
    public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
        this.register(item, new ClientItem(model, properties));
    }

    @Override
    public void accept(Identifier modelId, ItemModel.Unbaked model, ClientItem.Properties properties) {
        ClientItem clientItem = this.additionalItemInfos.put(modelId, new ClientItem(model, properties));
        if (clientItem != null) {
            throw new IllegalStateException("Duplicate item model definition for " + modelId);
        }
    }

    @Override
    public void finalizeAndValidate() {
        // Apply a filter, so we only consider our own content.
        BuiltInRegistries.ITEM.listElements().filter(this.itemFilter).forEach((Holder.Reference<Item> item) -> {
            if (!this.copies.containsKey(item.value())) {
                if (item.value() instanceof BlockItem blockItem && !this.itemInfos.containsKey(blockItem)) {
                    Identifier identifier = ModelLocationUtils.getModelLocation(blockItem.getBlock());
                    this.accept(blockItem, ItemModelUtils.plainModel(identifier));
                }
            }
        });
        this.copies.forEach((Item item, Item other) -> {
            ClientItem clientItem = this.itemInfos.get(other);
            if (clientItem == null) {
                throw new IllegalStateException("Missing donor: " + other + " -> " + item);
            } else {
                this.register(item, clientItem);
            }
        });
        List<Identifier> list = BuiltInRegistries.ITEM.listElements()
                // Apply a filter, so we only consider our own content.
                .filter(this.itemFilter)
                .filter((Holder.Reference<Item> item) -> !this.itemInfos.containsKey(item.value()))
                .map((Holder.Reference<Item> item) -> item.key().identifier())
                .toList();
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing item model definitions for: " + list);
        }
    }

    @Override
    public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
        return CompletableFuture.allOf(super.save(output, pathProvider),
                DataProvider.saveAll(output, ClientItem.CODEC, pathProvider::json, this.additionalItemInfos));
    }
}
