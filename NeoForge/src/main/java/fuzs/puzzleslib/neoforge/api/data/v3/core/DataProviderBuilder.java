package fuzs.puzzleslib.neoforge.api.data.v3.core;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v3.metadata.ModPackMetadataProvider;
import fuzs.puzzleslib.neoforge.impl.data.AbstractDataProviderBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.function.BiFunction;

/**
 * A builder for registering {@link net.minecraft.data.DataProvider DataProviders} to be run during data generation via
 * {@link GatherDataEvent}.
 * <p>
 * The builder replaces having to construct a {@link RegistrySetBuilder} manually: it mimics
 * {@link RegistrySetBuilder}'s {@code add} methods while remaining chainable, so registries and data providers can be
 * configured in a single fluent call. Registries are split into a world and a reloadable layer, each with their own
 * {@code add} method pair and their own setter accepting an existing {@link RegistrySetBuilder}. Methods without a
 * layer qualifier target the world (default) layer, methods qualified with {@code Reloadable} target the reloadable
 * layer.
 * <p>
 * The event listener is registered immediately when the builder is created; therefore, no terminal build or register
 * call is required; all configuration methods return this builder for chaining.
 */
public interface DataProviderBuilder {

    /**
     * Creates a new builder generating for the given mod.
     *
     * @param modId the mod id
     * @return the new builder instance
     */
    static DataProviderBuilder of(String modId) {
        return AbstractDataProviderBuilder.of(modId);
    }

