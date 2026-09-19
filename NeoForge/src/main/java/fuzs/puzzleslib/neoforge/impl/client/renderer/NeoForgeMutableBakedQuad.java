package fuzs.puzzleslib.neoforge.impl.client.renderer;

import fuzs.puzzleslib.common.api.client.renderer.v2.model.MutableBakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class NeoForgeMutableBakedQuad extends MutableBakedQuad {
    protected boolean ambientOcclusion;
    @Nullable
    protected BakedNormals bakedNormals;
    @Nullable
    protected Integer packedNormal0;
    @Nullable
    protected Integer packedNormal1;
    @Nullable
    protected Integer packedNormal2;
    @Nullable
    protected Integer packedNormal3;
    @Nullable
    protected BakedColors bakedColors;
    @Nullable
    protected Integer packedColor0;
    @Nullable
    protected Integer packedColor1;
    @Nullable
    protected Integer packedColor2;
    @Nullable
    protected Integer packedColor3;

    public NeoForgeMutableBakedQuad(BakedQuad bakedQuad) {
        super(bakedQuad);
        this.ambientOcclusion = bakedQuad.materialInfo().ambientOcclusion();
        this.bakedNormals = bakedQuad.bakedNormals();
        this.bakedColors = bakedQuad.bakedColors();
    }

    @Override
    public BakedQuad.MaterialInfo materialInfo() {
        return this.materialInfo != null ? this.materialInfo : new BakedQuad.MaterialInfo(this.sprite(),
                this.layer(),
                this.itemRenderType(),
                this.itemGlintRenderType(),
                this.itemGlintSpecialRenderType(),
                this.tintIndex(),
                this.shadeDirectionOverride(),
                this.lightEmission(),
                this.ambientOcclusion());
    }

    public boolean ambientOcclusion() {
        return this.ambientOcclusion;
    }

    public MutableBakedQuad ambientOcclusion(boolean ambientOcclusion) {
        this.materialInfo = null;
        this.ambientOcclusion = ambientOcclusion;
        return this;
    }

    public BakedNormals bakedNormals() {
        // Custom normals win over any existing or automatically computed.
        if (this.packedNormal0 != null || this.packedNormal1 != null || this.packedNormal2 != null
                || this.packedNormal3 != null) {
            return BakedNormals.of(Objects.requireNonNull(this.packedNormal0),
                    Objects.requireNonNull(this.packedNormal1),
                    Objects.requireNonNull(this.packedNormal2),
                    Objects.requireNonNull(this.packedNormal3));
        } else {
            return this.bakedNormals != null ? this.bakedNormals :
                    BakedNormals.of(BakedNormals.computeQuadNormal(this.position0(),
                            this.position1(),
                            this.position2(),
                            this.position3()));
        }
    }

    public BakedColors bakedColors() {
        return this.bakedColors != null ? this.bakedColors : BakedColors.of(Objects.requireNonNull(this.packedColor0),
                Objects.requireNonNull(this.packedColor1),
                Objects.requireNonNull(this.packedColor2),
                Objects.requireNonNull(this.packedColor3));
    }

    @Override
    public NeoForgeMutableBakedQuad computeQuadNormals() {
        // Leave as unspecified if the normals already are.
        if (this.bakedNormals != BakedNormals.UNSPECIFIED) {
            this.bakedNormals = null;
        }

        return this;
    }

    public NeoForgeMutableBakedQuad packedNormal0(int packedNormal) {
        this.bakedNormals = null;
        this.packedNormal0 = packedNormal;
        return this;
    }

    public NeoForgeMutableBakedQuad packedNormal1(int packedNormal) {
        this.bakedNormals = null;
        this.packedNormal1 = packedNormal;
        return this;
    }

    public NeoForgeMutableBakedQuad packedNormal2(int packedNormal) {
        this.bakedNormals = null;
        this.packedNormal2 = packedNormal;
        return this;
    }

    public NeoForgeMutableBakedQuad packedNormal3(int packedNormal) {
        this.bakedNormals = null;
        this.packedNormal3 = packedNormal;
        return this;
    }

    public NeoForgeMutableBakedQuad packedNormal(int vertexIndex, int packedNormal) {
        return switch (vertexIndex) {
            case 0 -> this.packedNormal0(packedNormal);
            case 1 -> this.packedNormal1(packedNormal);
            case 2 -> this.packedNormal2(packedNormal);
            case 3 -> this.packedNormal3(packedNormal);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public NeoForgeMutableBakedQuad packedNormal(int packedNormal) {
        return this.packedNormal0(packedNormal)
                .packedNormal1(packedNormal)
                .packedNormal2(packedNormal)
                .packedNormal3(packedNormal);
    }

    public NeoForgeMutableBakedQuad packedColor0(int packedColor) {
        this.bakedColors = null;
        this.packedColor0 = packedColor;
        return this;
    }

    public NeoForgeMutableBakedQuad packedColor1(int packedColor) {
        this.bakedColors = null;
        this.packedColor1 = packedColor;
        return this;
    }

    public NeoForgeMutableBakedQuad packedColor2(int packedColor) {
        this.bakedColors = null;
        this.packedColor2 = packedColor;
        return this;
    }

    public NeoForgeMutableBakedQuad packedColor3(int packedColor) {
        this.bakedColors = null;
        this.packedColor3 = packedColor;
        return this;
    }

    public NeoForgeMutableBakedQuad packedColor(int vertexIndex, int packedColor) {
        return switch (vertexIndex) {
            case 0 -> this.packedColor0(packedColor);
            case 1 -> this.packedColor1(packedColor);
            case 2 -> this.packedColor2(packedColor);
            case 3 -> this.packedColor3(packedColor);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public NeoForgeMutableBakedQuad packedColor(int packedColor) {
        return this.packedColor0(packedColor)
                .packedColor1(packedColor)
                .packedColor2(packedColor)
                .packedColor3(packedColor);
    }

    @Override
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
                this.materialInfo(),
                this.bakedNormals(),
                this.bakedColors());
    }
}
