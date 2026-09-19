package fuzs.puzzleslib.common.api.data.v3.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * A token representing a single advancement, created from its {@link Identifier}.
 * <p>
 * The token is used for registering an advancement by name via {@link #name()}, and for deriving the translation keys
 * of its title and description via {@link #title()} and {@link #description()}, following the vanilla advancement
 * language format.
 *
 * @param id the advancement id
 */
public record AdvancementToken(Identifier id) {

    /**
     * @return the title of the advancement, translating to {@code advancements.<namespace>.<path>.title}
     */
    public Component title() {
        return Component.translatable(this.id.toLanguageKey("advancements", "title").replace('/', '.'));
    }

    /**
     * @return the description of the advancement, translating to {@code advancements.<namespace>.<path>.description}
     */
    public Component description() {
        return Component.translatable(this.id.toLanguageKey("advancements", "description").replace('/', '.'));
    }

    /**
     * @return the advancement id in its string form, used for registering the advancement via
     *         {@link Advancement.Builder#save(BootstrapContext, String)}
     */
    public String name() {
        return this.id.toString();
    }
}
