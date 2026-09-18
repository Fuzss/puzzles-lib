package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientLevelEvents;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientPlayerEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Minecraft.class)
abstract class MinecraftFabricMixin {
    @Shadow
    @Nullable
    public ClientLevel level;
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;
    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(method = "<init>",
            at = @At(value = "INVOKE",
                     target = "Lcom/mojang/blaze3d/systems/RenderSystem;getBackendDescription()Ljava/lang/String;",
                     shift = At.Shift.AFTER,
                     remap = false))
    public void init(CallbackInfo callback) {
        // run after Fabric Data Generation Api for same behavior as Forge where load complete does not run
        // during data generation (not that we use Fabric's data generation, but ¯\_(ツ)_/¯)
        FabricLifecycleEvents.LOAD_COMPLETE.invoker().onLoadComplete();
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    public void setLevel(ClientLevel level, CallbackInfo callback) {
        if (this.level != null) {
            FabricClientLevelEvents.UNLOAD_LEVEL.invoker().onLevelUnload(Minecraft.class.cast(this), this.level);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/GameRenderer;resetData()V",
                     shift = At.Shift.AFTER))
    public void disconnect(Screen screen, boolean keepResourcePacks, boolean stopSound, CallbackInfo callback) {
        if (this.player != null && this.gameMode != null) {
            Connection connection = this.player.connection.getConnection();
            Objects.requireNonNull(connection, "connection is null");
            FabricClientPlayerEvents.PLAYER_LEAVE.invoker().onPlayerLeave(this.player, this.gameMode, connection);
        }

        if (this.level != null) {
            FabricClientLevelEvents.UNLOAD_LEVEL.invoker().onLevelUnload(Minecraft.class.cast(this), this.level);
        }
    }

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void pickBlockOrEntity(CallbackInfo callback) {
        if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
            EventResult result = FabricClientPlayerEvents.PICK_INTERACTION_INPUT.invoker()
                    .onPickInteraction(Minecraft.class.cast(this), this.player, this.hitResult);
            if (result.isInterrupt()) {
                callback.cancel();
            }
        }
    }
}
