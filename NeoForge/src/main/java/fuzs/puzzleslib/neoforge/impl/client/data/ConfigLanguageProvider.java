package fuzs.puzzleslib.neoforge.impl.client.data;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import fuzs.puzzleslib.common.api.client.data.v3.language.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.init.v3.registry.ContentRegistrationHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class ConfigLanguageProvider extends AbstractLanguageProvider {
    private final String modId;

    public ConfigLanguageProvider(DataProviderContext context) {
        // Generates the config screen translations into a separate language file namespace.
        // We must ensure this never writes to the mod's regular language file.
        super(Language.DEFAULT, context.getModId() + "-configs", context.getPackOutput());
        this.modId = context.getModId();
    }

    @Override
    public void addTranslations() {
        ModConfigs.getModConfigs(this.modId).forEach((ModConfig modConfig) -> {
            if (modConfig.getSpec() instanceof ModConfigSpec modConfigSpec) {
                this.addModConfig(modConfig.getModId(),
                        modConfig.getType().extension(),
                        modConfig.getFileName(),
                        modConfigSpec);
            }
        });
    }

    @Override
    public void add(String translationKey, String value) {
        Objects.requireNonNull(translationKey, "translation key is null");
        Objects.requireNonNull(value, "value is null");
        this.output.addProperty(translationKey, value);
    }

    private void addModConfig(String modId, String configType, String fileName, ModConfigSpec configSpec) {
        this.addConfigTitle(modId);
        this.addConfigFile(modId, fileName, configType);
        this.addConfigValues(modId, configSpec, configSpec.getSpec(), new ArrayList<>());
    }

    private void addConfigTitle(String modId) {
        this.add(modId + ".configuration.title", "%s Configuration");
    }

    private void addConfigFile(String modId, String fileName, String configType) {
        String configTypeName = this.sanitizeValueName(this.getConfigTypeName(fileName).orElse(configType));
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9]+", ".")
                .replaceFirst("^\\.", "")
                .replaceFirst("\\.$", "")
                .toLowerCase();
        this.add(modId + ".configuration.section." + sanitizedFileName, "%s " + configTypeName + " Settings");
        this.add(modId + ".configuration.section." + sanitizedFileName + ".title",
                "%s " + configTypeName + " Configuration");
    }

    private void addConfigValues(String modId, ModConfigSpec configSpec, UnmodifiableConfig config, List<String> path) {
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            String valueName = entry.getKey();
            String translationKey;
            String comment;
            if (entry.getValue() instanceof ModConfigSpec.ValueSpec valueSpec) {
                translationKey = this.getTranslationKey(modId, valueSpec, valueName);
                comment = valueSpec.getComment();
            } else if (entry.getValue() instanceof UnmodifiableConfig) {
                List<String> subPath = new ArrayList<>(path);
                subPath.add(valueName);
                translationKey = this.getLevelTranslationKey(modId, configSpec, subPath, valueName);
                comment = configSpec.getLevelComment(subPath);
                this.addConfigValues(modId, configSpec, entry.getValue(), subPath);
            } else {
                continue;
            }

            this.addConfigValueKey(translationKey, valueName);
            this.addConfigValueCommentKey(translationKey, comment);
            this.addConfigValueButton(translationKey);
        }
    }

    private String getTranslationKey(String modId, ModConfigSpec.ValueSpec valueSpec, String valueName) {
        String translationKey = valueSpec.getTranslationKey();
        return translationKey == null ? modId + ".configuration." + valueName : translationKey;
    }

    @SuppressWarnings("ConstantValue")
    private String getLevelTranslationKey(String modId, ModConfigSpec configSpec, List<String> subPath, String valueName) {
        String translationKey = configSpec.getLevelTranslationKey(subPath);
        return translationKey == null ? modId + ".configuration." + valueName : translationKey;
    }

    private void addConfigValueKey(String translationKey, String valueName) {
        this.add(translationKey, this.sanitizeValueName(valueName));
    }

    private void addConfigValueCommentKey(String translationKey, @Nullable String comment) {
        if (Strings.isBlank(comment)) {
            this.add(translationKey + ".tooltip", "");
            return;
        }

        List<String> styledStrings = this.getStylizedStrings(new ArrayList<>(Arrays.asList(comment.split("\\R"))));
        this.add(translationKey + ".tooltip", String.join("\n", styledStrings));
    }

    private void addConfigValueButton(String translationKey) {
        this.add(translationKey + ".button", "Edit");
    }

    private Optional<String> getConfigTypeName(String fileName) {
        int startIndex = fileName.lastIndexOf('-');
        int endIndex = fileName.lastIndexOf('.');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return Optional.of(fileName.substring(startIndex + 1, endIndex));
        } else {
            return Optional.empty();
        }
    }

    private String sanitizeValueName(String valueName) {
        String capitalizedValueName = ContentRegistrationHelper.getCapitalizedString(valueName);
        return capitalizedValueName.replace(" And ", " & ").replace(" Or ", " / ");
    }

    private List<String> getStylizedStrings(List<String> strings) {
        strings.removeIf((String string) -> string.matches("^ Default: .*"));
        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i).replaceAll("^ Range: ", "Value Range: ");
            ChatFormatting chatFormatting = i % 2 == 0 ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
            strings.set(i, chatFormatting + string + ChatFormatting.RESET);
        }

        return strings;
    }

    @Override
    public String getName() {
        return "Config Language (" + Language.DEFAULT + ")";
    }
}
