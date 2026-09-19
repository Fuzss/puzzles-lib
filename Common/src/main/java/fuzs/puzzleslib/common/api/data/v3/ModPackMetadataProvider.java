package fuzs.puzzleslib.common.api.data.v3;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.resources.v2.PackResourcesHelper;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

/**
 * A {@link PackMetadataGenerator} for generating the {@code pack.mcmeta} metadata of a built-in pack bundled with the
 * mod.
 * <p>
 * The pack description is derived from the display name of the mod via
 * {@link PackResourcesHelper#getPackDescription(String)}. Only the major pack format version is set, so the pack stays
 * compatible across different minor Minecraft versions.
 */
public final class ModPackMetadataProvider extends PackMetadataGenerator {

    /**
     * @param context the data provider context
     */
    public ModPackMetadataProvider(DataProviderContext context) {
        this(PackType.SERVER_DATA, context);
    }

    /**
     * @param packType the pack type
     * @param context  the data provider context
     */
    public ModPackMetadataProvider(PackType packType, DataProviderContext context) {
        this(packType, context.getModId(), context.getPackOutput());
    }

    /**
     * @param modId      the mod id
     * @param packOutput the pack output instance
     */
    public ModPackMetadataProvider(String modId, PackOutput packOutput) {
        this(PackType.SERVER_DATA, modId, packOutput);
    }

    /**
     * @param packType   the pack type
     * @param modId      the mod id
     * @param packOutput the pack output instance
     */
    public ModPackMetadataProvider(PackType packType, String modId, PackOutput packOutput) {
        super(packOutput);
        Component component = PackResourcesHelper.getPackDescription(modId);
        // Set only the major version here to stay compatible across different minor Minecraft versions.
        this.add(PackMetadataSection.forPackType(packType),
                new PackMetadataSection(component,
                        PackFormat.of(DetectedVersion.BUILT_IN.packVersion(packType).major()).minorRange()));
    }
}
