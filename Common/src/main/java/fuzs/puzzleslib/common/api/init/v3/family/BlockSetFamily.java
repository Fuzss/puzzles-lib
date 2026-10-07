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

public interface BlockSetFamily {
    /**
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

    static Writable base(RegistryManager registries, Holder.Reference<Block> baseBlock, String baseName) {
        BlockSetType blockSetType = new BlockSetType(registries.makeKey(baseName).toString());
        WoodType woodType = new WoodType(registries.makeKey(baseName).toString(), blockSetType);
        return new BlockSetFamilyRegistrar(registries, baseBlock, baseName, blockSetType, woodType);
    }

    static Writable stone(RegistryManager registries, Holder.Reference<Block> baseBlock, String baseName) {
        return base(registries,
                baseBlock,
                baseName).configureBlockFamily(BlockFamily.Builder::generateStonecutterRecipe)
                .generateFor(BlockSetVariant.STAIRS)
                .generateFor(BlockSetVariant.SLAB)
                .generateFor(BlockSetVariant.WALL);
    }

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

    Holder.Reference<Block> getBaseBlock();

    String getBaseName();

    BlockSetType getBlockSetType();

    WoodType getWoodType();

    BlockFamily getBlockFamily();

    Map<BlockSetVariant, Holder.Reference<Block>> getAllBlockVariants();

    Map<BlockSetVariant, Holder.Reference<Item>> getAllItemVariants();

    Map<BlockSetVariant, Holder.Reference<EntityType<?>>> getAllEntityVariants();

    Map<BlockSetVariant, Holder.Reference<Block>> getGeneratedBlockVariants();

    Map<BlockSetVariant, Holder.Reference<Item>> getGeneratedItemVariants();

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

    default Holder.Reference<Block> getBlock(BlockSetVariant variant) {
        return this.getAllBlockVariants().get(variant);
    }

    default Holder.Reference<Item> getItem(BlockSetVariant variant) {
        return this.getAllItemVariants().get(variant);
    }

    default Holder.Reference<EntityType<?>> getEntityType(BlockSetVariant variant) {
        return this.getAllEntityVariants().get(variant);
    }

    default void register() {
        BlockSetType.register(this.getBlockSetType());
        WoodType.register(this.getWoodType());
    }

    default void registerFor(BiConsumer<BlockEntityType<?>, Block> consumer, Map<BlockSetVariant, Holder<BlockEntityType<?>>> variants) {
        this.getGeneratedBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
            Holder<BlockEntityType<?>> blockEntity = variants.get(variant);
            if (blockEntity != null) {
                consumer.accept(blockEntity.value(), holder.value());
            }
        });
    }

    @Deprecated
    default void registerFor(GameplayContentContext context, Map<BlockSetVariant, Vector2ic> flammableVariants) {
        this.registerFor(context, Map.of(), flammableVariants);
    }

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

    default void registerFor(Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> variants) {
        this.getGeneratedEntityVariants().forEach((BlockSetVariant variant, Holder.Reference<EntityType<?>> holder) -> {
            Function<Holder<EntityType<?>>, DispenseItemBehavior> behaviorFactory = variants.get(variant);
            if (behaviorFactory != null) {
                DispenserBlock.registerBehavior(this.getItem(variant).value(), behaviorFactory.apply(holder));
            }
        });
    }

    interface Writable extends BlockSetFamily {
        Writable registerBlock(BlockSetVariant variant, Holder.Reference<Block> holder);

        Writable registerItem(BlockSetVariant variant, Holder.Reference<Item> holder);

        Writable registerEntityType(BlockSetVariant variant, Holder.Reference<EntityType<?>> holder);

        Writable provideBlock(BlockSetVariant variant, Holder.Reference<Block> holder);

        Writable provideItem(BlockSetVariant variant, Holder.Reference<Item> holder);

        Writable provideEntityType(BlockSetVariant variant, Holder.Reference<EntityType<?>> holder);

        default Writable provideFor(BlockSetVariant variant, Block block) {
            return this.provideBlock(variant, block.builtInRegistryHolder());
        }

        default Writable provideFor(BlockSetVariant variant, Item item) {
            return this.provideItem(variant, item.builtInRegistryHolder());
        }

        default Writable provideFor(BlockSetVariant variant, EntityType<?> entityType) {
            return this.provideEntityType(variant, entityType.builtInRegistryHolder());
        }

        default Writable provide(BlockFamily blockFamily) {
            blockFamily.getVariants().forEach((BlockFamily.Variant variant, Block block) -> {
                BlockSetVariant blockSetVariant = BlockSetVariant.fromVanilla(variant);
                if (blockSetVariant != null) {
                    this.provideFor(blockSetVariant, block);
                }
            });

            return this;
        }

        default Writable generateFor(BlockSetVariant variant) {
            return this.generateFor(variant, null);
        }

        Writable generateFor(BlockSetVariant variant, @Nullable String baseNameOverride);

        Writable configureBlockFamily(Consumer<BlockFamily.Builder> blockFamilyConsumer);
    }

    interface Context extends BlockSetFamily.Writable {
        String getName(UnaryOperator<String> name, @Nullable String baseNameOverride);

        default String getNameWithPrefix(String prefix, @Nullable String baseNameOverride) {
            return this.getName((String baseName) -> prefix + "_" + baseName, baseNameOverride);
        }

        default String getNameWithSuffix(String suffix, @Nullable String baseNameOverride) {
            return this.getName((String baseName) -> baseName + "_" + suffix, baseNameOverride);
        }

        RegistryManager getRegistries();
    }
}
