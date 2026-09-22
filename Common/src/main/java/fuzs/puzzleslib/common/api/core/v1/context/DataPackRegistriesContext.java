package fuzs.puzzleslib.common.api.core.v1.context;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Register data pack-driven dynamic registries.
 */
public interface DataPackRegistriesContext {
    /**
     * Registers an unsynchronized dynamic registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serializing registry values
     * @param <T>         the registry value type
     */
    @Deprecated
    default <T> void registerRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        this.registerWorldRegistry(registryKey, codec);
    }

    /**
     * Registers an unsynchronized world registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serializing registry values
     * @param <T>         the registry value type
     * @see net.minecraft.resources.RegistryDataLoader#WORLD_REGISTRIES
     */
    <T> void registerWorldRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec);

    /**
     * Registers an unsynchronized reloadable registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serializing registry values
     * @param <T>         the registry value type
     * @see net.minecraft.resources.RegistryDataLoader#RELOADABLE_REGISTRIES
     */
    <T> void registerReloadableRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec);

    /**
     * Registers a synchronized world registry.
     *
     * @param registryKey the registry resource key
     * @param codec       the codec for serializing registry values
     * @param <T>         the registry value type
     * @see net.minecraft.resources.RegistryDataLoader#WORLD_REGISTRIES
     */
    default <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        this.registerSyncedRegistry(registryKey, codec, codec);
    }

    /**
     * Registers a synchronized world registry.
     *
     * @param registryKey  the registry resource key
     * @param codec        the codec for serializing registry values
     * @param networkCodec an optional more compressed network codec for serializing registry values for synchronization
     *                     across networks
     * @param <T>          the registry value type
     * @see net.minecraft.resources.RegistryDataLoader#WORLD_REGISTRIES
     */
    <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec);
}
