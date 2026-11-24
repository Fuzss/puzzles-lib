package fuzs.puzzleslib.common.api.init.v3.family;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.BlockFamily;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.jspecify.annotations.Nullable;

/**
 * A single block variant of a {@link BlockSetFamily}, such as a stair, slab, or sign, that can be generated alongside
 * the family's base block.
 * <p>
 * Variants either mirror a vanilla {@link BlockFamily.Variant} or are {@link StandaloneBlockSetVariant standalone} when
 * no vanilla equivalent exists (for example, shelves and boats). They are added to a family by generating them via
 * {@link BlockSetFamily.Writable#generateFor(BlockSetVariant)}.
 * <p>
 * A variant's identity is its {@link #getSerializedName() serialized name}: variants with the same name must be
 * considered equal and can be used interchangeably as keys in the maps used by registration and the data providers.
 * Extending {@link VanillaBlockSetVariant} or {@link StandaloneBlockSetVariant} provides this equality. A custom
 * variant may therefore reuse the resources of an existing variant by reporting the same name, while supplying
 * different blocks or items. Variants backed by a vanilla variant should extend {@link VanillaBlockSetVariant}, as only
 * those are included when a vanilla {@link BlockFamily} is built.
 */
public interface BlockSetVariant extends StringRepresentable {
    /**
     * A chiseled block.
     */
    BlockSetVariant CHISELED = new VanillaBlockSetVariant(BlockFamily.Variant.CHISELED, BlockFamily.Builder::chiseled) {
        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(this.getSerializedName(), baseNameOverride);
        }
    };
    /**
     * A cracked block.
     */
    BlockSetVariant CRACKED = new VanillaBlockSetVariant(BlockFamily.Variant.CRACKED, BlockFamily.Builder::cracked) {
        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(this.getSerializedName(), baseNameOverride);
        }
    };
    /**
     * A cut block.
     */
    BlockSetVariant CUT = new VanillaBlockSetVariant(BlockFamily.Variant.CUT, BlockFamily.Builder::cut) {
        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(this.getSerializedName(), baseNameOverride);
        }
    };
    /**
     * A mosaic block.
     */
    BlockSetVariant MOSAIC = new VanillaBlockSetVariant(BlockFamily.Variant.MOSAIC, BlockFamily.Builder::mosaic);
    /**
     * A polished block.
     */
    BlockSetVariant POLISHED = new VanillaBlockSetVariant(BlockFamily.Variant.POLISHED, BlockFamily.Builder::polished) {
        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(this.getSerializedName(), baseNameOverride);
        }
    };
    /**
     * A brick block.
     */
    BlockSetVariant BRICKS = new VanillaBlockSetVariant(BlockFamily.Variant.BRICKS, BlockFamily.Builder::bricks);
    /**
     * A cobbled block.
     */
    BlockSetVariant COBBLED = new VanillaBlockSetVariant(BlockFamily.Variant.COBBLED, BlockFamily.Builder::cobbled) {
        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(this.getSerializedName(), baseNameOverride);
        }
    };
    /**
     * A tile block.
     */
    BlockSetVariant TILES = new VanillaBlockSetVariant(BlockFamily.Variant.TILES, BlockFamily.Builder::tiles);
    /**
     * A pillar block.
     */
    BlockSetVariant PILLAR = new VanillaBlockSetVariant(BlockFamily.Variant.PILLAR, BlockFamily.Builder::pillar) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), RotatedPillarBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                            }));
        }
    };
    /**
     * A log block.
     */
    BlockSetVariant LOG = new VanillaBlockSetVariant(BlockFamily.Variant.LOG, BlockFamily.Builder::log) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), RotatedPillarBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                        .strength(2.0F);
                            }));
        }
    };
    /**
     * A six-sided wood block.
     */
    BlockSetVariant WOOD = new StandaloneBlockSetVariant("wood") {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), RotatedPillarBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                        .strength(2.0F);
                            }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    /**
     * A stripped log block.
     */
    BlockSetVariant STRIPPED_LOG = new VanillaBlockSetVariant(BlockFamily.Variant.STRIPPED_LOG,
            BlockFamily.Builder::strippedLog) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), RotatedPillarBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                        .strength(2.0F);
                            }));
        }

        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getName((String baseName) -> "stripped_" + baseName + "_log", baseNameOverride);
        }
    };
    /**
     * A stripped six-sided wood block.
     */
    BlockSetVariant STRIPPED_WOOD = new StandaloneBlockSetVariant("stripped_wood") {
        @Override
        public String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            return context.getName((String baseName) -> "stripped_" + baseName + "_wood", baseNameOverride);
        }

        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), RotatedPillarBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                        .strength(2.0F);
                            }));
            context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
        }
    };
    /**
     * A stair block.
     */
    BlockSetVariant STAIRS = new VanillaBlockSetVariant(BlockFamily.Variant.STAIRS, BlockFamily.Builder::stairs) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new StairBlock(context.getBaseBlock()
                                            .value()
                                            .defaultBlockState(), properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofLegacyCopy(context.getBaseBlock().value());
                                    }));
        }
    };
    /**
     * A slab block.
     */
    BlockSetVariant SLAB = new VanillaBlockSetVariant(BlockFamily.Variant.SLAB, BlockFamily.Builder::slab) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), SlabBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                            }));
        }
    };
    /**
     * A wall block.
     */
    BlockSetVariant WALL = new VanillaBlockSetVariant(BlockFamily.Variant.WALL, BlockFamily.Builder::wall) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), WallBlock::new, () -> {
                                return BlockBehaviour.Properties.ofLegacyCopy(context.getBaseBlock().value())
                                        .forceSolidOn();
                            }));
        }
    };
    /**
     * A fence block.
     */
    BlockSetVariant FENCE = new VanillaBlockSetVariant(BlockFamily.Variant.FENCE, BlockFamily.Builder::fence) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), FenceBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                            }));
        }
    };
    /**
     * A fence gate block.
     */
    BlockSetVariant FENCE_GATE = new VanillaBlockSetVariant(BlockFamily.Variant.FENCE_GATE,
            BlockFamily.Builder::fenceGate) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new FenceGateBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn();
                                    }));
        }
    };
    /**
     * A door block.
     */
    BlockSetVariant DOOR = new VanillaBlockSetVariant(BlockFamily.Variant.DOOR, BlockFamily.Builder::door) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new DoorBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noOcclusion()
                                                .pushReaction(PushReaction.POPPED);
                                    }));
        }

        @Override
        public void registerItem(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerItem(this,
                    context.getRegistries().registerBlockItem(context.getBlock(this), DoubleHighBlockItem::new));
        }
    };
    /**
     * A trapdoor block.
     */
    BlockSetVariant TRAPDOOR = new VanillaBlockSetVariant(BlockFamily.Variant.TRAPDOOR, BlockFamily.Builder::trapdoor) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new TrapDoorBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noOcclusion()
                                                .isValidSpawn(Blocks::never);
                                    }));
        }
    };
    /**
     * A button block.
     */
    BlockSetVariant BUTTON = new VanillaBlockSetVariant(BlockFamily.Variant.BUTTON, BlockFamily.Builder::button) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new ButtonBlock(context.getBlockSetType(),
                                            30,
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .noCollision()
                                                .pushReaction(PushReaction.POPPED);
                                    }));
        }
    };
    /**
     * A pressure plate block.
     */
    BlockSetVariant PRESSURE_PLATE = new VanillaBlockSetVariant(BlockFamily.Variant.PRESSURE_PLATE,
            BlockFamily.Builder::pressurePlate) {
        @Override
        public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new PressurePlateBlock(context.getBlockSetType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision()
                                                .pushReaction(PushReaction.POPPED);
                                    }));
        }
    };
    /**
     * A standing sign block. Also registers the matching {@link #WALL_SIGN wall sign}.
     */
    BlockSetVariant SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new StandingSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            Holder<Block> signHolder = context.getBlock(this);
            context.registerBlock(WALL_SIGN,
                    context.getRegistries()
                            .registerBlock(WALL_SIGN.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new WallSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .overrideLootTable(signHolder.value().getLootTable())
                                                .overrideDescription(signHolder.value().getDescriptionId())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            context.registerItem(this,
                    context.getRegistries()
                            .registerBlockItem(signHolder,
                                    (Block block, Item.Properties properties) -> new StandingAndWallBlockItem(block,
                                            context.getBlock(WALL_SIGN).value(),
                                            Direction.DOWN,
                                            properties),
                                    () -> new Item.Properties().stacksTo(16).signText()));
        }
    };
    /**
     * The wall sign belonging to {@link #SIGN}. Registered together with it and not meant to be generated on its own.
     */
    BlockSetVariant WALL_SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.WALL_SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            throw new UnsupportedOperationException();
        }
    };
    /**
     * A hanging sign block. Also registers the matching {@link #WALL_HANGING_SIGN wall hanging sign}.
     */
    BlockSetVariant HANGING_SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.HANGING_SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new CeilingHangingSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            Holder<Block> hangingSignHolder = context.getBlock(this);
            context.registerBlock(WALL_HANGING_SIGN,
                    context.getRegistries()
                            .registerBlock(WALL_HANGING_SIGN.getName(context, baseNameOverride),
                                    (BlockBehaviour.Properties properties) -> new WallHangingSignBlock(context.getWoodType(),
                                            properties),
                                    () -> {
                                        return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                                .overrideLootTable(hangingSignHolder.value().getLootTable())
                                                .overrideDescription(hangingSignHolder.value().getDescriptionId())
                                                .forceSolidOn()
                                                .noCollision();
                                    }));
            context.registerItem(this,
                    context.getRegistries()
                            .registerBlockItem(hangingSignHolder,
                                    (Block block, Item.Properties properties) -> new HangingSignItem(block,
                                            context.getBlock(WALL_HANGING_SIGN).value(),
                                            properties),
                                    () -> new Item.Properties().stacksTo(16)));
        }
    };
    /**
     * The wall hanging sign belonging to {@link #HANGING_SIGN}. Registered together with it and not meant to be
     * generated on its own.
     */
    BlockSetVariant WALL_HANGING_SIGN = new StandaloneBlockSetVariant(BlockFamily.Variant.WALL_HANGING_SIGN) {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            throw new UnsupportedOperationException();
        }
    };
    /**
     * A shelf block.
     */
    BlockSetVariant SHELF = new StandaloneBlockSetVariant("shelf") {
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerBlock(this,
                    context.getRegistries()
                            .registerBlock(this.getName(context, baseNameOverride), ShelfBlock::new, () -> {
                                return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value())
                                        .sound(SoundType.SHELF);
                            }));
            context.registerItem(this,
                    context.getRegistries()
                            .registerBlockItem(context.getBlock(this),
                                    () -> new Item.Properties().component(DataComponents.CONTAINER,
                                            ItemContainerContents.EMPTY)));
        }
    };
    /**
     * A boat entity and its matching item.
     */
    BlockSetVariant BOAT = new StandaloneBlockSetVariant("boat") {
        @SuppressWarnings("unchecked")
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerEntityType(this,
                    (Holder.Reference<EntityType<?>>) (Holder.Reference<?>) context.getRegistries()
                            .registerEntityType(this.getName(context, baseNameOverride),
                                    () -> EntityType.Builder.of((EntityType<Boat> entityType, Level level) -> {
                                                return new Boat(entityType, level, () -> context.getItem(this).value());
                                            }, MobCategory.MISC)
                                            .noLootTable()
                                            .sized(1.375F, 0.5625F)
                                            .eyeHeight(0.5625F)
                                            .clientTrackingRange(10)));
            context.registerItem(this,
                    context.getRegistries()
                            .registerItem(this.getName(context, baseNameOverride),
                                    (Item.Properties properties) -> new BoatItem((EntityType<? extends AbstractBoat>) context.getEntityType(
                                            this).value(), properties),
                                    () -> new Item.Properties().stacksTo(1)));
        }
    };
    /**
     * A chest boat entity and its matching item.
     */
    BlockSetVariant CHEST_BOAT = new StandaloneBlockSetVariant("chest_boat") {
        @SuppressWarnings("unchecked")
        @Override
        public void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
            context.registerEntityType(this,
                    (Holder.Reference<EntityType<?>>) (Holder.Reference<?>) context.getRegistries()
                            .registerEntityType(this.getName(context, baseNameOverride),
                                    () -> EntityType.Builder.of((EntityType<ChestBoat> entityType, Level level) -> {
                                                return new ChestBoat(entityType, level, () -> context.getItem(this).value());
                                            }, MobCategory.MISC)
                                            .noLootTable()
                                            .sized(1.375F, 0.5625F)
                                            .eyeHeight(0.5625F)
                                            .clientTrackingRange(10)));
            context.registerItem(this,
                    context.getRegistries()
                            .registerItem(this.getName(context, baseNameOverride),
                                    (Item.Properties properties) -> new BoatItem((EntityType<? extends AbstractBoat>) context.getEntityType(
                                            this).value(), properties),
                                    () -> new Item.Properties().stacksTo(1)));
        }
    };

    /**
     * Generates and registers the blocks, items, and entities belonging to this variant.
     *
     * @param context          the family context used for registering the variant
     * @param baseNameOverride optional name override for the base block, or {@code null} to use the family's base name
     * @see BlockSetFamily.Writable#generateFor(BlockSetVariant)
     */
    void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride);

    /**
     * Resolves the registered name of this variant's blocks and items from the family's base name.
     * <p>
     * By default, the serialized name is appended to the base name.
     *
     * @param context the family context
     * @return the resolved name
     */
    default String getName(BlockSetFamily.Context context) {
        return this.getName(context, null);
    }

    /**
     * Resolves the registered name of this variant's blocks and items from the family's base name.
     * <p>
     * By default, the serialized name is appended to the base name.
     *
     * @param context          the family context
     * @param baseNameOverride optional name override for the base block, or {@code null} to use the family's base name
     * @return the resolved name
     */
    default String getName(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        return context.getNameWithSuffix(this.getSerializedName(), baseNameOverride);
    }

    interface Vanilla extends BlockSetVariant {
        /**
         * Returns the vanilla {@link BlockFamily.Variant} this variant corresponds to.
         *
         * @return the vanilla variant, or {@code null} if this variant has no vanilla equivalent
         *
         * @see #fromVanilla(BlockFamily.Variant)
         */
        BlockFamily.Variant toVanilla();

        /**
         * Adds the given block to the given vanilla block family builder using this variant's builder.
         *
         * @param blockFamily the block family builder
         * @param block       the block to add
         */
        void configureBlockFamily(BlockFamily.Builder blockFamily, Block block);

        /**
         * Retrieves the {@link BlockSetVariant} matching a vanilla {@link BlockFamily.Variant}.
         *
         * @param variant the vanilla variant to look up
         * @return the matching block set variant, or {@code null} if there is no equivalent
         */
        static @Nullable BlockSetVariant fromVanilla(BlockFamily.Variant variant) {
            return switch (variant) {
                case BUTTON -> BUTTON;
                case CHISELED -> CHISELED;
                case CRACKED -> CRACKED;
                case CUT -> CUT;
                case DOOR -> DOOR;
                case CUSTOM_FENCE, FENCE -> FENCE;
                case CUSTOM_FENCE_GATE, FENCE_GATE -> FENCE_GATE;
                case CUSTOM_HANGING_SIGN, HANGING_SIGN -> HANGING_SIGN;
                case LOG -> LOG;
                case STRIPPED_LOG -> STRIPPED_LOG;
                case MOSAIC -> MOSAIC;
                case SIGN -> SIGN;
                case SLAB -> SLAB;
                case STAIRS -> STAIRS;
                case PRESSURE_PLATE -> PRESSURE_PLATE;
                case POLISHED -> POLISHED;
                case TRAPDOOR -> TRAPDOOR;
                case WALL -> WALL;
                case WALL_SIGN -> WALL_SIGN;
                case CUSTOM_WALL_HANGING_SIGN, WALL_HANGING_SIGN -> WALL_HANGING_SIGN;
                case BRICKS -> BRICKS;
                case COBBLED -> COBBLED;
                case TILES -> TILES;
                case PILLAR -> PILLAR;
                default -> null;
            };
        }
    }
}
