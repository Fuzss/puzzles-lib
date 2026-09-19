package fuzs.puzzleslib.fabric.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.AttributesContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

public record AttributesContextFabricImpl(BiomeModificationContext.AttributesContext context,
                                          EnvironmentAttributeMap biome) implements AttributesContext {
    @Override
    public void addAll(EnvironmentAttributeMap attributes) {
        this.context.addAll(attributes);
    }

    @Override
    public void addAll(EnvironmentAttributeMap.Builder attributes) {
        this.addAll(attributes.build());
    }

    @Override
    public <T> void set(EnvironmentAttribute<T> attribute, T value) {
        this.context.set(attribute, value);
    }

    @Override
    public <T, M> void setModifier(EnvironmentAttribute<T> attribute, AttributeModifier<T, M> modifier, M value) {
        this.context.setModifier(attribute, modifier, value);
    }

    @Override
    public <T> EnvironmentAttributeMap.@Nullable Entry<T, ?> get(EnvironmentAttribute<T> attribute) {
        return this.biome.get(attribute);
    }

    @Override
    public <T> T getValue(EnvironmentAttribute<T> attribute) {
        return this.applyModifier(attribute, attribute.defaultValue());
    }

    @Override
    public boolean contains(EnvironmentAttribute<?> attribute) {
        return this.biome.contains(attribute);
    }

    @Override
    public <T> T applyModifier(EnvironmentAttribute<T> attribute, T value) {
        return this.biome.applyModifier(attribute, value);
    }
}
