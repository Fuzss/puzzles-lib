package fuzs.puzzleslib.neoforge.impl.event;

import com.mojang.datafixers.util.Either;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.neoforge.api.event.v1.ModifyEnchantmentsEvent;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NeoForgeEventImplHelper {

    private NeoForgeEventImplHelper() {
        // NO-OP
    }

    @SuppressWarnings("unchecked")
    public static <T> RegistryLoadTask.PendingRegistration<T> onModifyEnchantments(HolderGetter.Provider lookupProvider, RegistryLoadTask.@Nullable PendingRegistration<T> pendingRegistration) {
        if (pendingRegistration == null || !pendingRegistration.key().isFor(Registries.ENCHANTMENT)) {
            return pendingRegistration;
        }

        Either<T, Exception> value = pendingRegistration.value();
        if (value.left().isEmpty() || !(value.left().get() instanceof Enchantment(
                Component description, Enchantment.EnchantmentDefinition definition,
                HolderSet<Enchantment> exclusiveSet, DataComponentMap effects
        ))) {
            return pendingRegistration;
        }

        Enchantment.Builder builder = Enchantment.enchantment(definition);
        builder.exclusiveWith(exclusiveSet);
        // Copy the original effects so the builder starts out as a copy of the enchantment being modified.
        builder.effectMapBuilder.addAll(effects);
        effects.forEach((TypedDataComponent<?> component) -> {
            if (component.value() instanceof List<?> valueList) {
                builder.getEffectsList((DataComponentType<List<Object>>) component.type()).addAll(valueList);
            }
        });

        NeoForge.EVENT_BUS.post(new ModifyEnchantmentsEvent((ResourceKey<Enchantment>) pendingRegistration.key(),
                builder,
                lookupProvider));

        DataComponentMap updatedEffects = builder.effectMapBuilder.build();
        if (updatedEffects.equals(effects)) {
            return pendingRegistration;
        }

        // Keep the original description instead of deriving it from the resource key.
        Enchantment updatedEnchantment = new Enchantment(description, definition, exclusiveSet, updatedEffects);
        // Clear the known pack info to force the server to sync the modified data pack to the client.
        return new RegistryLoadTask.PendingRegistration<>(pendingRegistration.key(),
                Either.left((T) updatedEnchantment),
                new RegistrationInfo(Optional.empty(), pendingRegistration.registrationInfo().lifecycle()));
    }

    public static @Nullable Player getPlayerFromContainerMenu(AbstractContainerMenu abstractContainerMenu) {
        for (Slot slot : abstractContainerMenu.slots) {
            if (slot.container instanceof Inventory inventory) {
                return inventory.player;
            }
        }

        MinecraftServer server = ProxyImpl.get().getMinecraftServer();
        if (server != null) {
            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                if (serverPlayer.containerMenu == abstractContainerMenu) {
                    return serverPlayer;
                }
            }
        }

        return null;
    }

    public static Map.@Nullable Entry<GrindstoneMenu, Player> getGrindstoneMenuFromInputs(ItemStack primaryItemStack, ItemStack secondaryItemStack) {
        MinecraftServer minecraftServer = ProxyImpl.get().getMinecraftServer();
        if (minecraftServer != null) {
            for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
                if (serverPlayer.containerMenu instanceof GrindstoneMenu grindstoneMenu) {
                    if (grindstoneMenu.getSlot(0).getItem() == primaryItemStack
                            && grindstoneMenu.getSlot(1).getItem() == secondaryItemStack) {
                        return Map.entry(grindstoneMenu, serverPlayer);
                    }
                }
            }
        }

        return null;
    }
}
