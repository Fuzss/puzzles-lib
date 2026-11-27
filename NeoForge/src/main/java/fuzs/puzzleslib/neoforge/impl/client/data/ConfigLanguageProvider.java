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
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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

    public void addModConfig(String modId, String configType, String fileName, ModConfigSpec configSpec) {
        this.addConfigTitle(modId);
        this.addConfigFile(modId, fileName, configType);
        this.addConfigValues(modId, configSpec.getSpec(), new ArrayList<>(), configSpec::getLevelComment);
    }

    public void addConfigTitle(String modId) {
        this.add(modId + ".configuration.title", "%s Configuration");
    }

    public void addConfigFile(String modId, String fileName, String configType) {
        configType = getConfigTypeFromFileName(fileName).orElse(configType);
        configType = getConfigValueName(configType);
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", ".")
                .replaceFirst("^\\.", "")
                .replaceFirst("\\.$", "")
                .toLowerCase();
        this.add(modId + ".configuration.section." + fileName, "%s " + configType + " Settings");
        this.add(modId + ".configuration.section." + fileName + ".title", "%s " + configType + " Configuration");
    }

    private void addConfigValues(String modId, UnmodifiableConfig config, List<String> path, Function<List<String>, @Nullable String> levelCommentGetter) {
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            this.addConfigValue(modId, entry.getKey());
            String comment;
            if (entry.getValue() instanceof ModConfigSpec.ValueSpec valueSpec) {
                comment = valueSpec.getComment();
            } else if (entry.getValue() instanceof UnmodifiableConfig) {
                path = new ArrayList<>(path);
                path.add(entry.getKey());
                comment = levelCommentGetter.apply(path);
                this.addConfigValues(modId, entry.getValue(), path, levelCommentGetter);
            } else {
                comment = null;
            }

            this.addConfigValueComment(modId, entry.getKey(), comment);
            this.addConfigValueButton(modId, entry.getKey());
        }
    }

    public void addConfigValue(String modId, String valueName) {
        Objects.requireNonNull(valueName, "value name is null");
        this.addConfigValue(modId, Collections.singletonList(valueName));
    }

    public void addConfigValue(String modId, List<String> valuePath) {
        this.add(modId + ".configuration." + valuePath.getLast(), getConfigValueName(valuePath.getLast()));
    }

    public void addConfigValueComment(String modId, String valueName, @Nullable String comment) {
        Objects.requireNonNull(valueName, "value name is null");
        this.addConfigValueComment(modId,
                Collections.singletonList(valueName),
                comment != null ? new ArrayList<>(Arrays.asList(comment.split("\\R"))) : Collections.emptyList());
    }

    public void addConfigValueComment(String modId, List<String> valuePath, List<String> comments) {
        String value = String.join(System.lineSeparator(), getStylizedStrings(comments));
        // also put in empty strings, as translations are expected for config values without a comment as well
        this.add(modId + ".configuration." + valuePath.getLast() + ".tooltip", value);
    }

    public void addConfigValueButton(String modId, String valueName) {
        Objects.requireNonNull(valueName, "value name is null");
        this.addConfigValueButton(modId, Collections.singletonList(valueName));
    }

    public void addConfigValueButton(String modId, List<String> valuePath) {
        this.add(valuePath.getLast() + ".button", "Edit...");
        this.add(modId + ".configuration." + valuePath.getLast() + ".button", "Edit...");
    }

    private static Optional<String> getConfigTypeFromFileName(String fileName) {
        int startIndex = fileName.lastIndexOf('-');
        int endIndex = fileName.lastIndexOf('.');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return Optional.of(fileName.substring(startIndex + 1, endIndex));
        } else {
            return Optional.empty();
        }
    }

    private static String getConfigValueName(String valueName) {
        String capitalizedValueName = ContentRegistrationHelper.getCapitalizedString(valueName);
        return capitalizedValueName.replace(" And ", " & ").replace(" Or ", " / ");
    }

    private static List<String> getStylizedStrings(List<String> strings) {
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
