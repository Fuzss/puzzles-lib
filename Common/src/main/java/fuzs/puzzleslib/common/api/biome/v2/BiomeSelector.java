package fuzs.puzzleslib.common.api.biome.v2;

import fuzs.puzzleslib.common.api.core.v1.context.BiomeModificationsContext;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;

import java.util.function.BiPredicate;

/**
 * Decides whether a {@link BiomeTransformer} should be applied to a biome.
 * <p>
 * A selector is registered together with a transformer in {@link BiomeModificationsContext} and evaluated for every
 * biome in the current data pack. It receives the {@link RegistryAccess} of the current level and the {@link Holder} of
 * the biome being tested.
 *
 * @see BiomeTransformer
 * @see BiomeModificationsContext
 */
@FunctionalInterface
public interface BiomeSelector extends BiPredicate<RegistryAccess, Holder<Biome>> {

}
