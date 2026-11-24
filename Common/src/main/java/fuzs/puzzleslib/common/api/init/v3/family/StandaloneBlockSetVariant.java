package fuzs.puzzleslib.common.api.init.v3.family;

import net.minecraft.data.BlockFamily;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A {@link BlockSetVariant} that is not backed by a vanilla {@link BlockFamily.Variant}, used for variants that have no
 * vanilla equivalent such as shelves and boats.
 */
public abstract class StandaloneBlockSetVariant implements BlockSetVariant {
    private final String name;

    /**
     * Creates a new variant from the given serialized name.
     *
     * @param name the serialized name of the variant
     */
    public StandaloneBlockSetVariant(String name) {
        this.name = name;
    }

    /**
     * Creates a new variant using the recipe group of the given vanilla variant as its serialized name.
     *
     * @param variant the vanilla variant the name is derived from
     */
    public StandaloneBlockSetVariant(BlockFamily.Variant variant) {
        this(variant.getRecipeGroup());
    }

    @Override
    public abstract void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride);

    @Override
    public String toString() {
        return "Standalone[" + this.getSerializedName() + "]";
    }

    /**
     * @return the serialized name of this variant
     */
    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof BlockSetVariant variant)) {
            return false;
        } else {
            return Objects.equals(this.getSerializedName(), variant.getSerializedName());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSerializedName());
    }
}
