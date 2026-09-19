package fuzs.puzzleslib.common.api.biome.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;

import java.util.function.BiPredicate;

/**
 * Context given to a biome selector for deciding whether it applies to a biome or not.
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically
 * <code>net.fabricmc.fabric.api.biome.v1.BiomeModificationContext</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification
 * system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public interface BiomeSelector extends BiPredicate<RegistryAccess, Holder<Biome>> {

}
