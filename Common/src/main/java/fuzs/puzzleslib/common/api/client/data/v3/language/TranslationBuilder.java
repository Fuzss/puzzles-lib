package fuzs.puzzleslib.common.api.client.data.v3.language;

import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.Objects;
import java.util.function.Function;

/**
 * A builder for providing translations to a language file, passed to
 * {@link AbstractLanguageProvider#addTranslations(TranslationBuilder)}.
 * <p>
 * The builder is a functional interface: the single abstract {@link #add(String, String)} method accepts a raw
 * translation key and value, while all other methods are convenience overloads deriving the translation key from common
 * game objects like blocks, items, entity types, and registry keys.
 */
@FunctionalInterface
public interface TranslationBuilder {

    /**
     * Adds a translation for the given translation key.
     *
     * @param translationKey the translation key
     * @param value          the translation value
     */
    void add(String translationKey, String value);

    /**
     * Adds a translation for the given translation key with an additional key appended, separated by a period. The
     * additional key is skipped when it is empty.
     *
     * @param translationKey the base translation key
     * @param additionalKey  the additional key appended to the base key
     * @param value          the translation value
     */
    default void add(String translationKey, String additionalKey, String value) {
        Objects.requireNonNull(additionalKey, "additional key is null");
        this.add(translationKey + (additionalKey.isEmpty() ? "" : "." + additionalKey), value);
    }

    /**
     * Adds a translation for the given identifier.
     *
     * @param identifier the identifier used for deriving the translation key
     * @param value      the translation value
     */
    default void add(Identifier identifier, String value) {
        this.add(identifier, "", value);
    }

    /**
     * Adds a translation for the given identifier with an additional key appended.
     *
     * @param identifier    the identifier used for deriving the translation key
     * @param additionalKey the additional key appended to the derived key
     * @param value         the translation value
     */
    default void add(Identifier identifier, String additionalKey, String value) {
        Objects.requireNonNull(identifier, "resource identifier is null");
        this.add(identifier.toLanguageKey(), additionalKey, value);
    }

    /**
     * Adds a translation for the given translatable component, using its fallback value.
     *
     * @param component the translatable component
     */
    default void add(Component component) {
        Objects.requireNonNull(component, "component is null");
        if (component.getContents() instanceof TranslatableContents contents && contents.getFallback() != null) {
            this.add(contents.getKey(), contents.getFallback());
        } else {
            throw new IllegalArgumentException("Unsupported component: " + component);
        }
    }

    /**
     * Adds a translation for the given translatable component.
     *
     * @param component the translatable component
     * @param value     the translation value
     */
    default void add(Component component, String value) {
        Objects.requireNonNull(component, "component is null");
        if (component.getContents() instanceof TranslatableContents contents) {
            this.add(contents.getKey(), value);
        } else {
            throw new IllegalArgumentException("Unsupported component: " + component);
        }
    }

    /**
     * Adds a translation for the given holder.
     *
     * @param holder the holder used for deriving the translation key
     * @param value  the translation value
     */
    default void add(Holder<?> holder, String value) {
        this.add(holder, "", value);
    }

    /**
     * Adds a translation for the given holder with an additional key appended.
     *
     * @param holder        the holder used for deriving the translation key
     * @param additionalKey the additional key appended to the derived key
     * @param value         the translation value
     */
    default void add(Holder<?> holder, String additionalKey, String value) {
        Objects.requireNonNull(holder, "holder is null");
        this.add(holder.unwrapKey().orElseThrow(), additionalKey, value);
    }

    /**
     * Adds a translation for the given resource key.
     *
     * @param resourceKey the resource key used for deriving the translation key
     * @param value       the translation value
     */
    default void add(ResourceKey<?> resourceKey, String value) {
        this.add(resourceKey, "", value);
    }

