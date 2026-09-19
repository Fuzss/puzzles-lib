package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.common.api.client.core.v1.context.EntitySpectatorShadersContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class EntitySpectatorShadersContextFabricImpl implements EntitySpectatorShadersContext {
    private static final Map<EntityType<?>, Identifier> ENTITY_SPECTATOR_SHADERS = new LinkedHashMap<>();

    @Override
    public void registerSpectatorShader(EntityType<?> entityType, Identifier location) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(location, "shader location is null");
        ENTITY_SPECTATOR_SHADERS.put(entityType, location);
    }

    public static Identifier getEntityPostEffect(Entity cameraEntity) {
        return ENTITY_SPECTATOR_SHADERS.get(cameraEntity.getType());
    }
}
