package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.impl.PuzzlesLib;
import fuzs.puzzleslib.common.impl.PuzzlesLibMod;
import fuzs.puzzleslib.common.impl.event.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.impl.event.SpawnReasonMob;
import net.fabricmc.fabric.api.event.lifecycle.v1.EntityLoadData;
import net.fabricmc.fabric.impl.event.lifecycle.EntityLoadDataSetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Run before Fabric Api to give that a chance to override the serialized spawn reason after us.
 */
@Mixin(value = Mob.class, priority = 1500)
abstract class MobFabricMixin extends LivingEntity implements SpawnReasonMob, EntityLoadData, EntityLoadDataSetter {
    @Shadow
    @Nullable
    private LivingEntity target;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    @Nullable
    public EntitySpawnReason puzzleslib$getSpawnReason() {
        return this.spawnReason();
    }

    @Override
    public void puzzleslib$setSpawnReason(@Nullable EntitySpawnReason spawnReason) {
        if (this.puzzleslib$getSpawnReason() == spawnReason) {
            return;
        }

        try {
            Class<?> clazz = Class.forName("net.fabricmc.fabric.impl.event.lifecycle.EntityLoadDataSetter");
            MethodType methodType = MethodType.methodType(void.class, EntitySpawnReason.class);
            MethodHandle handle = MethodHandles.publicLookup().findVirtual(clazz, "fabric_setSpawnReason", methodType);
            handle.invoke(this, spawnReason);
        } catch (Throwable throwable) {
            PuzzlesLib.LOGGER.warn("Unable to set spawn reason {} for {}: {}",
                    spawnReason,
                    this,
                    throwable.getMessage());
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    public void setTarget(@Nullable LivingEntity livingEntity, CallbackInfo callback) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(livingEntity);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, target);
        if (result.isInterrupt()) {
            callback.cancel();
        } else if (target.getAsOptional().isPresent()) {
            this.target = target.get();
            callback.cancel();
        }
    }

    @Inject(method = "checkDespawn",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"),
            cancellable = true)
    public void checkDespawn(CallbackInfo callback) {
        EventResult result = FabricLivingEvents.CHECK_MOB_DESPAWN.invoker()
                .onCheckMobDespawn(Mob.class.cast(this), (ServerLevel) this.level());
        if (result.isInterrupt()) {
            if (result.getAsBoolean()) {
                this.discard();
            } else {
                this.noActionTime = 0;
            }

            callback.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo callback) {
        output.storeNullable(PuzzlesLibMod.id("spawn_type").toString(),
                SpawnReasonMob.CODEC,
                this.puzzleslib$getSpawnReason());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo callback) {
        EntitySpawnReason spawnReason = input.read(PuzzlesLibMod.id("spawn_type").toString(), SpawnReasonMob.CODEC)
                .orElse(null);
        this.puzzleslib$setSpawnReason(spawnReason);
    }
}
