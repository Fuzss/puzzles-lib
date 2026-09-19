package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import fuzs.puzzleslib.fabric.impl.client.core.context.EntitySpectatorShadersContextFabricImpl;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
abstract class GameRendererFabricMixin {

    @WrapWithCondition(method = "checkEntityPostEffect",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/client/renderer/GameRenderer;clearSpectatedEntityPostEffect()V"))
    public boolean checkEntityPostEffect(GameRenderer gameRenderer, @Nullable Entity cameraEntity) {
        // Vanilla has set no effect, so we look for one. This mirrors the implementation on NeoForge.
        if (cameraEntity != null) {
            Identifier location = EntitySpectatorShadersContextFabricImpl.getEntityPostEffect(cameraEntity);
            if (location != null) {
                this.setSpectatedEntityPostEffect(location);
                return false;
            }
        }

        return true;
    }

    @Shadow
    private void setSpectatedEntityPostEffect(Identifier id) {
        throw new RuntimeException();
    }
}
