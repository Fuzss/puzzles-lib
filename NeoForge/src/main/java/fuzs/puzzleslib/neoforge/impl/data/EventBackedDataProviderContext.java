package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public record EventBackedDataProviderContext(GatherDataEvent event,
                                             PackOutput packOutput) implements DataProviderContext {

    public EventBackedDataProviderContext(GatherDataEvent event) {
        this(event, event.getGenerator().getPackOutput());
    }

    @Override
    public String getModId() {
        return this.event.getModContainer().getModId();
    }

    @Override
    public PackOutput getPackOutput() {
        return this.packOutput;
    }

    @Override
    public CompletableFuture<HolderLookup.Provider> getRegistries() {
        return this.event.getReloadableLookupProvider();
    }

    @Override
    public CompletableFuture<HolderLookup.Provider> getWorldRegistries() {
        return this.event.getWorldLookupProvider();
    }

    @Override
    public ResourceManager getClientResources() {
        return this.event.getResourceManager(PackType.CLIENT_RESOURCES);
    }

    @Override
    public ResourceManager getServerResources() {
        return this.event.getResourceManager(PackType.SERVER_DATA);
    }
}
