package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class MobFabricMixin extends LivingEntity {

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "setTarget",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/entity/Mob;asValidTarget(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/LivingEntity;"))
    public @Nullable LivingEntity setTarget(@Nullable LivingEntity target, @Cancellable CallbackInfo callback) {
        MutableValue<@Nullable LivingEntity> targetValue = MutableValue.fromValue(target);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, targetValue);
        if (result.isInterrupt()) {
            callback.cancel();
        }

        return targetValue.get();
    }

    @Inject(method = "checkDespawn",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"),
            cancellable = true)
    public void checkDespawn(CallbackInfo callback) {
        EventResult result = FabricLivingEvents.CHECK_MOB_DESPAWN.invoker()
                .onCheckMobDespawn((Mob) (Object) this, (ServerLevel) this.level());
        if (result.isInterrupt()) {
            if (result.getAsBoolean()) {
                this.discard();
            } else {
                this.noActionTime = 0;
            }

            callback.cancel();
        }
    }
}
