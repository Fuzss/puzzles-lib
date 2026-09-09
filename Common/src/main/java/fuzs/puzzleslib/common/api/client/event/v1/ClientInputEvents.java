package fuzs.puzzleslib.common.api.client.event.v1;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.common.api.client.event.v1.gui.ScreenKeyboardEvents;
import fuzs.puzzleslib.common.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;

/**
 * Very similar to {@link ScreenMouseEvents} and {@link ScreenKeyboardEvents}, but fires when no screen is open to
 * handle input events in the {@link net.minecraft.client.gui.Hud}.
 */
public final class ClientInputEvents {
    public static final EventInvoker<MouseClick> MOUSE_CLICK = EventInvoker.lookup(MouseClick.class);
    public static final EventInvoker<MouseScroll> MOUSE_SCROLL = EventInvoker.lookup(MouseScroll.class);
    public static final EventInvoker<KeyPress> KEY_PRESS = EventInvoker.lookup(KeyPress.class);

    private ClientInputEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface MouseClick {

        /**
         * Called before a mouse button is clicked or released without a screen being open.
         *
         * @param mouseButtonInfo the mouse button info; for bundled values see
         *                        {@link com.mojang.blaze3d.platform.InputConstants}
         * @param action          the mouse button action; see {@link InputConstants#RELEASE},
         *                        {@link InputConstants#PRESS}, {@link InputConstants#REPEAT}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as already handled</li>
         *         <li>{@link EventResult#PASS PASS} to allow the event to be handled normally</li>
         *         </ul>
         */
        EventResult onMouseClick(MouseButtonInfo mouseButtonInfo, int action);
    }

    @FunctionalInterface
    public interface MouseScroll {

        /**
         * Called before a mouse has scrolled without a screen being open.
         *
         * @param mouseX             the x-position of the mouse cursor
         * @param mouseY             the y-position of the mouse cursor
         * @param scrollX            the horizontal scroll amount
         * @param scrollY            the vertical scroll amount
         * @param accumulatedScrollX the horizontal scroll amount from
         *                           {@link net.minecraft.client.ScrollWheelHandler#onMouseScroll(double, double)}
         * @param accumulatedScrollY the vertical scroll amount from
         *                           {@link net.minecraft.client.ScrollWheelHandler#onMouseScroll(double, double)}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as already handled</li>
         *         <li>{@link EventResult#PASS PASS} to allow the event to be handled normally</li>
         *         </ul>
         */
        EventResult onMouseScroll(double mouseX, double mouseY, double scrollX, double scrollY, double accumulatedScrollX, double accumulatedScrollY);
    }

    @FunctionalInterface
    public interface KeyPress {

        /**
         * Called before a key press, release or repeat action is handled.
         * <p>
         * Note that on NeoForge due to the native implementation of this event cancelling a key press is not
         * supported.
         *
         * @param keyEvent the key event; for bundled values see {@link com.mojang.blaze3d.platform.InputConstants}
         * @param action   the mouse button action; see {@link InputConstants#RELEASE}, {@link InputConstants#PRESS},
         *                 {@link InputConstants#REPEAT}
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} for marking the event as already handled</li>
         *         <li>{@link EventResult#PASS PASS} to allow the event to be handled normally</li>
         *         </ul>
         */
        EventResult onKeyPress(KeyEvent keyEvent, int action);
    }
}
