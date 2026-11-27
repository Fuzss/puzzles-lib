package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.common.api.client.core.v1.context.ResourcePackReloadListenersContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.resources.VanillaClientListeners;
import net.neoforged.neoforge.event.SortedReloadListenerEvent;

import java.util.Map;
import java.util.Objects;

public record ResourcePackReloadListenersContextNeoForgeImpl(AddClientReloadListenersEvent event) implements ResourcePackReloadListenersContext {
    private static final Map<Identifier, Identifier> VANILLA_CLIENT_RELOAD_LISTENERS = ImmutableMap.<Identifier, Identifier>builder()
            .put(LANGUAGES, VanillaClientListeners.LANGUAGE)
            .put(TEXTURES, VanillaClientListeners.TEXTURES)
            .put(SHADERS, VanillaClientListeners.SHADERS)
            .put(SOUNDS, VanillaClientListeners.SOUNDS)
            .put(SPLASH_TEXTS, VanillaClientListeners.SPLASHES)
            .put(ATLASES, VanillaClientListeners.ATLASES)
            .put(FONTS, VanillaClientListeners.FONTS)
            .put(GRASS_COLOR, VanillaClientListeners.GRASS_COLOR)
            .put(FOLIAGE_COLOR, VanillaClientListeners.FOLIAGE_COLOR)
            .put(DRY_FOLIAGE_COLOR, VanillaClientListeners.DRY_FOLIAGE_COLOR)
            .put(MODELS, VanillaClientListeners.MODELS)
            .put(EQUIPMENT_ASSETS, VanillaClientListeners.EQUIPMENT_ASSETS)
            .put(ENTITY_RENDERER, VanillaClientListeners.ENTITY_RENDERER)
            .put(BLOCK_ENTITY_RENDERER, VanillaClientListeners.BLOCK_ENTITY_RENDERER)
            .put(PARTICLE_RESOURCES, VanillaClientListeners.PARTICLE_RESOURCES)
            .put(WAYPOINT_STYLES, VanillaClientListeners.WAYPOINT_STYLES)
            .put(LEVEL_EXTRACTOR, VanillaClientListeners.LEVEL_EXTRACTOR)
            .put(CLOUD_RENDERER, VanillaClientListeners.CLOUD_RENDERER)
            .put(GPU_WARNLIST, VanillaClientListeners.GPU_WARNLIST)
            .put(REGIONAL_COMPLIANCES, VanillaClientListeners.REGIONAL_COMPLIANCES)
            .build();

    @Override
    public void registerReloadListener(Identifier id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "identifier is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        this.event.addListener(id, reloadListener);
    }

    @Override
    public void registerReloadListener(Identifier id, Identifier otherId, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "identifier is null");
        Objects.requireNonNull(otherId, "other identifier is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        registerReloadListener(this.event, id, otherId,
                reloadListener,
                VANILLA_CLIENT_RELOAD_LISTENERS);
    }

    private static void registerReloadListener(SortedReloadListenerEvent event, Identifier identifier, Identifier otherResourceLocation, PreparableReloadListener reloadListener, Map<Identifier, Identifier> vanillaReloadListeners) {
        if (vanillaReloadListeners.containsKey(identifier)) {
            event.addListener(otherResourceLocation, reloadListener);
            event.addDependency(vanillaReloadListeners.get(identifier), otherResourceLocation);
        } else if (vanillaReloadListeners.containsKey(otherResourceLocation)) {
            event.addListener(identifier, reloadListener);
            event.addDependency(identifier, vanillaReloadListeners.get(otherResourceLocation));
        } else {
            throw new RuntimeException("Unknown reload listeners: " + identifier + ", " + otherResourceLocation);
        }
    }
}
