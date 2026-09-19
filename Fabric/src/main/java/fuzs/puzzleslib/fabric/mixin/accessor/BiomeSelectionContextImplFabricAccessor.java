package fuzs.puzzleslib.fabric.mixin.accessor;

import net.fabricmc.fabric.impl.biome.modification.BiomeSelectionContextImpl;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeSelectionContextImpl.class)
public interface BiomeSelectionContextImplFabricAccessor {
    @Accessor("dynamicRegistries")
    RegistryAccess puzzleslib$getDynamicRegistries();
}
