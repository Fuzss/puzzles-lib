package fuzs.puzzleslib.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventImplHelper;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ResourceManagerRegistryLoadTask.class)
abstract class ResourceManagerRegistryLoadTaskNeoForgeMixin {
    @Unique
    private HolderGetter.Provider puzzleslib$lookupProvider;

    @Inject(method = "load", at = @At("HEAD"))
    public void load(RegistryOps.RegistryInfoLookup context, Executor executor, CallbackInfoReturnable<CompletableFuture<?>> callback) {
        this.puzzleslib$lookupProvider = context::lookup;
    }

    @ModifyReturnValue(method = "lambda$load$2", at = @At("RETURN"))
    private <T> RegistryLoadTask.PendingRegistration<T> load(RegistryLoadTask.PendingRegistration<T> pendingRegistration) {
        return NeoForgeEventImplHelper.onModifyEnchantments(this.puzzleslib$lookupProvider, pendingRegistration);
    }
}
