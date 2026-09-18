package fuzs.puzzleslib.common.api.resources.v2;

import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceMetadata;

/**
 * A functional interface for creating {@link PackResources} instances during pack construction, used by
 * {@link PackResourcesBuilder}.
 *
 * @param <T> the type of the created pack resources
 */
@FunctionalInterface
public interface PackResourcesSupplier<T extends PackResources> {
    /**
     * Creates a new {@link PackResources} instance for the given pack type.
     *
     * @param packType the type marking this pack as containing data or resource pack resources
     * @param location location info of this pack
     * @param metadata the metadata for the {@code pack.mcmeta} section
     * @return the created pack resources instance
     */
    T apply(PackType packType, PackLocationInfo location, ResourceMetadata metadata);
}
