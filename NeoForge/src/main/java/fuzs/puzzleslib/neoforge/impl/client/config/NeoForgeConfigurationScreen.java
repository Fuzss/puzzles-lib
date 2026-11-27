package fuzs.puzzleslib.neoforge.impl.client.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.common.api.core.v1.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * An extension of {@link ConfigurationScreen} that uses generated config translations.
 * <p>
 * It also allows for displaying configs from multiple mods in a single selection menu.
 * <p>
 * This class is synced between Fabric &amp; NeoForge, as there is no shared config library in Common to build against.
 */
public class NeoForgeConfigurationScreen extends OptionsSubScreen {
    /**
     * Show server configs last; all else are global configs that are merged under the same section header.
     */
    private static final List<ModConfig.Type> CONFIG_TYPE_DISPLAY_ORDER = List.of(ModConfig.Type.STARTUP,
            ModConfig.Type.LOCAL,
            ModConfig.Type.CLIENT,
            ModConfig.Type.SYNCED);
    /**
     * @see ConfigurationScreen#LANG_PREFIX
     */
    private static final String LANG_PREFIX = "neoforge.configuration.uitext.";
    /**
     * @see ConfigurationScreen#translationChecker
     */
    private static final ConfigurationScreen.TranslationChecker translationChecker = new ConfigurationScreen.TranslationChecker();
    /**
     * @see ConfigurationScreen#SECTION
     */
    private static final String SECTION = LANG_PREFIX + "section";
    /**
     * @see ConfigurationScreen#SECTION_TEXT
     */
    private static final String SECTION_TEXT = LANG_PREFIX + "sectiontext";
    /**
     * @see ConfigurationScreen#CRUMB
     */
    private static final String CRUMB = LANG_PREFIX + "breadcrumb.order";

    protected final Set<String> modIds;

    public NeoForgeConfigurationScreen(Screen lastScreen, String modId) {
        this(lastScreen, getConfigTitleComponent(modId), Collections.singleton(modId));
    }

    public NeoForgeConfigurationScreen(Screen lastScreen, String modId, String... modIds) {
        this(lastScreen, getConfigTitleComponent(modId), ImmutableSet.<String>builder().add(modId).add(modIds).build());
    }

    public NeoForgeConfigurationScreen(Screen lastScreen, Component title, Set<String> modIds) {
        super(lastScreen, Minecraft.getInstance().options, title);
        this.modIds = modIds;
    }

    @Override
    protected void addOptions() {
        boolean hasSectionHeader = false;
        List<Optional<ModConfig>> modConfigs = new ArrayList<>();
        for (ModConfig.Type type : CONFIG_TYPE_DISPLAY_ORDER) {
            // We combine all global config types under the same header, while only server configs get their own.
            if (type == ModConfig.Type.SYNCED) {
                hasSectionHeader = false;
            }

            for (ModConfig modConfig : ModConfigs.getConfigSet(type)) {
                if (this.modIds.contains(modConfig.getModId())) {
                    if (!hasSectionHeader) {
                        // This tricks the string widget into centering the text, while also allowing it to scroll instead of overflowing.
                        this.list.addSmall(new StringWidget(ConfigurationScreen.BIG_BUTTON_WIDTH,
                                Button.DEFAULT_HEIGHT,
                                getConfigSectionComponent(type),
                                this.font) {
                            @Override
                            public int getWidth() {
                                return this.width;
                            }
                        }.setMaxWidth(1, StringWidget.TextOverflow.SCROLLING), null);
                        hasSectionHeader = true;
                    }

                    Button button = Button.builder(Component.literal(modConfig.getFileName()), (Button buttonX) -> {
                        this.openModConfigScreen(modConfig, this);
                    }).width(ConfigurationScreen.BIG_BUTTON_WIDTH).build();
                    Component tooltip = this.getTooltipComponent(type, modConfig);
                    if (tooltip != null) {
                        button.setTooltip(Tooltip.create(tooltip));
                        button.active = false;
                        modConfigs.add(Optional.empty());
                    } else {
                        modConfigs.add(Optional.of(modConfig));
                    }

                    this.list.addSmall(button, null);
                }
            }
        }

        // When there is only one config, go to that config screen directly.
        if (modConfigs.size() == 1) {
            modConfigs.getFirst().ifPresent((ModConfig modConfig) -> {
                this.openModConfigScreen(modConfig, this.lastScreen);
            });
        }
    }

