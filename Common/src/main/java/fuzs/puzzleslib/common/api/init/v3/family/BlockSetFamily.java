package fuzs.puzzleslib.common.api.init.v3.family;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.common.impl.init.BlockSetFamilyRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A group of related blocks, items, and entities that share a base block and are generated together, such as a single
 * wood or stone type.
 * <p>
 * Families are created via {@link #base}, {@link #stone}, or {@link #wooden}, extended by generating
 * {@link BlockSetVariant BlockSetVariants}, and then used by registration and data generation code. Variants that are
 * generated are automatically picked up by the various data providers (tags, recipes, models, loot tables, language),
 * while existing blocks such as vanilla ones may be provided via {@link Writable#provideFor(BlockSetVariant, Block)} to
 * make them available for lookups without generating their resources again.
 */
public interface BlockSetFamily {
    /**
     * Maps the {@link BlockSetVariant BlockSetVariants} that require a block entity to the corresponding
     * {@link BlockEntityType}.
     *
     * @see #registerFor(BiConsumer, Map)
     */
    Map<BlockSetVariant, Holder<BlockEntityType<?>>> VARIANT_BLOCK_ENTITY_TYPE = ImmutableMap.of(BlockSetVariant.SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityTypes.SIGN),
            BlockSetVariant.WALL_SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityTypes.SIGN),
            BlockSetVariant.HANGING_SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityTypes.HANGING_SIGN),
            BlockSetVariant.WALL_HANGING_SIGN,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityTypes.HANGING_SIGN),
            BlockSetVariant.SHELF,
            BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityTypes.SHELF));
    /**
     * The default cooking times for wooden variants that are used as fuel.
     *
     * @see #registerFor(GameplayContentContext, Map, Map)
     */
    Map<BlockSetVariant, ResourceKey<ContextIntProvider>> VARIANT_WOODEN_COOKING_TIME = ImmutableMap.<BlockSetVariant, ResourceKey<ContextIntProvider>>builder()
            .put(BlockSetVariant.SLAB, ContextIntProviders.COOKING_TIME_WOOD_SLABS)
            .put(BlockSetVariant.DOOR, ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE)
            .put(BlockSetVariant.SIGN, ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE)
            .put(BlockSetVariant.BUTTON, ContextIntProviders.COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL)
            .put(BlockSetVariant.HANGING_SIGN, ContextIntProviders.COOKING_TIME_HANGING_SIGNS)
            .put(BlockSetVariant.BOAT, ContextIntProviders.COOKING_TIME_BOATS)
            .put(BlockSetVariant.CHEST_BOAT, ContextIntProviders.COOKING_TIME_BOATS)
            .put(BlockSetVariant.LOG, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.WOOD, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.STRIPPED_LOG, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.STRIPPED_WOOD, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.STAIRS, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.FENCE, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.FENCE_GATE, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.TRAPDOOR, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.PRESSURE_PLATE, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .put(BlockSetVariant.SHELF, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS)
            .build();
    /**
     * The default flammability values (encouragement and flammability) for wooden variants.
     *
     * @see #registerFor(GameplayContentContext, Map, Map)
     */
    Map<BlockSetVariant, Vector2ic> VARIANT_WOODEN_FLAMMABLE = ImmutableMap.of(BlockSetVariant.LOG,
            new Vector2i(5, 5),
            BlockSetVariant.WOOD,
            new Vector2i(5, 5),
            BlockSetVariant.STRIPPED_LOG,
            new Vector2i(5, 5),
            BlockSetVariant.STRIPPED_WOOD,
            new Vector2i(5, 5),
            BlockSetVariant.STAIRS,
            new Vector2i(5, 20),
            BlockSetVariant.SLAB,
            new Vector2i(5, 20),
            BlockSetVariant.FENCE,
            new Vector2i(5, 20),
            BlockSetVariant.FENCE_GATE,
            new Vector2i(5, 20),
            BlockSetVariant.SHELF,
            new Vector2i(30, 20));
    /**
     * The default dispense behaviors for entity variants such as boats.
     *
     * @see #registerFor(Map)
     */
    @SuppressWarnings("unchecked")
    Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> VARIANT_DISPENSE_BEHAVIOR = ImmutableMap.of(
            BlockSetVariant.BOAT,
            (Holder<EntityType<?>> holder) -> {
                return new BoatDispenseItemBehavior((EntityType<? extends AbstractBoat>) holder.value());
            },
            BlockSetVariant.CHEST_BOAT,
            (Holder<EntityType<?>> holder) -> {
                return new BoatDispenseItemBehavior((EntityType<? extends AbstractBoat>) holder.value());
            });

    /**
     * Creates a new block set family from the given base block, without generating any variants.
     *
     * @param registries the registry manager used for registering generated content
     * @param baseBlock  the base block of the family, such as planks or a wool block
     * @param baseName   the base name used for naming generated content
     * @return the new block set family
     *
     * @see #stone(RegistryManager, Holder.Reference, String)
     * @see #wooden(RegistryManager, Holder.Reference, String)
     */
    static Writable base(RegistryManager registries, Holder.Reference<Block> baseBlock, String baseName) {
        BlockSetType blockSetType = new BlockSetType(registries.makeKey(baseName).toString());
        WoodType woodType = new WoodType(registries.makeKey(baseName).toString(), blockSetType);
        return new BlockSetFamilyRegistrar(registries, baseBlock, baseName, blockSetType, woodType);
    }

    /**
     * Creates a new block set family from the given base block and generates the common stone variants (stairs, slab,
     * and wall) with stonecutter recipes enabled.
     *
     * @param registries the registry manager used for registering generated content
     * @param baseBlock  the base block of the family, such as a stone block
     * @param baseName   the base name used for naming generated content
     * @return the new block set family
     */
    static Writable stone(RegistryManager registries, Holder.Reference<Block> baseBlock, String baseName) {
        return base(registries,
                baseBlock,
                baseName).configureBlockFamily(BlockFamily.Builder::generateStonecutterRecipe)
                .generateFor(BlockSetVariant.STAIRS)
                .generateFor(BlockSetVariant.SLAB)
                .generateFor(BlockSetVariant.WALL);
    }

    /**
     * Creates a new block set family from the given base block and generates the full wooden variant set, including
     * logs, wood, boats, and chest boats.
     *
     * @param registries the registry manager used for registering generated content
     * @param baseBlock  the base block of the family, such as planks
     * @param baseName   the base name used for naming generated content
     * @return the new block set family
     */
    static Writable wooden(RegistryManager registries, Holder.Reference<Block> baseBlock, String baseName) {
        return base(registries, baseBlock, baseName).configureBlockFamily((BlockFamily.Builder blockFamily) -> {
                    blockFamily.recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks");
                })
                .generateFor(BlockSetVariant.LOG)
                .generateFor(BlockSetVariant.WOOD)
                .generateFor(BlockSetVariant.STRIPPED_LOG)
                .generateFor(BlockSetVariant.STRIPPED_WOOD)
                .generateFor(BlockSetVariant.STAIRS)
                .generateFor(BlockSetVariant.SLAB)
                .generateFor(BlockSetVariant.FENCE)
                .generateFor(BlockSetVariant.FENCE_GATE)
                .generateFor(BlockSetVariant.DOOR)
                .generateFor(BlockSetVariant.TRAPDOOR)
                .generateFor(BlockSetVariant.PRESSURE_PLATE)
                .generateFor(BlockSetVariant.BUTTON)
                .generateFor(BlockSetVariant.SIGN)
                .generateFor(BlockSetVariant.HANGING_SIGN)
                .generateFor(BlockSetVariant.SHELF)
                .generateFor(BlockSetVariant.BOAT)
                .generateFor(BlockSetVariant.CHEST_BOAT);
    }

    /**
     * Returns the base block of this family.
     *
     * @return the base block
     */
    Holder.Reference<Block> getBaseBlock();

    /**
     * Returns the base name used for naming generated content.
     *
     * @return the base name
     */
    String getBaseName();

    /**
     * Returns the {@link BlockSetType} used when generating interactive blocks such as doors and buttons.
     *
     * @return the block set type
     */
    BlockSetType getBlockSetType();

    /**
     * Returns the {@link WoodType} used when generating wooden blocks such as signs.
     *
     * @return the wood type
     */
    WoodType getWoodType();

    /**
     * Returns a vanilla {@link BlockFamily} built from the generated variants, used by the data providers.
     *
     * @return the vanilla block family
     */
    BlockFamily getBlockFamily();

    /**
     * Returns all {@link BlockSetVariant BlockSetVariants} known to this family, both generated and provided.
     *
     * @return all block variants mapped by variant
     *
     * @see #getGeneratedBlockVariants()
     */
    Map<BlockSetVariant, Holder.Reference<Block>> getAllBlockVariants();

    /**
     * Returns all item variants known to this family, both generated and provided.
     *
     * @return all item variants mapped by variant
     *
     * @see #getGeneratedItemVariants()
     */
    Map<BlockSetVariant, Holder.Reference<Item>> getAllItemVariants();

    /**
     * Returns all entity variants known to this family, both generated and provided.
     *
     * @return all entity variants mapped by variant
     *
     * @see #getGeneratedEntityVariants()
     */
    Map<BlockSetVariant, Holder.Reference<EntityType<?>>> getAllEntityVariants();

    /**
     * Returns only the block variants generated by this family, excluding provided ones.
     *
     * @return the generated block variants mapped by variant
     */
    Map<BlockSetVariant, Holder.Reference<Block>> getGeneratedBlockVariants();

    /**
     * Returns only the item variants generated by this family, excluding provided ones.
     *
     * @return the generated item variants mapped by variant
     */
    Map<BlockSetVariant, Holder.Reference<Item>> getGeneratedItemVariants();

    /**
     * Returns only the entity variants generated by this family, excluding provided ones.
     *
     * @return the generated entity variants mapped by variant
     */
    Map<BlockSetVariant, Holder.Reference<EntityType<?>>> getGeneratedEntityVariants();

    /**
     * @deprecated use {@link #getGeneratedBlockVariants()}
     */
    @Deprecated
    default Map<BlockSetVariant, Holder.Reference<Block>> getBlockVariants() {
        return this.getGeneratedBlockVariants();
    }

    /**
     * @deprecated use {@link #getGeneratedItemVariants()}
     */
    @Deprecated
    default Map<BlockSetVariant, Holder.Reference<Item>> getItemVariants() {
        return this.getGeneratedItemVariants();
    }

    /**
     * @deprecated use {@link #getGeneratedEntityVariants()}
     */
    @Deprecated
    default Map<BlockSetVariant, Holder.Reference<EntityType<?>>> getEntityVariants() {
        return this.getGeneratedEntityVariants();
    }

    /**
     * Returns the block for the given variant, whether generated or provided.
     *
     * @param variant the block variant
     * @return the block, or {@code null} if this family does not have the variant
     */
    default Holder.Reference<Block> getBlock(BlockSetVariant variant) {
        return this.getAllBlockVariants().get(variant);
    }

    /**
     * Returns the item for the given variant, whether generated or provided.
     *
     * @param variant the item variant
     * @return the item, or {@code null} if this family does not have the variant
     */
    default Holder.Reference<Item> getItem(BlockSetVariant variant) {
        return this.getAllItemVariants().get(variant);
    }

    /**
     * Returns the entity type for the given variant, whether generated or provided.
     *
     * @param variant the entity variant
     * @return the entity type, or {@code null} if this family does not have the variant
     */
    default Holder.Reference<EntityType<?>> getEntityType(BlockSetVariant variant) {
        return this.getAllEntityVariants().get(variant);
    }

    /**
     * Registers the {@link BlockSetType} and {@link WoodType} of this family.
     */
    default void register() {
        BlockSetType.register(this.getBlockSetType());
        WoodType.register(this.getWoodType());
    }

    /**
     * Registers the generated blocks of this family with a block entity type via the given consumer.
     *
     * @param consumer the consumer accepting a block entity type and the block it applies to
     * @param variants the block entity types mapped by block set variant
     * @see #VARIANT_BLOCK_ENTITY_TYPE
     */
    default void registerFor(BiConsumer<BlockEntityType<?>, Block> consumer, Map<BlockSetVariant, Holder<BlockEntityType<?>>> variants) {
        this.getGeneratedBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            Holder<BlockEntityType<?>> blockEntity = variants.get(variant);
            if (blockEntity != null) {
                consumer.accept(blockEntity.value(), holder.value());
            }
        });
    }

    /**
     * @deprecated use {@link #registerFor(GameplayContentContext, Map, Map)}
     */
    @Deprecated
    default void registerFor(GameplayContentContext context, Map<BlockSetVariant, Vector2ic> flammableVariants) {
        this.registerFor(context, Map.of(), flammableVariants);
    }

    /**
     * Registers the generated items of this family as fuel and the generated blocks as flammable.
     *
     * @param context           the gameplay content context
     * @param fuelVariants      the cooking times mapped by block set variant
     * @param flammableVariants the flammability values (encouragement and flammability) mapped by block set variant
     * @see #VARIANT_WOODEN_COOKING_TIME
     * @see #VARIANT_WOODEN_FLAMMABLE
     */
    default void registerFor(GameplayContentContext context, Map<BlockSetVariant, ResourceKey<ContextIntProvider>> fuelVariants, Map<BlockSetVariant, Vector2ic> flammableVariants) {
        this.getGeneratedItemVariants().forEach((BlockSetVariant variant, Holder.Reference<Item> holder) -> {
            ResourceKey<ContextIntProvider> fuelValue = fuelVariants.get(variant);
            if (fuelValue != null) {
                context.registerFuel(holder, fuelValue);
            }
        });
        this.getGeneratedBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            Vector2ic flammable = flammableVariants.get(variant);
            if (flammable != null) {
                context.registerFlammable(holder, flammable.x(), flammable.y());
            }
        });
    }

    /**
     * Registers dispense behaviors for the generated entity variants of this family.
     *
     * @param variants the dispense behaviors mapped by block set variant
     * @see #VARIANT_DISPENSE_BEHAVIOR
     */
    default void registerFor(Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> variants) {
        this.getGeneratedEntityVariants().forEach((BlockSetVariant variant, Holder.Reference<EntityType<?>> holder) -> {
            Function<Holder<EntityType<?>>, DispenseItemBehavior> behaviorFactory = variants.get(variant);
            if (behaviorFactory != null) {
                DispenserBlock.registerBehavior(this.getItem(variant).value(), behaviorFactory.apply(holder));
            }
        });
    }

    /**
     * A mutable {@link BlockSetFamily} used while the family is being built. Generated variants are added via
     * {@link #generateFor(BlockSetVariant)}, while existing blocks can be made available for lookups via the
     * {@code provideFor} methods and {@link #provide(BlockFamily)}.
     */
    interface Writable extends BlockSetFamily {
        /**
         * Registers a generated block for the given variant. The entry is included in both the generated and all
         * variant lookups.
         *
         * @param variant the variant the block belongs to
         * @param holder  the registered block
         * @return this family instance
         */
        Writable registerBlock(BlockSetVariant variant, Holder.Reference<Block> holder);

        /**
         * Registers a generated item for the given variant. The entry is included in both the generated and all variant
         * lookups.
         *
         * @param variant the variant the item belongs to
         * @param holder  the registered item
         * @return this family instance
         */
        Writable registerItem(BlockSetVariant variant, Holder.Reference<Item> holder);

        /**
         * Registers a generated entity type for the given variant. The entry is included in both the generated and all
         * variant lookups.
         *
         * @param variant the variant the entity type belongs to
         * @param holder  the registered entity type
         * @return this family instance
         */
        Writable registerEntityType(BlockSetVariant variant, Holder.Reference<EntityType<?>> holder);

        /**
         * Provides an existing block for the given variant, making it available for lookups without generating any
         * resources for it.
         *
         * @param variant the variant the block belongs to
         * @param holder  the provided block
         * @return this family instance
         */
        Writable provideBlock(BlockSetVariant variant, Holder.Reference<Block> holder);

        /**
         * Provides an existing item for the given variant, making it available for lookups without generating any
         * resources for it.
         *
         * @param variant the variant the item belongs to
         * @param holder  the provided item
         * @return this family instance
         */
        Writable provideItem(BlockSetVariant variant, Holder.Reference<Item> holder);

        /**
         * Provides an existing entity type for the given variant, making it available for lookups without generating
         * any resources for it.
         *
         * @param variant the variant the entity type belongs to
         * @param holder  the provided entity type
         * @return this family instance
         */
        Writable provideEntityType(BlockSetVariant variant, Holder.Reference<EntityType<?>> holder);

        /**
         * @see #provideBlock(BlockSetVariant, Holder.Reference)
         */
        default Writable provideFor(BlockSetVariant variant, Block block) {
            return this.provideBlock(variant, block.builtInRegistryHolder());
        }

        /**
         * @see #provideItem(BlockSetVariant, Holder.Reference)
         */
        default Writable provideFor(BlockSetVariant variant, Item item) {
            return this.provideItem(variant, item.builtInRegistryHolder());
        }

        /**
         * @see #provideEntityType(BlockSetVariant, Holder.Reference)
         */
        default Writable provideFor(BlockSetVariant variant, EntityType<?> entityType) {
            return this.provideEntityType(variant, entityType.builtInRegistryHolder());
        }

        /**
         * Provides all variants of the given vanilla {@link BlockFamily} that have a matching {@link BlockSetVariant}.
         *
         * @param blockFamily the vanilla block family to provide variants from
         * @return this family instance
         *
         * @see BlockSetVariant#fromVanilla(BlockFamily.Variant)
         */
        default Writable provide(BlockFamily blockFamily) {
            blockFamily.getVariants().forEach((BlockFamily.Variant variant, Block block) -> {
                BlockSetVariant blockSetVariant = BlockSetVariant.fromVanilla(variant);
                if (blockSetVariant != null) {
                    this.provideFor(blockSetVariant, block);
                }
            });

            return this;
        }

        /**
         * @see #generateFor(BlockSetVariant, String)
         */
        default Writable generateFor(BlockSetVariant variant) {
            return this.generateFor(variant, null);
        }

        /**
         * Generates and registers the content of the given variant.
         *
         * @param variant          the variant to generate
         * @param baseNameOverride optional name override for the base block, or {@code null} to use the family's base
         *                         name
         * @return this family instance
         */
        Writable generateFor(BlockSetVariant variant, @Nullable String baseNameOverride);

        /**
         * Applies additional configuration to the vanilla {@link BlockFamily} built from this family's generated
         * variants.
         *
         * @param blockFamilyConsumer the consumer configuring the block family builder
         * @return this family instance
         */
        Writable configureBlockFamily(Consumer<BlockFamily.Builder> blockFamilyConsumer);
    }

    /**
     * The construction context passed to {@link BlockSetVariant BlockSetVariants} while they are generated, exposing
     * naming and registration helpers.
     */
    interface Context extends BlockSetFamily.Writable {
        /**
         * Resolves a block or item name by applying the given naming operator to the family's base name.
         *
         * @param name             the naming operator applied to the family's base name
         * @param baseNameOverride the optional name override replacing the family's base name, or {@code null}
         * @return the resolved name
         */
        String getName(UnaryOperator<String> name, @Nullable String baseNameOverride);

        /**
         * @see #getName(UnaryOperator, String)
         */
        default String getNameWithPrefix(String prefix, @Nullable String baseNameOverride) {
            return this.getName((String baseName) -> prefix + "_" + baseName, baseNameOverride);
        }

        /**
         * @see #getName(UnaryOperator, String)
         */
        default String getNameWithSuffix(String suffix, @Nullable String baseNameOverride) {
            return this.getName((String baseName) -> baseName + "_" + suffix, baseNameOverride);
        }

        /**
         * Returns the registry manager used for registering generated content.
         *
         * @return the registry manager
         */
        RegistryManager getRegistries();
    }
}
