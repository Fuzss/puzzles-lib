package fuzs.puzzleslib.common.api.client.data.v3.language;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ColorCollection;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A base implementation of {@link DataProvider} for generating language files for the mod.
 * <p>
 * Subclasses implement {@link #addTranslations(TranslationBuilder)} and register translations via the various
 * {@code add} methods of the provided {@link TranslationBuilder}, mirroring the vanilla providers. Translations for the
 * common content of a {@link BlockSetFamily} can be generated automatically via
 * {@link #generateFor(TranslationBuilder, BlockSetFamily, String)}. The translations of the mod are validated, so all
 * required translations must be provided by either adding them or overriding
 * {@link #mustHaveTranslationKey(Holder.Reference, String)}.
 */
public abstract class AbstractLanguageProvider implements DataProvider {
    /**
     * The default names for the sixteen vanilla dye colors.
     *
     * @see ColorCollection#NAMES
     */
    public static final ColorCollection<String> COLOR_NAMES = new ColorCollection<>("White",
            "Orange",
            "Magenta",
            "Light Blue",
            "Yellow",
            "Lime",
            "Pink",
            "Gray",
            "Light Gray",
            "Cyan",
            "Purple",
            "Blue",
            "Brown",
            "Green",
            "Red",
            "Black");
    /**
     * The default block name providers for the common {@link BlockSetVariant BlockSetVariants}, used by
     * {@link #generateFor(TranslationBuilder, BlockSetFamily, String)}.
     *
     * @see #generateFor(BiConsumer, Map, Map, String)
     */
    public static final Map<BlockSetVariant, UnaryOperator<String>> VARIANT_BLOCK_NAMES = ImmutableMap.<BlockSetVariant, UnaryOperator<String>>builder()
            .put(BlockSetVariant.CHISELED, (String baseName) -> "Chiseled " + baseName)
            .put(BlockSetVariant.CRACKED, (String baseName) -> "Cracked " + baseName)
            .put(BlockSetVariant.CUT, (String baseName) -> "Cut " + baseName)
            .put(BlockSetVariant.MOSAIC, (String baseName) -> baseName + " Mosaic")
            .put(BlockSetVariant.POLISHED, (String baseName) -> "Polished " + baseName)
            .put(BlockSetVariant.BRICKS, (String baseName) -> baseName + " Bricks")
            .put(BlockSetVariant.COBBLED, (String baseName) -> "Cobbled " + baseName)
            .put(BlockSetVariant.TILES, (String baseName) -> baseName + " Tiles")
            .put(BlockSetVariant.PILLAR, (String baseName) -> baseName + " Pillar")
            .put(BlockSetVariant.LOG, (String baseName) -> baseName + " Log")
            .put(BlockSetVariant.WOOD, (String baseName) -> baseName + " Wood")
            .put(BlockSetVariant.STRIPPED_LOG, (String baseName) -> "Stripped " + baseName + " Log")
            .put(BlockSetVariant.STRIPPED_WOOD, (String baseName) -> "Stripped " + baseName + " Wood")
            .put(BlockSetVariant.STAIRS, (String baseName) -> baseName + " Stairs")
            .put(BlockSetVariant.SLAB, (String baseName) -> baseName + " Slab")
            .put(BlockSetVariant.WALL, (String baseName) -> baseName + " Wall")
            .put(BlockSetVariant.FENCE, (String baseName) -> baseName + " Fence")
            .put(BlockSetVariant.FENCE_GATE, (String baseName) -> baseName + " Fence Gate")
            .put(BlockSetVariant.DOOR, (String baseName) -> baseName + " Door")
            .put(BlockSetVariant.TRAPDOOR, (String baseName) -> baseName + " Trapdoor")
            .put(BlockSetVariant.BUTTON, (String baseName) -> baseName + " Button")
            .put(BlockSetVariant.PRESSURE_PLATE, (String baseName) -> baseName + " Pressure Plate")
            .put(BlockSetVariant.SIGN, (String baseName) -> baseName + " Sign")
            .put(BlockSetVariant.HANGING_SIGN, (String baseName) -> baseName + " Hanging Sign")
            .put(BlockSetVariant.SHELF, (String baseName) -> baseName + " Shelf")
            .build();
    /**
     * The default item name providers for the common {@link BlockSetVariant BlockSetVariants}, used by
     * {@link #generateFor(TranslationBuilder, BlockSetFamily, String)}.
     *
     * @see #generateFor(BiConsumer, Map, Map, String)
     */
    public static final Map<BlockSetVariant, UnaryOperator<String>> VARIANT_ITEM_NAMES = ImmutableMap.<BlockSetVariant, UnaryOperator<String>>builder()
            .put(BlockSetVariant.BOAT, (String baseName) -> baseName + " Boat")
            .put(BlockSetVariant.CHEST_BOAT, (String baseName) -> baseName + " Chest Boat")
            .build();
    /**
     * The default entity name providers for the common {@link BlockSetVariant BlockSetVariants}, used by
     * {@link #generateFor(TranslationBuilder, BlockSetFamily, String)}.
     *
     * @see #generateFor(BiConsumer, Map, Map, String)
     */
    public static final Map<BlockSetVariant, UnaryOperator<String>> VARIANT_ENTITY_NAMES = ImmutableMap.<BlockSetVariant, UnaryOperator<String>>builder()
            .put(BlockSetVariant.BOAT, (String baseName) -> baseName + " Boat")
            .put(BlockSetVariant.CHEST_BOAT, (String baseName) -> baseName + " Chest Boat")
            .build();

    /**
     * The language file id, built from the mod id and the language code.
     */
    private final Identifier filePath;
    /**
     * The path provider for the language files.
     */
    private final PackOutput.PathProvider pathProvider;

    /**
     * @param context the data provider context
     */
    public AbstractLanguageProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    /**
     * @param languageCode the language code, e.g. {@code en_us}
     * @param context      the data provider context
     */
    public AbstractLanguageProvider(String languageCode, DataProviderContext context) {
        this(languageCode, context.getModId(), context.getPackOutput());
    }

    /**
     * @param modId      the mod id
     * @param packOutput the pack output instance
     */
    public AbstractLanguageProvider(String modId, PackOutput packOutput) {
        this("en_us", modId, packOutput);
    }

    /**
     * @param languageCode the language code, e.g. {@code en_us}
     * @param modId        the mod id
     * @param packOutput   the pack output instance
     */
    public AbstractLanguageProvider(String languageCode, String modId, PackOutput packOutput) {
        this.filePath = Identifier.fromNamespaceAndPath(modId, languageCode);
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang");
    }

    /**
     * Registers all translations of this provider via the various {@code add} methods of the given translation
     * builder.
     *
     * @param builder the translation builder translations are registered with
     */
    public abstract void addTranslations(TranslationBuilder builder);

    /**
     * Generates translations for all blocks, items, and entity types of the given block set family using the default
     * variant name providers.
     *
     * @param translationBuilder the translation builder translations are registered with
     * @param blockSetFamily     the block set family
     * @param baseName           the base name the variant names are derived from
     */
    public void generateFor(TranslationBuilder translationBuilder, BlockSetFamily blockSetFamily, String baseName) {
        this.generateFor(translationBuilder::add, blockSetFamily.getBlockVariants(), VARIANT_BLOCK_NAMES, baseName);
        this.generateFor(translationBuilder::add, blockSetFamily.getItemVariants(), VARIANT_ITEM_NAMES, baseName);
        this.generateFor(translationBuilder::add, blockSetFamily.getEntityVariants(), VARIANT_ENTITY_NAMES, baseName);
    }

    /**
     * Generates translations for the given variants using the given variant name providers.
     *
     * @param translationConsumer the consumer receiving each translated value and its generated name
     * @param variants            the variants to translate, mapped by block set variant
     * @param variantNames        the name providers, mapped by block set variant
     * @param baseName            the base name the variant names are derived from
     * @param <T>                 the translation type
     */
    public <T> void generateFor(BiConsumer<T, String> translationConsumer, Map<BlockSetVariant, Holder.Reference<T>> variants, Map<BlockSetVariant, UnaryOperator<String>> variantNames, String baseName) {
        variants.forEach((BlockSetVariant variant, Holder.Reference<T> holder) -> {
            UnaryOperator<String> variantName = variantNames.get(variant);
            if (variantName != null) {
                translationConsumer.accept(holder.value(), variantName.apply(baseName));
            }
        });
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject languageOutput = new JsonObject();
        this.addTranslations((String translationKey, String value) -> {
            Objects.requireNonNull(translationKey, "translation key is null");
            Objects.requireNonNull(value, "value is null");
            if (languageOutput.has(translationKey)) {
                throw new IllegalStateException("Created duplicate translation key: " + translationKey);
            } else {
                languageOutput.addProperty(translationKey, value);
            }
        });

        this.verifyRequiredTranslationKeys(languageOutput);
        return DataProvider.saveStable(cache, languageOutput, this.pathProvider.json(this.filePath));
    }

    /**
     * Validates that all required translation keys for content of the mod have been added.
     *
     * @param jsonObject the generated translations
     */
    private void verifyRequiredTranslationKeys(JsonObject jsonObject) {
        this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.BLOCK, TranslationBuilder::addBlock);
        this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.ITEM, TranslationBuilder::addItem);
        this.verifyRequiredTranslationKeys(jsonObject::has,
                BuiltInRegistries.ENTITY_TYPE,
                TranslationBuilder::addEntityType);
        this.verifyRequiredTranslationKeys(jsonObject::has,
                BuiltInRegistries.ATTRIBUTE,
                TranslationBuilder::addAttribute);
        this.verifyRequiredTranslationKeys(jsonObject::has,
                BuiltInRegistries.MOB_EFFECT,
                TranslationBuilder::addMobEffect);
    }

    /**
     * Validates that all required translation keys of the given registry have been added, throwing for missing
     * translations of the mod.
     *
     * @param predicate                  the predicate used for checking that a translation key has been added
     * @param registry                   the registry to validate
     * @param holderTranslationCollector the collector used for deriving the translation key of each registry entry
     * @param <T>                        the registry element type
     * @see net.minecraft.server.Bootstrap#getMissingTranslations(Language)
     */
    private <T> void verifyRequiredTranslationKeys(Predicate<String> predicate, Registry<T> registry, HolderTranslationCollector<T> holderTranslationCollector) {
        registry.listElements()
                .filter((Holder.Reference<T> holder) -> holder.key()
                        .identifier()
                        .getNamespace()
                        .equals(this.filePath.getNamespace()))
                .forEach((Holder.Reference<T> holder) -> {
                    holderTranslationCollector.accept((String translationKey, String value) -> {
                        Objects.requireNonNull(translationKey, "translation key is null");
                        if (this.mustHaveTranslationKey(holder, translationKey) && !predicate.test(translationKey)) {
                            throw new IllegalStateException("Missing translation key '%s' for '%s'".formatted(
                                    translationKey,
                                    holder));
                        }
                    }, holder, "");
                });
    }

    /**
     * Determines whether a translation key is required for the given registry entry. Override to skip validation for
     * specific translation keys.
     *
     * @param holder         the registry entry
     * @param translationKey the translation key
     * @return if the translation key must be present
     */
    @ApiStatus.OverrideOnly
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return true;
    }

    @Override
    public String getName() {
        return "Language (" + this.filePath.getPath() + ")";
    }

    /**
     * A collector deriving the translation key of a registry entry via a {@link TranslationBuilder}.
     *
     * @param <T> the registry element type
     */
    @FunctionalInterface
    private interface HolderTranslationCollector<T> {
        /**
         * @param translationBuilder the translation builder used for deriving the translation key
         * @param holder             the registry entry
         * @param value              the translation value
         */
        void accept(TranslationBuilder translationBuilder, Holder<T> holder, String value);
    }
}