    /**
     * Creates a new builder generating for the given mod, adding the given data provider factories.
     *
     * @param modId         the mod id
     * @param dataProviders the data provider factories
     * @return the new builder instance
     */
    static DataProviderBuilder of(String modId, DataProviderContext.Factory... dataProviders) {
        return AbstractDataProviderBuilder.of(modId, dataProviders);
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
    static DataProviderBuilder ofBuiltIn(Identifier id, PackType packType) {
        return AbstractDataProviderBuilder.ofBuiltIn(id, packType);
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
    static DataProviderBuilder ofBuiltIn(Identifier id, PackType packType, DataProviderContext.Factory... dataProviders) {
        return AbstractDataProviderBuilder.ofBuiltIn(id, packType, dataProviders);
    }

    /**
     * Adds a registry and its bootstrap to the world layer, mirroring
     * {@link RegistrySetBuilder#add(ResourceKey, SingleRegistryBootstrap)}.
     *
     * @param key       the registry key
     * @param bootstrap the bootstrap
     * @param <T>       the registry element type
     * @return this builder instance
     */
    @Deprecated
    default <T> DataProviderBuilder add(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap) {
        return this.addWorldBootstrap(key, bootstrap);
    }

    /**
     * Adds a multi-registry bootstrap to the world layer, mirroring
     * {@link RegistrySetBuilder#add(MultiRegistryBootstrap)}.
     *
     * @param bootstrap the bootstrap
     * @return this builder instance
     */
    @Deprecated
    default DataProviderBuilder add(MultiRegistryBootstrap bootstrap) {
        return this.addWorldBootstrap(bootstrap);
    }

    /**
     * Adds a registry and its bootstrap to the world layer, mirroring
     * {@link RegistrySetBuilder#add(ResourceKey, SingleRegistryBootstrap)}.
     *
     * @param key       the registry key
     * @param bootstrap the bootstrap
     * @param <T>       the registry element type
     * @return this builder instance
     */
    <T> DataProviderBuilder addWorldBootstrap(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap);

    /**
     * Adds a multi-registry bootstrap to the world layer, mirroring
     * {@link RegistrySetBuilder#add(MultiRegistryBootstrap)}.
     *
     * @param bootstrap the bootstrap
     * @return this builder instance
     */
    DataProviderBuilder addWorldBootstrap(MultiRegistryBootstrap bootstrap);

    /**
     * Adds a registry and its bootstrap to the reloadable layer, mirroring
     * {@link RegistrySetBuilder#add(ResourceKey, SingleRegistryBootstrap)}.
     *
     * @param key       the registry key
     * @param bootstrap the bootstrap
     * @param <T>       the registry element type
     * @return this builder instance
     */
    @Deprecated
    default <T> DataProviderBuilder addReloadable(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap) {
        return this.addReloadableBootstrap(key, bootstrap);
    }

    /**
     * Adds a multi-registry bootstrap to the reloadable layer, mirroring
     * {@link RegistrySetBuilder#add(MultiRegistryBootstrap)}.
     *
     * @param bootstrap the bootstrap
     * @return this builder instance
     */
    @Deprecated
    default DataProviderBuilder addReloadable(MultiRegistryBootstrap bootstrap) {
        return this.addReloadableBootstrap(bootstrap);
    }

    /**
     * Adds a registry and its bootstrap to the reloadable layer, mirroring
     * {@link RegistrySetBuilder#add(ResourceKey, SingleRegistryBootstrap)}.
     *
     * @param key       the registry key
     * @param bootstrap the bootstrap
     * @param <T>       the registry element type
     * @return this builder instance
     */
    <T> DataProviderBuilder addReloadableBootstrap(ResourceKey<? extends Registry<T>> key, SingleRegistryBootstrap<T> bootstrap);

    /**
     * Adds a multi-registry bootstrap to the reloadable layer, mirroring
     * {@link RegistrySetBuilder#add(MultiRegistryBootstrap)}.
     *
     * @param bootstrap the bootstrap
     * @return this builder instance
     */
    DataProviderBuilder addReloadableBootstrap(MultiRegistryBootstrap bootstrap);

    /**
     * Sets an existing {@link RegistrySetBuilder} for the world (default) layer, replacing the internal one.
     * <p>
     * The builder may only be replaced while it does not contain any entries yet.
     *
     * @param registrySetBuilder the registry set builder to use
     * @return this builder instance
     */
    @Deprecated
    DataProviderBuilder setRegistrySetBuilder(RegistrySetBuilder registrySetBuilder);

    /**
     * Sets an existing {@link RegistrySetBuilder} for the reloadable layer, replacing the internal one.
     * <p>
     * The builder may only be replaced while it does not contain any entries yet.
     *
     * @param registrySetBuilder the registry set builder to use
     * @return this builder instance
     */
    @Deprecated
    DataProviderBuilder setReloadableRegistrySetBuilder(RegistrySetBuilder registrySetBuilder);

    /**
     * Adds a single data provider factory.
     *
     * @param dataProvider the data provider factory
     * @return this builder instance
     */
    DataProviderBuilder addProvider(DataProviderContext.Factory dataProvider);

    /**
     * Adds multiple data provider factories.
     *
     * @param dataProviders the data provider factories
     * @return this builder instance
     */
    DataProviderBuilder addProvider(DataProviderContext.Factory... dataProviders);

    /**
     * Adds a single loot table sub-provider entry to the accumulated loot table provider.
     *
     * @param entry the loot table sub-provider entry
     * @return this builder instance
     */
    DataProviderBuilder addLootProvider(LootTableProvider.SubProviderEntry entry);

    /**
     * Adds loot table sub-provider entries to the accumulated loot table provider.
     *
     * @param entries the loot table sub-provider entries
     * @return this builder instance
     */
    DataProviderBuilder addLootProvider(LootTableProvider.SubProviderEntry... entries);

    /**
     * Adds a loot table sub-provider to the accumulated loot table provider, using the given context key set.
     *
     * @param provider the loot table sub-provider factory
     * @param paramSet the loot context parameter set
     * @return this builder instance
     */
    DataProviderBuilder addLootProvider(LootTableSubProvider.Factory provider, ContextKeySet paramSet);

    /**
     * Adds a single advancement sub-provider.
     *
     * @param provider the advancement sub-provider factory
     * @return this builder instance
     */
    DataProviderBuilder addAdvancementProvider(AdvancementSubProvider.Factory provider);

    /**
     * Adds multiple advancement sub-providers.
     *
     * @param providers the advancement sub-provider factories
     * @return this builder instance
     */
    DataProviderBuilder addAdvancementProvider(AdvancementSubProvider.Factory... providers);

    /**
     * Adds a recipe provider, covering both {@link net.minecraft.data.recipes.RecipeProvider} and
     * {@link net.minecraft.data.recipes.BrewingProvider} implementations since both are {@link Runnable}.
     *
     * @param provider the recipe provider factory
     * @return this builder instance
     */
    DataProviderBuilder addRecipeProvider(BiFunction<BootstrapContext<Recipe<?>>, BootstrapContext<Advancement>, ? extends Runnable> provider);
}
