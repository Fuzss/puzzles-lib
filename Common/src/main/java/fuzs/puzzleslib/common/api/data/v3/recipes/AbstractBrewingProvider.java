package fuzs.puzzleslib.common.api.data.v3.recipes;

import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.data.DataGenerationScopes;
import fuzs.puzzleslib.common.impl.data.IdBoundRecipeOutput;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.BrewingProvider;
import net.minecraft.data.recipes.BrewingRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaBrewingProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractBrewingProvider extends VanillaBrewingProvider implements Runnable {

    public AbstractBrewingProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(ProxyImpl.get()
                .getIdBoundRecipeOutput(DataGenerationScopes.MOD_ID.get(), recipeOutput, advancementOutput));
    }

    @Override
    protected void buildTransformations() {
        String modId = ((IdBoundRecipeOutput) this.output).modId;
        for (BrewingProvider.ContainerTransformation transformation : this.containerTransformations) {
            for (Holder<Potion> potion : this.potions) {
                if (potion.is((ResourceKey<Potion> key) -> key.identifier().getNamespace().equals(modId))) {
                    this.save(BrewingRecipeBuilder.brewingContainerTransform(transformation.container(),
                            potion,
                            transformation.reagent(),
                            transformation.output()));
                }
            }
        }
    }

    @Override
    public final void run() {
        this.buildRecipes();
    }

    @Override
    protected abstract void buildMixes();
}
