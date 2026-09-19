package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.common.api.core.v1.context.GameplayContentContext;
import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class GameplayContentContextFabricImpl implements GameplayContentContext {
    @Override
    public void registerFlammable(Holder<Block> flammableBlock, int encouragement, int flammability) {
        Preconditions.checkArgument(encouragement > 0, "encouragement is non-positive");
        Preconditions.checkArgument(flammability > 0, "flammability is non-positive");
        Objects.requireNonNull(flammableBlock, "flammable block is null");
        // flammability == burn, encouragement == spread
        FlammableBlockRegistry.getDefaultInstance().add(flammableBlock.value(), flammability, encouragement);
    }

    @Override
    public void registerStrippable(Holder<Block> unstrippedBlock, Holder<Block> strippedBlock) {
        Objects.requireNonNull(unstrippedBlock, "unstripped block is null");
        Objects.requireNonNull(strippedBlock, "stripped block is null");
        BlockTransformerHelper.registerStripping(unstrippedBlock.value(), strippedBlock.value());
    }

    @Override
    public void registerFlattenable(Holder<Block> unflattenedBlock, Holder<Block> flattenedBlock) {
        Objects.requireNonNull(unflattenedBlock, "unflattened block is null");
        Objects.requireNonNull(flattenedBlock, "flattened block is null");
        BlockTransformerHelper.registerFlattening(unflattenedBlock.value(), flattenedBlock.value().defaultBlockState());
    }

    @Override
    public void registerTillable(Holder<Block> untilledBlock, Holder<Block> tilledBlock) {
        Objects.requireNonNull(untilledBlock, "untilled block is null");
        Objects.requireNonNull(tilledBlock, "tilled block is null");
        BlockTransformerHelper.registerTilling(untilledBlock.value(), tilledBlock.value().defaultBlockState());
    }

    @Override
    public void registerOxidizable(Holder<Block> unoxidizedBlock, Holder<Block> oxidizedBlock) {
        Objects.requireNonNull(unoxidizedBlock, "unoxidized block is null");
        Objects.requireNonNull(oxidizedBlock, "oxidized block is null");
        OxidizableBlocksRegistry.registerNextStage(unoxidizedBlock.value(), oxidizedBlock.value());
    }

    @Override
    public void registerWaxable(Holder<Block> unwaxedBlock, Holder<Block> waxedBlock) {
        Objects.requireNonNull(unwaxedBlock, "unwaxed block is null");
        Objects.requireNonNull(waxedBlock, "waxed block is null");
        OxidizableBlocksRegistry.registerWaxable(unwaxedBlock.value(), waxedBlock.value());
    }
}
