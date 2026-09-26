package fuzs.puzzleslib.neoforge.impl.core.context;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.common.api.core.v1.context.DataPackRegistriesContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;

import java.util.Objects;

public record DataPackRegistriesContextNeoForgeImpl(NewDatapackRegistryEvent event) implements DataPackRegistriesContext {

    @Override
    public <T> void registerWorldRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        this.event.worldRegistry(registryKey, codec);
    }

    @Override
    public <T> void registerReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        this.event.reloadableRegistry(registryKey, codec);
    }

    @Override
    public <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        Objects.requireNonNull(networkCodec, "network codec is null");
        this.event.worldRegistry(registryKey, codec, networkCodec);
    }
}
