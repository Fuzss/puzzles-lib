package fuzs.puzzleslib.common.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface SubmitArmWithItemCallback {
    EventInvoker<SubmitArmWithItemCallback> EVENT = EventInvoker.lookup(SubmitArmWithItemCallback.class);

    /**
     * Called before the player's arm holding an item is rendered in first-person mode.
     * <p>
     * This allows for completely taking over rendering as a whole.
     *
     * @param handsAndItemsRenderer the first-person hands and items renderer
     * @param playerState           the player render state used for first-person rendering
     * @param state                 the first-person hands and items render state
     * @param partialTicks          current partial tick time
     * @param xRot                  the pitch interpolated for current tick delta from {@link Player#getXRot()}
     * @param hand                  the player hand
     * @param attack                the forward swing state of the hand from attacking / mining, originally retrieved
     *                              from {@link Player#getSwingAnimation(float)}
     * @param itemStack             the {@link ItemStack} held in the hand
     * @param inverseArmHeight      the height the hand is rendered at, changes when switching between hotbar items and
     *                              after triggering the attack cool-down
     * @param poseStack             the current {@link PoseStack}
     * @param submitNodeCollector   the current {@link SubmitNodeCollector}
     * @param lightCoords           packed light the hand is rendered with
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the hand from rendering</li>
     *         <li>{@link EventResult#PASS PASS} to allow the hand to render normally</li>
     *         </ul>
     */
    EventResult onSubmitArmWithItem(FirstPersonHandsAndItemsRenderer handsAndItemsRenderer, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords);
}
