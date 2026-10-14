package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.common.api.container.v1.MenuProviderWithData;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MenuProviderWithData.class)
interface MenuProviderWithDataFabricMixin<T> extends ExtendedMenuProvider<T> {
    @Override
    default T getScreenOpeningData(ServerPlayer player) {
        return this.getMenuData(player);
    }

    @Shadow
    T getMenuData(ServerPlayer player);
}
