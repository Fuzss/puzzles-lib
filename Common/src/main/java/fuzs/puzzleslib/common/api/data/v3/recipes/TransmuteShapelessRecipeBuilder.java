package fuzs.puzzleslib.common.api.data.v3.recipes;

import fuzs.puzzleslib.common.impl.item.TransmuteShapelessRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A {@link ShapelessRecipeBuilder} for generating a shapeless recipe that also transmutes an input ingredient onto the
 * crafting result.
 * <p>
 * The generated recipe is a custom {@link TransmuteShapelessRecipe} based on the same {@link ShapelessRecipe} data,
 * which copies the components of the first matching {@link #input(Ingredient) input} ingredient in the crafting grid
 * onto the result stack. Saving requires an input to be set via {@link #input(ItemLike)} or
 * {@link #input(Ingredient)}.
 */
public class TransmuteShapelessRecipeBuilder extends ShapelessRecipeBuilder {
    private final ResourceKey<RecipeSerializer<?>> serializerKey;
    private Ingredient input;

    /**
     * @param serializerKey the recipe serializer the generated recipe is registered with
     * @param holderGetter  the holder getter used for resolving items
     * @param category      the recipe category
     * @param result        the recipe result
     */
    public TransmuteShapelessRecipeBuilder(ResourceKey<RecipeSerializer<?>> serializerKey, HolderGetter<Item> holderGetter, RecipeCategory category, ItemStackTemplate result) {
        super(holderGetter, category, result);
        this.serializerKey = serializerKey;
    }

    /**
     * Creates a new transmute shapeless recipe builder for a single result item.
     *
     * @param recipeSerializer the recipe serializer the generated recipe is registered with
     * @param holderGetter     the holder getter used for resolving items
     * @param category         the recipe category
     * @param result           the recipe result
     * @return the recipe builder
     */
    public static TransmuteShapelessRecipeBuilder shapeless(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result) {
        return shapeless(recipeSerializer, holderGetter, category, result, 1);
    }

    /**
     * Creates a new transmute shapeless recipe builder for the given result count.
     *
     * @param recipeSerializer the recipe serializer the generated recipe is registered with
     * @param holderGetter     the holder getter used for resolving items
     * @param category         the recipe category
     * @param result           the recipe result
     * @param count            the result count
     * @return the recipe builder
     */
    public static TransmuteShapelessRecipeBuilder shapeless(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result, int count) {
        return new TransmuteShapelessRecipeBuilder(recipeSerializer,
                holderGetter,
                category,
                new ItemStackTemplate(result.asItem(), count));
    }

    /**
     * Requires the given tag as an ingredient.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder requires(TagKey<Item> tag) {
        super.requires(tag);
        return this;
    }

    /**
     * Requires the given item as an ingredient.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder requires(ItemLike item) {
        super.requires(item);
        return this;
    }

    /**
     * Requires the given item as an ingredient in the given count.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder requires(ItemLike item, int count) {
        super.requires(item, count);
        return this;
    }

    /**
     * Requires the given ingredient.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient) {
        super.requires(ingredient);
        return this;
    }

    /**
     * Requires the given ingredient in the given count.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder requires(Ingredient ingredient, int count) {
        super.requires(ingredient, count);
        return this;
    }

    /**
     * Adds a criterion for unlocking the recipe.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        super.unlockedBy(name, criterion);
        return this;
    }

    /**
     * Sets the recipe group.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapelessRecipeBuilder group(@Nullable String group) {
        super.group(group);
        return this;
    }

    /**
     * Sets the transmute input ingredient from the given item.
     *
     * @param input the transmute input
     * @return this builder instance
     */
    public TransmuteShapelessRecipeBuilder input(ItemLike input) {
        return this.input(Ingredient.of(input));
    }

    /**
     * Sets the transmute input ingredient whose components are copied onto the crafting result.
     *
     * @param input the transmute input
     * @return this builder instance
     */
    public TransmuteShapelessRecipeBuilder input(Ingredient input) {
        Objects.requireNonNull(input, "input is null");
        this.input = input;
        return this;
    }

    /**
     * Saves the generated recipe, wrapping the given output so the resulting recipe is rebuilt as a
     * {@link TransmuteShapelessRecipe}.
     *
     * @param output the recipe output to save to
     * @param id     the recipe id
     */
    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Objects.requireNonNull(this.input, "input is null");
        super.save(TransformingRecipeOutput.transformed(output, (Recipe<?> recipe) -> {
            return new TransmuteShapelessRecipe(TransmuteShapelessRecipeBuilder.this.serializerKey,
                    (ShapelessRecipe) recipe,
                    TransmuteShapelessRecipeBuilder.this.input);
        }), id);
    }
}
