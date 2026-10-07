package fuzs.puzzleslib.common.api.client.init.v1.family;

import fuzs.puzzleslib.common.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

/**
 * Client side extension for the registration methods of {@link BlockSetFamily}.
 */
public final class ClientBlockSetFamily {

    private ClientBlockSetFamily() {
        // NO-OP
    }

    /**
     * Registers the entity renderers for the {@link BlockSetVariant#BOAT} and {@link BlockSetVariant#CHEST_BOAT} entity
     * types of the given block set family.
     *
     * @param blockSetFamily      the block set family
     * @param context             the entity renderers context
     * @param boatModelLayer      the model layer used for the boat renderer
     * @param chestBoatModelLayer the model layer used for the chest boat renderer
     */
    @SuppressWarnings("unchecked")
    public static void registerFor(BlockSetFamily blockSetFamily, EntityRenderersContext context, ModelLayerLocation boatModelLayer, ModelLayerLocation chestBoatModelLayer) {
        context.registerEntityRenderer((EntityType<? extends AbstractBoat>) blockSetFamily.getEntityType(BlockSetVariant.BOAT)
                .value(), (EntityRendererProvider.Context contextX) -> new BoatRenderer(contextX, boatModelLayer));
        context.registerEntityRenderer((EntityType<? extends AbstractBoat>) blockSetFamily.getEntityType(BlockSetVariant.CHEST_BOAT)
                .value(), (EntityRendererProvider.Context contextX) -> new BoatRenderer(contextX, chestBoatModelLayer));
    }
}
