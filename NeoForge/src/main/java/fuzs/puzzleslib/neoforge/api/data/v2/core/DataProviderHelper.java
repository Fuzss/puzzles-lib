package fuzs.puzzleslib.neoforge.api.data.v2.core;

import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.api.data.v2.ModPackMetadataProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.resources.v2.PackResourcesHelper;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.data.EventBackedDataProviderContext;
import fuzs.puzzleslib.neoforge.mixin.accessor.GatherDataEventNeoForgeAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A helper class for registering {@link DataProvider DataProviders} which run during data generation in a development
 * environment.
 */
public final class DataProviderHelper {

    private DataProviderHelper() {
        // NO-OP
    }

    /**
     * Register {@link DataProvider DataProviders} to be run during data generation via {@link GatherDataEvent}.
     *
     * @param modId         the mod id
     * @param dataProviders the data provider factories
     */
    public static void registerDataProviders(String modId, DataProviderContext.Factory... dataProviders) {
        registerDataProviders(modId, new RegistrySetBuilder(), dataProviders);
    }

    /**
     * Register {@link DataProvider DataProviders} for a built-in pack bundled with the mod to be run during data
     * generation via {@link GatherDataEvent}.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param id            the id
     * @param packType      the pack type
     * @param dataProviders the data provider factories to run
     */
    public static void registerDataProviders(Identifier id, PackType packType, DataProviderContext.Factory... dataProviders) {
        registerDataProviders(id, packType, new RegistrySetBuilder(), dataProviders);
    }

    /**
     * Register {@link DataProvider DataProviders} to be run during data generation via {@link GatherDataEvent}.
     *
     * @param modId              the mod id
     * @param registrySetBuilder the optional registry set builder
     * @param dataProviders      the data provider factories
     */
    public static void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, DataProviderContext.Factory... dataProviders) {
        registerDataProviders(modId, registrySetBuilder, dataProviders, (DataProviderContext.Factory factory) -> {
            return (GatherDataEvent event, PackOutput packOutput) -> {
                return factory.apply(new EventBackedDataProviderContext(event, packOutput));
            };
        });
    }

    /**
     * Register {@link DataProvider DataProviders} for a built-in pack bundled with the mod to be run during data
     * generation via {@link GatherDataEvent}.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param id                 the id
     * @param packType           the pack type
     * @param registrySetBuilder the optional registry set builder
     * @param dataProviders      the data provider factories to run
     */
    public static void registerDataProviders(Identifier id, PackType packType, RegistrySetBuilder registrySetBuilder, DataProviderContext.Factory... dataProviders) {
        registerDataProviders(id,
                packType,
                registrySetBuilder,
                ArrayUtils.add(dataProviders, (DataProviderContext context) -> {
                    return new ModPackMetadataProvider(packType, context);
                }),
                (DataProviderContext.Factory factory) -> {
                    return (GatherDataEvent event, PackOutput packOutput) -> {
                        return factory.apply(new EventBackedDataProviderContext(event, packOutput));
                    };
                });
    }

    private static <T> void registerDataProviders(String modId, RegistrySetBuilder registrySetBuilder, T[] dataProviders, Function<T, DataProviderFactory> factoryTransformer) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            return;
        }

        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent.Client event) -> {
                addDataProviders(event,
                        registrySetBuilder,
                        dataProviders,
                        factoryTransformer,
                        new RootDataProviderFactory() {
                            @Override
                            public <T extends DataProvider> T apply(DataProvider.Factory<T> factory) {
                                return event.createProvider(factory::create);
                            }
                        });
            });
        });
    }

    private static <T> void registerDataProviders(Identifier id, PackType packType, RegistrySetBuilder registrySetBuilder, T[] dataProviders, Function<T, DataProviderFactory> factoryTransformer) {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            return;
        }

        NeoForgeModContainerHelper.getOptionalModEventBus(id.getNamespace()).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent.Client event) -> {
                Path outputFolder = Path.of(packType.getDirectory(),
                        id.getNamespace(),
                        PackResourcesHelper.getBuiltInDomain(packType),
                        id.getPath());
                DataGenerator.PackGenerator packGenerator = event.getGenerator()
                        .getPackGenerator(true, id.toString(), outputFolder.toString());
                addDataProviders(event,
                        registrySetBuilder,
                        dataProviders,
                        factoryTransformer,
                        packGenerator::addProvider);
            });
        });
    }

    private static <T> void addDataProviders(GatherDataEvent event, RegistrySetBuilder registrySetBuilder, T[] dataProviders, Function<T, DataProviderFactory> factoryTransformer, RootDataProviderFactory factory) {
        if (!registrySetBuilder.getEntryKeys().isEmpty()) {
            // Make sure this generates for all namespaces (namely vanilla) by passing a null set.
            // Also, run this manually so it is added to the correct generator.
            CompletableFuture<HolderLookup.Provider> worldRegistries = factory.apply((PackOutput packOutput) -> {
                return DatapackBuiltinEntriesProvider.forWorldLayer(packOutput,
                        "world",
                        event.getWorldLookupProvider(),
                        registrySetBuilder,
                        (Set<String>) null);
            }).getRegistryProvider();
            ((GatherDataEventNeoForgeAccessor) event).puzzleslib$setWorldRegistriesWithModdedEntries(worldRegistries);
        }

        // FIXME this must use its own correct registry set builder, which will be solved when migrating to a builder implementation
        if (!registrySetBuilder.getEntryKeys().isEmpty()) {
            // Make sure this generates for all namespaces (namely vanilla) by passing a null set.
            // Also, run this manually so it is added to the correct generator.
            CompletableFuture<HolderLookup.Provider> reloadableRegistries = factory.apply((PackOutput packOutput) -> {
                return DatapackBuiltinEntriesProvider.forReloadableLayer(packOutput,
                        "reloadable",
                        event.getWorldLookupProvider(),
                        event.getReloadableLookupProvider(),
                        registrySetBuilder,
                        (Set<String>) null);
            }).getRegistryProvider();
            ((GatherDataEventNeoForgeAccessor) event).puzzleslib$setReloadableRegistriesWithModdedEntries(
                    reloadableRegistries);
        }

        for (T dataProviderFactory : dataProviders) {
            factory.apply((PackOutput packOutput) -> {
                return factoryTransformer.apply(dataProviderFactory).apply(event, packOutput);
            });
        }
    }

    @FunctionalInterface
    private interface RootDataProviderFactory {
        <T extends DataProvider> T apply(DataProvider.Factory<T> factory);
    }

    @FunctionalInterface
    private interface DataProviderFactory {
        DataProvider apply(GatherDataEvent event, PackOutput packOutput);
    }
}
