package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.common.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Player.class)
abstract class PlayerFabricMixin extends LivingEntity {

    protected PlayerFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick$0(CallbackInfo callback) {
        FabricPlayerEvents.PLAYER_TICK_START.invoker().onStartPlayerTick((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick$1(CallbackInfo callback) {
        FabricPlayerEvents.PLAYER_TICK_END.invoker().onEndPlayerTick((Player) (Object) this);
    }

    @ModifyReturnValue(method = "getDestroySpeed", at = @At("TAIL"))
    public float getDestroySpeed(float speed, BlockState state) {
        MutableFloat breakSpeed = MutableFloat.fromValue(speed);
        EventResult result = FabricPlayerEvents.CALCULATE_BLOCK_BREAK_SPEED.invoker()
                .onCalculateBlockBreakSpeed((Player) (Object) this, state, breakSpeed);
        if (result.isInterrupt()) {
            breakSpeed.accept(-1.0F);
        }

        return breakSpeed.getAsFloat();
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource source, CallbackInfo callback) {
        // this will fire twice for players, since the die method calls super on LivingEntity, where this is hooked in again
        // Forge has it implemented like this, so let's leave it for now for parity
        // can't easily filter out the second call on Forge unfortunately
        EventResult result = FabricLivingEvents.LIVING_DEATH.invoker().onLivingDeath(this, source);
        if (result.isInterrupt()) {
            callback.cancel();
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected float actuallyHurt(float dmg, ServerLevel level, DamageSource source, @Cancellable CallbackInfo callback) {
        if (!this.isInvulnerableTo(level, source)) {
            Either<Unit, Float> result = FabricEventImplHelper.onLivingHurt(this, level, source, dmg);
            result.ifLeft((Unit _) -> {
                callback.cancel();
            });
            return result.right().orElse(dmg);
        } else {
            return dmg;
        }
    }

    @ModifyReturnValue(method = "getProjectile", at = @At("RETURN"))
    public ItemStack getProjectile(ItemStack projectile, ItemStack heldWeapon) {
        if (heldWeapon.getItem() instanceof ProjectileWeaponItem) {
            MutableValue<ItemStack> projectileValue = MutableValue.fromValue(projectile);
            FabricLivingEvents.PICK_PROJECTILE.invoker().onPickProjectile(this, heldWeapon, projectileValue);
            return Objects.requireNonNull(projectileValue.get(), "projectile is null");
        } else {
            return projectile;
        }
    }
}
