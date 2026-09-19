package fuzs.puzzleslib.common.api.data.v3.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

/**
 * A base implementation of {@link AdvancementSubProvider} for generating advancements for the mod.
 * <p>
 * Subclasses implement {@link #generate()} and register advancements via the various {@code save} methods of
 * {@link Advancement.Builder} using the inherited {@code output}, mirroring the vanilla providers. The display info
 * required for those advancements can be created with the various {@code display} methods. The generated advancements
 * are emitted to the reloadable registry.
 */
public abstract class AbstractAdvancementProvider extends AdvancementSubProvider {

    /**
     * @param output the bootstrap context advancements are registered to
     */
    public AbstractAdvancementProvider(BootstrapContext<Advancement> output) {
        super(output);
    }

    /**
     * Creates the display info builder for the given icon and advancement id.
     *
     * @param icon the display icon
     * @param id   the advancement id
     * @return the display info builder
     */
    protected static DisplayInfoBuilder display(ItemLike icon, Identifier id) {
        return display(new ItemStackTemplate(icon.asItem()), id);
    }

    /**
     * Creates the display info builder for the given icon and advancement id.
     *
     * @param icon the display icon
     * @param id   the advancement id
     * @return the display info builder
     */
    protected static DisplayInfoBuilder display(ItemStackTemplate icon, Identifier id) {
        return display(icon, new AdvancementToken(id));
    }

    /**
     * Creates the display info builder for the given icon and advancement token.
     *
     * @param icon  the display icon
     * @param token the advancement token
     * @return the display info builder
     */
    protected static DisplayInfoBuilder display(ItemLike icon, AdvancementToken token) {
        return display(new ItemStackTemplate(icon.asItem()), token);
    }

    /**
     * Creates the display info builder for the given icon and advancement token, deriving the title and description
     * from the token.
     *
     * @param icon  the display icon
     * @param token the advancement token
     * @return the display info builder
     */
    protected static DisplayInfoBuilder display(ItemStackTemplate icon, AdvancementToken token) {
        return display(icon, token.title(), token.description());
    }

    /**
     * Creates the display info builder for the given icon, title, and description.
     *
     * @param icon        the display icon
     * @param title       the display title
     * @param description the display description
     * @return the display info builder
     */
    protected static DisplayInfoBuilder display(ItemLike icon, Component title, Component description) {
        return display(new ItemStackTemplate(icon.asItem()), title, description);
    }

    /**
     * Creates the display info builder for the given icon, title, and description.
     *
     * @param icon        the display icon
     * @param title       the display title
     * @param description the display description
     * @return the display info builder
     */
    protected static DisplayInfoBuilder display(ItemStackTemplate icon, Component title, Component description) {
        return new DisplayInfoBuilder(icon, title, description);
    }

    /**
     * Registers all advancements of this provider via the various {@code save} methods of {@link Advancement.Builder},
     * which are then emitted to the reloadable registry.
     */
    @Override
    public abstract void generate();
}
