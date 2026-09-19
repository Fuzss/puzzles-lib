package fuzs.puzzleslib.common.api.biome.v2.context;

import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

/**
 * Context for modifying the environment attributes of a biome.
 *
 * @see EnvironmentAttributeMap
 * @see EnvironmentAttributeMap.Builder
 */
public interface AttributesContext {
    /**
     * Add all attributes of the given map to the biome.
     *
     * @param attributes the attributes to add
     * @see EnvironmentAttributeMap.Builder#putAll(EnvironmentAttributeMap)
     */
    void addAll(EnvironmentAttributeMap attributes);

    /**
     * Add all attributes of the given builder to the biome.
     *
     * @param attributes the builder containing the attributes to add
     * @see EnvironmentAttributeMap.Builder#putAll(EnvironmentAttributeMap)
     */
    void addAll(EnvironmentAttributeMap.Builder attributes);

    /**
     * Set the value of an attribute.
     *
     * @param attribute the attribute to set
     * @param value     the value to set
     * @param <T>       the attribute value type
     * @see EnvironmentAttributeMap.Builder#set(EnvironmentAttribute, Object)
     */
    <T> void set(EnvironmentAttribute<T> attribute, T value);

    /**
     * Apply a modifier to the value of an attribute.
     *
     * @param attribute the attribute to modify
     * @param modifier  the modifier to apply
     * @param value     the modifier argument
     * @param <T>       the attribute value type
     * @param <M>       the modifier argument type
     * @see EnvironmentAttributeMap.Builder#modify(EnvironmentAttribute, AttributeModifier, Object)
     */
    <T, M> void setModifier(EnvironmentAttribute<T> attribute, AttributeModifier<T, M> modifier, M value);

    /**
     * Get the entry of an attribute.
     *
     * @param attribute the attribute to get the entry of
     * @param <T>       the attribute value type
     * @return the attribute entry, or null if the attribute is not present
     *
     * @see EnvironmentAttributeMap#get(EnvironmentAttribute)
     */
    <T> EnvironmentAttributeMap.@Nullable Entry<T, ?> get(EnvironmentAttribute<T> attribute);

    /**
     * Get the value of an attribute, resolved using its default value when the attribute is not present.
     *
     * @param attribute the attribute to get the value of
     * @param <T>       the attribute value type
     * @return the attribute value
     *
     * @see EnvironmentAttributeMap#get(EnvironmentAttribute)
     * @see EnvironmentAttribute#defaultValue()
     */
    <T> T getValue(EnvironmentAttribute<T> attribute);

    /**
     * Check whether an attribute is present.
     *
     * @param attribute the attribute to check
     * @return whether the attribute is present
     *
     * @see EnvironmentAttributeMap#contains(EnvironmentAttribute)
     */
    boolean contains(EnvironmentAttribute<?> attribute);

    /**
     * Apply the modifier of an attribute to a value, or return the value unchanged when the attribute is not present.
     *
     * @param attribute the attribute to apply the modifier of
     * @param value     the value to modify
     * @param <T>       the attribute value type
     * @return the modified value
     *
     * @see EnvironmentAttributeMap#applyModifier(EnvironmentAttribute, Object)
     */
    <T> T applyModifier(EnvironmentAttribute<T> attribute, T value);
}
