package fuzs.puzzleslib.common.api.data.v3.recipes;

import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A {@link RecipeOutput} decorator that transforms every recipe via an {@link UnaryOperator} before passing it on to
 * the wrapped output.
 * <p>
 * This allows using the vanilla recipe builders with custom recipe implementations based on the corresponding vanilla
 * recipe types, as done by {@link TransmuteShapedRecipeBuilder} and {@link TransmuteShapelessRecipeBuilder}.
 */
public interface TransformingRecipeOutput extends RecipeOutput {

    /**
     * Wraps the given recipe output, applying the given operator to every recipe before it is passed on.
     *
     * @param recipeOutput the recipe output to wrap
     * @param operator     the operator applied to every recipe
     * @return the transforming recipe output
     */
    static RecipeOutput transformed(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) {
        return ProxyImpl.get().getTransformingRecipeOutput(recipeOutput, operator);
    }

    /**
     * @return the wrapped recipe output
     */
    RecipeOutput output();

    /**
     * @return the operator applied to every recipe
     */
    UnaryOperator<Recipe<?>> operator();

    /**
     * Transforms the recipe via {@link #operator()} before delegating to {@link #output()}.
     */
    @Override
    default void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
        this.output().accept(key, this.operator().apply(recipe), advancement);
    }

    @Override
    default Advancement.Builder advancement() {
        return this.output().advancement();
    }

    @Override
    default <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
        return this.output().lookup(key);
    }

    @SuppressWarnings("deprecation")
    @Override
    default <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
        return this.output().listContextElements(key);
    }
}
