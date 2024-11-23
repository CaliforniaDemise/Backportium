package surreal.backportium.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class ModelTrident extends ModelBase {

    private final ModelRenderer trident;

    public ModelTrident() {
        this.textureWidth = 32;
        this.textureHeight = 32;

        this.trident = new ModelRenderer(this, 0, 0);

        // Pole
        this.trident.setTextureOffset(0, 6);
        this.trident.addBox(-0.5F, 2F, -0.5F, 1, 25, 1);

        // Base
        this.trident.setTextureOffset(4, 0);
        this.trident.addBox(-1.5F, 0F, -0.5F, 3, 2, 1);

        // Left Spike
        this.trident.setTextureOffset(4, 3);
        this.trident.addBox(-2.5F, -3F, -0.5F, 1, 4, 1);

        // Middle Spike
        this.trident.setTextureOffset(0, 0);
        this.trident.addBox(-0.5F, -4F, -0.5F, 1, 4, 1);

        // Right Spike
        this.trident.setTextureOffset(4, 3);
        this.trident.addBox(1.5F, -3F, -0.5F, 1, 4, 1);
    }

    public void render(float scale) {
        this.trident.render(scale);
    }

    @Override
    public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        render(scale);
    }
}