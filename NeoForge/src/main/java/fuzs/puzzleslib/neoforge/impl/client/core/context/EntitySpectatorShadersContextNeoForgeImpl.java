package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.common.api.client.core.v1.context.EntitySpectatorShadersContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.RegisterEntitySpectatorShadersEvent;

import java.util.Objects;

public record EntitySpectatorShadersContextNeoForgeImpl(RegisterEntitySpectatorShadersEvent event) implements EntitySpectatorShadersContext {

    @Override
    public void registerSpectatorShader(EntityType<?> entityType, Identifier location) {
        Objects.requireNonNull(entityType, "entity type is null");
        Objects.requireNonNull(location, "identifier is null");
        this.event.register(entityType, location);
    }
}
