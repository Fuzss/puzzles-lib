package fuzs.puzzleslib.common.api.client.renderer.v1;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.common.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.util.context.ContextKey;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An implementation for custom data attached to render states.
 */
public final class RenderStateExtraData {
    /**
     * These render states support extra data on all available mod loaders.
     */
    private static final Collection<Class<?>> BASE_RENDER_STATE_TYPES = List.of(EntityRenderState.class,
            LevelRenderState.class,
            PlayerRenderState.class,
            MapRenderState.class,
            MapRenderState.MapDecorationRenderState.class);

    /**
     * Gets a nullable value for a key from the provided render state.
     *
     * @param state the render state
     * @param key   the render property key
     * @param <T>   the value type
     * @return the render property value associated with the key
     */
    public static <T> @Nullable T get(Object state, ContextKey<T> key) {
        Preconditions.checkArgument(hasExtraData(state), "state is missing render state extra data");
        return ClientProxyImpl.get().getRenderStateData(state, key);
    }

    /**
     * Gets a value for a key from the provided render state, or the provided fallback when not present.
     *
     * @param state        the render state
     * @param key          the render property key
     * @param defaultValue the fallback value
     * @param <T>          the value type
     * @return the render property value associated with the key
     */
    public static <T> T getOrDefault(Object state, ContextKey<T> key, T defaultValue) {
        Preconditions.checkArgument(hasExtraData(state), "state is missing render state extra data");
        T value = ClientProxyImpl.get().getRenderStateData(state, key);
        return value != null ? value : defaultValue;
    }

    /**
     * Does the provided render state contain a value for a key.
     *
     * @param state the render state
     * @param key   the render property key
     * @param <T>   the value type
     * @return is there a render property value associated with the key
     */
    public static <T> boolean has(Object state, ContextKey<T> key) {
        Preconditions.checkArgument(hasExtraData(state), "state is missing render state extra data");
        return ClientProxyImpl.get().getRenderStateData(state, key) != null;
    }

    /**
     * Sets a non-null value for a key to the provided render state.
     *
     * @param state the render state
     * @param key   the render property key
     * @param value the render property value
     * @param <T>   the value type
     */
    public static <T> void set(Object state, ContextKey<T> key, T value) {
        Preconditions.checkArgument(hasExtraData(state), "state is missing render state extra data");
        Objects.requireNonNull(value, "value is null");
        ClientProxyImpl.get().setRenderStateData(state, key, value);
    }

    /**
     * Removes a value for a key from the provided render state.
     *
     * @param state the render state
     * @param key   the render property key
     * @param <T>   the value type
     */
    public static <T> void remove(Object state, ContextKey<T> key) {
        Preconditions.checkArgument(hasExtraData(state), "state is missing render state extra data");
        ClientProxyImpl.get().setRenderStateData(state, key, null);
    }

    /**
     * Does the provided render state support extra data.
     *
     * @param state the render state
     * @return is the render state supporting extra data
     */
    public static boolean hasExtraData(Object state) {
        for (Class<?> clazz : BASE_RENDER_STATE_TYPES) {
            if (clazz.isInstance(state)) {
                return true;
            }
        }

        return false;
    }
}