    /**
     * Adds a translation for the given resource key with an additional key appended.
     *
     * @param resourceKey   the resource key used for deriving the translation key
     * @param additionalKey the additional key appended to the derived key
     * @param value         the translation value
     */
    default void add(ResourceKey<?> resourceKey, String additionalKey, String value) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        String registry = Registries.elementsDirPath(resourceKey.registryKey());
        this.add(registry, resourceKey.identifier(), additionalKey, value);
    }

    /**
     * Adds a translation for the given identifier within the given registry.
     *
     * @param registry   the registry directory path, e.g. {@code block}
     * @param identifier the identifier used for deriving the translation key
     * @param value      the translation value
     */
    default void add(String registry, Identifier identifier, String value) {
        this.add(registry, identifier, "", value);
    }

    /**
     * Adds a translation for the given identifier within the given registry with an additional key appended.
     *
     * @param registry      the registry directory path, e.g. {@code block}
     * @param identifier    the identifier used for deriving the translation key
     * @param additionalKey the additional key appended to the derived key
     * @param value         the translation value
     */
    default void add(String registry, Identifier identifier, String additionalKey, String value) {
        Objects.requireNonNull(registry, "registry is null");
        Objects.requireNonNull(identifier, "resource identifier is null");
        this.add(identifier.toLanguageKey(registry), additionalKey, value);
    }

    /**
     * Adds a translation for the given tag key, prefixed with {@code tag.}.
     *
     * @param tagKey the tag key used for deriving the translation key
     * @param value  the translation value
     */
    default void add(TagKey<?> tagKey, String value) {
        Objects.requireNonNull(tagKey, "tag key is null");
        String registry = Registries.elementsDirPath(tagKey.registry());
        this.add("tag." + tagKey.location().toLanguageKey(registry), value);
    }

    /**
     * Adds a translation for the given block holder.
     *
     * @param block the block holder
     * @param value the translation value
     */
    default void addBlock(Holder<Block> block, String value) {
        Objects.requireNonNull(block, "block is null");
        this.add(block.value(), value);
    }

    /**
     * Adds a translation for the given block.
     *
     * @param block the block
     * @param value the translation value
     */
    default void add(Block block, String value) {
        this.add(block, "", value);
    }

    /**
     * Adds a translation for the given block with an additional key appended.
     *
     * @param block         the block
     * @param additionalKey the additional key appended to the description id
     * @param value         the translation value
     */
    default void add(Block block, String additionalKey, String value) {
        Objects.requireNonNull(block, "block is null");
        this.add(block.getDescriptionId(), additionalKey, value);
    }

    /**
     * Adds a translation for the given item holder.
     *
     * @param item  the item holder
     * @param value the translation value
     */
    default void addItem(Holder<Item> item, String value) {
        Objects.requireNonNull(item, "item is null");
        this.add(item.value(), value);
    }

    /**
     * Adds a translation for the given item.
     *
     * @param item  the item
     * @param value the translation value
     */
    default void add(Item item, String value) {
        this.add(item, "", value);
    }

    /**
     * Adds a translation for the given item with an additional key appended.
     *
     * @param item          the item
     * @param additionalKey the additional key appended to the description id
     * @param value         the translation value
     */
    default void add(Item item, String additionalKey, String value) {
        Objects.requireNonNull(item, "item is null");
        this.add(item.getDescriptionId(), additionalKey, value);
    }

    /**
     * Adds a translation for the given spawn egg item, appending {@code Spawn Egg} to the given value.
     *
     * @param item  the spawn egg item
     * @param value the translation value
     */
    default void addSpawnEgg(Item item, String value) {
        Objects.requireNonNull(item, "item is null");
        if (item instanceof SpawnEggItem) {
            this.add(item, value + " Spawn Egg");
        } else {
            throw new IllegalArgumentException("Unsupported item: " + item);
        }
    }

    /**
     * Adds a translation for the given mob effect holder.
     *
     * @param mobEffect the mob effect holder
     * @param value     the translation value
     */
    default void addMobEffect(Holder<MobEffect> mobEffect, String value) {
        Objects.requireNonNull(mobEffect, "mob effect is null");
        this.add(mobEffect.value(), value);
    }

    /**
     * Adds a translation for the given mob effect.
     *
     * @param mobEffect the mob effect
     * @param value     the translation value
     */
    default void add(MobEffect mobEffect, String value) {
        this.add(mobEffect, "", value);
    }

    /**
     * Adds a translation for the given mob effect with an additional key appended.
     *
     * @param mobEffect     the mob effect
     * @param additionalKey the additional key appended to the description id
     * @param value         the translation value
     */
    default void add(MobEffect mobEffect, String additionalKey, String value) {
        Objects.requireNonNull(mobEffect, "mob effect is null");
        this.add(mobEffect.getDescriptionId(), additionalKey, value);
    }

    /**
     * Adds a translation for the given entity type holder.
     *
     * @param entityType the entity type holder
     * @param value      the translation value
     */
    default void addEntityType(Holder<? extends EntityType<?>> entityType, String value) {
        Objects.requireNonNull(entityType, "entity type is null");
        this.add(entityType.value(), value);
    }

    /**
     * Adds a translation for the given entity type.
     *
     * @param entityType the entity type
     * @param value      the translation value
     */
    default void add(EntityType<?> entityType, String value) {
        this.add(entityType, "", value);
    }

    /**
     * Adds a translation for the given entity type with an additional key appended.
     *
     * @param entityType    the entity type
     * @param additionalKey the additional key appended to the description id
     * @param value         the translation value
     */
    default void add(EntityType<?> entityType, String additionalKey, String value) {
        Objects.requireNonNull(entityType, "entity type is null");
        this.add(entityType.getDescriptionId(), additionalKey, value);
    }

    /**
     * Adds a translation for the given attribute holder.
     *
     * @param attribute the attribute holder
     * @param value     the translation value
     */
    default void addAttribute(Holder<Attribute> attribute, String value) {
        Objects.requireNonNull(attribute, "attribute is null");
        this.add(attribute.value(), value);
    }

    /**
     * Adds a translation for the given attribute.
     *
     * @param attribute the attribute
     * @param value     the translation value
     */
    default void add(Attribute attribute, String value) {
        this.add(attribute, "", value);
    }

    /**
     * Adds a translation for the given attribute with an additional key appended.
     *
     * @param attribute     the attribute
     * @param additionalKey the additional key appended to the description id
     * @param value         the translation value
     */
    default void add(Attribute attribute, String additionalKey, String value) {
        Objects.requireNonNull(attribute, "attribute is null");
        this.add(attribute.getDescriptionId(), additionalKey, value);
    }

    /**
     * Adds a translation for the given stat type holder.
     *
     * @param statType the stat type holder
     * @param value    the translation value
     */
    default void addStatType(Holder<StatType<?>> statType, String value) {
        Objects.requireNonNull(statType, "stat type is null");
        this.add(statType.value(), value);
    }

    /**
     * Adds a translation for the given stat type.
     *
     * @param statType the stat type
     * @param value    the translation value
     */
    default void add(StatType<?> statType, String value) {
        this.add(statType, "", value);
    }

    /**
     * Adds a translation for the given stat type with an additional key appended.
     *
     * @param statType      the stat type
     * @param additionalKey the additional key appended to the display name key
     * @param value         the translation value
     */
    default void add(StatType<?> statType, String additionalKey, String value) {
        Objects.requireNonNull(statType, "stat type is null");
        Objects.requireNonNull(statType.getDisplayName(), "component is null");
        if (statType.getDisplayName().getContents() instanceof TranslatableContents contents) {
            this.add(contents.getKey(), additionalKey, value);
        } else {
            throw new IllegalArgumentException("Unsupported component: " + statType.getDisplayName());
        }
    }

    /**
     * Adds translations for the given potion, covering tipped arrows, potions, splash potions, and lingering potions.
     *
     * @param potion the potion holder
     * @param value  the translation value
     */
    default void addPotion(Holder<Potion> potion, String value) {
        Objects.requireNonNull(potion, "potion is null");
        Function<Item, Component> potionNameGetter = (Item item) -> {
            return new PotionContents(potion).getName(item.getDescriptionId() + ".effect.");
        };
        this.add(potionNameGetter.apply(Items.TIPPED_ARROW), "Arrow of " + value);
        this.add(potionNameGetter.apply(Items.POTION), "Potion of " + value);
        this.add(potionNameGetter.apply(Items.SPLASH_POTION), "Splash Potion of " + value);
        this.add(potionNameGetter.apply(Items.LINGERING_POTION), "Lingering Potion of " + value);
    }

    /**
     * Adds a subtitle translation for the given sound event holder.
     *
     * @param soundEvent the sound event holder
     * @param value      the translation value
     */
    default void addSoundEvent(Holder<SoundEvent> soundEvent, String value) {
        Objects.requireNonNull(soundEvent, "sound event is null");
        this.add(soundEvent.value(), value);
    }

    /**
     * Adds a subtitle translation for the given sound event.
     *
     * @param soundEvent the sound event
     * @param value      the translation value
     */
    default void add(SoundEvent soundEvent, String value) {
        Objects.requireNonNull(soundEvent, "sound event is null");
        this.add("subtitles." + soundEvent.location().getPath(), value);
    }

    /**
     * Adds a translation for the given creative mode tab holder.
     *
     * @param creativeModeTab the creative mode tab holder
     * @param value           the translation value
     */
    default void addCreativeModeTab(Holder<CreativeModeTab> creativeModeTab, String value) {
        Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
        this.add(creativeModeTab.value(), value);
    }

    /**
     * Adds a translation for the given creative mode tab.
     *
     * @param creativeModeTab the creative mode tab
     * @param value           the translation value
     */
    default void add(CreativeModeTab creativeModeTab, String value) {
        Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
        this.add(creativeModeTab.getDisplayName(), value);
    }

    /**
     * Adds a translation for the given biome.
     *
     * @param biome the biome
     * @param value the translation value
     */
    default void addBiome(ResourceKey<Biome> biome, String value) {
        Objects.requireNonNull(biome, "biome is null");
        this.add(biome.identifier().toLanguageKey("biome"), value);
    }

    /**
     * Adds a generic damage type translation for the given damage type, of the form {@code death.attack.<path>}.
     *
     * @param damageType the damage type
     * @param value      the translation value
     */
    default void addGenericDamageType(ResourceKey<DamageType> damageType, String value) {
        Objects.requireNonNull(damageType, "damage type is null");
        this.add("death.attack." + damageType.identifier().getPath(), value);
    }

    /**
     * Adds a player damage type translation for the given damage type, of the form {@code death.attack.<path>.player}.
     *
     * @param damageType the damage type
     * @param value      the translation value
     */
    default void addPlayerDamageType(ResourceKey<DamageType> damageType, String value) {
        Objects.requireNonNull(damageType, "damage type is null");
        this.add("death.attack." + damageType.identifier().getPath() + ".player", value);
    }

    /**
     * Adds an item damage type translation for the given damage type, of the form {@code death.attack.<path>.item}.
     *
     * @param damageType the damage type
     * @param value      the translation value
     */
    default void addItemDamageType(ResourceKey<DamageType> damageType, String value) {
        Objects.requireNonNull(damageType, "damage type is null");
        this.add("death.attack." + damageType.identifier().getPath() + ".item", value);
    }

    /**
     * Adds title and author translations for the given painting variant.
     *
     * @param paintingVariant the painting variant
     * @param title           the painting title
     * @param author          the painting author
     */
    default void addPaintingVariant(ResourceKey<PaintingVariant> paintingVariant, String title, String author) {
        Objects.requireNonNull(paintingVariant, "painting variant is null");
        // do not use the registry name, it is "painting_variant", not "painting"
        this.add(paintingVariant.identifier().toLanguageKey("painting", "title"), title);
        this.add(paintingVariant.identifier().toLanguageKey("painting", "author"), author);
    }

    /**
     * Adds a translation for the given key mapping.
     *
     * @param keyMapping the key mapping
     * @param value      the translation value
     */
    default void add(KeyMapping keyMapping, String value) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        this.add(keyMapping.getName(), value);
    }

    /**
     * Adds a translation for the main key category of the given mod.
     *
     * @param modId the mod id
     * @param value the translation value
     */
    default void addKeyCategory(String modId, String value) {
        this.add(new KeyMapping.Category(Identifier.fromNamespaceAndPath(modId, "main")).label(), value);
    }

    /**
     * Adds a translation for the given game rule.
     *
     * @param gameRule the game rule
     * @param value    the translation value
     */
    default void add(GameRule<?> gameRule, String value) {
        this.add(gameRule, "", value);
    }

    /**
     * Adds a translation for the description of the given game rule.
     *
     * @param gameRule the game rule
     * @param value    the translation value
     */
    default void addGameRuleDescription(GameRule<?> gameRule, String value) {
        this.add(gameRule, "description", value);
    }

    /**
     * Adds a translation for the given game rule with an additional key appended.
     *
     * @param gameRule      the game rule
     * @param additionalKey the additional key appended to the description id
     * @param value         the translation value
     */
    default void add(GameRule<?> gameRule, String additionalKey, String value) {
        Objects.requireNonNull(gameRule, "game rule is null");
        this.add(gameRule.getDescriptionId(), additionalKey, value);
    }
}
