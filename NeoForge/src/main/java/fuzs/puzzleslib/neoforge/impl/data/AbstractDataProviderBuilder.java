package fuzs.puzzleslib.neoforge.impl.data;

import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v3.metadata.ModPackMetadataProvider;
import fuzs.puzzleslib.common.api.resources.v2.PackResourcesHelper;
import fuzs.puzzleslib.common.impl.data.DataGenerationScopes;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.data.event.DatapackRegistryGatherer;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.neoforged.neoforge.data.event.PackGenerator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     * The registry bootstrap entries for the world layer.
     */
    private final List<RegistryEntry> worldRegistryEntries = new ArrayList<>();
    /**
     * The registry bootstrap entries for the reloadable layer.
     */
    private final List<RegistryEntry> reloadableRegistryEntries = new ArrayList<>();
    /**
     * The loot table sub-providers accumulated into a single loot table provider.
     */
    private final List<SubProviderEntry> lootTableSubProviders = new ArrayList<>();

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
    public <T> DataProviderBuilder addWorldBootstrap(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap) {
        this.worldRegistryEntries.add(new SingleRegistryEntry<>(key, bootstrap));
        return this;
    }

    @Override
    public DataProviderBuilder addWorldBootstrap(MultiRegistryBootstrap bootstrap) {
        this.worldRegistryEntries.add(new MultiRegistryEntry(bootstrap));
        return this;
    }

    @Override
    public <T> DataProviderBuilder addReloadableBootstrap(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap) {
        this.reloadableRegistryEntries.add(new SingleRegistryEntry<>(key, bootstrap));
        return this;
    }

    @Override
    public DataProviderBuilder addReloadableBootstrap(MultiRegistryBootstrap bootstrap) {
        this.reloadableRegistryEntries.add(new MultiRegistryEntry(bootstrap));
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
    public DataProviderBuilder addLootProvider(LootTableSubProvider.Factory provider, ContextKeySet paramSet) {
        Objects.requireNonNull(provider, "loot table sub-provider is null");
        Objects.requireNonNull(paramSet, "context key set is null");
        return this.addLootProvider(provider, BuiltInRegistries.CONTEXT_KEY_SET.wrapAsHolder(paramSet));
    }

    @Override
    public DataProviderBuilder addLootProvider(LootTableSubProvider.Factory provider, Holder<ContextKeySet> paramSet) {
        Objects.requireNonNull(provider, "loot table sub-provider is null");
        Objects.requireNonNull(paramSet, "context key set is null");
        this.lootTableSubProviders.add(new SubProviderEntry((LootTableSubProvider.Context context) -> {
            return ScopedValue.where(DataGenerationScopes.MOD_ID, this.modId).call(() -> provider.create(context));
        }, paramSet));
        return this;
    }

    @Override
    public DataProviderBuilder addAdvancementProvider(AdvancementSubProvider.Factory provider) {
        Objects.requireNonNull(provider, "advancement sub-provider is null");
        return this.addReloadableBootstrap(Registries.ADVANCEMENT, new AdvancementProvider(List.of(provider)));
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
        return this.addReloadableBootstrap(new MultiRegistryBootstrap() {
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
     * Creates the pack generator used for generating the registry set builders.
     *
     * @param event the event
     * @return the pack generator targeting the registry output
     */
    protected abstract PackGenerator createRegistryPackGenerator(GatherDataEvent event);

    /**
     * Whether registry entries are added to the global registry set via {@link GatherDataRegistryEntriesEvent} instead
     * of being generated into this builder's own pack.
     *
     * @return whether registry entries are gathered globally
     */
    protected abstract boolean generatesGlobalRegistryEntries();

    /**
     * The provider name used for generating world registry objects. This must be unique per event, since
     * {@link PackGenerator} registers providers by their bare name.
     *
     * @return the world registry provider name
     */
    protected String getWorldRegistryProviderName() {
        return "world";
    }

    /**
     * The provider name used for generating reloadable registry objects. This must be unique per event, since
     * {@link PackGenerator} registers providers by their bare name.
     *
     * @return the reloadable registry provider name
     */
    protected String getReloadableRegistryProviderName() {
        return "reloadable";
    }

    /**
     * Registers the data generation event listeners reading this builder's current state when the events fire.
     */
    private void registerEventListener() {
        if (!ModLoaderEnvironment.INSTANCE.isDataGeneration()) {
            return;
        }

        NeoForgeModContainerHelper.getOptionalModEventBus(this.modId).ifPresent((IEventBus eventBus) -> {
            if (this.generatesGlobalRegistryEntries()) {
                eventBus.addListener(this::addGlobalRegistryEntries);
            }

            eventBus.addListener((final GatherDataEvent.Client event) -> {
                this.addDataProviders(event);
            });
        });
    }

    private void addGlobalRegistryEntries(GatherDataRegistryEntriesEvent event) {
        this.addLootTableProvider();
        for (RegistryEntry entry : this.worldRegistryEntries) {
            entry.add(event);
        }

        for (RegistryEntry entry : this.reloadableRegistryEntries) {
            entry.add(event);
        }
    }

    private void addDataProviders(GatherDataEvent event) {
        RootDataProviderFactory factory = this.createRootDataProviderFactory(event);
        PackGenerator generator = this.createRegistryPackGenerator(event);

        if (!this.generatesGlobalRegistryEntries()) {
            this.addLocalRegistryEntries(generator);
        }

        for (DataProviderContext.Factory dataProvider : this.dataProviders) {
            factory.apply((PackOutput packOutput) -> {
                return dataProvider.apply(new EventBackedDataProviderContext(event, packOutput, generator));
            });
        }
    }

    private void addLocalRegistryEntries(PackGenerator generator) {
        this.addLootTableProvider();
        if (!this.worldRegistryEntries.isEmpty()) {
            // Make sure this generates for all namespaces (namely vanilla) by passing a null set.
            generator.createWorldRegistryObjects(createRegistrySetBuilder(this.worldRegistryEntries),
                    null,
                    this.getWorldRegistryProviderName());
        }

        if (!this.reloadableRegistryEntries.isEmpty()) {
            // Make sure this generates for all namespaces (namely vanilla) by passing a null set.
            generator.createReloadableRegistryObjects(createRegistrySetBuilder(this.reloadableRegistryEntries),
                    null,
                    this.getReloadableRegistryProviderName());
        }
    }

    private void addLootTableProvider() {
        if (this.lootTableSubProviders.isEmpty()) {
            return;
        }

        // Accumulated loot table sub-providers are materialized into a single loot table provider right before the
        // reloadable layer is built, so they share one random sequence collision map.
        List<LootTableProvider.SubProviderEntry> providers = this.lootTableSubProviders.stream()
                .map(SubProviderEntry::repack)
                .toList();
        this.addReloadableBootstrap(Registries.LOOT_TABLE, new LootTableProvider(Set.of(), providers));
    }

    private static RegistrySetBuilder createRegistrySetBuilder(List<RegistryEntry> entries) {
        RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

        for (RegistryEntry entry : entries) {
            entry.add(registrySetBuilder);
        }

        return registrySetBuilder;
    }

    private sealed interface RegistryEntry {
        void add(RegistrySetBuilder registrySetBuilder);

        void add(DatapackRegistryGatherer gatherer);
    }

    private record SingleRegistryEntry<T>(ResourceKey<? extends Registry<T>> key,
                                          SingleRegistryBootstrap<T> bootstrap) implements RegistryEntry {
        @Override
        public void add(RegistrySetBuilder registrySetBuilder) {
            registrySetBuilder.add(this.key, this.bootstrap);
        }

        @Override
        public void add(DatapackRegistryGatherer gatherer) {
            gatherer.add(this.key, this.bootstrap);
        }
    }

    private record MultiRegistryEntry(MultiRegistryBootstrap bootstrap) implements RegistryEntry {
        @Override
        public void add(RegistrySetBuilder registrySetBuilder) {
            registrySetBuilder.add(this.bootstrap);
        }

        @Override
        public void add(DatapackRegistryGatherer gatherer) {
            gatherer.add(this.bootstrap);
        }
    }

    private record SubProviderEntry(LootTableSubProvider.Factory bootstrap, Holder<ContextKeySet> paramSet) {
        public LootTableProvider.SubProviderEntry repack() {
            return new LootTableProvider.SubProviderEntry(this.bootstrap(), this.paramSet().value());
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

        @Override
        protected PackGenerator createRegistryPackGenerator(GatherDataEvent event) {
            return event.getDefaultPackGenerator();
        }

        @Override
        protected boolean generatesGlobalRegistryEntries() {
            return true;
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
            DataGenerator.PackGenerator packGenerator = event.getGenerator()
                    .getPackGenerator(true, this.packId.toString(), this.getOutputFolder().toString());
            return packGenerator::addProvider;
        }

        @Override
        protected PackGenerator createRegistryPackGenerator(GatherDataEvent event) {
            PackOutput packOutput = event.getGenerator().getPackOutput(this.getOutputFolder().toString());
            return event.getPackGenerator(packOutput);
        }

        @Override
        protected boolean generatesGlobalRegistryEntries() {
            return false;
        }

        private Path getOutputFolder() {
            return Path.of(this.packType.getDirectory(),
                    this.packId.getNamespace(),
                    PackResourcesHelper.getBuiltInDomain(this.packType),
                    this.packId.getPath());
        }

        @Override
        protected String getWorldRegistryProviderName() {
            // The pack id makes the provider name unique, as PackGenerator registers providers by their bare name.
            return this.packId + "/" + super.getWorldRegistryProviderName();
        }

        @Override
        protected String getReloadableRegistryProviderName() {
            // The pack id makes the provider name unique, as PackGenerator registers providers by their bare name.
            return this.packId + "/" + super.getReloadableRegistryProviderName();
        }
    }
}
