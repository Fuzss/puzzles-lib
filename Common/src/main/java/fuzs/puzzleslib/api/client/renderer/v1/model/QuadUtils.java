package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Similar to NeoForge's {@code net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer} and Fabric's
 * {@code net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView}.
 */
public final class QuadUtils {
    /**
     * Number of vertex indices.
     */
    public static final int VERTEX_COUNT = 4;
    /**
     * Count of components in a vertex.
     */
    public static final int VERTEX_STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / VERTEX_COUNT;
    /**
     * Start index for position components.
     */
    public static final int VERTEX_POSITION =
            DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.POSITION) / VERTEX_COUNT;
    /**
     * Start index for color components.
     */
    public static final int VERTEX_COLOR =
            DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.COLOR) / VERTEX_COUNT;
    /**
     * Start index for uv components.
     */
    public static final int VERTEX_UV = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV) / VERTEX_COUNT;
    /**
     * Start index for uv components.
     */
    public static final int VERTEX_UV0 = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV0) / VERTEX_COUNT;
    /**
     * Start index for uv components.
     */
    public static final int VERTEX_UV1 = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV1) / VERTEX_COUNT;
    /**
     * Start index for uv components.
     */
    public static final int VERTEX_UV2 = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV2) / VERTEX_COUNT;
    /**
     * Start index for normal components.
     */
    public static final int VERTEX_NORMAL =
            DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.NORMAL) / VERTEX_COUNT;

    private QuadUtils() {
        // NO-OP
    }

    /**
     * Creates a deep copy of a baked quad.
     *
     * @param bakedQuad the quad to copy
     * @return the copied quad
     */
    public static BakedQuad copy(BakedQuad bakedQuad) {
        return ClientProxyImpl.get().copyBakedQuad(bakedQuad);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the position vector for the vertex
     */
    public static Vector3f getPosition(BakedQuad bakedQuad, int vertexIndex) {
        return new Vector3f(getX(bakedQuad, vertexIndex), getY(bakedQuad, vertexIndex), getZ(bakedQuad, vertexIndex));
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the x-position component for the vertex
     */
    public static float getX(BakedQuad bakedQuad, int vertexIndex) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_POSITION;
        return Float.intBitsToFloat(bakedQuad.getVertices()[offset]);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the y-position component for the vertex
     */
    public static float getY(BakedQuad bakedQuad, int vertexIndex) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_POSITION;
        return Float.intBitsToFloat(bakedQuad.getVertices()[offset + 1]);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the z-position component for the vertex
     */
    public static float getZ(BakedQuad bakedQuad, int vertexIndex) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_POSITION;
        return Float.intBitsToFloat(bakedQuad.getVertices()[offset + 2]);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the normal vector for the vertex
     */
    public static Vector3f getNormal(BakedQuad bakedQuad, int vertexIndex) {
        return new Vector3f(getNormalX(bakedQuad, vertexIndex),
                getNormalY(bakedQuad, vertexIndex),
                getNormalZ(bakedQuad, vertexIndex));
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the x-normal component for the vertex
     */
    public static float getNormalX(BakedQuad bakedQuad, int vertexIndex) {
        int packedNormal = getPackedNormal(bakedQuad, vertexIndex);
        return ((byte) (packedNormal & 0xFF)) / 127.0F;
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the y-normal component for the vertex
     */
    public static float getNormalY(BakedQuad bakedQuad, int vertexIndex) {
        int packedNormal = getPackedNormal(bakedQuad, vertexIndex);
        return ((byte) ((packedNormal >> 8) & 0xFF)) / 127.0F;
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the z-normal component for the vertex
     */
    public static float getNormalZ(BakedQuad bakedQuad, int vertexIndex) {
        int packedNormal = getPackedNormal(bakedQuad, vertexIndex);
        return ((byte) ((packedNormal >> 16) & 0xFF)) / 127.0F;
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the packed normal for the vertex
     */
    public static int getPackedNormal(BakedQuad bakedQuad, int vertexIndex) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL;
        return bakedQuad.getVertices()[offset];
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the packed color for the vertex
     */
    public static int getColor(BakedQuad bakedQuad, int vertexIndex) {
        return bakedQuad.getVertices()[vertexIndex * VERTEX_STRIDE + VERTEX_COLOR];
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the uv vector for the vertex
     */
    public static Vector2f getUv(BakedQuad bakedQuad, int vertexIndex) {
        return new Vector2f(getU(bakedQuad, vertexIndex), getV(bakedQuad, vertexIndex));
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the uv vector for the vertex
     */
    public static long getPackedUv(BakedQuad bakedQuad, int vertexIndex) {
        return packUv(getU(bakedQuad, vertexIndex), getV(bakedQuad, vertexIndex));
    }

    /**
     * Copied from Minecraft 26.2.
     *
     * @param u the u-component
     * @param v the v-component
     * @return the packed uv
     */
    public static long packUv(float u, float v) {
        long high = Float.floatToIntBits(u) & 4294967295L;
        long low = Float.floatToIntBits(v) & 4294967295L;
        return high << 32 | low;
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the u-component for the vertex
     */
    public static float getU(BakedQuad bakedQuad, int vertexIndex) {
        return Float.intBitsToFloat(bakedQuad.getVertices()[vertexIndex * VERTEX_STRIDE + VERTEX_UV]);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @return the v-component for the vertex
     */
    public static float getV(BakedQuad bakedQuad, int vertexIndex) {
        return Float.intBitsToFloat(bakedQuad.getVertices()[vertexIndex * VERTEX_STRIDE + VERTEX_UV + 1]);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param position    the position vector
     */
    public static void setPosition(BakedQuad bakedQuad, int vertexIndex, Vector3f position) {
        setPosition(bakedQuad, vertexIndex, position.x, position.y, position.z);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param x           the x-position component
     * @param y           the y-position component
     * @param z           the z-position component
     */
    public static void setPosition(BakedQuad bakedQuad, int vertexIndex, float x, float y, float z) {
        setX(bakedQuad, vertexIndex, x);
        setY(bakedQuad, vertexIndex, y);
        setZ(bakedQuad, vertexIndex, z);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param x           the x-position component
     */
    public static void setX(BakedQuad bakedQuad, int vertexIndex, float x) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_POSITION;
        bakedQuad.getVertices()[offset] = Float.floatToRawIntBits(x);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param y           the y-position component
     */
    public static void setY(BakedQuad bakedQuad, int vertexIndex, float y) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_POSITION;
        bakedQuad.getVertices()[offset + 1] = Float.floatToRawIntBits(y);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param z           the z-position component
     */
    public static void setZ(BakedQuad bakedQuad, int vertexIndex, float z) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_POSITION;
        bakedQuad.getVertices()[offset + 2] = Float.floatToRawIntBits(z);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param normal      the normal vector
     */
    public static void setNormal(BakedQuad bakedQuad, int vertexIndex, Vector3f normal) {
        setNormal(bakedQuad, vertexIndex, normal.x, normal.y, normal.z);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param x           the x-normal component
     * @param y           the y-normal component
     * @param z           the z-normal component
     */
    public static void setNormal(BakedQuad bakedQuad, int vertexIndex, float x, float y, float z) {
        int packedNormal =
                ((int) (x * 127.0F) & 0xFF) | (((int) (y * 127.0F) & 0xFF) << 8) | (((int) (z * 127.0F) & 0xFF) << 16);
        setPackedNormal(bakedQuad, vertexIndex, packedNormal);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param x           the x-normal component
     * @param y           the y-normal component
     * @param z           the z-normal component
     */
    public static void setPackedNormal(BakedQuad bakedQuad, int vertexIndex, int packedNormal) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL;
        bakedQuad.getVertices()[offset] = packedNormal;
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param red         the unpacked red component
     * @param green       the unpacked green component
     * @param blue        the unpacked blue component
     * @param alpha       the unpacked alpha component
     */
    public static void setColor(BakedQuad bakedQuad, int vertexIndex, int red, int green, int blue, int alpha) {
        int packedColor = ((alpha & 0xFF) << 24) | ((blue & 0xFF) << 16) | ((green & 0xFF) << 8) | (red & 0xFF);
        setPackedColor(bakedQuad, vertexIndex, packedColor);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param packedColor the packed color
     */
    public static void setPackedColor(BakedQuad bakedQuad, int vertexIndex, int packedColor) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_COLOR;
        bakedQuad.getVertices()[offset] = packedColor;
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param uv          the uv vector
     */
    public static void setUv(BakedQuad bakedQuad, int vertexIndex, Vector2f uv) {
        setUv(bakedQuad, vertexIndex, uv.x, uv.y);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param u           the u-component
     * @param v           the v-component
     */
    public static void setUv(BakedQuad bakedQuad, int vertexIndex, float u, float v) {
        setU(bakedQuad, vertexIndex, u);
        setV(bakedQuad, vertexIndex, v);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param packedUV    the packed uv
     */
    public static void setPackedUv(BakedQuad bakedQuad, int vertexIndex, long packedUV) {
        setU(bakedQuad, vertexIndex, unpackU(packedUV));
        setV(bakedQuad, vertexIndex, unpackV(packedUV));
    }

    /**
     * Copied from Minecraft 26.2.
     *
     * @param packedUV the packed uv
     * @return the u-component
     */
    public static float unpackU(long packedUV) {
        int bits = (int) (packedUV >> 32);
        return Float.intBitsToFloat(bits);
    }

    /**
     * Copied from Minecraft 26.2.
     *
     * @param packedUV the packed uv
     * @return the v-component
     */
    public static float unpackV(long packedUV) {
        return Float.intBitsToFloat((int) packedUV);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param u           the u-component
     */
    public static void setU(BakedQuad bakedQuad, int vertexIndex, float u) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_UV;
        bakedQuad.getVertices()[offset] = Float.floatToRawIntBits(u);
    }

    /**
     * @param bakedQuad   the baked quad
     * @param vertexIndex the vertex index
     * @param v           the v-component
     */
    public static void setV(BakedQuad bakedQuad, int vertexIndex, float v) {
        int offset = vertexIndex * VERTEX_STRIDE + VERTEX_UV;
        bakedQuad.getVertices()[offset + 1] = Float.floatToRawIntBits(v);
    }

    /**
     * Manually recalculate the normal for the quad when the geometry has become invalid from changing the quad
     * position.
     *
     * @param bakedQuad the baked quad
     */
    public static void fillNormal(BakedQuad bakedQuad) {
        Vector3f v0 = getPosition(bakedQuad, 0);
        Vector3f v1 = getPosition(bakedQuad, 1);
        Vector3f v2 = getPosition(bakedQuad, 2);
        Vector3f v3 = getPosition(bakedQuad, 3);
        v3.sub(v1);
        v2.sub(v0);
        v2.cross(v3);
        v2.normalize();
        for (int i = 0; i < VERTEX_COUNT; i++) {
            setNormal(bakedQuad, i, v2);
        }
    }
}
