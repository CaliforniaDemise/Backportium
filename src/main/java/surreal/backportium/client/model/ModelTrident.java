package surreal.backportium.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class ModelTrident extends ModelBase {

	private final ModelRenderer pole, base, left_spike, middle_spike, right_spike;

	public ModelTrident() {
		textureWidth = 32;
		textureHeight = 32;

		this.pole = new ModelRenderer(this, 0, 6);
		this.base = new ModelRenderer(this, 4, 0);
		this.left_spike = new ModelRenderer(this, 4, 3);
		this.middle_spike = new ModelRenderer(this, 0, 0);
		this.right_spike = new ModelRenderer(this, 4, 3);

		this.right_spike.mirror = true;

		this.pole.addBox(-0.5F, 2F, -0.5F, 1, 25, 1);
		this.base.addBox(-1.5F, 0F, -0.5F, 3, 2, 1);
		this.left_spike.addBox(-2.5F, -3F, -0.5F, 1, 4, 1);
		this.middle_spike.addBox(-0.5F, -4F, -0.5F, 1, 4, 1);
		this.right_spike.addBox(1.5F, -3F, -0.5F, 1, 4, 1);
	}

	public void render(float scale) {
		this.pole.render(scale);
		this.base.render(scale);
		this.left_spike.render(scale);
		this.middle_spike.render(scale);
		this.right_spike.render(scale);
	}

	@Override
	public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		render(scale);
	}

	public void setRotationAngles(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}