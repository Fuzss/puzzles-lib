package fuzs.puzzleslib.common.impl.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * A base {@link RecipeOutput} implementation for data generation that updates all recipes and recipe unlock
 * advancements to the mod id.
 * <p>
 * Vanilla recipe builders derive the recipe id from the namespace of the result item, which places recipes for items of
 * other namespaces (like vanilla items) in their namespace. This implementation instead keeps the original path and
 * swaps the namespace for the mod id. Recipe unlock advancements are rebuilt accordingly, as they embed the recipe id
 * in both a criterion and a reward.
 */
public class IdBoundRecipeOutput implements RecipeOutput {
    public final String modId;
    private final BootstrapContext<Recipe<?>> recipeOutput;
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
            Advancement advancement = this.updateAdvancement(advancementHolder.value(), key, updatedKey);
            Identifier advancementId = Identifier.fromNamespaceAndPath(this.modId, advancementHolder.id().getPath());
            new AdvancementHolder(advancementId, advancement).register(this.advancementOutput);
        }
    }

    @Override
    public Advancement.Builder advancement() {
        return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
    }

    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
        return this.recipeOutput.lookup(key);
    }

    @Deprecated
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
     *
     * @param advancement the original advancement
     * @param originalKey the original recipe id
     * @param updatedKey  the recipe id updated to the mod id
     * @return the advancement referencing the updated recipe id
     */
    private Advancement updateAdvancement(Advancement advancement, ResourceKey<Recipe<?>> originalKey, ResourceKey<Recipe<?>> updatedKey) {
        Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        for (Map.Entry<String, Criterion<?>> entry : advancement.criteria().entrySet()) {
            Criterion<?> criterion = entry.getValue();
            if (criterion.triggerInstance() instanceof RecipeUnlockedTrigger.TriggerInstance(
                    Optional<Holder<LootItemCondition>> player, HolderSet<Recipe<?>> recipes
            )) {
                HolderSet<Recipe<?>> holderSet = this.updateRecipeHolders(recipes, originalKey, updatedKey);
                criterion = new Criterion<>(CriteriaTriggers.RECIPE_UNLOCKED,
                        new RecipeUnlockedTrigger.TriggerInstance(player, holderSet));
            }

            criteria.put(entry.getKey(), criterion);
        }

        AdvancementRewards rewards = advancement.rewards();
        List<ResourceKey<Recipe<?>>> updatedRecipes = rewards.recipes()
                .stream()
                .map((ResourceKey<Recipe<?>> key) -> key.equals(originalKey) ? updatedKey : key)
                .toList();
        AdvancementRewards updatedRewards = new AdvancementRewards(rewards.experience(),
                rewards.loot(),
                updatedRecipes,
                rewards.function());
        return new Advancement(advancement.parent(),
                advancement.display(),
                updatedRewards,
                criteria,
                advancement.requirements(),
                advancement.sendsTelemetryEvent());
    }

    /**
     * Replaces the holder of the original recipe id with the holder of the updated recipe id.
     *
     * @param holderSet   the original holder set
     * @param originalKey the original recipe id
     * @param updatedKey  the recipe id updated to the mod id
     * @return the holder set referencing the updated recipe id
     */
    private HolderSet<Recipe<?>> updateRecipeHolders(HolderSet<Recipe<?>> holderSet, ResourceKey<Recipe<?>> originalKey, ResourceKey<Recipe<?>> updatedKey) {
        List<Holder<Recipe<?>>> holders = new ArrayList<>(holderSet.size());
        for (Holder<Recipe<?>> holder : holderSet) {
            Optional<ResourceKey<Recipe<?>>> optionalKey = holder.unwrapKey();
            if (optionalKey.isPresent() && optionalKey.get().equals(originalKey)) {
                // the holder is bound once the recipe has been registered above
                holders.add(this.recipeOutput.lookup(Registries.RECIPE).getOrThrow(updatedKey));
            } else {
                holders.add(holder);
            }
        }

        return HolderSet.direct(holders);
    }
}
