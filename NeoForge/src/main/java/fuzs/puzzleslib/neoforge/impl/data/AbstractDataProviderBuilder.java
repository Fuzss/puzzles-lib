package fuzs.puzzleslib.neoforge.impl.data;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v3.metadata.ModPackMetadataProvider;
import fuzs.puzzleslib.common.api.resources.v2.PackResourcesHelper;
import fuzs.puzzleslib.common.impl.data.DataGenerationScopes;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.puzzleslib.neoforge.mixin.accessor.GatherDataEventNeoForgeAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * The shared implementation of {@link DataProviderBuilder}, handling both generation for the mod itself and for
 * built-in packs bundled with the mod.
 */
public abstract class AbstractDataProviderBuilder implements DataProviderBuilder {
    /**
     * The mod id used for looking up the mod event bus.
     */
    private final String modId;
    /**
     * The data provider factories to be created.
     */
    private final List<DataProviderContext.Factory> dataProviders = new ArrayList<>();
    /**
     * The registry set builder for the world layer.
     */
    private RegistrySetBuilder worldRegistrySetBuilder = new RegistrySetBuilder();
    /**
     * The registry set builder for the reloadable layer.
     */
    private RegistrySetBuilder reloadableRegistrySetBuilder = new RegistrySetBuilder();
    /**
     * The loot table sub-providers accumulated into a single loot table provider.
     */
    private final List<LootTableProvider.SubProviderEntry> lootTableSubProviders = new ArrayList<>();

    protected AbstractDataProviderBuilder(String modId) {
        this.modId = Objects.requireNonNull(modId, "mod id is null");
    }

    /**
     * Creates a new builder generating for the given mod.
     *
     * @param modId the mod id
     * @return the new builder instance
     */
    public static DataProviderBuilder of(String modId) {
        return of(modId, new DataProviderContext.Factory[0]);
    }

    /**
     * Creates a new builder generating for the given mod, adding the given data provider factories.
     *
     * @param modId         the mod id
     * @param dataProviders the data provider factories
     * @return the new builder instance
     */
    public static DataProviderBuilder of(String modId, DataProviderContext.Factory... dataProviders) {
        AbstractDataProviderBuilder builder = new RootDataProviderBuilder(modId);
        builder.addProvider(dataProviders);
        builder.registerEventListener();
        return builder;
    }

    /**
     * Creates a new builder generating for a built-in pack bundled with the mod.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     * A {@link ModPackMetadataProvider} is added automatically.
     *
     * @param id       the pack id
     * @param packType the pack type
     * @return the new builder instance
     */
    public static DataProviderBuilder ofBuiltIn(Identifier id, PackType packType) {
        return ofBuiltIn(id, packType, new DataProviderContext.Factory[0]);
    }

    /**
     * Creates a new builder generating for a built-in pack bundled with the mod, adding the given data provider
     * factories.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     * A {@link ModPackMetadataProvider} is added automatically.
     *
     * @param id            the pack id
     * @param packType      the pack type
     * @param dataProviders the data provider factories
     * @return the new builder instance
     */
    public static DataProviderBuilder ofBuiltIn(Identifier id, PackType packType, DataProviderContext.Factory... dataProviders) {
        AbstractDataProviderBuilder builder = new BuiltInDataProviderBuilder(id, packType);
        builder.addProvider((DataProviderContext context) -> new ModPackMetadataProvider(packType, context));
        builder.addProvider(dataProviders);
        builder.registerEventListener();
        return builder;
    }

    @Override
    public <T> DataProviderBuilder add(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap) {
        this.worldRegistrySetBuilder.add(key, bootstrap);
        return this;
    }

    @Override
    public DataProviderBuilder add(MultiRegistryBootstrap bootstrap) {
        this.worldRegistrySetBuilder.add(bootstrap);
        return this;
    }

    @Override
    public <T> DataProviderBuilder addReloadable(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap) {
        this.reloadableRegistrySetBuilder.add(key, bootstrap);
        return this;
    }

    @Override
    public DataProviderBuilder addReloadable(MultiRegistryBootstrap bootstrap) {
        this.reloadableRegistrySetBuilder.add(bootstrap);
        return this;
    }

