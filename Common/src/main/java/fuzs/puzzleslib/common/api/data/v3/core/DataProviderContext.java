package fuzs.puzzleslib.common.api.data.v3.core;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A context class used for {@link DataProvider DataProviders}.
 * <p>
 * Offers similar capabilities as NeoForge's {@code net.neoforged.neoforge.data.event.GatherDataEvent}.
 */
public interface DataProviderContext {
    /**
     * @return the generating mod id
     */
    String getModId();

    /**
     * @return the pack output
     */
    PackOutput getPackOutput();

    /**
     * @return the input paths provided via {@code --input}
     */
    Collection<Path> getInputs();

    /**
     * @return the full registry lookup provider
     */
    CompletableFuture<HolderLookup.Provider> getRegistries();

    /**
     * @return the client resource manager
     */
    @Nullable ResourceManager getClientResources();

    /**
     * @return the server resource manager
     */
    @Nullable ResourceManager getServerResources();

    /**
     * A simple shortcut for a data provider factory requiring this context.
     */
    @FunctionalInterface
    interface Factory extends Function<DataProviderContext, DataProvider> {
        // NO-OP
    }
}
