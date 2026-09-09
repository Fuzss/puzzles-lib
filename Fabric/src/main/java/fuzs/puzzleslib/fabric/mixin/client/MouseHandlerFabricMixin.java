package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
abstract class MouseHandlerFabricMixin {

    @Inject(method = "onButton",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/Gui;overlay()Lnet/minecraft/client/gui/screens/Overlay;",
                     ordinal = 0),
            cancellable = true)
    private void onButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo callback) {
        EventResult eventResult = FabricClientEvents.MOUSE_CLICK.invoker().onMouseClick(rawButtonInfo, action);
        if (eventResult.isInterrupt()) {
            callback.cancel();
        }
    }

    @Inject(method = "onScroll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            cancellable = true)
    private void onScroll(CallbackInfo callback, @Local(ordinal = 3) double scaledXOffset, @Local(ordinal = 4) double scaledYOffset, @Local Vector2i wheelXY) {
        EventResult eventResult = FabricClientEvents.MOUSE_SCROLL.invoker()
                .onMouseScroll(scaledXOffset, scaledYOffset, wheelXY.x(), wheelXY.y());
        if (eventResult.isInterrupt()) {
            callback.cancel();
        }
    }
}
