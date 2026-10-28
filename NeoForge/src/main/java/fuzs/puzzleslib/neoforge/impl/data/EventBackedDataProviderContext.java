package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.PackGenerator;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public record EventBackedDataProviderContext(GatherDataEvent event,
                                             PackOutput packOutput,
                                             PackGenerator packGenerator) implements DataProviderContext {

    public EventBackedDataProviderContext(GatherDataEvent event) {
        this(event, event.getGenerator().getPackOutput(), event.getDefaultPackGenerator());
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
    public Collection<Path> getInputs() {
        return this.event.getInputs();
    }

    @Override
    public CompletableFuture<HolderLookup.Provider> getRegistries() {
        return this.packGenerator.getReloadableLookupProvider();
    }

    @Override
    public CompletableFuture<HolderLookup.Provider> getWorldRegistries() {
        return this.packGenerator.getWorldLookupProvider();
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
