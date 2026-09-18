package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
abstract class FirstPersonHandsAndItemsRendererFabricMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void submitArmWithItem(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo callback) {
        EventResult result = FabricRendererEvents.SUBMIT_ARM_WITH_ITEM.invoker()
                .onSubmitArmWithItem(FirstPersonHandsAndItemsRenderer.class.cast(this),
                        playerState,
                        state,
                        partialTicks,
                        xRot,
                        hand,
                        attack,
                        itemStack,
                        inverseArmHeight,
                        poseStack,
                        submitNodeCollector,
                        lightCoords);
        if (result.isInterrupt()) {
            callback.cancel();
        }
    }
}
