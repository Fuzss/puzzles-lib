package fuzs.puzzleslib.fabric.impl.client.key;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import fuzs.puzzleslib.common.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.common.api.client.key.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingContext;
import net.minecraft.client.KeyMapping;

public final class FabricKeyMappingHelper implements KeyMappingHelper {
    public static final BiMap<KeyActivationContext, KeyMappingContext> KEY_CONTEXTS = ImmutableBiMap.of(
            KeyActivationContext.UNIVERSAL, KeyMappingContext.ALL,
            KeyActivationContext.GAME, KeyMappingContext.IN_GAME,
            KeyActivationContext.SCREEN, KeyMappingContext.IN_SCREEN
    );

    @Override
    public KeyActivationContext getKeyActivationContext(KeyMapping keyMapping) {
        return KEY_CONTEXTS.inverse().getOrDefault(KeyMappingContext.of(keyMapping), KeyActivationContext.UNIVERSAL);
    }

    @Override
    public boolean isConflictingWith(KeyMapping keyMapping, KeyMapping otherKeyMapping) {
        return KeyMappingContext.conflicts(KeyMappingContext.of(keyMapping), KeyMappingContext.of(otherKeyMapping));
    }
}
