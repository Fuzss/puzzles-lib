package fuzs.puzzleslib.common.api.resources.v1;

import fuzs.puzzleslib.common.api.core.v1.ModContainer;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.flag.FeatureFlagSet;

/**
 * This class provides some simple helper methods for constructing simple
 * {@link net.minecraft.server.packs.PackResources} implementation for either the client or server.
 */
public final class PackResourcesHelper {

    private PackResourcesHelper() {
        // NO-OP
    }

    /**
     * Create a simple pack title for a {@link PackType}.
     *
     * @param packType the pack type
     * @return the title component
     */
    public static Component getPackTitle(PackType packType) {
        return Component.literal(
                "Generated " + (packType == PackType.CLIENT_RESOURCES ? "Resource" : "Data") + " Pack");
    }

    /**
     * Create a fancy pack description for dynamic resources from a mod.
     *
     * @param modId the source mod for the pack
     * @return the description component
     */
    public static Component getPackDescription(String modId) {
        return ModLoaderEnvironment.INSTANCE.getModContainer(modId)
                .map(ModContainer::getDisplayName)
                .map((String name) -> {
                    return Component.literal("Resources for " + name);
                })
                .orElseGet(() -> Component.literal("Resources (" + modId + ")"));
    }

    /**
     * Creates the location for a built-in pack bundled in the mod jar.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param identifier the identifier for the pack
     * @param packType   the pack type
     * @return the pack location inside {@code resources}
     */
    public static Identifier getBuiltInPack(Identifier identifier, PackType packType) {
        return identifier.withPrefix(packType.getDirectory() + "/" + identifier.getNamespace() + "/" + (
                packType == PackType.CLIENT_RESOURCES ? "resourcepacks" : "datapacks") + "/");
    }

    /**
     * Creates a new {@link Pack.Metadata} instance with additional parameters only supported on NeoForge.
     *
     * @param description       the pack description component
     * @param packCompatibility the pack version, ideally retrieved from
     *                          {@link net.minecraft.WorldVersion#packVersion(PackType)}
     * @param featureFlagSet    the feature flags provided by this pack
     * @param isHidden          controls whether the pack is hidden from user-facing screens like the resource pack and
     *                          data pack selection screens
     * @return the created pack info instance
     */
    public static Pack.Metadata createPackInfo(Component description, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean isHidden) {
        return ProxyImpl.get().createPackInfo(description, packCompatibility, featureFlagSet, isHidden);
    }

    /**
     * Is the pack hidden from the user e.g., in {@link net.minecraft.client.gui.screens.packs.PackSelectionScreen} and
     * in {@link net.minecraft.server.commands.DataPackCommand}.
     *
     * @param pack the pack
     * @return is the pack hidden
     */
    public static boolean isPackHidden(Pack pack) {
        return ProxyImpl.get().isPackHidden(pack);
    }

    /**
     * Set the pack hidden from the user e.g., in {@link net.minecraft.client.gui.screens.packs.PackSelectionScreen} and
     * in {@link net.minecraft.server.commands.DataPackCommand}.
     *
     * @param pack the pack
     */
    public static void setPackHidden(Pack pack) {
        ProxyImpl.get().setPackHidden(pack, true);
    }
}
