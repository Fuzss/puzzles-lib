package fuzs.puzzleslib.common.api.data.v3.recipes;

import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.data.DataGenerationScopes;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.packs.VanillaBrewingProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractBrewingProvider extends VanillaBrewingProvider {

    public AbstractBrewingProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(ProxyImpl.get()
                .getIdBoundRecipeOutput(DataGenerationScopes.MOD_ID.get(), recipeOutput, advancementOutput));
    }

    @Override
    protected abstract void buildMixes();
}
