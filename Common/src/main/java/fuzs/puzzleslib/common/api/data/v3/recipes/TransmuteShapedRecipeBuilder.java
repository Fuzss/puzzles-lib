package fuzs.puzzleslib.common.api.data.v3.recipes;

import fuzs.puzzleslib.common.impl.item.TransmuteShapedRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A {@link ShapedRecipeBuilder} for generating a shaped recipe that also transmutes an input ingredient onto the
 * crafting result.
 * <p>
 * The generated recipe is a custom {@link TransmuteShapedRecipe} based on the same {@link ShapedRecipe} data, which
 * copies the components of the first matching {@link #input(Ingredient) input} ingredient in the crafting grid onto the
 * result stack. Saving requires an input to be set via {@link #input(ItemLike)} or {@link #input(Ingredient)}.
 */
public class TransmuteShapedRecipeBuilder extends ShapedRecipeBuilder {
    private final ResourceKey<RecipeSerializer<?>> serializerKey;
    private Ingredient input;

    /**
     * @param serializerKey the recipe serializer the generated recipe is registered with
     * @param holderGetter  the holder getter used for resolving items
     * @param category      the recipe category
     * @param result        the recipe result
     */
    public TransmuteShapedRecipeBuilder(ResourceKey<RecipeSerializer<?>> serializerKey, HolderGetter<Item> holderGetter, RecipeCategory category, ItemStackTemplate result) {
        super(holderGetter, category, result);
        this.serializerKey = serializerKey;
    }

    /**
     * Creates a new transmute shaped recipe builder for a single result item.
     *
     * @param recipeSerializer the recipe serializer the generated recipe is registered with
     * @param holderGetter     the holder getter used for resolving items
     * @param category         the recipe category
     * @param result           the recipe result
     * @return the recipe builder
     */
    public static TransmuteShapedRecipeBuilder shaped(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result) {
        return shaped(recipeSerializer, holderGetter, category, result, 1);
    }

    /**
     * Creates a new transmute shaped recipe builder for the given result count.
     *
     * @param recipeSerializer the recipe serializer the generated recipe is registered with
     * @param holderGetter     the holder getter used for resolving items
     * @param category         the recipe category
     * @param result           the recipe result
     * @param count            the result count
     * @return the recipe builder
     */
    public static TransmuteShapedRecipeBuilder shaped(ResourceKey<RecipeSerializer<?>> recipeSerializer, HolderGetter<Item> holderGetter, RecipeCategory category, ItemLike result, int count) {
        return new TransmuteShapedRecipeBuilder(recipeSerializer,
                holderGetter,
                category,
                new ItemStackTemplate(result.asItem(), count));
    }

    /**
     * Defines the given symbol for a tag.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        super.define(symbol, tag);
        return this;
    }

    /**
     * Defines the given symbol for an item.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder define(Character symbol, ItemLike item) {
        super.define(symbol, item);
        return this;
    }

    /**
     * Defines the given symbol for an ingredient.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        super.define(symbol, ingredient);
        return this;
    }

    /**
     * Adds a pattern row.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder pattern(String row) {
        super.pattern(row);
        return this;
    }

    /**
     * Adds a criterion for unlocking the recipe.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        super.unlockedBy(name, criterion);
        return this;
    }

    /**
     * Sets the recipe group.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder group(@Nullable String group) {
        super.group(group);
        return this;
    }

    /**
     * Sets whether a notification is shown when the recipe is unlocked.
     *
     * @return this builder instance
     */
    @Override
    public TransmuteShapedRecipeBuilder showNotification(boolean showNotification) {
        super.showNotification(showNotification);
        return this;
    }

    /**
     * Sets the transmute input ingredient from the given item.
     *
     * @param input the transmute input
     * @return this builder instance
     */
    public TransmuteShapedRecipeBuilder input(ItemLike input) {
        return this.input(Ingredient.of(input));
    }

    /**
     * Sets the transmute input ingredient whose components are copied onto the crafting result.
     *
     * @param input the transmute input
     * @return this builder instance
     */
    public TransmuteShapedRecipeBuilder input(Ingredient input) {
        Objects.requireNonNull(input, "input is null");
        this.input = input;
        return this;
    }

    /**
     * Saves the generated recipe, wrapping the given output so the resulting recipe is rebuilt as a
     * {@link TransmuteShapedRecipe}.
     *
     * @param output the recipe output to save to
     * @param id     the recipe id
     */
    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Objects.requireNonNull(this.input, "input is null");
        super.save(TransformingRecipeOutput.transformed(output, (Recipe<?> recipe) -> {
            return new TransmuteShapedRecipe(TransmuteShapedRecipeBuilder.this.serializerKey,
                    (ShapedRecipe) recipe,
                    TransmuteShapedRecipeBuilder.this.input);
        }), id);
    }
}
