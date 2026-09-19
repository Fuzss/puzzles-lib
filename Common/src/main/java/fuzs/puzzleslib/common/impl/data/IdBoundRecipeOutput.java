package fuzs.puzzleslib.common.impl.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

/**
 * A base {@link RecipeOutput} implementation for data generation that updates all recipes and recipe unlock
 * advancements to the mod id.
 * <p>
 * Vanilla recipe builders derive the recipe id from the namespace of the result item, which places recipes for items of
 * other namespaces (like vanilla items) in their namespace. This implementation instead keeps the original path and
 * swaps the namespace for the mod id. Recipe unlock advancements embedding the recipe id in both a criterion and a
 * reward are rebuilt via {@link RecipeUnlockAdvancementBuilder} for the updated recipe id.
 */
public abstract class IdBoundRecipeOutput implements RecipeOutput {
    /**
     * The mod id.
     */
    public final String modId;
    /**
     * The bootstrap context recipes are registered to.
     */
    private final BootstrapContext<Recipe<?>> recipeOutput;
    /**
     * The bootstrap context recipe unlock advancements are registered to.
     */
    private final BootstrapContext<Advancement> advancementOutput;

    /**
     * @param modId             the mod id to update all recipes and advancements to
     * @param recipeOutput      the bootstrap context recipes are registered to
     * @param advancementOutput the bootstrap context recipe unlock advancements are registered to
     */
    public IdBoundRecipeOutput(String modId, BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        this.modId = modId;
        this.recipeOutput = recipeOutput;
        this.advancementOutput = advancementOutput;
    }

    @Override
    public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
        // Update all recipes to the mod id, so they do not depend on the result item namespace
        // It would place new recipes for vanilla items in 'minecraft' which we do not want.
        ResourceKey<Recipe<?>> updatedKey = this.updateRecipeId(key);
        this.recipeOutput.register(updatedKey, recipe);
        if (advancementHolder != null) {
            // When the recipe id was not updated the original advancement is still valid as is.
            AdvancementHolder updatedHolder =
                    updatedKey.equals(key) ? advancementHolder : this.updateAdvancement(advancementHolder, updatedKey);
            updatedHolder.register(this.advancementOutput);
        }
    }

    @SuppressWarnings("removal")
    @Override
    public Advancement.Builder advancement() {
        return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
    }

    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
        return this.recipeOutput.lookup(key);
    }

    @SuppressWarnings("deprecation")
    @Override
    public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
        return this.recipeOutput.listContextElements(key);
    }

    /**
     * Relocates a recipe id to the mod id by keeping its path.
     *
     * @param key the original recipe id
     * @return the recipe id updated to the mod id
     */
    private ResourceKey<Recipe<?>> updateRecipeId(ResourceKey<Recipe<?>> key) {
        if (key.identifier().getNamespace().equals(this.modId)) {
            return key;
        } else {
            Identifier identifier = Identifier.fromNamespaceAndPath(this.modId, key.identifier().getPath());
            return ResourceKey.create(Registries.RECIPE, identifier);
        }
    }

    /**
     * Rebuilds a recipe unlocking advancement so it references the updated recipe id instead of the original one.
     * <p>
     * The advancement is reconstructed via {@link RecipeUnlockAdvancementBuilder} to inherit all changes made to the
     * vanilla advancement format, while the advancement id keeps its original path and only updates the namespace.
     *
     * @param originalAdvancement the original advancement
     * @param updatedKey          the recipe id updated to the mod id
     * @return the advancement referencing the updated recipe id
     */
    private AdvancementHolder updateAdvancement(AdvancementHolder originalAdvancement, ResourceKey<Recipe<?>> updatedKey) {
        RecipeUnlockAdvancementBuilder builder = new RecipeUnlockAdvancementBuilder();
        for (Map.Entry<String, Criterion<?>> entry : originalAdvancement.value().criteria().entrySet()) {
            // The recipe unlocking criteria are rebuilt by the advancement builder for the updated recipe id.
            if (!(entry.getValue().triggerInstance() instanceof RecipeUnlockedTrigger.TriggerInstance)) {
                builder.unlockedBy(entry.getKey(), entry.getValue());
            }
        }

        // The category only affects the advancement id generated by the builder.
        // It is replaced below with the original path updated to the mod id.
        AdvancementHolder updatedAdvancement = builder.build(this, updatedKey, RecipeCategory.MISC);
        Identifier updatedId = Identifier.fromNamespaceAndPath(this.modId, originalAdvancement.id().getPath());
        return new AdvancementHolder(updatedId, updatedAdvancement.value());
    }
}
