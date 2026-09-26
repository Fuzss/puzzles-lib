package fuzs.puzzleslib.common.api.event.v1;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemClickBehaviorCallback {
    EventInvoker<ItemClickBehaviorCallback> EVENT = EventInvoker.lookup(ItemClickBehaviorCallback.class);

    /**
     * Handles menu interactions when clicking items on top of each other in a container menu. Allows for overriding
     * item behavior otherwise implemented via {@link ItemStack#overrideStackedOnOther(Slot, ClickAction, Player)} and
     * {@link ItemStack#overrideOtherStackedOnMe(ItemStack, Slot, ClickAction, Player, SlotAccess)} on a per-item
     * basis.
     * <p>
     * The event runs whenever a slot in an {@link net.minecraft.world.inventory.AbstractContainerMenu} is clicked and
     * provides the item in the slot and the item currently carried by the cursor.
     *
     * @param hoveredItem      the item in the slot hovered by the mouse cursor
     * @param hoveredSlot      the slot hovered by the mouse cursor
     * @param itemHeldByCursor the item carried by the cursor
     * @param slotHeldByCursor the slot abstraction for the cursor
     * @param clickAction      the mouse button that was used in the click
     * @param player           the player
     * @return <ul>
     *         <li>{@link EventResult#ALLOW ALLOW} to allow normal container menu click behavior to run</li>
     *         <li>{@link EventResult#DENY DENY} to prevent normal click behavior, which allows for implementing a custom
     *         interaction as vanilla does for bundles</li>
     *         <li>{@link EventResult#PASS PASS} to fall back to other callbacks and eventually resolve the otherwise overridden vanilla methods</li>
     *         </ul>
     */
    EventResult onItemClickBehavior(ItemStack hoveredItem, Slot hoveredSlot, ItemStack itemHeldByCursor, SlotAccess slotHeldByCursor, ClickAction clickAction, Player player);
}
