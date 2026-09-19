package fuzs.puzzleslib.common.api.data.v3.recipes;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.data.DataGenerationScopes;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A base implementation of {@link RecipeProvider} for generating recipes for the mod.
 * <p>
 * The recipe output is id-bound to the mod id, so recipes and their unlock advancements are relocated to the mod id
 * instead of the namespace of the result item. Subclasses implement {@link #buildRecipes()}.
 */
public abstract class AbstractRecipeProvider extends RecipeProvider implements Runnable {

    /**
     * @param recipeOutput      the bootstrap context recipes are registered to
     * @param advancementOutput the bootstrap context recipe unlock advancements are registered to
     */
    public AbstractRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
        this.output = ProxyImpl.get().getIdBoundRecipeOutput(DataGenerationScopes.MOD_ID.get(),
                recipeOutput,
                advancementOutput);
    }

    /**
     * Creates the default variant providers for wood block set families, covering the variants that are crafted from
     * other blocks of the family.
     *
     * @param blockSetFamily the block set family the providers operate on
     * @return the variant providers mapped by block set variant
     * @see #generateFor(BlockSetFamily, Map, Map)
     */
    public static Map<BlockSetVariant, FamilyRecipeProvider> createVariantWoodProviders(BlockSetFamily blockSetFamily) {
        return ImmutableMap.<BlockSetVariant, FamilyRecipeProvider>builder()
                .put(BlockSetVariant.WOOD,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.woodFromLogs(result, blockSetFamily.getItem(BlockSetVariant.LOG).value());
                        })
                .put(BlockSetVariant.STRIPPED_WOOD,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.woodFromLogs(result,
                                    blockSetFamily.getItem(BlockSetVariant.STRIPPED_LOG).value());
                        })
                .put(BlockSetVariant.SHELF,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.shelf(result, blockSetFamily.getItem(BlockSetVariant.STRIPPED_LOG).value());
                        })
                .put(BlockSetVariant.BOAT,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.woodenBoat(result, input);
                        })
                .put(BlockSetVariant.CHEST_BOAT,
                        (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                            recipeProvider.chestBoat(result, blockSetFamily.getItem(BlockSetVariant.BOAT).value());
                        })
                .build();
    }

    /**
     * Creates a recipe name for a crafting method from the id of the given recipe serializer.
     *
     * @param resultItem       the recipe result
     * @param recipeSerializer the recipe serializer of the crafting method
     * @return the recipe name
     */
    public static String getCraftingMethodRecipeName(ItemLike resultItem, RecipeSerializer<?> recipeSerializer) {
        Identifier identifier = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        Objects.requireNonNull(identifier, "identifier is null");
        return getCraftingMethodRecipeName(resultItem, identifier.getPath());
    }

    /**
     * @param resultItem     the recipe result
     * @param craftingMethod the path of the crafting method
     * @return the recipe name
     */
    public static String getCraftingMethodRecipeName(ItemLike resultItem, String craftingMethod) {
        return getItemName(resultItem) + "_from_" + craftingMethod;
    }

    /**
     * Creates a stonecutting recipe name from the given result and material.
     *
     * @param resultItem the recipe result
     * @param material   the stonecutting material
     * @return the recipe name
     */
    public static String getStonecuttingRecipeName(ItemLike resultItem, ItemLike material) {
        return getConversionRecipeName(resultItem, material) + "_stonecutting";
    }

    /**
     * Creates a smithing recipe name from the given result.
     *
     * @param resultItem the recipe result
     * @return the recipe name
     */
    public static String getSmithingRecipeName(ItemLike resultItem) {
        return getItemName(resultItem) + "_smithing";
    }

    /**
     * Creates the criterion name used to unlock a recipe by the given tag.
     *
     * @param tagKey the tag
     * @return the criterion name
     */
    public static String getHasName(TagKey<Item> tagKey) {
        return "has_" + tagKey.location().getPath();
    }

    @Override
    public final void run() {
        this.buildRecipes();
    }

    /**
     * Registers all recipes of this provider via the various builder methods inherited from {@link RecipeProvider},
     * which are then emitted by {@link #run()}.
     */
    @Override
    public abstract void buildRecipes();

    /**
     * Generates recipes for all blocks of the given block set family.
     *
     * @param blockSetFamily the block set family
     * @see #generateFor(BlockSetFamily, Map, Map)
     */
    public void generateFor(BlockSetFamily blockSetFamily) {
        this.generateFor(blockSetFamily, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * Generates recipes for all blocks of the given block set family, using the given variant providers for individual
     * crafting and stonecutting variants.
     *
     * @param blockSetFamily      the block set family
     * @param craftingVariants    the crafting variant providers
     * @param stonecutterVariants the stonecutter variant providers
     */
    public void generateFor(BlockSetFamily blockSetFamily, Map<BlockSetVariant, FamilyRecipeProvider> craftingVariants, Map<BlockSetVariant, FamilyRecipeProvider> stonecutterVariants) {
        BlockFamily blockFamily = blockSetFamily.getBlockFamily();
        this.generateRecipes(blockFamily, FeatureFlags.DEFAULT_FLAGS);
        blockSetFamily.getItemVariants().forEach((BlockSetVariant variant, Holder.Reference<Item> holder) -> {
            if (blockFamily.shouldGenerateCraftingRecipe()) {
                FamilyRecipeProvider recipeProvider = craftingVariants.get(variant);
                if (recipeProvider != null) {
                    Block baseBlock;
                    BlockFamily.Variant vanillaVariant = variant.toVanilla();
                    if (vanillaVariant != null) {
                        baseBlock = this.getBaseBlockForCrafting(blockFamily, vanillaVariant);
                    } else {
                        baseBlock = blockSetFamily.getBaseBlock().value();
                    }

                    recipeProvider.create(this,
                            holder.value(),
                            baseBlock,
                            blockFamily.getRecipeGroupPrefix(),
                            blockFamily.getRecipeUnlockedBy());
                }
            }

            if (blockFamily.shouldGenerateStonecutterRecipe()) {
                FamilyRecipeProvider recipeProvider = stonecutterVariants.get(variant);
                if (recipeProvider != null) {
                    Block baseBlock = blockSetFamily.getBaseBlock().value();
                    recipeProvider.create(this,
                            holder.value(),
                            baseBlock,
                            blockFamily.getRecipeGroupPrefix(),
                            blockFamily.getRecipeUnlockedBy());
                }
            }
        });
    }

    /**
     * Generates a stair recipe for the given result from the given ingredient.
     *
     * @param recipeCategory the recipe category
     * @param resultItem     the recipe result
     * @param ingredientItem the recipe ingredient
     */
    public void stair(RecipeCategory recipeCategory, ItemLike resultItem, ItemLike ingredientItem) {
        this.stairBuilder(recipeCategory, resultItem, Ingredient.of(ingredientItem))
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output);
    }

    /**
     * Creates a stair recipe builder for the given result and ingredient.
     *
     * @param recipeCategory the recipe category
     * @param resultItem     the recipe result
     * @param ingredient     the recipe ingredient
     * @return the recipe builder
     */
    public RecipeBuilder stairBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
        return this.shaped(recipeCategory, resultItem, 4)
                .define('#', ingredient)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###");
    }

    /**
     * Generates smelting and blasting recipes for the given result from the given ingredient, using 200 ticks of
     * cooking time.
     *
     * @param resultItem     the recipe result
     * @param ingredientItem the recipe ingredient
     * @param experience     the experience reward
     * @see #metalCooking(ItemLike, ItemLike, float, int)
     */
    public void metalCooking(ItemLike resultItem, ItemLike ingredientItem, float experience) {
        this.metalCooking(resultItem, ingredientItem, experience, 200);
    }

    /**
     * Generates smelting and blasting recipes for the given result from the given ingredient, using the given
     * experience reward and cooking time.
     *
     * @param resultItem      the recipe result
     * @param ingredientItem  the recipe ingredient
     * @param experience      the experience reward
     * @param baseCookingTime the smelting cooking time, halved for blasting
     */
    public void metalCooking(ItemLike resultItem, ItemLike ingredientItem, float experience, int baseCookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredientItem),
                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                resultItem,
                experience,
                baseCookingTime).unlockedBy(getHasName(ingredientItem), this.has(ingredientItem)).save(this.output);
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredientItem),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        resultItem,
                        experience,
                        baseCookingTime / 2)
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getBlastingRecipeName(resultItem));
    }

    /**
     * Generates smelting, smoking, and campfire cooking recipes for the given result from the given ingredient, using
     * 0.35 experience reward and 200 ticks of cooking time.
     *
     * @param resultItem     the recipe result
     * @param ingredientItem the recipe ingredient
     * @see #foodCooking(ItemLike, ItemLike, float, int)
     */
    public void foodCooking(ItemLike resultItem, ItemLike ingredientItem) {
        this.foodCooking(resultItem, ingredientItem, 0.35F, 200);
    }

    /**
     * Generates smelting, smoking, and campfire cooking recipes for the given result from the given ingredient, using
     * the given experience reward and cooking time.
     *
     * @param resultItem       the recipe result
     * @param ingredientItem   the recipe ingredient
     * @param experienceReward the experience reward
     * @param baseCookingTime  the smelting cooking time, halved for smoking and tripled for campfire cooking
     */
    public void foodCooking(ItemLike resultItem, ItemLike ingredientItem, float experienceReward, int baseCookingTime) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredientItem),
                RecipeCategory.FOOD,
                CookingBookCategory.FOOD,
                resultItem,
                experienceReward,
                baseCookingTime).unlockedBy(getHasName(ingredientItem), this.has(ingredientItem)).save(this.output);
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(ingredientItem),
                        RecipeCategory.FOOD,
                        resultItem,
                        experienceReward,
                        baseCookingTime / 2)
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getCraftingMethodRecipeName(resultItem, SmokingRecipe.SERIALIZER));
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ingredientItem),
                        RecipeCategory.FOOD,
                        resultItem,
                        experienceReward,
                        baseCookingTime * 3)
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getCraftingMethodRecipeName(resultItem, CampfireCookingRecipe.SERIALIZER));
    }

    /**
     * Creates a stonecutting recipe builder for the given result and ingredient, using a count of 1.
     *
     * @param recipeCategory the recipe category
     * @param resultItem     the recipe result
     * @param ingredient     the recipe ingredient
     * @return the recipe builder
     * @see #stonecutterResultFromBaseBuilder(RecipeCategory, ItemLike, Ingredient, int)
     */
    public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
        return this.stonecutterResultFromBaseBuilder(recipeCategory, resultItem, ingredient, 1);
    }

    /**
     * Creates a stonecutting recipe builder for the given result and ingredient.
     *
     * @param recipeCategory the recipe category
     * @param resultItem     the recipe result
     * @param ingredient     the recipe ingredient
     * @param count          the result count
     * @return the recipe builder
     */
    public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient, int count) {
        return SingleItemRecipeBuilder.stonecutting(ingredient, recipeCategory, resultItem, count);
    }

    /**
     * Generates a smithing transform recipe for the given result from the given template, base, and material.
     *
     * @param recipeCategory the recipe category
     * @param resultItem     the recipe result
     * @param templateItem   the smithing template
     * @param baseItem       the smithing base item
     * @param materialItem   the smithing material
     */
    public void smithing(RecipeCategory recipeCategory, ItemLike resultItem, ItemLike templateItem, ItemLike baseItem, ItemLike materialItem) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(templateItem),
                        Ingredient.of(baseItem),
                        Ingredient.of(materialItem),
                        recipeCategory,
                        resultItem.asItem())
                .unlocks(getHasName(materialItem), this.has(materialItem))
                .save(this.output, getSmithingRecipeName(resultItem));
    }

    /**
     * Generates a waxing recipe for the given result from the given ingredient and honeycomb.
     *
     * @param resultItem     the recipe result
     * @param ingredientItem the recipe ingredient
     */
    public void waxing(ItemLike resultItem, ItemLike ingredientItem) {
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, resultItem)
                .requires(ingredientItem)
                .requires(Items.HONEYCOMB)
                .group(getItemName(resultItem))
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getConversionRecipeName(resultItem, Items.HONEYCOMB));
    }

    /**
     * Creates a family recipe for a single block set variant, used by
     * {@link #generateFor(BlockSetFamily, Map, Map)}.
     */
    @FunctionalInterface
    public interface FamilyRecipeProvider {
        /**
         * Creates and saves the recipe for a single variant.
         *
         * @param recipeProvider    the recipe provider the recipe is saved to
         * @param result            the recipe result
         * @param input             the recipe input
         * @param recipeGroupPrefix the recipe group prefix
         * @param recipeUnlockedBy  the criterion name used to unlock the recipe
         */
        void create(RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy);

        /**
         * @return a stonecutting family recipe provider using {@link RecipeCategory#BUILDING_BLOCKS} and a count of 1
         */
        static FamilyRecipeProvider stonecutting() {
            return stonecutting(RecipeCategory.BUILDING_BLOCKS, 1);
        }

        /**
         * @param recipeCategory the recipe category
         * @param count          the result count
         * @return a stonecutting family recipe provider
         */
        static FamilyRecipeProvider stonecutting(RecipeCategory recipeCategory, int count) {
            return (RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy) -> {
                SingleItemRecipeBuilder recipeBuilder = SingleItemRecipeBuilder.stonecutting(Ingredient.of(input),
                        recipeCategory,
                        result,
                        count);
                recipeBuilder.unlockedBy(recipeUnlockedBy.orElseGet(() -> getHasName(input)),
                        recipeProvider.has(input));
                recipeBuilder.save(recipeProvider.output, getStonecuttingRecipeName(result, input));
            };
        }
    }
}
