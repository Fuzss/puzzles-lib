package fuzs.puzzleslib.common.api.resources.v2;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A builder for creating {@link Pack} and {@link RepositorySource} instances from a
 * {@link net.minecraft.server.packs.PackResources} implementation supplier.
 * <p>
 * This unifies the pack construction helpers provided by {@link PackResourcesHelper} and
 * {@link AbstractModPackResources}. Properties that are not explicitly set fall back to sensible defaults.
 */
public final class PackResourcesBuilder {
    /**
     * The pack type for this pack.
     */
    private final PackType type;
    /**
     * The id for this pack, used for internal references and is stored in <code>options.txt</code>.
     */
    private final Identifier packId;
    /**
     * The {@link net.minecraft.server.packs.PackResources} implementation supplier.
     */
    private final PackResourcesSupplier<?> packSupplier;
    /**
     * The title of this pack shown in the pack selection screen.
     */
    private Component title;
    /**
     * The description for this pack shown in the pack selection screen.
     */
    private Component description;
    /**
     * Whether this pack is required; a required pack cannot be disabled.
     */
    private boolean required = true;
    /**
     * Insertion end in the pack list, new packs are usually inserted at the top above vanilla.
     */
    private Pack.Position position = Pack.Position.TOP;
    /**
     * Whether this pack has a fixed position and cannot be moved up or down, like a server or world resource pack.
     */
    private boolean fixedPosition;
    /**
     * Whether this pack is hidden from user-facing screens like the resource pack and data pack selection screens.
     */
    private boolean hidden;
    /**
     * The pack compatibility.
     */
    private PackCompatibility packCompatibility = PackCompatibility.COMPATIBLE;
    /**
     * The feature flags provided by this pack.
     */
    private FeatureFlagSet featureFlagSet = FeatureFlagSet.of();

    /**
     * @param type         type marking this pack as containing data or resource pack resources
     * @param packId       id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param packSupplier {@link net.minecraft.server.packs.PackResources} implementation supplier
     */
    private PackResourcesBuilder(PackType type, Identifier packId, PackResourcesSupplier<?> packSupplier) {
        this.type = Objects.requireNonNull(type, "type is null");
        this.packId = Objects.requireNonNull(packId, "pack id is null");
        this.packSupplier = Objects.requireNonNull(packSupplier, "pack supplier is null");
        this.title = Objects.requireNonNull(PackResourcesHelper.getPackTitle(type), "title is null");
        this.description = Objects.requireNonNull(PackResourcesHelper.getPackDescription(packId.getNamespace()),
                "description is null");
    }

    /**
     * Creates a new builder for a built-in client resource pack.
     *
     * @param packId       id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param packSupplier {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @return the created builder instance
     */
    public static PackResourcesBuilder client(Identifier packId, PackResourcesSupplier<?> packSupplier) {
        return of(PackType.CLIENT_RESOURCES, packId, packSupplier);
    }

    /**
     * Creates a new builder for a built-in server data pack.
     *
     * @param packId       id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param packSupplier {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @return the created builder instance
     */
    public static PackResourcesBuilder server(Identifier packId, PackResourcesSupplier<?> packSupplier) {
        return of(PackType.SERVER_DATA, packId, packSupplier);
    }

    /**
     * Creates a new builder for a built-in pack of the given {@link PackType}.
     *
     * @param packType     type marking this pack as containing data or resource pack resources
     * @param packId       id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param packSupplier {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @return the created builder instance
     */
    public static PackResourcesBuilder of(PackType packType, Identifier packId, PackResourcesSupplier<?> packSupplier) {
        return new PackResourcesBuilder(packType, packId, packSupplier);
    }

    /**
     * Set the title of this pack shown in the pack selection screen.
     *
     * @param title the pack title
     * @return this builder instance
     */
    public PackResourcesBuilder title(Component title) {
        this.title = Objects.requireNonNull(title, "title is null");
        return this;
    }

    /**
     * Set the description for this pack shown in the pack selection screen.
     *
     * @param description the pack description
     * @return this builder instance
     */
    public PackResourcesBuilder description(Component description) {
        this.description = Objects.requireNonNull(description, "description is null");
        return this;
    }

    /**
     * Set whether this pack is required; a required pack cannot be disabled.
     *
     * @param required a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to
     *                 the left side; this is used for the vanilla resource pack
     * @return this builder instance
     */
    public PackResourcesBuilder required(boolean required) {
        this.required = required;
        return this;
    }

    /**
     * Set the insertion end in the pack list, new packs are usually inserted at the top above vanilla.
     *
     * @param position the pack position
     * @return this builder instance
     */
    public PackResourcesBuilder position(Pack.Position position) {
        this.position = Objects.requireNonNull(position, "position is null");
        return this;
    }

