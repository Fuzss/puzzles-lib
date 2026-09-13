package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Copied from Minecraft 26.2.
 */
public class QuadCollection {
    public static final QuadCollection EMPTY = new QuadCollection(List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of());
    private final List<BakedQuad> all;
    private final List<BakedQuad> unculled;
    private final List<BakedQuad> north;
    private final List<BakedQuad> south;
    private final List<BakedQuad> east;
    private final List<BakedQuad> west;
    private final List<BakedQuad> up;
    private final List<BakedQuad> down;

    private QuadCollection(List<BakedQuad> all, List<BakedQuad> unculled, List<BakedQuad> north, List<BakedQuad> south, List<BakedQuad> east, List<BakedQuad> west, List<BakedQuad> up, List<BakedQuad> down) {
        this.all = all;
        this.unculled = unculled;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.up = up;
        this.down = down;
    }

    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        return switch (direction) {
            case null -> this.unculled;
            case NORTH -> this.north;
            case SOUTH -> this.south;
            case EAST -> this.east;
            case WEST -> this.west;
            case UP -> this.up;
            case DOWN -> this.down;
        };
    }

    public List<BakedQuad> getAll() {
        return this.all;
    }

    public static class Builder {
        private final ImmutableList.Builder<BakedQuad> unculledFaces = ImmutableList.builder();
        private final Multimap<Direction, BakedQuad> culledFaces = ArrayListMultimap.create();

        public Builder addCulledFace(Direction direction, BakedQuad quad) {
            this.culledFaces.put(direction, quad);
            return this;
        }

        public Builder addUnculledFace(BakedQuad quad) {
            this.unculledFaces.add(quad);
            return this;
        }

        public Builder addAll(QuadCollection quadCollection) {
            this.culledFaces.putAll(Direction.UP, quadCollection.up);
            this.culledFaces.putAll(Direction.DOWN, quadCollection.down);
            this.culledFaces.putAll(Direction.NORTH, quadCollection.north);
            this.culledFaces.putAll(Direction.SOUTH, quadCollection.south);
            this.culledFaces.putAll(Direction.EAST, quadCollection.east);
            this.culledFaces.putAll(Direction.WEST, quadCollection.west);
            this.unculledFaces.addAll(quadCollection.unculled);
            return this;
        }

        private static QuadCollection createFromSublists(List<BakedQuad> all, int unculledCount, int northCount, int southCount, int eastCount, int westCount, int upCount, int downCount) {
            int index = 0;
            List<BakedQuad> unculled = all.subList(index, index += unculledCount);
            List<BakedQuad> north = all.subList(index, index += northCount);
            List<BakedQuad> south = all.subList(index, index += southCount);
            List<BakedQuad> east = all.subList(index, index += eastCount);
            List<BakedQuad> west = all.subList(index, index += westCount);
            List<BakedQuad> up = all.subList(index, index += upCount);
            List<BakedQuad> down = all.subList(index, index + downCount);
            return new QuadCollection(all, unculled, north, south, east, west, up, down);
        }

        public QuadCollection build() {
            ImmutableList<BakedQuad> unculledFaces = this.unculledFaces.build();
            if (this.culledFaces.isEmpty()) {
                return unculledFaces.isEmpty() ? QuadCollection.EMPTY : new QuadCollection(unculledFaces,
                        unculledFaces,
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of());
            }

            ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
            quads.addAll(unculledFaces);
            Collection<BakedQuad> north = this.culledFaces.get(Direction.NORTH);
            quads.addAll(north);
            Collection<BakedQuad> south = this.culledFaces.get(Direction.SOUTH);
            quads.addAll(south);
            Collection<BakedQuad> east = this.culledFaces.get(Direction.EAST);
            quads.addAll(east);
            Collection<BakedQuad> west = this.culledFaces.get(Direction.WEST);
            quads.addAll(west);
            Collection<BakedQuad> up = this.culledFaces.get(Direction.UP);
            quads.addAll(up);
            Collection<BakedQuad> down = this.culledFaces.get(Direction.DOWN);
            quads.addAll(down);
            return createFromSublists(quads.build(),
                    unculledFaces.size(),
                    north.size(),
                    south.size(),
                    east.size(),
                    west.size(),
                    up.size(),
                    down.size());
        }
    }
}