    @Override
    public DataProviderBuilder setRegistrySetBuilder(RegistrySetBuilder registrySetBuilder) {
        Preconditions.checkState(this.worldRegistrySetBuilder.getEntryKeys().isEmpty(),
                "world registry set builder already contains registry entries");
        this.worldRegistrySetBuilder = Objects.requireNonNull(registrySetBuilder, "registry set builder is null");
        return this;
    }

    @Override
    public DataProviderBuilder setReloadableRegistrySetBuilder(RegistrySetBuilder registrySetBuilder) {
        Preconditions.checkState(this.reloadableRegistrySetBuilder.getEntryKeys().isEmpty(),
                "reloadable registry set builder already contains registry entries");
        this.reloadableRegistrySetBuilder = Objects.requireNonNull(registrySetBuilder, "registry set builder is null");
        return this;
    }

    @Override
    public DataProviderBuilder addProvider(DataProviderContext.Factory dataProvider) {
        this.dataProviders.add(Objects.requireNonNull(dataProvider, "data provider is null"));
        return this;
    }

    @Override
    public DataProviderBuilder addProvider(DataProviderContext.Factory... dataProviders) {
        for (DataProviderContext.Factory dataProvider : dataProviders) {
            this.addProvider(dataProvider);
        }

        return this;
    }

    @Override
    public DataProviderBuilder addLootProvider(LootTableProvider.SubProviderEntry entry) {
        Objects.requireNonNull(entry, "loot table sub-provider entry is null");
        LootTableSubProvider.Factory factory = entry.bootstrap();
        this.lootTableSubProviders.add(new LootTableProvider.SubProviderEntry((LootTableSubProvider.Context context) -> {
            return ScopedValue.where(DataGenerationScopes.MOD_ID, this.modId).call(() -> factory.create(context));
        }, entry.paramSet()));
        return this;
    }

    @Override
    public DataProviderBuilder addLootProvider(LootTableProvider.SubProviderEntry... entries) {
        for (LootTableProvider.SubProviderEntry entry : entries) {
            this.addLootProvider(entry);
        }

        return this;
    }

    @Override
    public DataProviderBuilder addLootProvider(LootTableSubProvider.Factory provider, ContextKeySet paramSet) {
        Objects.requireNonNull(provider, "loot table sub-provider is null");
        Objects.requireNonNull(paramSet, "context key set is null");
        return this.addLootProvider(new LootTableProvider.SubProviderEntry(provider, paramSet));
    }

    @Override
    public DataProviderBuilder addAdvancementProvider(AdvancementSubProvider.Factory provider) {
        Objects.requireNonNull(provider, "advancement sub-provider is null");
        return this.addReloadable(Registries.ADVANCEMENT, new AdvancementProvider(List.of(provider)));
    }

    @Override
    public DataProviderBuilder addAdvancementProvider(AdvancementSubProvider.Factory... providers) {
        for (AdvancementSubProvider.Factory provider : providers) {
            this.addAdvancementProvider(provider);
        }

        return this;
    }

