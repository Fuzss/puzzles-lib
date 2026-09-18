package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.common.api.client.event.v1.renderer.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public final class FabricRendererEvents {
    /**
     * Fires before the name tag of an entity is rendered.
     */
    public static final Event<SubmitNameTagCallback> SUBMIT_NAME_TAG = FabricEventFactory.createResult(
            SubmitNameTagCallback.class);
    /**
     * Called during {@link EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}, for setting up the
     * render state of an entity for future rendering.
     */
    public static final Event<ExtractEntityRenderStateCallback> EXTRACT_ENTITY_RENDER_STATE = FabricEventFactory.create(
            ExtractEntityRenderStateCallback.class);
    /**
     * Called before a living entity model is submitted for rendering.
     */
    public static final Event<SubmitLivingEntityEvents.Before> BEFORE_SUBMIT_LIVING_ENTITY = FabricEventFactory.createResult(
            SubmitLivingEntityEvents.Before.class);
    /**
     * Called after a living entity model is submitted for rendering.
     */
    public static final Event<SubmitLivingEntityEvents.After> AFTER_SUBMIT_LIVING_ENTITY = FabricEventFactory.create(
            SubmitLivingEntityEvents.After.class);
    /**
     * Called before the player's arm holding an item is rendered in first-person mode.
     */
    public static final Event<SubmitArmWithItemCallback> SUBMIT_ARM_WITH_ITEM = FabricEventFactory.createResult(
            SubmitArmWithItemCallback.class);
    /**
     * Runs before camera angle setup is done, allows for additional control over roll (which vanilla itself does not
     * support) in addition to pitch and yaw.
     */
    public static final Event<ComputeCameraAnglesCallback> COMPUTE_CAMERA_ANGLES = FabricEventFactory.create(
            ComputeCameraAnglesCallback.class);
    /**
     * Called after the fog color is calculated from the current block overlay or biome. Allows for modifying the
     * color.
     */
    public static final Event<FogEvents.Color> FOG_COLOR = FabricEventFactory.create(FogEvents.Color.class);
    /**
     * Called before fog is rendered, allows for controlling fog start and end distance.
     */
    public static final Event<FogEvents.Setup> SETUP_FOG = FabricEventFactory.create(FogEvents.Setup.class);
    /**
     * Runs after field of view is calculated, based on the game setting, but before in-game effects such as nausea are
     * applied.
     */
    public static final Event<ComputeFieldOfViewCallback> COMPUTE_FIELD_OF_VIEW = FabricEventFactory.create(
            ComputeFieldOfViewCallback.class);

    private FabricRendererEvents() {
        // NO-OP
    }
}
