package fuzs.puzzleslib.common.impl.init;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

/**
 * An implementation similar to {@link Holder.Direct} on top of {@link Holder.Reference}, useful for accessing
 * {@link Reference#key()}.
 */
public final class StandAloneHolder<T> extends Holder.Reference<T> {

    public StandAloneHolder(ResourceKey<T> key, T value) {
        super(Type.STAND_ALONE, new HolderOwner<>() {
            @Override
            public boolean canSerializeIn(HolderOwner<T> context) {
                return true;
            }
        }, key, value);
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(value, "value is null");
    }

    @Override
    public void bindValue(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "StandAloneReference{" + this.key() + "=" + this.value() + "}";
    }
}
