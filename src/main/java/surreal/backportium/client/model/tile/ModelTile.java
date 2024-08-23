package surreal.backportium.client.model.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public abstract class ModelTile<T extends TileEntity> extends ModelBase {

    public abstract void render(Minecraft minecraft, T tile, double x, double y, double z, float scale, float partialTicks);

    @Override
    public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        throw new AssertionError("This isn't intended to be used");
    }
}
