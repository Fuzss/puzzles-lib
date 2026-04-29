package fuzs.puzzleslib.common.impl.init;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A {@link Holder} implementation similar to DeferredHolder on NeoForge, but extending
 * {@link net.minecraft.core.Holder.Reference} to allow for using {@link Reference#key()}.
 * <p>
 * Supports lazy initialization on Fabric &amp; NeoForge.
 */
public final class LazyHolder<T> extends Holder.Reference<T> {
    private final Supplier<Holder<T>> holderSupplier;
    @Nullable
    private Holder<T> holder;

    public LazyHolder(ResourceKey<? extends Registry<? super T>> registryKey, Holder<T> holder) {
        this(registryKey, holder.unwrapKey().orElseThrow(), () -> holder);
        Objects.requireNonNull(holder, "holder is null");
    }

    public LazyHolder(ResourceKey<? extends Registry<? super T>> registryKey, ResourceKey<T> key, Supplier<Holder<T>> holderSupplier) {
        super(Holder.Reference.Type.STAND_ALONE, new HolderOwner<>() {
            @Override
            public boolean canSerializeIn(HolderOwner<T> context) {
                throw new UnsupportedOperationException();
            }
        }, key, null);
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(holderSupplier, "holder supplier is null");
        this.holderSupplier = holderSupplier;
    }

    private void bindHolder() {
        if (this.holder == null) {
            this.holder = this.holderSupplier.get();
        }
    }

    @Override
    public T value() {
        this.bindHolder();
        Objects.requireNonNull(this.holder, () -> "holder for " + this.key() + " is null");
        return this.holder.value();
    }

    @Override
    public boolean is(TagKey<T> tagKey) {
        this.bindHolder();
        Objects.requireNonNull(this.holder, () -> "holder for " + this.key() + " is null");
        return this.holder.is(tagKey);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        this.bindHolder();
        return this.holder == null || this.holder.canSerializeIn(owner);
    }

    @Override
    public boolean isBound() {
        this.bindHolder();
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean areComponentsBound() {
        this.bindHolder();
        return this.holder != null && this.holder.areComponentsBound();
    }

    @Override
    public void bindValue(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindTags(Collection<TagKey<T>> tags) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindComponents(DataComponentMap components) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<TagKey<T>> tags() {
        this.bindHolder();
        Objects.requireNonNull(this.holder, () -> "holder for " + this.key() + " is null");
        return this.holder.tags();
    }

    @Override
    public DataComponentMap components() {
        this.bindHolder();
        Objects.requireNonNull(this.holder, () -> "holder for " + this.key() + " is null");
        return this.holder.components();
    }

    @Override
    public String toString() {
        return "LazyReference{" + this.key() + (this.holder != null ? "=" + this.value() : "") + "}";
    }
}
