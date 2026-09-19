package fuzs.puzzleslib.fabric.mixin.accessor;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface BiomeFabricAccessor {
    @Accessor("climateSettings")
    Biome.ClimateSettings puzzleslib$getClimateSettings();
}
