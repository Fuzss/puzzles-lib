package fuzs.puzzleslib.common.api.data.v3.tags;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.common.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.common.impl.data.SortingTagBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A base implementation of {@link TagsProvider} for generating tags for the mod.
 * <p>
 * Tags of the mod's namespace must be defined by this provider, while tags of other namespaces are always considered
 * present, so they can be referenced without being generated here. Tag builders are created as
 * {@link SortingTagBuilder} instances to yield consistent output, and are exposed as {@link AbstractTagAppender}
 * instances which additionally support removals.
 *
 * @param <T> the type of value the tags are for
 */
public abstract class AbstractTagsProvider<T> extends TagsProvider<T> {
    /**
     * The default block tags for the common {@link BlockSetVariant BlockSetVariants}.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .put(BlockSetVariant.BUTTON, BlockItemTags.BUTTONS.block())
            .put(BlockSetVariant.DOOR, BlockItemTags.DOORS.block())
            .put(BlockSetVariant.FENCE, BlockItemTags.FENCES.block())
            .put(BlockSetVariant.FENCE_GATE, BlockItemTags.FENCE_GATES.block())
            .put(BlockSetVariant.SIGN, BlockTags.STANDING_SIGNS)
            .put(BlockSetVariant.SLAB, BlockItemTags.SLABS.block())
            .put(BlockSetVariant.STAIRS, BlockItemTags.STAIRS.block())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.PRESSURE_PLATES)
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.TRAPDOORS.block())
            .put(BlockSetVariant.WALL, BlockItemTags.WALLS.block())
            .put(BlockSetVariant.WALL_SIGN, BlockTags.WALL_SIGNS)
            .put(BlockSetVariant.HANGING_SIGN, BlockTags.CEILING_HANGING_SIGNS)
            .put(BlockSetVariant.WALL_HANGING_SIGN, BlockTags.WALL_HANGING_SIGNS)
            .build();
    /**
     * The {@link #VARIANT_BLOCK_TAGS} extended by stone-specific block tags.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_STONE_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .putAll(VARIANT_BLOCK_TAGS)
            .put(BlockSetVariant.BUTTON, BlockItemTags.STONE_BUTTONS.block())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.STONE_PRESSURE_PLATES)
            .buildKeepingLast();
    /**
     * The {@link #VARIANT_BLOCK_TAGS} extended by wooden block tags.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_WOODEN_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .putAll(VARIANT_BLOCK_TAGS)
            .put(BlockSetVariant.LOG, TagFactory.COMMON.registerBlockTag("natural_logs"))
            .put(BlockSetVariant.WOOD, TagFactory.COMMON.registerBlockTag("natural_woods"))
            .put(BlockSetVariant.STRIPPED_LOG, TagFactory.COMMON.registerBlockTag("stripped_logs"))
            .put(BlockSetVariant.STRIPPED_WOOD, TagFactory.COMMON.registerBlockTag("stripped_woods"))
            .put(BlockSetVariant.BUTTON, BlockItemTags.WOODEN_BUTTONS.block())
            .put(BlockSetVariant.DOOR, BlockItemTags.WOODEN_DOORS.block())
            .put(BlockSetVariant.FENCE, BlockItemTags.WOODEN_FENCES.block())
            .put(BlockSetVariant.SLAB, BlockItemTags.WOODEN_SLABS.block())
            .put(BlockSetVariant.STAIRS, BlockItemTags.WOODEN_STAIRS.block())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockItemTags.WOODEN_PRESSURE_PLATES.block())
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.WOODEN_TRAPDOORS.block())
            .put(BlockSetVariant.SHELF, BlockItemTags.WOODEN_SHELVES.block())
            .buildKeepingLast();
    /**
     * The default item tags for the common {@link BlockSetVariant BlockSetVariants}.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .put(BlockSetVariant.BUTTON, BlockItemTags.BUTTONS.item())
            .put(BlockSetVariant.DOOR, BlockItemTags.DOORS.item())
            .put(BlockSetVariant.FENCE, BlockItemTags.FENCES.item())
            .put(BlockSetVariant.FENCE_GATE, BlockItemTags.FENCE_GATES.item())
            .put(BlockSetVariant.SLAB, BlockItemTags.SLABS.item())
            .put(BlockSetVariant.STAIRS, BlockItemTags.STAIRS.item())
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.TRAPDOORS.item())
            .put(BlockSetVariant.WALL, BlockItemTags.WALLS.item())
            .put(BlockSetVariant.SIGN, BlockItemTags.SIGNS.item())
            .put(BlockSetVariant.HANGING_SIGN, BlockItemTags.HANGING_SIGNS.item())
            .put(BlockSetVariant.BOAT, ItemTags.BOATS)
            .put(BlockSetVariant.CHEST_BOAT, ItemTags.CHEST_BOATS)
            .build();
    /**
     * The {@link #VARIANT_ITEM_TAGS} extended by stone-specific item tags.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_STONE_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .putAll(VARIANT_ITEM_TAGS)
            .put(BlockSetVariant.BUTTON, BlockItemTags.STONE_BUTTONS.item())
            .buildKeepingLast();
    /**
     * The {@link #VARIANT_ITEM_TAGS} extended by wooden item tags.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_WOODEN_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .putAll(VARIANT_ITEM_TAGS)
            .put(BlockSetVariant.LOG, TagFactory.COMMON.registerItemTag("natural_logs"))
            .put(BlockSetVariant.WOOD, TagFactory.COMMON.registerItemTag("natural_woods"))
            .put(BlockSetVariant.STRIPPED_LOG, TagFactory.COMMON.registerItemTag("stripped_logs"))
            .put(BlockSetVariant.STRIPPED_WOOD, TagFactory.COMMON.registerItemTag("stripped_woods"))
            .put(BlockSetVariant.BUTTON, BlockItemTags.WOODEN_BUTTONS.item())
            .put(BlockSetVariant.DOOR, BlockItemTags.WOODEN_DOORS.item())
            .put(BlockSetVariant.FENCE, BlockItemTags.WOODEN_FENCES.item())
            .put(BlockSetVariant.SLAB, BlockItemTags.WOODEN_SLABS.item())
            .put(BlockSetVariant.STAIRS, BlockItemTags.WOODEN_STAIRS.item())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockItemTags.WOODEN_PRESSURE_PLATES.item())
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.WOODEN_TRAPDOORS.item())
            .put(BlockSetVariant.SHELF, BlockItemTags.WOODEN_SHELVES.item())
            .buildKeepingLast();
    /**
     * The default entity type tags for the common {@link BlockSetVariant BlockSetVariants}.
     *
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<EntityType<?>>> VARIANT_ENTITY_TYPE_TAGS = ImmutableMap.<BlockSetVariant, TagKey<EntityType<?>>>builder()
            .put(BlockSetVariant.BOAT, EntityTypeTags.BOAT)
            .put(BlockSetVariant.CHEST_BOAT, TagFactory.COMMON.registerEntityTypeTag("boats"))
            .build();

    /**
     * @param registryKey the registry key of the elements tags are generated for
     * @param context     the data provider context
     */
    public AbstractTagsProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    /**
     * @param registryKey the registry key of the elements tags are generated for
     * @param modId       the mod id used for determining whether a tag must be generated
     * @param packOutput  the pack output
     * @param registries  the registries used for looking up elements
     */
    public AbstractTagsProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries, CompletableFuture.completedFuture((TagKey<T> tagKey) -> {
            return Objects.equals(tagKey.location().getNamespace(), modId) ? Optional.empty() :
                    Optional.of(TagBuilder.create());
        }));
    }

    /**
     * Adds all tags of this provider via the various {@link #tag} methods, which are then emitted by
     * {@link TagsProvider#run}.
     */
    @Override
    public abstract void addTags(HolderLookup.Provider context);

    /**
     * Creates a {@link SortingTagBuilder}, so tag entries are sorted for consistent output.
     */
    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
        return this.builders.computeIfAbsent(tag.location(), (Identifier id) -> new SortingTagBuilder());
    }

    /**
     * @see #tag(Identifier)
     */
    public AbstractTagAppender<T> tag(String id) {
        return this.tag(Identifier.parse(id));
    }

    /**
     * @see #tag(Identifier, boolean)
     */
    public AbstractTagAppender<T> tag(String id, boolean replace) {
        return this.tag(Identifier.parse(id), replace);
    }

    /**
     * Gets the appender for the tag with the given id, creating it if necessary.
     *
     * @param id the tag id
     * @return the tag appender
     */
    public AbstractTagAppender<T> tag(Identifier id) {
        return this.tag(TagKey.create(this.registryKey, id));
    }

    /**
     * Gets the appender for the tag with the given id, creating it if necessary, and sets whether it replaces the
     * parent tag.
     *
     * @param id      the tag id
     * @param replace if the tag replaces the parent tag
     * @return the tag appender
     */
    public AbstractTagAppender<T> tag(Identifier id, boolean replace) {
        return this.tag(TagKey.create(this.registryKey, id), replace);
    }

    /**
     * Gets the appender for the given tag, creating it if necessary.
     *
     * @param tag the tag key
     * @return the tag appender
     */
    @Override
    public AbstractTagAppender<T> tag(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return KeyedValueProvider.tags(builder);
    }

    /**
     * Gets the appender for the given tag, creating it if necessary, and sets whether it replaces the parent tag.
     *
     * @param tag     the tag key
     * @param replace if the tag replaces the parent tag
     * @return the tag appender
     */
    @Override
    public AbstractTagAppender<T> tag(TagKey<T> tag, boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        builder.setReplace(replace);
        return KeyedValueProvider.tags(builder);
    }

    /**
     * Adds the given holders to their corresponding variant tags.
     *
     * @param variants    the holders mapped by block set variant
     * @param variantTags the tags mapped by block set variant
     */
    public final void generateFor(Map<BlockSetVariant, Holder.Reference<T>> variants, Map<BlockSetVariant, TagKey<T>> variantTags) {
        variants.forEach((BlockSetVariant variant, Holder.Reference<T> holder) -> {
            TagKey<T> tag = variantTags.get(variant);
            if (tag != null) {
                this.tag(tag).add(holder);
            }
        });
    }
}
