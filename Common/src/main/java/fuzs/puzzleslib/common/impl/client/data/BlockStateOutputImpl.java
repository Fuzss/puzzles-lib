package fuzs.puzzleslib.common.impl.client.data;

import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Predicate;

public final class BlockStateOutputImpl extends ModelProvider.BlockStateGeneratorCollector {
    private final Predicate<Holder.Reference<Block>> blockFilter;

    public BlockStateOutputImpl(Predicate<Holder.Reference<Block>> blockFilter) {
        this.blockFilter = blockFilter;
    }

    @Override
    public void validate() {
        List<Identifier> list = BuiltInRegistries.BLOCK.listElements()
                // Apply a filter, so we only consider our own content.
                .filter(this.blockFilter)
                .filter((Holder.Reference<Block> holder) -> !this.generators.containsKey(holder.value()))
                .map((Holder.Reference<Block> holder) -> holder.key().identifier())
                .toList();
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + list);
        }
    }
}
