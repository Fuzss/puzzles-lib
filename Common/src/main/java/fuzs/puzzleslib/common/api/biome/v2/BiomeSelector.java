package fuzs.puzzleslib.common.api.biome.v2;

import fuzs.puzzleslib.common.api.core.v1.context.BiomeTransformationsContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;

import java.util.function.BiPredicate;

/**
 * Decides whether a {@link BiomeTransformer} should be applied to a biome.
 * <p>
 * A selector is registered together with a transformer in {@link BiomeTransformationsContext} and evaluated for every
 * biome in the current data pack. It receives a {@link HolderGetter.Provider} for the registries of the current level
 * and the {@link Holder} of the biome being tested.
 *
 * @see BiomeTransformer
 * @see BiomeTransformationsContext
 */
@FunctionalInterface
public interface BiomeSelector extends BiPredicate<HolderGetter.Provider, Holder<Biome>> {

}
