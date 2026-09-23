package fuzs.puzzleslib.common.impl.client.gui;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class WidgetTooltipHolderImpl extends WidgetTooltipHolder {
    private final AbstractWidget abstractWidget;
    private List<? extends FormattedText> tooltipLines;
    @Nullable
    private final BiFunction<ClientTooltipPositioner, AbstractWidget, ClientTooltipPositioner> tooltipPositionerFactory;
    private final Function<List<? extends FormattedText>, List<FormattedCharSequence>> tooltipLineProcessor;
    @Nullable
    private final Supplier<List<? extends FormattedText>> tooltipLinesSupplier;
    private final boolean extraSpaceAfterFirstLine;

    public WidgetTooltipHolderImpl(AbstractWidget abstractWidget, TooltipBuilderImpl builder) {
        this.abstractWidget = abstractWidget;
        this.tooltipLines =
                builder.tooltipLinesSupplier != null ? Collections.emptyList() : List.copyOf(builder.tooltipLines);
        this.tooltipLineProcessor = builder.tooltipLineProcessor;
        this.tooltipPositionerFactory = builder.tooltipPositionerFactory;
        this.tooltipLinesSupplier = builder.tooltipLinesSupplier;
        this.extraSpaceAfterFirstLine = builder.extraSpaceAfterFirstLine;
        super.setDelay(builder.tooltipDelay);
        super.set(new Tooltip(CommonComponents.EMPTY, null, Optional.empty(), null) {
            @Override
            public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
                Language language = Language.getInstance();
                if (this.cachedTooltip == null || language != this.splitWithLanguage) {
                    this.cachedTooltip = this.processTooltipLines(WidgetTooltipHolderImpl.this.tooltipLines);
                    this.splitWithLanguage = language;
                }

                return this.cachedTooltip;
            }

            private List<FormattedCharSequence> processTooltipLines(List<? extends FormattedText> tooltipLines) {
                Preconditions.checkState(!tooltipLines.isEmpty(), "lines is empty");
                return WidgetTooltipHolderImpl.this.tooltipLineProcessor.apply(tooltipLines);
            }
        });
    }

    @Override
    public void setDelay(Duration delay) {
        // our tooltip implementation is immutable, when something tries to change it reset to vanilla
        WidgetTooltipHolder holder = new WidgetTooltipHolder();
        this.abstractWidget.tooltip = holder;
        holder.setDelay(delay);
    }

    @Override
    public void set(@Nullable Tooltip tooltip) {
        // our tooltip implementation is immutable, when something tries to change it reset to vanilla
        WidgetTooltipHolder holder = new WidgetTooltipHolder();
        this.abstractWidget.tooltip = holder;
        holder.set(tooltip);
    }

    @NonNull
    @Override
    public Tooltip get() {
        Tooltip tooltip = super.get();
        Objects.requireNonNull(tooltip, "tooltip is null");
        return tooltip;
    }

    /**
     * @see WidgetTooltipHolder#refreshTooltipForNextRenderPass(GuiGraphicsExtractor, int, int, boolean, boolean,
     *         ScreenRectangle)
     */
    @Override
    public void refreshTooltipForNextRenderPass(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovering, boolean focused, ScreenRectangle screenRectangle) {
        this.refreshLines(this.getLinesForNextRenderPass());
        if (this.tooltipLines.isEmpty()) {
            return;
        }

        Tooltip tooltip = super.get();
        if (tooltip == null) {
            this.wasDisplayed = false;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            boolean shouldDisplay = hovering || focused && minecraft.getLastInputType().isKeyboard();
            if (shouldDisplay != this.wasDisplayed) {
                if (shouldDisplay) {
                    this.displayStartTime = Util.getMillis();
                }

                this.wasDisplayed = shouldDisplay;
            }

            if (shouldDisplay && Util.getMillis() - this.displayStartTime > this.delay.toMillis()) {
                List<ClientTooltipComponent> components = tooltip.toCharSequence(minecraft)
                        .stream()
                        .map(ClientTooltipComponent::create)
                        .collect(Collectors.toList());
                tooltip.component().ifPresent((TooltipComponent component) -> {
                    components.add(components.isEmpty() ? 0 : 1, ClientTooltipComponent.create(component));
                });
                guiGraphics.setTooltipForNextFrameInternal(minecraft.font,
                        components,
                        mouseX,
                        mouseY,
                        this.createTooltipPositioner(screenRectangle, hovering, focused),
                        tooltip.style(),
                        focused,
                        this.extraSpaceAfterFirstLine);
            }
        }
    }

    @Nullable
    private List<? extends FormattedText> getLinesForNextRenderPass() {
        return this.tooltipLinesSupplier != null ? this.tooltipLinesSupplier.get() : null;
    }

    private void refreshLines(@Nullable List<? extends FormattedText> lines) {
        if (lines != null && !Objects.equals(lines, this.tooltipLines)) {
            this.tooltipLines = lines;
            this.get().cachedTooltip = null;
        }
    }

    @Override
    public ClientTooltipPositioner createTooltipPositioner(ScreenRectangle screenRectangle, boolean hovering, boolean focused) {
        ClientTooltipPositioner tooltipPositioner = super.createTooltipPositioner(screenRectangle, hovering, focused);
        if (this.tooltipPositionerFactory != null) {
            return this.tooltipPositionerFactory.apply(tooltipPositioner, this.abstractWidget);
        } else {
            return tooltipPositioner;
        }
    }
}
