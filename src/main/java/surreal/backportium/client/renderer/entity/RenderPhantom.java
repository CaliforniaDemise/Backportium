package surreal.backportium.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.client.renderer.entity.layer.LayerPhantomEyes;
import surreal.backportium.entity.v1_13.EntityPhantom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderPhantom extends RenderLiving<EntityPhantom> {

    private static final ResourceLocation PHANTOM_TEXTURE = new ResourceLocation("textures/entity/phantom.png");

    public RenderPhantom(RenderManager manager, ModelBase model, float shadowsizeIn) {
        super(manager, model, shadowsizeIn);
        this.addLayer(new LayerPhantomEyes<>(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityPhantom entity) {
        return PHANTOM_TEXTURE;
    }
}
