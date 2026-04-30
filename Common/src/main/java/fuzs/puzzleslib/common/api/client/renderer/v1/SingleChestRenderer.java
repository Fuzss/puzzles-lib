package fuzs.puzzleslib.common.api.client.renderer.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.model.ConditionalBlockModel;
import net.minecraft.client.renderer.block.model.properties.conditional.IsXmas;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * An extension of {@link ChestRenderer} that allows specifying custom chest texture materials for single chests (e.g.
 * ender chest).
 *
 * @param <T> the chest block entity type
 * @param <M> the chest model type
 */
public abstract class SingleChestRenderer<T extends BlockEntity & LidBlockEntity, M extends ChestModel, S extends SingleChestRenderer.SingleChestRenderState> extends ChestRenderer<T> {
    /**
     * The sprite set.
     */
    protected final SpriteGetter sprites;
    /**
     * The chest model.
     */
    protected final M model;

    /**
     * @param context the renderer context
     * @param model   the single chest model
     */
    public SingleChestRenderer(BlockEntityRendererProvider.Context context, M model) {
        super(context);
        this.sprites = context.sprites();
        this.model = model;
    }

    /**
     * Creates a single chest model factory which is able to use Christmas textures.
     *
     * @param texture the chest texture location
     * @return the model factory
     *
     * @see BuiltInBlockModels#createXmasChest(MultiblockChestResources)
     */
    public static BuiltInBlockModels.SpecialModelFactory createXmasChest(Identifier texture) {
        return BuiltInBlockModels.specialModelWithPropertyDispatch(ChestBlock.FACING,
                (Direction facing) -> new ConditionalBlockModel.Unbaked(Optional.empty(),
                        new IsXmas(),
                        BuiltInBlockModels.createChest(ChestSpecialRenderer.CHRISTMAS.single(),
                                ChestType.SINGLE,
                                facing),
                        BuiltInBlockModels.createChest(texture, ChestType.SINGLE, facing)));
    }

    @Override
    public S createRenderState() {
        return (S) new SingleChestRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, ChestRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        ((S) state).chestMaterial = this.isXmas() ? null : this.getChestSprite(blockEntity);
    }

    /**
     * Get the single chest texture material for the chest type.
     *
     * @param blockEntity the block entity
     * @return the single chest texture material
     */
    protected abstract SpriteId getChestSprite(T blockEntity);

    /**
     * When active, the vanilla chest model will be used automatically.
     *
     * @return should use holiday textures
     */
    protected boolean isXmas() {
        return this.xmasTextures;
    }

    @Override
    public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        SpriteId sprite = ((S) state).chestMaterial;
        if (sprite != null) {
            poseStack.pushPose();
            poseStack.mulPose(modelTransformation(state.facing));
            submitNodeCollector.submitModel(this.model,
                    ((S) state).getOpenness(),
                    poseStack,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    -1,
                    sprite,
                    this.sprites,
                    0,
                    state.breakProgress);
            poseStack.popPose();
        } else {
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

    /**
     * A custom chest render state that stores the {@link SpriteId} directly, to allow for working around vanilla's
     * {@link net.minecraft.client.renderer.blockentity.state.ChestRenderState.ChestMaterialType} enum class.
     */
    public static class SingleChestRenderState extends ChestRenderState {
        /**
         * The chest material. The vanilla chest model will render when {@code null}.
         */
        @Nullable
        public SpriteId chestMaterial;

        /**
         * @return the chest lid openness
         */
        public float getOpenness() {
            float openness = this.open;
            openness = 1.0F - openness;
            return 1.0F - openness * openness * openness;
        }
    }
}
