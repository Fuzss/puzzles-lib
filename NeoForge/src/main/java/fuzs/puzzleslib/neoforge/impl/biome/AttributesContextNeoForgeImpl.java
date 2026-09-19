package fuzs.puzzleslib.neoforge.impl.biome;

import fuzs.puzzleslib.common.api.biome.v2.context.AttributesContext;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.neoforged.neoforge.common.world.EnvironmentAttributeMapBuilder;
import org.jspecify.annotations.Nullable;

public record AttributesContextNeoForgeImpl(EnvironmentAttributeMapBuilder context) implements AttributesContext {
    @Override
    public void addAll(EnvironmentAttributeMap attributes) {
        this.context.putAll(attributes);
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
        this.context.modify(attribute, modifier, value);
    }

    @Override
    public <T> EnvironmentAttributeMap.@Nullable Entry<T, ?> get(EnvironmentAttribute<T> attribute) {
        return this.context.get(attribute);
    }

    @Override
    public <T> T getValue(EnvironmentAttribute<T> attribute) {
        return this.applyModifier(attribute, attribute.defaultValue());
    }

    @Override
    public boolean contains(EnvironmentAttribute<?> attribute) {
        return this.get(attribute) != null;
    }

    @Override
    public <T> T applyModifier(EnvironmentAttribute<T> attribute, T value) {
        EnvironmentAttributeMap.Entry<T, ?> entry = this.get(attribute);
        return entry != null ? entry.applyModifier(value) : value;
    }
}
