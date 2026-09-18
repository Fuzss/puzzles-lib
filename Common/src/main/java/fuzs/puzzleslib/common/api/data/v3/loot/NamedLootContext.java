package fuzs.puzzleslib.common.api.data.v3.loot;

import net.minecraft.data.loot.LootTableSubProvider;

/**
 * An enhanced {@link LootTableSubProvider.Context} that additionally provides the generating mod id.
 * <p>
 * Instances are created by the loader-specific registration (on NeoForge this is handled by
 * {@code DataProviderBuilder}), so that a loot table sub-provider can scope its validation to its own mod.
 */
public interface NamedLootContext extends LootTableSubProvider.Context {
    /**
     * @return the generating mod id
     */
    String getModId();
}
