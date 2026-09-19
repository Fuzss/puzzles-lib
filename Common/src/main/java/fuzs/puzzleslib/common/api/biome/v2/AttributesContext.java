package fuzs.puzzleslib.common.api.biome.v2;

import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

/**
 * @see EnvironmentAttributeMap
 * @see EnvironmentAttributeMap.Builder
 */
public interface AttributesContext {
    /**
     * @see EnvironmentAttributeMap.Builder#putAll(EnvironmentAttributeMap)
     */
    void addAll(EnvironmentAttributeMap attributes);

    /**
     * @see EnvironmentAttributeMap.Builder#putAll(EnvironmentAttributeMap)
     */
    void addAll(EnvironmentAttributeMap.Builder attributes);

    /**
     * @see EnvironmentAttributeMap.Builder#set(EnvironmentAttribute, Object)
     */
    <T> void set(EnvironmentAttribute<T> attribute, T value);

    /**
     * @see EnvironmentAttributeMap.Builder#modify(EnvironmentAttribute, AttributeModifier, Object)
     */
    <T, M> void setModifier(EnvironmentAttribute<T> attribute, AttributeModifier<T, M> modifier, M value);

    /**
     * @see EnvironmentAttributeMap#get(EnvironmentAttribute)
     */
    <T> EnvironmentAttributeMap.@Nullable Entry<T, ?> get(EnvironmentAttribute<T> attribute);

    /**
     * @see EnvironmentAttributeMap#get(EnvironmentAttribute)
     * @see EnvironmentAttribute#defaultValue()
     */
    <T> T getValue(EnvironmentAttribute<T> attribute);

    /**
     * @see EnvironmentAttributeMap#contains(EnvironmentAttribute)
     */
    boolean contains(EnvironmentAttribute<?> attribute);

    /**
     * @see EnvironmentAttributeMap#applyModifier(EnvironmentAttribute, Object)
     */
    <T> T applyModifier(EnvironmentAttribute<T> attribute, T value);
}
