package fuzs.puzzleslib.common.api.container.v1;

import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.NonInteractiveResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Small helper class related to working with implementations of {@link AbstractContainerMenu}.
 */
public final class ContainerMenuHelper {

    private ContainerMenuHelper() {
        // NO-OP
    }

    /**
     * Opens a menu on both client and server while also providing additional data.
     *
     * @param player       the player opening the menu
     * @param menuProvider the menu factory
     * @param data         the additional data to be sent to the client
     */
    public static <T> void openMenu(Player player, MenuProvider menuProvider, T data) {
        Objects.requireNonNull(player, "player is null");
        Objects.requireNonNull(menuProvider, "menu provider is null");
        Objects.requireNonNull(data, "data is null");
        ProxyImpl.get().openMenu(player, menuProvider, data);
    }

    /**
     * Replace the container slot belonging to the provided equipment slot with a non-interactive variant.
     * <p>
     * Useful for handheld container items such as bags while their menu is open.
     *
     * @param containerMenu the container menu
     * @param slot          the equipment slot
     */
    public static void setSlotLocked(AbstractContainerMenu containerMenu, EquipmentSlot slot) {
        for (int slotNum = 0; slotNum < containerMenu.slots.size(); slotNum++) {
            Slot originalSlot = containerMenu.slots.get(slotNum);
            if (originalSlot.container instanceof Inventory inventory && originalSlot.getContainerSlot() == getSlotNum(
                    inventory,
                    slot)) {
                NonInteractiveResultSlot updatedSlot = new NonInteractiveResultSlot(originalSlot.container,
                        originalSlot.getContainerSlot(),
                        originalSlot.x,
                        originalSlot.y) {
                    @Override
                    public boolean isFake() {
                        return false;
                    }
                };
                updatedSlot.index = originalSlot.index;
                containerMenu.slots.set(slotNum, updatedSlot);
                break;
            }
        }
    }

    /**
     * Gets the {@link Slot#getContainerSlot()} for any {@link EquipmentSlot}.
     *
     * @param inventory the player inventory
     * @param slot      the equipment slot
     * @return the container slot
     */
    public static int getSlotNum(Inventory inventory, EquipmentSlot slot) {
        for (Int2ObjectMap.Entry<EquipmentSlot> entry : Inventory.EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet()) {
            if (entry.getValue() == slot) {
                return entry.getIntKey();
            }
        }

        return inventory.getSelectedSlot();
    }

    /**
     * Replace the container slot representing the currently selected hotbar slot with a non-interactive variant.
     * <p>
     * Useful for handheld container items such as bags while their menu is open.
     *
     * @param containerMenu the container menu
     */
    @Deprecated
    public static void setSelectedSlotLocked(AbstractContainerMenu containerMenu) {
        setSlotLocked(containerMenu, EquipmentSlot.MAINHAND);
    }

    /**
     * Allows for creating a container backed by a list of items.
     * <p>
     * Useful for items that are stored on e.g. a block entity, but not accessible via a container, but should be
     * accessible via a menu implementation.
     * <p>
     * An example would be a slot for setting a filter item that is kept separate from the main container inventory.
     *
     * @param items     list of items
     * @param container listener for changes to the item list
     * @return the created container
     */
    @Deprecated
    public static SimpleContainer createListBackedContainer(NonNullList<ItemStack> items, @Nullable Container container) {
        return createListBackedContainer(items, container != null ? (Container containerX) -> {
            container.setChanged();
        } : null);
    }

    /**
     * Allows for creating a container backed by a list of items.
     * <p>
     * Useful for items that are stored on e.g. a block entity, but not accessible via a container, but should be
     * accessible via a menu implementation.
     * <p>
     * An example would be a slot for setting a filter item that is kept separate from the main container inventory.
     *
     * @param items    list of items
     * @param listener listener for changes to the item list
     * @return the created container
     */
    @Deprecated
    public static SimpleContainer createListBackedContainer(NonNullList<ItemStack> items, @Nullable Consumer<Container> listener) {
        SimpleContainer simpleContainer = new SimpleContainer() {
            @Override
            public void setChanged() {
                if (listener != null) {
                    listener.accept(this);
                }
            }
        };
        simpleContainer.size = items.size();
        simpleContainer.items = items;
        return simpleContainer;
    }

    /**
     * Writes contents from a list of items to a container.
     * <p>
     * Intended to be used with
     * {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity#setItems(NonNullList)} to be able to keep
     * the internal items list final.
     *
     * @param from the source item list
     * @param to   the target container
     */
    public static void copyItemsIntoContainer(NonNullList<ItemStack> from, Container to) {
        for (int slotNum = 0; slotNum < from.size(); slotNum++) {
            if (slotNum < to.getContainerSize()) {
                to.setItem(slotNum, from.get(slotNum));
            }
        }
    }

    /**
     * Writes contents from a list of items to another list of items.
     * <p>
     * Intended to be used with
     * {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity#setItems(NonNullList)} to be able to keep
     * the internal items list final.
     *
     * @param from the source item list
     * @param to   the target item list
     */
    public static void copyItemsIntoList(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        for (int slotNum = 0; slotNum < from.size(); slotNum++) {
            if (slotNum < to.size()) {
                to.set(slotNum, from.get(slotNum));
            }
        }
    }
}
