package fuzs.puzzleslib.common.api.data.v3.advancements;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * A builder for {@link DisplayInfo}, configuring how an advancement is presented in the advancement selection screen.
 * <p>
 * Instances are created via the various {@code display} methods of {@link AbstractAdvancementProvider} and are passed
 * to the advancement builder.
 *
 * @see DisplayInfo
 */
public class DisplayInfoBuilder {
    /**
     * The title of the advancement.
     */
    private final Component title;
    /**
     * The description of the advancement.
     */
    private final Component description;
    /**
     * The icon of the advancement.
     */
    private final ItemStackTemplate icon;
    /**
     * The background texture of the advancement tab, only used for root advancements.
     */
    private Optional<ClientAsset.ResourceTexture> background = Optional.empty();
    /**
     * The frame type of the advancement.
     */
    private AdvancementType type = AdvancementType.TASK;
    /**
     * If a toast is shown when the advancement is completed.
     */
    private boolean showToast = true;
    /**
     * If completing the advancement is announced in chat.
     */
    private boolean announceChat = true;
    /**
     * If the advancement is hidden until it is completed.
     */
    private boolean hidden = false;

    /**
     * @param icon        the display icon
     * @param title       the display title
     * @param description the display description
     */
    DisplayInfoBuilder(ItemStackTemplate icon, Component title, Component description) {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    /**
     * @param background texture location for the advancement tab background
     * @return the builder
     */
    public DisplayInfoBuilder setBackground(@Nullable Identifier background) {
        this.background = Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new);
        return this;
    }

    /**
     * @param background texture for the advancement tab background
     * @return the builder
     */
    public DisplayInfoBuilder setBackground(ClientAsset.@Nullable ResourceTexture background) {
        this.background = Optional.ofNullable(background);
        return this;
    }

    /**
     * @param type the advancement type, controlling frame and reward appearance
     * @return the builder
     */
    public DisplayInfoBuilder setType(AdvancementType type) {
        Objects.requireNonNull(type, "type is null");
        this.type = type;
        return this;
    }

    /**
     * @param showToast if a toast is shown when the advancement is completed
     * @return the builder
     */
    public DisplayInfoBuilder setShowToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    /**
     * @param announceChat if completing, the advancement is announced in chat
     * @return the builder
     */
    public DisplayInfoBuilder setAnnounceChat(boolean announceChat) {
        this.announceChat = announceChat;
        return this;
    }

    /**
     * @param hidden if the advancement is hidden until it is completed
     * @return the builder
     */
    public DisplayInfoBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    /**
     * Creates the display info from this builder.
     *
     * @return the display info
     */
    public DisplayInfo build() {
        return new DisplayInfo(this.icon,
                this.title,
                this.description,
                this.background,
                this.type,
                this.showToast,
                this.announceChat,
                this.hidden);
    }
}
