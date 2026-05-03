package fuzs.puzzleslib.common.api.core.v1.context;

import com.google.common.base.Predicates;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Predicate;

/**
 * Register patches for item data components.
 */
public interface ItemComponentsContext {

    /**
     * @param item        the item
     * @param initializer apply changes to the data component map builder before it is finalized
     */
    @Deprecated(forRemoval = true)
    default void registerItemComponentsPatch(Item item, Initializer<Item> initializer) {
        this.registerItemComponentsPatch(item,
                (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider context, Item itemInstance) -> {
                    initializer.run(components, builder, context, itemInstance.builtInRegistryHolder().key());
                });
    }

    /**
     * @param itemPredicate the item filter
     * @param initializer   apply changes to the data component map builder before it is finalized
     */
    @Deprecated(forRemoval = true)
    default void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer<Item> initializer) {
        this.registerItemComponentsPatch(itemPredicate,
                (DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider context, Item itemInstance) -> {
                    initializer.run(components, builder, context, itemInstance.builtInRegistryHolder().key());
                });
    }

    /**
     * @param item        the item
     * @param initializer apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Item item, InitializerV2 initializer);

    /**
     * @param itemPredicate the item filter
     * @param initializer   apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Predicate<Item> itemPredicate, InitializerV2 initializer);

    /**
     * @param initializer apply changes to the data component map builder before it is finalized
     */
    default void registerItemComponentsPatch(InitializerV2 initializer) {
        this.registerItemComponentsPatch(Predicates.alwaysTrue(), initializer);
    }

    /**
     * @param <T> the value type
     * @see net.minecraft.core.component.DataComponentInitializers.Initializer
     */
    @Deprecated(forRemoval = true)
    @FunctionalInterface
    interface Initializer<T> {
        /**
         * @param components the access for getting existing values
         * @param builder    the builder for setting new values
         * @param registries the holder lookup
         * @param key        the resource key
         */
        void run(DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, ResourceKey<T> key);
    }

    /**
     * @see net.minecraft.core.component.DataComponentInitializers.Initializer
     */
    @FunctionalInterface
    interface InitializerV2 {
        /**
         * @param components the access for getting existing values
         * @param builder    the builder for setting new values
         * @param registries the holder lookup
         * @param item       the item
         */
        void run(DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item);
    }
}