    protected void openModConfigScreen(ModConfig modConfig, Screen lastScreen) {
        Component component = getConfigTitleComponent(modConfig.getModId());
        this.minecraft.gui.setScreen(new CustomConfigurationSectionScreen(lastScreen,
                modConfig.getType(),
                modConfig,
                component));
    }

    /**
     * @see ConfigurationScreen#addOptions()
     */
    protected Component getTooltipComponent(ModConfig.Type type, ModConfig modConfig) {
        if (!((ModConfigSpec) modConfig.getSpec()).isLoaded()) {
            return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_NOT_LOADED;
        } else if (type == ModConfig.Type.SYNCED && this.minecraft.getCurrentServer() != null && (
                !this.minecraft.hasSingleplayerServer() || !this.minecraft.getSingleplayerServer().isPublished())) {
            return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE;
        } else if (type == ModConfig.Type.SYNCED && this.minecraft.hasSingleplayerServer()
                && this.minecraft.getSingleplayerServer().isPublished()) {
            return ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN;
        } else {
            return null;
        }
    }

    /**
     * The title component shown on top of the config selection screen.
     *
     * @param modId the mod id
     * @return the title component
     */
    private static Component getConfigTitleComponent(String modId) {
        return Component.literal(ModContainer.getDisplayName(modId) + " Settings");
    }

    /**
     * Creates a section component for grouping configs based on their type.
     *
     * @param type the config type
     * @return the config type section component
     */
    private static Component getConfigSectionComponent(ModConfig.Type type) {
        String message;
        if (type != ModConfig.Type.SYNCED) {
            message = "Global Configurations";
        } else {
            message = "World Configurations";
        }

        return Component.literal(message).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
    }

    public static class CustomConfigurationSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {

