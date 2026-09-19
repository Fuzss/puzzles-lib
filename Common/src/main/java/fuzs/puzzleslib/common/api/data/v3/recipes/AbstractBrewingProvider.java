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

/**
 * A base implementation of {@link VanillaBrewingProvider} for generating brewing recipes for the mod.
 * <p>
 * Vanilla containers and container transformations are reused, but the output is id-bound to the mod id, and container
 * transformations are only generated for potions of the mod. Subclasses implement {@link #buildMixes()}.
 */
public abstract class AbstractBrewingProvider extends VanillaBrewingProvider implements Runnable {

    /**
     * @param recipeOutput      the bootstrap context recipes are registered to
     * @param advancementOutput the bootstrap context recipe unlock advancements are registered to
     */
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

    /**
     * Registers all brewing mixes of this provider via the various {@code buildMix} methods inherited from
     * {@link BrewingProvider}.
     */
    @Override
    protected abstract void buildMixes();
}
