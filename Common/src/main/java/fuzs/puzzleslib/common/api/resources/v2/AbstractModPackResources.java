package fuzs.puzzleslib.common.api.resources.v2;

import fuzs.puzzleslib.common.api.core.v1.ModContainer;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * A basic implementation of {@link PackResources} fit to be used for a built-in pack providing runtime-generated
 * assets.
 * <p>
 * This pack automatically uses the mod's mod logo for the pack icon.
 */
public abstract class AbstractModPackResources implements PackResources {
    /**
     * The pack type for this pack, set internally during construction.
     */
    private final PackType type;
    /**
     * Location info of this pack, set internally during construction.
     */
    private final PackLocationInfo location;
    /**
     * The metadata for the {@code pack.mcmeta} section, set internally during construction.
     */
    private final ResourceMetadata metadata;
    /**
     * The parsed pack id for this pack, set internally during construction.
     */
    private final Identifier packId;

    /**
     * @param type     type marking this pack as containing data or resource pack resources
     * @param location location info of this pack
     * @param metadata the metadata for the {@code pack.mcmeta} section
     */
    public AbstractModPackResources(PackType type, PackLocationInfo location, ResourceMetadata metadata) {
        this.type = type;
        this.location = location;
        this.metadata = metadata;
        this.packId = extractPackId(this.packId());
    }

    /**
     * Parses the pack id and ensures it contains an explicit namespace that is not {@code minecraft}.
     *
     * @param packId the pack id to parse
     * @return the parsed pack id
     *
     * @throws IdentifierException if the pack id cannot be parsed or uses the default namespace
     */
    private static Identifier extractPackId(String packId) {
        Identifier id = Identifier.tryParse(packId);
        if (id == null) {
            throw new IdentifierException("Unable to parse namespace from pack id: " + packId);
        }

        if (Identifier.DEFAULT_NAMESPACE.equals(id.getNamespace())) {
            throw new IdentifierException("Pack id must not use the default namespace: " + packId);
        }

        return id;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... path) {
        return ModLoaderEnvironment.INSTANCE.getModContainer(this.packId.getNamespace())
                .flatMap((ModContainer container) -> container.findResource(path))
                .<IoSupplier<InputStream>>map((Path resource) -> {
                    return () -> Files.newInputStream(resource);
                })
                .orElse(null);
    }

    @Override
    public abstract @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier location);

    @Override
    public abstract void listResources(PackType type, String namespace, String directory, PackResources.ResourceOutput output);

    @Override
    public Set<String> getNamespaces(PackType type) {
        return this.type == type ? Collections.singleton(this.packId.getNamespace()) : Collections.emptySet();
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) {
        return this.metadata.getSection(type).orElse(null);
    }

    @Override
    public PackLocationInfo location() {
        return this.location;
    }

    @Override
    public void close() {
        // NO-OP
    }
}
