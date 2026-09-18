package fuzs.puzzleslib.common.api.data.v2;

import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.packs.VanillaBrewingProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractBrewingProvider extends VanillaBrewingProvider {

    public AbstractBrewingProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(ProxyImpl.get().getIdBoundRecipeOutput("minecraft", recipeOutput, advancementOutput));
    }

    @Override
    protected abstract void buildMixes();
}
