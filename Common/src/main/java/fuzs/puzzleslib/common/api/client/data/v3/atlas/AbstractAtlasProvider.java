package fuzs.puzzleslib.common.api.client.data.v3.atlas;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A base implementation of {@link AtlasProvider} for generating atlas definition files for the mod.
 * <p>
 * Subclasses implement {@link #addAtlases()} and register atlas contents via the various {@code add} methods, which are
 * then emitted by {@link #run(CachedOutput)}. The contents are added to the definitions of the known vanilla atlases.
 */
public abstract class AbstractAtlasProvider extends AtlasProvider {
    /**
     * The known vanilla atlas configs, mapped by their texture id.
     */
    private final Map<Identifier, AtlasManager.AtlasConfig> knownAtlases = AtlasManager.KNOWN_ATLASES.stream()
            .collect(Collectors.toMap(AtlasManager.AtlasConfig::textureId, Function.identity()));
    /**
     * The sprite sources to generate, mapped by their atlas definition location.
     */
    private final Map<Identifier, List<SpriteSource>> values = new LinkedHashMap<>();

    /**
     * @param context the data provider context
     */
    public AbstractAtlasProvider(DataProviderContext context) {
        this(context.getPackOutput());
    }

    /**
     * @param packOutput the pack output instance
     */
    public AbstractAtlasProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        this.addAtlases();
        return CompletableFuture.allOf(this.values.entrySet()
                .stream()
                .map((Map.Entry<Identifier, List<SpriteSource>> entry) -> {
                    return this.storeAtlas(output, entry.getKey(), entry.getValue());
                })
                .toArray(CompletableFuture[]::new));
    }

    /**
     * Registers all atlas contents of this provider via the various {@code add} methods.
     */
    public abstract void addAtlases();

    /**
     * Adds the given sprite to the atlas it belongs to by wrapping it in a single-file sprite source.
     *
     * @param sprite the sprite to add
     */
    protected void addMaterial(SpriteId sprite) {
        this.add(this.knownAtlases.get(sprite.atlasLocation()).definitionLocation(), forMaterial(sprite));
    }

    /**
     * Adds the given sprite sources to the atlas with the given id.
     *
     * @param id      the atlas definition location
     * @param sources the sprite sources
     */
    protected void add(Identifier id, SpriteSource... sources) {
        this.add(id, Arrays.asList(sources));
    }

    /**
     * Adds the given sprite sources to the atlas with the given id.
     *
     * @param id      the atlas definition location
     * @param sources the sprite sources
     */
    protected void add(Identifier id, List<SpriteSource> sources) {
        this.values.computeIfAbsent(id, (Identifier _) -> new ArrayList<>()).addAll(sources);
    }
}
