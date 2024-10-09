package surreal.backportium.client.model.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class ModelPhantom extends ModelBase {

	private final ModelRenderer head, body, tail1, tail2, wing1_L, wing2_L, wing1_R, wing2_R;

	public ModelPhantom() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.head.cubeList.add(new ModelBox(this.head, 0, 0, -3.5F, -3.0F, -8.0F, 7, 3, 5, 0.0F, false));

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 21.5F, -3.0F);
		this.body.cubeList.add(new ModelBox(this.body, 0, 8, -2.5F, -1.5F, 0.0F, 5, 3, 9, 0.0F, false));
		this.body.rotateAngleX = MathHelper.sin(358F);

		this.tail1 = new ModelRenderer(this);
		this.tail1.setRotationPoint(0.0F, -1.5F, 9.0F);
		this.tail1.cubeList.add(new ModelBox(this.tail1, 3, 20, -1.5F, 0.0F, 0.0F, 3, 2, 6, 0.0F, false));

		this.tail2 = new ModelRenderer(this);
		this.tail2.setRotationPoint(0.0F, 0.5F, 6.0F);
		this.tail2.cubeList.add(new ModelBox(this.tail2, 4, 29, -0.5F, 0.0F, 0.0F, 1, 1, 6, 0.0F, false));
		this.tail1.addChild(this.tail2);

		this.wing1_L = new ModelRenderer(this);
		this.wing1_L.setRotationPoint(2.5F, -1.5F, 1.5F);
		this.wing1_L.cubeList.add(new ModelBox(this.wing1_L, 23, 12, 0.0F, 0.0F, -1.5F, 6, 2, 9, 0.0F, false));

		this.wing2_L = new ModelRenderer(this);
		this.wing1_L.addChild(this.wing2_L);
		this.wing2_L.setRotationPoint(6.0F, 0.0F, 0.0F);
		this.wing2_L.cubeList.add(new ModelBox(this.wing2_L, 16, 24, 0.0F, 0.0F, -1.5F, 13, 1, 9, 0.0F, false));

		this.wing1_R = new ModelRenderer(this);
		this.wing1_R.setRotationPoint(-2.5F, -1.5F, 1.5F);
		this.wing1_R.cubeList.add(new ModelBox(this.wing1_R, 23, 12, -6.0F, 0.0F, -1.5F, 6, 2, 9, 0.0F, true));

		this.wing2_R = new ModelRenderer(this);
		this.wing1_R.addChild(this.wing2_R);
		this.wing2_R.setRotationPoint(-6.0F, 0.0F, 0.0F);
		this.wing2_R.cubeList.add(new ModelBox(this.wing2_R, 16, 24, -13.0F, 0.0F, -1.5F, 13, 1, 9, 0.0F, true));

		this.body.addChild(this.wing1_L);
		this.body.addChild(this.wing1_R);
		this.body.addChild(this.tail1);
	}

	@Override
	public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.head.render(f5);
		this.body.render(f5);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, @Nonnull Entity entityIn) {
		Minecraft mc = Minecraft.getMinecraft();
		float parTicks = mc.isGamePaused() ? 0.0F : mc.getRenderPartialTicks();
		float rot = MathHelper.sin((entityIn.ticksExisted + parTicks) / 8.0F) * 0.25F;

		this.wing1_L.rotateAngleZ = rot;
		this.wing2_L.rotateAngleZ = rot;

		this.wing1_R.rotateAngleZ = -rot;
		this.wing2_R.rotateAngleZ = -rot;

		float f = MathHelper.abs(rot * 2);
		f *= -f;
		this.tail1.rotateAngleX = f;
		this.tail2.rotateAngleX = f;
	}
}