    /**
     * Set whether this pack has a fixed position and cannot be moved up or down.
     *
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @return this builder instance
     */
    public PackResourcesBuilder fixedPosition(boolean fixedPosition) {
        this.fixedPosition = fixedPosition;
        return this;
    }

    /**
     * Set whether this pack is hidden from user-facing screens.
     *
     * @param hidden controls whether the pack is hidden from user-facing screens like the resource pack and data pack
     *               selection screens, only available on Forge
     * @return this builder instance
     */
    public PackResourcesBuilder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    /**
     * Set the pack compatibility.
     *
     * @param packCompatibility the pack version, ideally retrieved from
     *                          {@link net.minecraft.WorldVersion#packVersion(PackType)}
     * @return this builder instance
     */
    public PackResourcesBuilder compatibility(PackCompatibility packCompatibility) {
        this.packCompatibility = Objects.requireNonNull(packCompatibility, "pack compatibility is null");
        return this;
    }

    /**
     * Set the feature flags provided by this pack.
     *
     * @param featureFlagSet the feature flags provided by this pack
     * @return this builder instance
     */
    public PackResourcesBuilder featureFlags(FeatureFlagSet featureFlagSet) {
        this.featureFlagSet = Objects.requireNonNull(featureFlagSet, "feature flag set is null");
        return this;
    }

    /**
     * Builds the {@link Pack} from the current configuration.
     * <p>
     * The title and description default to generated components provided by {@link PackResourcesHelper} when they are
     * not explicitly set on this builder.
     *
     * @return the built pack
     */
    public Pack buildPack() {
        return new Pack(this.location(), this.resources(), this.metadata(), this.selectionConfig());
    }

    /**
     * Creates the location info for this pack.
     *
     * @return the created location info instance
     */
    private PackLocationInfo location() {
        return new PackLocationInfo(this.packId.toString(), this.title, PackSource.BUILT_IN, Optional.empty());
    }

    /**
     * Creates the resources supplier wrapping the configured {@link PackResourcesSupplier}.
     *
     * @return the created resources supplier instance
     */
    private Pack.ResourcesSupplier resources() {
        return new Pack.ResourcesSupplier() {
            @Override
            public PackMetadataResources openMetadata(PackLocationInfo location) {
                return PackResourcesBuilder.this.packSupplier.apply(PackResourcesBuilder.this.type,
                        location,
                        this.metadata(PackResourcesBuilder.this.description, PackResourcesBuilder.this.featureFlagSet));
            }

            @Override
            public Stream<PackResources> openResources(PackLocationInfo location, Pack.Metadata metadata) {
                return Stream.of(PackResourcesBuilder.this.packSupplier.apply(PackResourcesBuilder.this.type,
                        location,
                        this.metadata(metadata.description(), metadata.requestedFeatures())));
            }

            /**
             * Creates the resource metadata for this pack from the given description and feature flags.
             *
             * @param description  the description for this pack shown in the pack selection screen
             * @param featureFlags the feature flags provided by this pack
             * @return the created resource metadata instance
             */
            private ResourceMetadata metadata(Component description, FeatureFlagSet featureFlags) {
                PackMetadataSection packSection = new PackMetadataSection(description,
                        new InclusiveRange<>(SharedConstants.getCurrentVersion()
                                .packVersion(PackResourcesBuilder.this.type)));
                if (featureFlags.isEmpty()) {
                    return ResourceMetadata.of(PackMetadataSection.forPackType(PackResourcesBuilder.this.type),
                            packSection);
                } else {
                    return ResourceMetadata.of(PackMetadataSection.forPackType(PackResourcesBuilder.this.type),
                            packSection,
                            FeatureFlagsMetadataSection.TYPE,
                            new FeatureFlagsMetadataSection(featureFlags));
                }
            }
        };
    }

    /**
     * Creates the metadata for this pack.
     *
     * @return the created metadata instance
     */
    private Pack.Metadata metadata() {
        return PackResourcesHelper.createPackInfo(this.description,
                this.packCompatibility,
                this.featureFlagSet,
                this.hidden);
    }

    /**
     * Creates the selection config from the configured required, position and fixed position properties.
     *
     * @return the created selection config instance
     */
    private PackSelectionConfig selectionConfig() {
        return new PackSelectionConfig(this.required, this.position, this.fixedPosition);
    }

    /**
     * Builds a {@link RepositorySource} providing the {@link Pack} created by {@link #buildPack()}.
     *
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public RepositorySource buildRepositorySource() {
        return (Consumer<Pack> consumer) -> {
            consumer.accept(this.buildPack());
        };
    }
}
