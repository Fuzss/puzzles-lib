package fuzs.puzzleslib.common.impl.core.proxy;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.common.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.common.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.common.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.common.impl.core.ModContext;
import fuzs.puzzleslib.common.impl.core.context.ModConstructorImpl;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.UnaryOperator;

public interface FactoriesProxy {

    ModConstructorImpl<ModConstructor> getModConstructorImpl();

    ModContext getModContext(String modId);

    RegistryFactory getRegistryFactory();

    ToolTypeHelper getToolTypeHelper();

    CombinedIngredients getCombinedIngredients();

    <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder);

    DataAttachmentRegistryImpl getDataAttachmentRegistry();

    RecipeOutput getTransformingRecipeOutput(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator);

    RecipeOutput getIdBoundRecipeOutput(String modId, BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput);

    void synchronizeRecipeSerializer(Holder<? extends RecipeSerializer<?>> serializer);
}
