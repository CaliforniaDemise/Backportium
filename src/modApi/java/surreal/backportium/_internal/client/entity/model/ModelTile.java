package surreal.backportium._internal.client.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

public abstract class ModelTile extends ModelBase {

    public abstract void render(Minecraft minecraft, TileEntity tile, double x, double y, double z, float scale, float partialTicks);

    @Override
    public void render(@NotNull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        throw new AssertionError("This isn't intended to be used");
    }
}
