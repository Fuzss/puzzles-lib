package fuzs.puzzleslib.common.api.client.data.v3.particles;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A base implementation of {@link DataProvider} for generating particle description files for the mod.
 * <p>
 * Subclasses implement {@link #addParticles()} and register particle descriptions via the various {@code add} methods,
 * which are then emitted by {@link #run(CachedOutput)}. When a client resource manager is available, the textures
 * referenced by the descriptions are validated to exist.
 */
public abstract class AbstractParticleProvider implements DataProvider {
    /**
     * The codec for serializing particle description files.
     */
    public static final Codec<ParticleDescription> CODEC = Identifier.CODEC.listOf()
            .fieldOf("textures")
            .xmap(ParticleDescription::new, ParticleDescription::getTextures)
            .codec();

    /**
     * The particle descriptions to generate, mapped by their id.
     */
    private final Map<Identifier, ParticleDescription> values = new LinkedHashMap<>();
    /**
     * The path provider for the particle description files.
     */
    private final PackOutput.PathProvider pathProvider;
    /**
     * The client resource manager used for validating textures, may be {@code null} when unavailable.
     */
    private final @Nullable ResourceManager clientResourceManager;

    /**
     * @param context the data provider context
     */
    public AbstractParticleProvider(DataProviderContext context) {
        this(context.getPackOutput(), context.getClientResources());
    }

    /**
     * @param packOutput            the pack output instance
     * @param clientResourceManager the client resource manager used for validating textures, may be {@code null}
     */
    public AbstractParticleProvider(PackOutput packOutput, @Nullable ResourceManager clientResourceManager) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "particles");
        this.clientResourceManager = clientResourceManager;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        this.addParticles();
        return DataProvider.saveAll(cachedOutput, CODEC, this.pathProvider, this.values);
    }

    /**
     * Registers all particle descriptions of this provider via the various {@code add} methods.
     */
    public abstract void addParticles();

    /**
     * Adds a particle description for the given particle type with a single texture matching the particle type id.
     *
     * @param particleType the particle type
     */
    public void add(ParticleType<?> particleType) {
        this.add(particleType, -1);
    }

    /**
     * Adds a particle description for the given particle type, using a range of textures generated from the particle
     * type id.
     *
     * @param particleType the particle type
     * @param indexEnd     the last texture index, or {@code -1} for a single texture
     */
    public void add(ParticleType<?> particleType, int indexEnd) {
        this.add(particleType, BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), indexEnd);
    }

    /**
     * Adds a particle description for the given particle type, using a range of textures generated from the particle
     * type id.
     *
     * @param particleType the particle type
     * @param indexStart   the first texture index
     * @param indexEnd     the last texture index, or {@code -1} for a single texture
     */
    public void add(ParticleType<?> particleType, int indexStart, int indexEnd) {
        this.add(particleType, BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), indexStart, indexEnd);
    }

    /**
     * Adds a particle description for the given particle type, using a range of textures generated from the given
     * texture identifier.
     *
     * @param particleType the particle type
     * @param identifier   the texture identifier
     * @param indexEnd     the last texture index, or {@code -1} for a single texture
     */
    public void add(ParticleType<?> particleType, Identifier identifier, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), identifier, indexEnd);
    }

    /**
     * Adds a particle description for the given particle type, using a range of textures generated from the given
     * texture identifier.
     *
     * @param particleType the particle type
     * @param identifier   the texture identifier
     * @param indexStart   the first texture index
     * @param indexEnd     the last texture index, or {@code -1} for a single texture
     */
    public void add(ParticleType<?> particleType, Identifier identifier, int indexStart, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), identifier, indexStart, indexEnd);
    }

    /**
     * Adds a particle description for the given id, using a range of textures generated from the given texture
     * identifier.
     *
     * @param id         the particle description id
     * @param identifier the texture identifier
     * @param indexEnd   the last texture index, or {@code -1} for a single texture
     */
    public void add(Identifier id, Identifier identifier, int indexEnd) {
        this.add(id, identifier, 0, indexEnd);
    }

    /**
     * Adds a particle description for the given id, using a range of textures generated from the given texture
     * identifier. Each texture is named by appending its index to the texture path, e.g. {@code namespace:path_0}. If
     * the end index is less than the start index, the texture order is reversed.
     *
     * @param id         the particle description id
     * @param identifier the texture identifier
     * @param indexStart the first texture index
     * @param indexEnd   the last texture index, or {@code -1} for a single texture
     */
    public void add(Identifier id, Identifier identifier, int indexStart, int indexEnd) {
        if (indexEnd == -1) {
            this.add(id, new ParticleDescription(Collections.singletonList(identifier)));
        } else {
            List<Identifier> textures = IntStream.rangeClosed(Math.min(indexStart, indexEnd),
                            Math.max(indexStart, indexEnd))
                    .mapToObj((int index) -> Identifier.fromNamespaceAndPath(identifier.getNamespace(),
                            identifier.getPath() + "_" + index))
                    .collect(Collectors.toList());
            if (indexEnd < indexStart) {
                Collections.reverse(textures);
            }

            this.add(id, new ParticleDescription(textures));
        }
    }

    /**
     * Adds the given particle description for the given id. When a client resource manager is available, all
     * referenced textures are validated to exist before the description is added.
     *
     * @param id                  the particle description id
     * @param particleDescription the particle description
     */
    public void add(Identifier id, ParticleDescription particleDescription) {
        if (this.clientResourceManager != null) {
            this.validate(id, particleDescription, this.clientResourceManager);
        }

        if (this.values.putIfAbsent(id, particleDescription) != null) {
            throw new IllegalStateException("Duplicate particle description: " + id);
        }
    }

    /**
     * Validates that all textures referenced by the particle description exist as {@code textures/particle/<path>.png}.
     *
     * @param id                  the particle description id, used for the error message
     * @param particleDescription the particle description
     * @param resourceManager     the client resource manager used for looking up textures
     */
    protected void validate(Identifier id, ParticleDescription particleDescription, ResourceManager resourceManager) {
        Objects.requireNonNull(resourceManager, "resource manager is null");
        List<String> missingTextures = particleDescription.getTextures().stream().filter((Identifier identifier) -> {
            return resourceManager.getResource(identifier.withPath((String string) -> {
                return "textures/particle/" + string + ".png";
            })).isEmpty();
        }).map(Identifier::toString).toList();
        if (!missingTextures.isEmpty()) {
            throw new IllegalArgumentException(
                    "Couldn't define particle description %s as it is missing following texture(s): %s".formatted(id,
                            String.join(",", missingTextures)));
        }
    }

    @Override
    public String getName() {
        return "Particle Descriptions";
    }
}
