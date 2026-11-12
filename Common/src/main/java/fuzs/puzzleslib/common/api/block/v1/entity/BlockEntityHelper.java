package fuzs.puzzleslib.common.api.block.v1.entity;

import fuzs.puzzleslib.common.mixin.accessor.BlockEntityAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Helpers for changing the type of a block entity after it has been created.
 */
public final class BlockEntityHelper {

    private BlockEntityHelper() {
        // NO-OP
    }

    /**
     * Sets the type of the given block entity.
     *
     * @param blockEntity the block entity
     * @param type        the new type
     */
    public static void setType(BlockEntity blockEntity, BlockEntityType<?> type) {
        ((BlockEntityAccessor) blockEntity).puzzleslib$setType(type);
    }

    /**
     * Sets the type and the block state of the given block entity.
     *
     * @param blockEntity the block entity
     * @param type        the new type
     * @param blockState  the new block state
     */
    @SuppressWarnings("deprecation")
    public static void init(BlockEntity blockEntity, BlockEntityType<?> type, BlockState blockState) {
        setType(blockEntity, type);
        blockEntity.setBlockState(blockState);
    }
}
