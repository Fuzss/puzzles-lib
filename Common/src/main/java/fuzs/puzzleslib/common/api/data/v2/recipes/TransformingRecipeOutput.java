package fuzs.puzzleslib.common.api.data.v2.recipes;

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
 * Allows for using vanilla recipe builders with custom recipe implementations based on vanilla recipe types.
 */
public interface TransformingRecipeOutput extends RecipeOutput {

    static RecipeOutput transformed(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) {
        return ProxyImpl.get().getTransformingRecipeOutput(recipeOutput, operator);
    }

    RecipeOutput output();

    UnaryOperator<Recipe<?>> operator();

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
