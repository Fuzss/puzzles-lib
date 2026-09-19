package fuzs.puzzleslib.common.api.data.v3.core;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A context class for providing instances required by a {@link DataProvider}.
 * <p>
 * Offers similar capabilities as NeoForge's {@code net.neoforged.neoforge.data.event.GatherDataEvent}.
 */
public interface DataProviderContext {
    /**
     * @return the generating mod id
     */
    String getModId();

    /**
     * @return the pack output instance
     */
    PackOutput getPackOutput();

    /**
     * @return the reloadable registries lookup provider, this is the default lookup provider to be used by mods
     *
     * @see VanillaRegistries#RELOADABLE_BUILDER
     */
    CompletableFuture<HolderLookup.Provider> getRegistries();

    /**
     * @return the world registries lookup provider, only to be used when the reloadable lookup provider is not
     *         sufficient
     *
     * @see VanillaRegistries#WORLD_BUILDER
     */
    CompletableFuture<HolderLookup.Provider> getWorldRegistries();

    /**
     * @return the client resource manager
     */
    @Nullable ResourceManager getClientResources();

    /**
     * @return the server resource manager
     */
    @Nullable ResourceManager getServerResources();

    /**
     * A simple shortcut for a data provider factory requiring an instance of this context, which helps with complaints
     * about parametrized varargs.
     */
    @FunctionalInterface
    interface Factory extends Function<DataProviderContext, DataProvider> {
        // NO-OP
    }
}
