package surreal.backportium.client.model.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import surreal.backportium.tile.v1_13.TileConduit;

public abstract class ModelConduit extends ModelTile<TileConduit> {

    public static class Shell extends ModelConduit {

        private final ModelRenderer shell;

        public Shell() {
            this.textureWidth = 32;
            this.textureHeight = 16;

            this.shell = new ModelRenderer(this, 0, 0);
            this.shell.addBox(-3F, -3F, -3F, 6, 6, 6);
        }

        @Override
        public void render(Minecraft mc, TileConduit tile, double x, double y, double z, float scale, float partialTicks) {
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
        public void render(Minecraft mc, TileConduit tile, double x, double y, double z, float scale, float partialTicks) {
            this.cage.render(scale);
        }
    }
}