        public CustomConfigurationSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
            super(parent, type, modConfig, title);
            // This is set to GAME for startup configs, which is wrong, as they reload just fine.
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        public CustomConfigurationSectionScreen(Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
            super(parentContext, parent, valueSpecs, key, entrySet, title);
            // This is set to GAME for startup configs, which is wrong, as they reload just fine.
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        @Override
        protected Component getTooltipComponent(String key, ModConfigSpec.@Nullable Range<?> range) {
            String tooltipKey = this.getTranslationKey(key) + ".tooltip";
            Component tooltipText = Component.translatable(tooltipKey);
            if (tooltipText.getString().isEmpty()) {
                return CommonComponents.EMPTY;
            }

            Component valueName = this.getTranslationComponent(key);
            return Component.empty()
                    .append(valueName)
                    .append(CommonComponents.NEW_LINE)
                    .append(CommonComponents.NEW_LINE)
                    .append(tooltipText);
        }

        @Override
        public ConfigurationScreen.ConfigurationSectionScreen rebuild() {
            return super.rebuild();
        }

        @Override
        public void onClose() {
            // Move the warning message regarding the restart type for changed settings to the individual config screen
            // instead of having it on the main config selection screen.
            // This makes it much easier to implement, especially as we remove the relog / restart options which are not necessary.
            super.onClose();
            if (this.changed && !(this.lastScreen instanceof ConfigurationScreen.ConfigurationSectionScreen)) {
                switch (this.needsRestart) {
                    case GAME -> {
                        this.openConfirmScreen(ConfigurationScreen.GAME_RESTART_TITLE,
                                ConfigurationScreen.GAME_RESTART_MESSAGE);
                    }
                    case WORLD -> {
                        if (this.minecraft.level != null) {
                            this.openConfirmScreen(ConfigurationScreen.SERVER_RESTART_TITLE,
                                    ConfigurationScreen.SERVER_RESTART_MESSAGE);
                        }
                    }
                }
            }
        }

        private void openConfirmScreen(Component title, Component message) {
            this.minecraft.gui.setScreen(new ConfirmScreen((boolean hasConfirmed) -> {
                if (hasConfirmed) {
                    super.onClose();
                } else {
                    this.minecraft.gui.setScreen(this);
                }
            }, title, message, CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
        }

        @Nullable
        protected Element createSection(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection) {
            // Make sure our own screen is used for submenus.
            if (subconfig.isEmpty()) {
                return null;
            } else {
                return new Element(this.getTranslationComponent(key).append(CommonComponents.ELLIPSIS),
                        this.getTooltipComponent(key, null),
                        Button.builder(Component.translatable(SECTION,
                                        Component.translatable(translationChecker.check(getTranslationKey(key) + ".button", SECTION_TEXT))),
                                        (Button button) -> this.minecraft.gui.setScreen(new CustomConfigurationSectionScreen(
                                                this.context,
                                                this,
                                                subconfig.valueMap(),
                                                key,
                                                subsection.entrySet(),
                                                this.getTranslationComponent(key)).rebuild()))
                                .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
                                .build(),
                        false);
            }
        }

        @Nullable
        protected <T> Element createList(String key, ModConfigSpec.ListValueSpec spec, ModConfigSpec.ConfigValue<List<T>> list) {
            // Make sure our own screen is used for submenus.
            return new Element(this.getTranslationComponent(key).append(CommonComponents.ELLIPSIS),
                    this.getTooltipComponent(key, null),
                    Button.builder(Component.translatable(SECTION,
                                    Component.translatable(translationChecker.check(getTranslationKey(key) + ".button", SECTION_TEXT))),
                                    button -> this.minecraft.gui.setScreen(new CustomConfigurationListScreen<>(Context.list(this.context,
                                            this),
                                            key,
                                            Component.translatable(CRUMB,
                                                    this.getTitle(),
                                                    ConfigurationScreen.CRUMB_SEPARATOR,
                                                    this.getTranslationComponent(key)),
                                            spec,
                                            list).rebuild()))
                            .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
                            .build(),
                    false);
        }
    }

    public static class CustomConfigurationListScreen<T> extends ConfigurationScreen.ConfigurationListScreen<T> {

        public CustomConfigurationListScreen(Context context, String key, Component title, ModConfigSpec.ListValueSpec spec, ModConfigSpec.ConfigValue<List<T>> valueList) {
            super(context, key, title, spec, valueList);
            // This is set to GAME for startup configs, which is wrong, as they reload just fine.
            this.needsRestart = ModConfigSpec.RestartType.NONE;
        }

        @Override
        protected Component getTooltipComponent(String key, ModConfigSpec.@Nullable Range<?> range) {
            String tooltipKey = this.getTranslationKey(key) + ".tooltip";
            Component tooltipText = Component.translatable(tooltipKey);
            if (tooltipText.getString().isEmpty()) {
                return CommonComponents.EMPTY;
            }

            Component valueName = this.getTranslationComponent(key);
            return Component.empty()
                    .append(valueName)
                    .append(CommonComponents.NEW_LINE)
                    .append(CommonComponents.NEW_LINE)
                    .append(tooltipText);
        }

        @Override
        public ConfigurationScreen.ConfigurationSectionScreen rebuild() {
            return super.rebuild();
        }

        @Override
        protected @Nullable Element createOtherValue(int idx, T entry) {
            // Remove the tooltip for list entries.
            Element element = super.createOtherValue(idx, entry);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createStringListValue(int idx, String value) {
            // Remove the tooltip for list entries.
            Element element = super.createStringListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createDoubleListValue(int idx, Double value) {
            // Remove the tooltip for list entries.
            Element element = super.createDoubleListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createLongListValue(int idx, Long value) {
            // Remove the tooltip for list entries.
            Element element = super.createLongListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createIntegerListValue(int idx, Integer value) {
            // Remove the tooltip for list entries.
            Element element = super.createIntegerListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }

        @Override
        protected @Nullable Element createBooleanListValue(int idx, Boolean value) {
            // Remove the tooltip for list entries.
            Element element = super.createBooleanListValue(idx, value);
            if (element != null && element.widget() != null) {
                element.widget().setTooltip(null);
            }

            return element;
        }
    }
}
