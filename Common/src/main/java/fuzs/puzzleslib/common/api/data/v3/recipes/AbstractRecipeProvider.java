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

public abstract class AbstractRecipeProvider extends RecipeProvider implements Runnable {

    public AbstractRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
        this.output = ProxyImpl.get().getIdBoundRecipeOutput(DataGenerationScopes.MOD_ID.get(),
                recipeOutput,
                advancementOutput);
    }

    /**
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

    public static String getCraftingMethodRecipeName(ItemLike resultItem, RecipeSerializer<?> recipeSerializer) {
        Identifier identifier = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        Objects.requireNonNull(identifier, "identifier is null");
        return getCraftingMethodRecipeName(resultItem, identifier.getPath());
    }

    public static String getCraftingMethodRecipeName(ItemLike resultItem, String craftingMethod) {
        return getItemName(resultItem) + "_from_" + craftingMethod;
    }

    public static String getStonecuttingRecipeName(ItemLike resultItem, ItemLike material) {
        return getConversionRecipeName(resultItem, material) + "_stonecutting";
    }

    public static String getSmithingRecipeName(ItemLike resultItem) {
        return getItemName(resultItem) + "_smithing";
    }

    public static String getHasName(TagKey<Item> tagKey) {
        return "has_" + tagKey.location().getPath();
    }

    @Override
    public final void run() {
        this.buildRecipes();
    }

    @Override
    public abstract void buildRecipes();

    public void generateFor(BlockSetFamily blockSetFamily) {
        this.generateFor(blockSetFamily, Collections.emptyMap(), Collections.emptyMap());
    }

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

    public void stair(RecipeCategory recipeCategory, ItemLike resultItem, ItemLike ingredientItem) {
        this.stairBuilder(recipeCategory, resultItem, Ingredient.of(ingredientItem))
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output);
    }

    public RecipeBuilder stairBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
        return this.shaped(recipeCategory, resultItem, 4)
                .define('#', ingredient)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###");
    }

    public void metalCooking(ItemLike resultItem, ItemLike ingredientItem, float experience) {
        this.metalCooking(resultItem, ingredientItem, experience, 200);
    }

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

    public void foodCooking(ItemLike resultItem, ItemLike ingredientItem) {
        this.foodCooking(resultItem, ingredientItem, 0.35F, 200);
    }

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

    public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
        return this.stonecutterResultFromBaseBuilder(recipeCategory, resultItem, ingredient, 1);
    }

    public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient, int count) {
        return SingleItemRecipeBuilder.stonecutting(ingredient, recipeCategory, resultItem, count);
    }

    public void smithing(RecipeCategory recipeCategory, ItemLike resultItem, ItemLike templateItem, ItemLike baseItem, ItemLike materialItem) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(templateItem),
                        Ingredient.of(baseItem),
                        Ingredient.of(materialItem),
                        recipeCategory,
                        resultItem.asItem())
                .unlocks(getHasName(materialItem), this.has(materialItem))
                .save(this.output, getSmithingRecipeName(resultItem));
    }

    public void waxing(ItemLike resultItem, ItemLike ingredientItem) {
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, resultItem)
                .requires(ingredientItem)
                .requires(Items.HONEYCOMB)
                .group(getItemName(resultItem))
                .unlockedBy(getHasName(ingredientItem), this.has(ingredientItem))
                .save(this.output, getConversionRecipeName(resultItem, Items.HONEYCOMB));
    }

    @FunctionalInterface
    public interface FamilyRecipeProvider {
        void create(RecipeProvider recipeProvider, ItemLike result, ItemLike input, Optional<String> recipeGroupPrefix, Optional<String> recipeUnlockedBy);

        static FamilyRecipeProvider stonecutting() {
            return stonecutting(RecipeCategory.BUILDING_BLOCKS, 1);
        }

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
