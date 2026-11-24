package fuzs.puzzleslib.common.api.init.v3.family;

import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A {@link BlockSetVariant} backed by a vanilla {@link BlockFamily.Variant}, such as stairs or a slab.
 * <p>
 * The serialized name is derived from the vanilla variant's recipe group and serves as the variant's identity. A custom
 * subclass overriding {@link #registerBlock(BlockSetFamily.Context, String)} (for example, to generate a falling slab)
 * therefore remains interchangeable with the built-in variant for registration and all data generation.
 */
public abstract class VanillaBlockSetVariant implements BlockSetVariant.Vanilla {
    private final BlockFamily.Variant variant;
    private final BiConsumer<BlockFamily.Builder, Block> variantBuilder;

    public VanillaBlockSetVariant(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, Block> variantBuilder) {
        this.variant = variant;
        this.variantBuilder = variantBuilder;
    }

    @Override
    public final void generateFor(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        this.registerBlock(context, baseNameOverride);
        this.registerItem(context, baseNameOverride);
    }

    /**
     * Registers this variant's block. By default, a plain block copying the base block's properties is used.
     *
     * @param context          the family context used for registering the block
     * @param baseNameOverride optional name override for the base block, or {@code null} to use the family's base name
     */
    public void registerBlock(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        context.registerBlock(this,
                context.getRegistries()
                        .registerBlock(this.getName(context, this.getSerializedName(), baseNameOverride), () -> {
                            return BlockBehaviour.Properties.ofFullCopy(context.getBaseBlock().value());
                        }));
    }

    /**
     * Registers this variant's block item for the block registered via {@link #registerBlock}.
     *
     * @param context          the family context used for registering the item
     * @param baseNameOverride optional name override for the base block, or {@code null} to use the family's base name
     */
    public void registerItem(BlockSetFamily.Context context, @Nullable String baseNameOverride) {
        context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
    }

    /**
     * Resolves the registered name of this variant's blocks and items from the family's base name.
     *
     * @param context          the family context
     * @param variantName      the variant's serialized name
     * @param baseNameOverride optional name override for the base block, or {@code null} to use the family's base name
     * @return the resolved name
     */
    public abstract String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride);

    @Override
    public BlockFamily.Variant toVanilla() {
        return this.variant;
    }

    @Override
    public void configureBlockFamily(BlockFamily.Builder blockFamily, Block block) {
        this.variantBuilder.accept(blockFamily, block);
    }

    @Override
    public String toString() {
        return "Vanilla[" + this.getSerializedName() + "]";
    }

    @Override
    public String getSerializedName() {
        return this.variant.getRecipeGroup();
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

    public static class Prefix extends VanillaBlockSetVariant {

        public Prefix(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, Block> variantBuilder) {
            super(variant, variantBuilder);
        }

        @Override
        public String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride) {
            return context.getNameWithPrefix(variantName, baseNameOverride);
        }
    }

    public static class Suffix extends VanillaBlockSetVariant {

        public Suffix(BlockFamily.Variant variant, BiConsumer<BlockFamily.Builder, Block> variantBuilder) {
            super(variant, variantBuilder);
        }

        @Override
        public String getName(BlockSetFamily.Context context, String variantName, @Nullable String baseNameOverride) {
            return context.getNameWithSuffix(variantName, baseNameOverride);
        }
    }
}
