package fuzs.puzzleslib.common.api.data.v3.tags;

import net.minecraft.core.Holder;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * A base implementation of {@link TagAppender} for adding entries to and removing entries from a tag.
 * <p>
 * In addition to the vanilla methods, this offers overloads accepting {@link Identifier} and {@link Holder.Reference},
 * and removal methods supported by the loader-specific implementations. Removals are prefixed with {@code !} when
 * converted to a string list via {@link #asStringList()}.
 *
 * @param <T> the type of value the tag is for
 */
public abstract class AbstractTagAppender<T> implements TagAppender<T> {
    /**
     * The tag builder entries are added to.
     */
    protected final TagBuilder builder;

    /**
     * @param builder the tag builder entries are added to
     */
    public AbstractTagAppender(TagBuilder builder) {
        this.builder = builder;
    }

    /**
     * Adds the entry with the given id to the tag.
     *
     * @param id the entry id
     * @return this appender instance
     */
    public AbstractTagAppender<T> add(Identifier id) {
        this.builder.addElement(id);
        return this;
    }

    /**
     * @see #add(Identifier)
     */
    public AbstractTagAppender<T> add(Identifier... ids) {
        for (Identifier id : ids) {
            this.add(id);
        }

        return this;
    }

    /**
     * Adds the entry referenced by the given key to the tag.
     *
     * @param key the entry key
     * @return this appender instance
     */
    @Override
    public AbstractTagAppender<T> add(ResourceKey<T> key) {
        return this.add(key.identifier());
    }

    /**
     * @see #add(ResourceKey)
     */
    @Override
    @SafeVarargs
    public final AbstractTagAppender<T> add(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.add(key);
        }

        return this;
    }

    /**
     * Adds all entries referenced by the given keys to the tag.
     *
     * @param keys the entry keys
     * @return this appender instance
     */
    @Override
    public AbstractTagAppender<T> addAll(Collection<ResourceKey<T>> keys) {
        keys.forEach(this::add);
        return this;
    }

    /**
     * @see #addAll(Collection)
     */
    @Override
    public AbstractTagAppender<T> addAll(Stream<ResourceKey<T>> keys) {
        keys.forEach(this::add);
        return this;
    }

    /**
     * Adds the entry referenced by the given holder to the tag.
     *
     * @param holder the entry holder
     * @return this appender instance
     */
    public AbstractTagAppender<T> add(Holder.Reference<? extends T> holder) {
        return this.add((ResourceKey<T>) holder.key());
    }

    /**
     * @see #add(Holder.Reference)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> add(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.add(holder);
        }

        return this;
    }

    /**
     * Adds the optional entry with the given id to the tag, which is skipped when it cannot be resolved.
     *
     * @param id the entry id
     * @return this appender instance
     */
    public AbstractTagAppender<T> addOptional(Identifier id) {
        this.builder.addOptionalElement(id);
        return this;
    }

    /**
     * @see #addOptional(Identifier)
     */
    public AbstractTagAppender<T> addOptional(String id) {
        return this.addOptional(Identifier.parse(id));
    }

    /**
     * @see #addOptional(Identifier)
     */
    public AbstractTagAppender<T> addOptional(String... ids) {
        for (String id : ids) {
            this.addOptional(id);
        }

        return this;
    }

    /**
     * @see #addOptional(Identifier)
     */
    public AbstractTagAppender<T> addOptional(Identifier... ids) {
        for (Identifier id : ids) {
            this.addOptional(id);
        }

        return this;
    }

    /**
     * Adds the optional entry referenced by the given key to the tag, which is skipped when it cannot be resolved.
     *
     * @param key the entry key
     * @return this appender instance
     */
    @Override
    public AbstractTagAppender<T> addOptional(ResourceKey<T> key) {
        return this.addOptional(key.identifier());
    }

    /**
     * @see #addOptional(ResourceKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.addOptional(key);
        }

        return this;
    }

    /**
     * Adds all optional entries referenced by the given keys to the tag, which are skipped when they cannot be resolved.
     *
     * @param keys the entry keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> addAllOptional(Collection<ResourceKey<T>> keys) {
        keys.forEach(this::addOptional);
        return this;
    }

    /**
     * @see #addAllOptional(Collection)
     */
    public AbstractTagAppender<T> addAllOptional(Stream<ResourceKey<T>> keys) {
        keys.forEach(this::addOptional);
        return this;
    }

    /**
     * Adds the optional entry referenced by the given holder to the tag, which is skipped when it cannot be resolved.
     *
     * @param holder the entry holder
     * @return this appender instance
     */
    public AbstractTagAppender<T> addOptional(Holder.Reference<? extends T> holder) {
        return this.addOptional((ResourceKey<T>) holder.key());
    }

    /**
     * @see #addOptional(Holder.Reference)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> addOptional(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.addOptional(holder);
        }

        return this;
    }

    /**
     * Adds the referenced tag with the given id to the tag, so all of its entries are included.
     *
     * @param id the tag id
     * @return this appender instance
     */
    public AbstractTagAppender<T> addTag(Identifier id) {
        this.builder.addTag(id);
        return this;
    }

    /**
     * @see #addTag(Identifier)
     */
    public AbstractTagAppender<T> addTag(Identifier... ids) {
        for (Identifier id : ids) {
            this.addTag(id);
        }

        return this;
    }

    /**
     * Adds the given referenced tag to the tag, so all of its entries are included.
     *
     * @param tag the tag key
     * @return this appender instance
     */
    @Override
    public AbstractTagAppender<T> addTag(TagKey<T> tag) {
        return this.addTag(tag.location());
    }

    /**
     * @see #addTag(TagKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> addTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.addTag(tag);
        }

        return this;
    }

    /**
     * Adds all referenced tags with the given keys to the tag, so all of their entries are included.
     *
     * @param keys the tag keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> addAllTags(Collection<TagKey<T>> keys) {
        keys.forEach(this::addTag);
        return this;
    }

    /**
     * @see #addAllTags(Collection)
     */
    public AbstractTagAppender<T> addAllTags(Stream<TagKey<T>> keys) {
        keys.forEach(this::addTag);
        return this;
    }

    /**
     * Adds the optional referenced tag with the given id to the tag, which is skipped when it cannot be resolved.
     *
     * @param id the tag id
     * @return this appender instance
     */
    public AbstractTagAppender<T> addOptionalTag(Identifier id) {
        this.builder.addOptionalTag(id);
        return this;
    }

    /**
     * @see #addOptionalTag(Identifier)
     */
    public AbstractTagAppender<T> addOptionalTag(String id) {
        return this.addOptionalTag(Identifier.parse(id));
    }

    /**
     * @see #addOptionalTag(Identifier)
     */
    public AbstractTagAppender<T> addOptionalTag(String... ids) {
        for (String id : ids) {
            this.addOptionalTag(id);
        }

        return this;
    }

    /**
     * @see #addOptionalTag(Identifier)
     */
    public AbstractTagAppender<T> addOptionalTag(Identifier... ids) {
        for (Identifier id : ids) {
            this.addOptionalTag(id);
        }

        return this;
    }

    /**
     * Adds the optional referenced tag to the tag, which is skipped when it cannot be resolved.
     *
     * @param tag the tag key
     * @return this appender instance
     */
    @Override
    public AbstractTagAppender<T> addOptionalTag(TagKey<T> tag) {
        return this.addOptionalTag(tag.location());
    }

    /**
     * @see #addOptionalTag(TagKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> addOptionalTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.addOptionalTag(tag);
        }

        return this;
    }

    /**
     * Adds all optional referenced tags with the given keys to the tag, which are skipped when they cannot be resolved.
     *
     * @param keys the tag keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> addAllOptionalTags(Collection<TagKey<T>> keys) {
        keys.forEach(this::addOptionalTag);
        return this;
    }

    /**
     * @see #addAllOptionalTags(Collection)
     */
    public AbstractTagAppender<T> addAllOptionalTags(Stream<TagKey<T>> keys) {
        keys.forEach(this::addOptionalTag);
        return this;
    }

    /**
     * Removes the entry with the given id from the tag.
     *
     * @param id the entry id
     * @return this appender instance
     */
    public abstract AbstractTagAppender<T> remove(Identifier id);

    /**
     * @see #remove(Identifier)
     */
    public AbstractTagAppender<T> remove(Identifier... ids) {
        for (Identifier id : ids) {
            this.remove(id);
        }

        return this;
    }

    /**
     * Removes the entry referenced by the given key from the tag.
     *
     * @param key the entry key
     * @return this appender instance
     */
    public AbstractTagAppender<T> remove(ResourceKey<T> key) {
        return this.remove(key.identifier());
    }

    /**
     * @see #remove(ResourceKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> remove(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.remove(key);
        }

        return this;
    }

    /**
     * Removes all entries referenced by the given keys from the tag.
     *
     * @param keys the entry keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeAll(Collection<ResourceKey<T>> keys) {
        keys.forEach(this::remove);
        return this;
    }

    /**
     * @see #removeAll(Collection)
     */
    public AbstractTagAppender<T> removeAll(Stream<ResourceKey<T>> keys) {
        keys.forEach(this::remove);
        return this;
    }

    /**
     * Removes the entry referenced by the given holder from the tag.
     *
     * @param holder the entry holder
     * @return this appender instance
     */
    public AbstractTagAppender<T> remove(Holder.Reference<? extends T> holder) {
        return this.remove((ResourceKey<T>) holder.key());
    }

    /**
     * @see #remove(Holder.Reference)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> remove(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.remove(holder);
        }

        return this;
    }

    /**
     * @see #removeOptional(Identifier)
     */
    public AbstractTagAppender<T> removeOptional(String id) {
        return this.removeOptional(Identifier.parse(id));
    }

    /**
     * @see #removeOptional(Identifier)
     */
    public AbstractTagAppender<T> removeOptional(String... ids) {
        for (String id : ids) {
            this.removeOptional(id);
        }

        return this;
    }

    /**
     * Removes the optional entry with the given id from the tag, which is skipped when it cannot be resolved.
     *
     * @param id the entry id
     * @return this appender instance
     */
    public abstract AbstractTagAppender<T> removeOptional(Identifier id);

    /**
     * @see #removeOptional(Identifier)
     */
    public AbstractTagAppender<T> removeOptional(Identifier... ids) {
        for (Identifier id : ids) {
            this.removeOptional(id);
        }

        return this;
    }

    /**
     * Removes the optional entry referenced by the given key from the tag, which is skipped when it cannot be resolved.
     *
     * @param key the entry key
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeOptional(ResourceKey<T> key) {
        return this.removeOptional(key.identifier());
    }

    /**
     * @see #removeOptional(ResourceKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> removeOptional(ResourceKey<T>... keys) {
        for (ResourceKey<T> key : keys) {
            this.removeOptional(key);
        }

        return this;
    }

    /**
     * Removes all optional entries referenced by the given keys from the tag, which are skipped when they cannot be
     * resolved.
     *
     * @param keys the entry keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeAllOptional(Collection<ResourceKey<T>> keys) {
        keys.forEach(this::removeOptional);
        return this;
    }

    /**
     * @see #removeAllOptional(Collection)
     */
    public AbstractTagAppender<T> removeAllOptional(Stream<ResourceKey<T>> keys) {
        keys.forEach(this::removeOptional);
        return this;
    }

    /**
     * Removes the optional entry referenced by the given holder from the tag, which is skipped when it cannot be
     * resolved.
     *
     * @param holder the entry holder
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeOptional(Holder.Reference<? extends T> holder) {
        return this.removeOptional((ResourceKey<T>) holder.key());
    }

    /**
     * @see #removeOptional(Holder.Reference)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> removeOptional(Holder.Reference<? extends T>... holders) {
        for (Holder.Reference<? extends T> holder : holders) {
            this.removeOptional(holder);
        }

        return this;
    }

    /**
     * Removes the referenced tag with the given id from the tag.
     *
     * @param id the tag id
     * @return this appender instance
     */
    public abstract AbstractTagAppender<T> removeTag(Identifier id);

    /**
     * @see #removeTag(Identifier)
     */
    public AbstractTagAppender<T> removeTag(Identifier... ids) {
        for (Identifier id : ids) {
            this.removeTag(id);
        }

        return this;
    }

    /**
     * Removes the given referenced tag from the tag.
     *
     * @param tag the tag key
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeTag(TagKey<T> tag) {
        return this.removeTag(tag.location());
    }

    /**
     * @see #removeTag(TagKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> removeTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.removeTag(tag);
        }

        return this;
    }

    /**
     * Removes all referenced tags with the given keys from the tag.
     *
     * @param keys the tag keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeAllTags(Collection<TagKey<T>> keys) {
        keys.forEach(this::removeTag);
        return this;
    }

    /**
     * @see #removeAllTags(Collection)
     */
    public AbstractTagAppender<T> removeAllTags(Stream<TagKey<T>> keys) {
        keys.forEach(this::removeTag);
        return this;
    }

    /**
     * @see #removeOptionalTag(Identifier)
     */
    public AbstractTagAppender<T> removeOptionalTag(String id) {
        return this.removeOptionalTag(Identifier.parse(id));
    }

    /**
     * @see #removeOptionalTag(Identifier)
     */
    public AbstractTagAppender<T> removeOptionalTag(String... ids) {
        for (String id : ids) {
            this.removeOptionalTag(id);
        }

        return this;
    }

    /**
     * Removes the optional referenced tag with the given id from the tag, which is skipped when it cannot be resolved.
     *
     * @param id the tag id
     * @return this appender instance
     */
    public abstract AbstractTagAppender<T> removeOptionalTag(Identifier id);

    /**
     * @see #removeOptionalTag(Identifier)
     */
    public AbstractTagAppender<T> removeOptionalTag(Identifier... ids) {
        for (Identifier id : ids) {
            this.removeOptionalTag(id);
        }

        return this;
    }

    /**
     * Removes the optional referenced tag from the tag, which is skipped when it cannot be resolved.
     *
     * @param tag the tag key
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeOptionalTag(TagKey<T> tag) {
        return this.removeOptionalTag(tag.location());
    }

    /**
     * @see #removeOptionalTag(TagKey)
     */
    @SafeVarargs
    public final AbstractTagAppender<T> removeOptionalTag(TagKey<T>... tags) {
        for (TagKey<T> tag : tags) {
            this.removeOptionalTag(tag);
        }

        return this;
    }

    /**
     * Removes all optional referenced tags with the given keys from the tag, which are skipped when they cannot be
     * resolved.
     *
     * @param keys the tag keys
     * @return this appender instance
     */
    public AbstractTagAppender<T> removeAllOptionalTags(Collection<TagKey<T>> keys) {
        keys.forEach(this::removeOptionalTag);
        return this;
    }

    /**
     * @see #removeAllOptionalTags(Collection)
     */
    public AbstractTagAppender<T> removeAllOptionalTags(Stream<TagKey<T>> keys) {
        keys.forEach(this::removeOptionalTag);
        return this;
    }

    /**
     * Returns all entries of this appender as strings, with removals prefixed by {@code !}.
     *
     * @return all entries as a string list
     */
    public abstract List<String> asStringList();

    /**
     * Do not use the vanilla method, there is an issue with the ModernFix mod overwriting it.
     *
     * @see TagEntry#elementOrTag()
     */
    protected final String elementOrTag(TagEntry entry) {
        return new ExtraCodecs.TagOrElementLocation(entry.id, entry.tag).toString();
    }
}
