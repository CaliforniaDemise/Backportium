package surreal.backportium.client.renderer.block.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Use baked models and rotate them
public class BakedConduit implements IBakedModel {

    protected static final ResourceLocation CONDUIT_SPRITE = new ResourceLocation("textures/block/conduit.png");

    private static final Minecraft mc = Minecraft.getMinecraft();

    protected final TextureAtlasSprite sprite;
    protected final VertexFormat format;

    private final List<BakedQuad>[] quads;

    public BakedConduit(IModelState state, VertexFormat format) {
        this.sprite = mc.getTextureMapBlocks().getAtlasSprite(CONDUIT_SPRITE.toString());
        this.format = format;
        this.quads = getQuadLists(state);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null) {
            return Collections.emptyList();
        }
        return this.quads[0];
    }

    private List<BakedQuad>[] getQuadLists(IModelState state) {
        List<BakedQuad>[] quads = new List[2];
        List<BakedQuad> quadList1 = new ArrayList<>();

        double start = (1F / 16) * 5;
        double end = 1F - start;

        quadList1.add(createQuad(new Vec3d(end, end, end), new Vec3d(end, end, start), new Vec3d(end, start, start), new Vec3d(end, start, end), this.sprite));
        quads[0] = quadList1;
        return quads;
    }

    private void addVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double dx, double dy, double dz, double du, double dv) {
        float x = (float) dx, y = (float) dy, z = (float) dz;
        float u = (float) du, v = (float) dv;

        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, x, y, z, 1.0F);
                    break;
                case COLOR:
                    builder.put(e, 1.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0F, 1F);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);

        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        builder.setTexture(sprite);
        addVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0);
        addVertex(builder, normal, v2.x, v2.y, v2.z, 0, 16);
        addVertex(builder, normal, v3.x, v3.y, v3.z, 16, 16);
        addVertex(builder, normal, v4.x, v4.y, v4.z, 16, 0);

        return builder.build();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.sprite;
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
