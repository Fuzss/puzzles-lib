package fuzs.puzzleslib.neoforge.mixin.accessor;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@Mixin(GatherDataEvent.class)
public interface GatherDataEventNeoForgeAccessor {
    @Accessor("worldRegistriesWithModdedEntries")
    void puzzleslib$setWorldRegistriesWithModdedEntries(CompletableFuture<HolderLookup.Provider> registries);

    @Accessor("reloadableRegistriesWithModdedEntries")
    void puzzleslib$setReloadableRegistriesWithModdedEntries(CompletableFuture<HolderLookup.Provider> registries);
}
