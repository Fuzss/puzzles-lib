package fuzs.puzzleslib.common.api.init.v3.registry;

import fuzs.puzzleslib.common.api.event.v1.CommonSetupCallback;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.item.TransmuteShapedRecipe;
import fuzs.puzzleslib.common.impl.item.TransmuteShapelessRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.SkullBlock;

/**
 * Contains methods for registering various gameplay content.
 */
public final class ContentRegistrationHelper {
    /**
     * The shaped transmute recipe serializer id that is used during registration.
     */
    public static final String TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID = "crafting_transmute_shaped";
    /**
     * The shapeless transmute recipe serializer id that is used during registration.
     */
    public static final String TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID = "crafting_transmute_shapeless";

    private ContentRegistrationHelper() {
        // NO-OP
    }

    /**
     * Creates the resource key for the shaped transmute recipe serializer of the provided mod.
     *
     * @param modId the namespace used for registration
     * @return the resource key for the shaped transmute recipe serializer
     */
    public static ResourceKey<RecipeSerializer<?>> getTransmuteShapedRecipeSerializer(String modId) {
        return ResourceKey.create(Registries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(modId, TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID));
    }

    /**
     * Creates the resource key for the shapeless transmute recipe serializer of the provided mod.
     *
     * @param modId the namespace used for registration
     * @return the resource key for the shapeless transmute recipe serializer
     */
    public static ResourceKey<RecipeSerializer<?>> getTransmuteShapelessRecipeSerializer(String modId) {
        return ResourceKey.create(Registries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(modId, TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID));
    }

    /**
     * Registers mod-specific recipe serializers for custom transmute recipes.
     *
     * @param registryManager the registry manager instance
     */
    public static void registerTransmuteRecipeSerializers(RegistryManager registryManager) {
        Holder.Reference<RecipeSerializer<TransmuteShapedRecipe>> shapedSerializer = TransmuteRecipeFactory.register(
                registryManager,
                TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID,
                ShapedRecipe.SERIALIZER,
                TransmuteShapedRecipe::new);
        ProxyImpl.get().synchronizeRecipeSerializer(shapedSerializer);
        Holder.Reference<RecipeSerializer<TransmuteShapelessRecipe>> shapelessSerializer = TransmuteRecipeFactory.register(
                registryManager,
                TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID,
                ShapelessRecipe.SERIALIZER,
                TransmuteShapelessRecipe::new);
        ProxyImpl.get().synchronizeRecipeSerializer(shapelessSerializer);
    }

    /**
     * Registers a new skull block type.
     *
     * @param id the name used for the skull block type
     * @return the skull block type
     */
    public static SkullBlock.Type registerSkullBlockType(Identifier id) {
        String string = id.toString();
        SkullBlock.Type skullBlockType = () -> string;
        CommonSetupCallback.EVENT.register(() -> {
            SkullBlock.Type.TYPES.put(skullBlockType.getSerializedName(), skullBlockType);
        });
        return skullBlockType;
    }

    /**
     * Registers an enchantment.
     *
     * @param context the bootstrap context
     * @param key     the resource key for the enchantment
     * @param builder the enchantment builder
     */
    public static void registerEnchantment(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.identifier()));
    }

    /**
     * Registers a damage type using the default hurt damage effects.
     *
     * @param context the bootstrap context
     * @param key     the resource key for the damage type
     */
    public static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key) {
        context.register(key, new DamageType(key.identifier().getPath(), 0.1F));
    }

    /**
     * Registers a damage type.
     *
     * @param context       the bootstrap context
     * @param key           the resource key for the damage type
     * @param damageEffects the effects applied when taking damage of this type
     */
    public static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key, DamageEffects damageEffects) {
        context.register(key, new DamageType(key.identifier().getPath(), 0.1F, damageEffects));
    }

    /**
     * Registers a trim material.
     *
     * @param context          the bootstrap context
     * @param key              the resource key for the trim material
     * @param descriptionColor the color applied to the trim material description
     * @param paletteId        the identifier for the trim palette
     */
    public static void registerTrimMaterial(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> key, int descriptionColor, Identifier paletteId) {
        Component component = getTranslationComponent(key).withStyle(Style.EMPTY.withColor(descriptionColor));
        context.register(key, new TrimMaterial(paletteId, component));
    }

    /**
     * Registers an instrument without any durability damage.
     *
     * @param context     the bootstrap context
     * @param key         the resource key for the instrument
     * @param soundEvent  the sound event played when using the instrument
     * @param useDuration how long the instrument is used for in seconds
     * @param range       the range in blocks the sound can be heard from
     */
    public static void registerInstrument(BootstrapContext<Instrument> context, ResourceKey<Instrument> key, Holder<SoundEvent> soundEvent, float useDuration, float range) {
        registerInstrument(context, key, soundEvent, useDuration, range, 0);
    }

    /**
     * Registers an instrument.
     *
     * @param context          the bootstrap context
     * @param key              the resource key for the instrument
     * @param soundEvent       the sound event played when using the instrument
     * @param useDuration      how long the instrument is used for in seconds
     * @param range            the range in blocks the sound can be heard from
     * @param durabilityDamage the durability damage dealt to the instrument when used
     */
    public static void registerInstrument(BootstrapContext<Instrument> context, ResourceKey<Instrument> key, Holder<SoundEvent> soundEvent, float useDuration, float range, int durabilityDamage) {
        context.register(key,
                new Instrument(soundEvent, useDuration, range, durabilityDamage, getTranslationComponent(key)));
    }

    /**
     * Registers a jukebox song.
     *
     * @param context          the bootstrap context
     * @param key              the resource key for the jukebox song
     * @param soundEvent       the sound event played by the jukebox
     * @param lengthInSeconds  the length of the song in seconds
     * @param comparatorOutput the redstone comparator output while the song is playing
     */
    public static void registerJukeboxSong(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder<SoundEvent> soundEvent, float lengthInSeconds, int comparatorOutput) {
        context.register(key,
                new JukeboxSong(soundEvent, getTranslationComponent(key), lengthInSeconds, comparatorOutput));
    }

    /**
     * Creates a translation component for the provided resource key for use as a display name.
     *
     * @param key the resource key
     * @return the translation component
     */
    public static MutableComponent getTranslationComponent(ResourceKey<?> key) {
        return Component.translatable(getTranslationKey(key));
    }

    /**
     * Creates the translation key for the provided resource key.
     *
     * @param key the resource key
     * @return the translation key
     */
    public static String getTranslationKey(ResourceKey<?> key) {
        return Util.makeDescriptionId(Registries.elementsDirPath(key.registryKey()), key.identifier());
    }
}