    @Override
    public DataProviderBuilder addRecipeProvider(BiFunction<BootstrapContext<Recipe<?>>, BootstrapContext<Advancement>, ? extends Runnable> provider) {
        Objects.requireNonNull(provider, "recipe provider is null");
        String modId = this.modId;
        return this.addReloadable(new MultiRegistryBootstrap() {
            @Override
            public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
                return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
            }

            @Override
            public void run(BootstrapGetter registries) {
                ScopedValue.where(DataGenerationScopes.MOD_ID, modId)
                        .run(() -> provider.apply(registries.get(Registries.RECIPE),
                                registries.get(Registries.ADVANCEMENT)).run());
            }
        });
    }

    /**
     * Creates the factory responsible for adding providers to the target generator.
     *
     * @param event the event
     * @return the root data provider factory
     */
    protected abstract RootDataProviderFactory createRootDataProviderFactory(GatherDataEvent event);

    /**
     * Registers the {@link GatherDataEvent} listener reading this builder's current state when the event fires.
     */
    private void registerEventListener() {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            return;
        }

        NeoForgeModContainerHelper.getOptionalModEventBus(this.modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final GatherDataEvent.Client event) -> {
                this.addDataProviders(event);
            });
        });
    }

    private void addDataProviders(GatherDataEvent event) {
        RootDataProviderFactory factory = this.createRootDataProviderFactory(event);

        // Accumulated loot table sub-providers are materialized into a single loot table provider right before the
        // reloadable layer is built, so they share one random sequence collision map.
        if (!this.lootTableSubProviders.isEmpty()) {
            this.addReloadable(Registries.LOOT_TABLE,
                    new LootTableProvider(Set.of(), List.copyOf(this.lootTableSubProviders)));
        }

        if (!this.worldRegistrySetBuilder.getEntryKeys().isEmpty()) {
            // Make sure this generates for all namespaces (namely vanilla) by passing a null set.
            // Also, run this manually so it is added to the correct generator.
            CompletableFuture<HolderLookup.Provider> worldRegistries = factory.apply((PackOutput packOutput) -> {
                return DatapackBuiltinEntriesProvider.forWorldLayer(packOutput,
                        "world",
                        event.getWorldLookupProvider(),
                        this.worldRegistrySetBuilder,
                        (Set<String>) null);
            }).getRegistryProvider();
            ((GatherDataEventNeoForgeAccessor) event).puzzleslib$setWorldRegistriesWithModdedEntries(worldRegistries);
        }

        if (!this.reloadableRegistrySetBuilder.getEntryKeys().isEmpty()) {
            // Make sure this generates for all namespaces (namely vanilla) by passing a null set.
            // Also, run this manually so it is added to the correct generator.
            CompletableFuture<HolderLookup.Provider> reloadableRegistries = factory.apply((PackOutput packOutput) -> {
                return DatapackBuiltinEntriesProvider.forReloadableLayer(packOutput,
                        "reloadable",
                        event.getWorldLookupProvider(),
                        event.getReloadableLookupProvider(),
                        this.reloadableRegistrySetBuilder,
                        (Set<String>) null);
            }).getRegistryProvider();
            ((GatherDataEventNeoForgeAccessor) event).puzzleslib$setReloadableRegistriesWithModdedEntries(
                    reloadableRegistries);
        }

        for (DataProviderContext.Factory dataProvider : this.dataProviders) {
            factory.apply((PackOutput packOutput) -> {
                return dataProvider.apply(new EventBackedDataProviderContext(event, packOutput));
            });
        }
    }

    @FunctionalInterface
    protected interface RootDataProviderFactory {
        <T extends DataProvider> T apply(DataProvider.Factory<T> factory);
    }

    private static final class RootDataProviderBuilder extends AbstractDataProviderBuilder {

        RootDataProviderBuilder(String modId) {
            super(modId);
        }

        @Override
        protected RootDataProviderFactory createRootDataProviderFactory(GatherDataEvent event) {
            return new RootDataProviderFactory() {
                @Override
                public <T extends DataProvider> T apply(DataProvider.Factory<T> providerFactory) {
                    return event.createProvider(providerFactory::create);
                }
            };
        }
    }

    private static final class BuiltInDataProviderBuilder extends AbstractDataProviderBuilder {
        private final Identifier packId;
        private final PackType packType;

        BuiltInDataProviderBuilder(Identifier id, PackType packType) {
            super(Objects.requireNonNull(id, "pack id is null").getNamespace());
            this.packId = id;
            this.packType = Objects.requireNonNull(packType, "pack type is null");
        }

        @Override
        protected RootDataProviderFactory createRootDataProviderFactory(GatherDataEvent event) {
            Path outputFolder = Path.of(this.packType.getDirectory(),
                    this.packId.getNamespace(),
                    PackResourcesHelper.getBuiltInDomain(this.packType),
                    this.packId.getPath());
            DataGenerator.PackGenerator packGenerator = event.getGenerator()
                    .getPackGenerator(true, this.packId.toString(), outputFolder.toString());
            return packGenerator::addProvider;
        }
    }
}
