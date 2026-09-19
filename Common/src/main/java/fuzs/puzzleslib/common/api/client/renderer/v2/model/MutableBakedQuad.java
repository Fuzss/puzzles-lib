package fuzs.puzzleslib.common.api.client.renderer.v2.model;

import fuzs.puzzleslib.common.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class MutableBakedQuad {
    protected Vector3fc position0;
    protected Vector3fc position1;
    protected Vector3fc position2;
    protected Vector3fc position3;

    protected long packedUV0;
    protected long packedUV1;
    protected long packedUV2;
    protected long packedUV3;

    protected Direction direction;
    protected BakedQuad.@Nullable MaterialInfo materialInfo;
    protected TextureAtlasSprite sprite;
    protected ChunkSectionLayer layer;
    protected RenderType itemRenderType;
    protected RenderType itemGlintRenderType;
    protected RenderType itemGlintSpecialRenderType;
    protected int tintIndex;
    protected Direction shadeDirectionOverride;
    protected int lightEmission;

    public MutableBakedQuad(BakedQuad bakedQuad) {
        this.position0 = bakedQuad.position0();
        this.position1 = bakedQuad.position1();
        this.position2 = bakedQuad.position2();
        this.position3 = bakedQuad.position3();

        this.packedUV0 = bakedQuad.packedUV0();
        this.packedUV1 = bakedQuad.packedUV1();
        this.packedUV2 = bakedQuad.packedUV2();
        this.packedUV3 = bakedQuad.packedUV3();

        this.direction = bakedQuad.direction();
        this.materialInfo = bakedQuad.materialInfo();
        this.sprite = bakedQuad.materialInfo().sprite();
        this.layer = bakedQuad.materialInfo().layer();
        this.itemRenderType = bakedQuad.materialInfo().itemRenderType();
        this.itemGlintRenderType = bakedQuad.materialInfo().itemGlintRenderType();
        this.itemGlintSpecialRenderType = bakedQuad.materialInfo().itemGlintSpecialRenderType();
        this.tintIndex = bakedQuad.materialInfo().tintIndex();
        this.shadeDirectionOverride = bakedQuad.materialInfo().shadeDirectionOverride();
        this.lightEmission = bakedQuad.materialInfo().lightEmission();
    }

    public static MutableBakedQuad toMutable(BakedQuad bakedQuad) {
        return ClientProxyImpl.get().getMutableBakedQuad(bakedQuad);
    }

    public Vector3fc position0() {
        return this.position0;
    }

    public Vector3fc position1() {
        return this.position1;
    }

    public Vector3fc position2() {
        return this.position2;
    }

    public Vector3fc position3() {
        return this.position3;
    }

    public Vector3fc position(int vertexIndex) {
        return switch (vertexIndex) {
            case 0 -> this.position0();
            case 1 -> this.position1();
            case 2 -> this.position2();
            case 3 -> this.position3();
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public long packedUV0() {
        return this.packedUV0;
    }

    public long packedUV1() {
        return this.packedUV1;
    }

    public long packedUV2() {
        return this.packedUV2;
    }

    public long packedUV3() {
        return this.packedUV3;
    }

    public long packedUV(int vertexIndex) {
        return switch (vertexIndex) {
            case 0 -> this.packedUV0();
            case 1 -> this.packedUV1();
            case 2 -> this.packedUV2();
            case 3 -> this.packedUV3();
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public Direction direction() {
        return this.direction;
    }

    public BakedQuad.MaterialInfo materialInfo() {
        return this.materialInfo != null ? this.materialInfo : new BakedQuad.MaterialInfo(this.sprite(),
                this.layer(),
                this.itemRenderType(),
                this.itemGlintRenderType(),
                this.itemGlintSpecialRenderType(),
                this.tintIndex(),
                this.shadeDirectionOverride(),
                this.lightEmission());
    }

    public TextureAtlasSprite sprite() {
        return this.sprite;
    }

    public ChunkSectionLayer layer() {
        return this.layer;
    }

    public RenderType itemRenderType() {
        return this.itemRenderType;
    }

    public RenderType itemGlintRenderType() {
        return this.itemGlintRenderType;
    }

    public RenderType itemGlintSpecialRenderType() {
        return this.itemGlintSpecialRenderType;
    }

    public int tintIndex() {
        return this.tintIndex;
    }

    public Direction shadeDirectionOverride() {
        return this.shadeDirectionOverride;
    }

    public int lightEmission() {
        return this.lightEmission;
    }

    public MutableBakedQuad position0(Vector3fc position) {
        this.position0 = position;
        return this.computeQuadNormals();
    }

    public MutableBakedQuad position1(Vector3fc position) {
        this.position1 = position;
        return this.computeQuadNormals();
    }

    public MutableBakedQuad position2(Vector3fc position) {
        this.position2 = position;
        return this.computeQuadNormals();
    }

    public MutableBakedQuad position3(Vector3fc position) {
        this.position3 = position;
        return this.computeQuadNormals();
    }

    public MutableBakedQuad position(int vertexIndex, Vector3fc position) {
        return switch (vertexIndex) {
            case 0 -> this.position0(position);
            case 1 -> this.position1(position);
            case 2 -> this.position2(position);
            case 3 -> this.position3(position);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public MutableBakedQuad packedUV0(long packedUV) {
        this.packedUV0 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV1(long packedUV) {
        this.packedUV1 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV2(long packedUV) {
        this.packedUV2 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV3(long packedUV) {
        this.packedUV3 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV(int vertexIndex, long packedUV) {
        return switch (vertexIndex) {
            case 0 -> this.packedUV0(packedUV);
            case 1 -> this.packedUV1(packedUV);
            case 2 -> this.packedUV2(packedUV);
            case 3 -> this.packedUV3(packedUV);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public MutableBakedQuad direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    public MutableBakedQuad sprite(TextureAtlasSprite sprite) {
        this.materialInfo = null;
        this.sprite = sprite;
        return this;
    }

    public void layer(ChunkSectionLayer layer) {
        this.materialInfo = null;
        this.layer = layer;
    }

    public void itemRenderType(RenderType itemRenderType) {
        this.materialInfo = null;
        this.itemRenderType = itemRenderType;
    }

    public MutableBakedQuad itemGlintRenderType(RenderType itemGlintRenderType) {
        this.materialInfo = null;
        this.itemGlintRenderType = itemGlintRenderType;
        return this;
    }

    public MutableBakedQuad itemGlintSpecialRenderType(RenderType itemGlintSpecialRenderType) {
        this.materialInfo = null;
        this.itemGlintSpecialRenderType = itemGlintSpecialRenderType;
        return this;
    }

    public MutableBakedQuad tintIndex(int tintIndex) {
        this.materialInfo = null;
        this.tintIndex = tintIndex;
        return this;
    }

    public MutableBakedQuad shadeDirectionOverride(Direction shadeDirectionOverride) {
        this.materialInfo = null;
        this.shadeDirectionOverride = shadeDirectionOverride;
        return this;
    }

    public MutableBakedQuad lightEmission(int lightEmission) {
        this.materialInfo = null;
        this.lightEmission = lightEmission;
        return this;
    }

    public MutableBakedQuad computeQuadNormals() {
        return this;
    }

    public BakedQuad toImmutable() {
        return new BakedQuad(this.position0(),
                this.position1(),
                this.position2(),
                this.position3(),
                this.packedUV0(),
                this.packedUV1(),
                this.packedUV2(),
                this.packedUV3(),
                this.direction(),
                this.materialInfo());
    }
}
