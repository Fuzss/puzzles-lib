package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.common.api.container.v1.MenuProviderWithData;
import fuzs.puzzleslib.neoforge.impl.init.MenuTypeWithData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MenuProviderWithData.class)
interface MenuProviderWithDataNeoForgeMixin<T> extends MenuProvider {
    @Override
    default void writeClientSideData(ServerPlayer player, AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        MenuTypeWithData.encodeMenuData(menu, buffer, this.getMenuData(player));
    }

    @Shadow
    T getMenuData(ServerPlayer player);
}
