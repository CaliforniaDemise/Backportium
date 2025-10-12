package surreal.backportium.client.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import surreal.backportium._internal.client.entity.model.ModelTile;

public abstract class ModelConduit extends ModelTile {

    public static class Shell extends ModelConduit {

        private final ModelRenderer shell;

        public Shell() {
            this.textureWidth = 32;
            this.textureHeight = 16;

            this.shell = new ModelRenderer(this, 0, 0);
            this.shell.addBox(-3F, -3F, -3F, 6, 6, 6);
        }

        @Override
        public void render(Minecraft mc, TileEntity tile, double x, double y, double z, float scale, float partialTicks) {
            this.shell.render(scale);
        }
    }

    public static class Cage extends ModelConduit {

        private final ModelRenderer cage;

        public Cage() {
            this.textureWidth = 32;
            this.textureHeight = 16;

            this.cage = new ModelRenderer(this, 0, 0);
            this.cage.addBox(-4F, -4F, -4F, 8, 8, 8);
        }

        @Override
        public void render(Minecraft mc, TileEntity tile, double x, double y, double z, float scale, float partialTicks) {
            this.cage.render(scale);
        }
    }

    protected ModelConduit() {}
}
