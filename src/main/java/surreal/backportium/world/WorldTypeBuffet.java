package surreal.backportium.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

// TODO This doesn't get saved properly :D Would most likely need transformation, better to make worlds be able to read and save stuff without going outside world type.
public class WorldTypeBuffet extends WorldType {

    private Biome biome = null;

    public WorldTypeBuffet(String name) {
        super(name);
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @Override
    public void onCustomizeButton(@NotNull Minecraft mc, @NotNull GuiCreateWorld guiCreateWorld) {
        mc.displayGuiScreen(new GuiCustomizeBuffetWorld(guiCreateWorld, this));
    }

    @Nullable
    public Biome getBiome() {
        return this.biome;
    }

    public void setBiome(@Nullable Biome biome) {
        this.biome = biome;
    }

    @NotNull
    @Override
    public BiomeProvider getBiomeProvider(@NotNull World world) {
        Biome b = this.biome;
        if (b == null) b = Biomes.OCEAN;
        return new BiomeProviderBuffet(b);
    }

    @SideOnly(Side.CLIENT)
    private static class GuiCustomizeBuffetWorld extends GuiScreen {
        protected final int[] biomesArray;
        private final GuiCreateWorld parent;
        private final WorldTypeBuffet worldType;
        private String title;
        private BiomeList list;
        private GuiButton select;

        public GuiCustomizeBuffetWorld(GuiCreateWorld parent, WorldTypeBuffet buffet) {
            this.biomesArray = this.initBiomesArray();
            this.parent = parent;
            this.worldType = buffet;
            buffet.setBiome(null);
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            this.title = I18n.format("createWorld.customize.buffet.title");
            this.list = new BiomeList();
            this.select = this.addButton(new GuiButton(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.format("createWorld.customize.preset.select")));
            this.addButton(new GuiButton(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.format("gui.cancel")));
            this.updateValidity();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.list.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 2, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            this.list.handleMouseInput();
        }

        @Override
        public void onGuiClosed() {
            Keyboard.enableRepeatEvents(false);
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            switch (button.id) {
                case 0:
                    this.worldType.setBiome(this.list.getBiome(this.list.selected));
                    this.mc.displayGuiScreen(this.parent);
                    break;
                case 1:
                    this.mc.displayGuiScreen(this.parent);
                    break;
            }
        }

        public void updateValidity() {
            this.select.enabled = this.isSelectionValid();
        }

        private boolean isSelectionValid() {
            return this.list.selected >= 0 && this.list.selected < this.list.getSize();
        }

        @SuppressWarnings("deprecation")
        private int[] initBiomesArray() {
            List<Biome> list =  ForgeRegistries.BIOMES.getValues();
            int[] ints = new int[list.size()];
            for (int i = 0; i < ints.length; ++i) {
                ints[i] = Biome.getIdForBiome(list.get(i));
            }
            return ints;
        }

        private class BiomeList extends GuiSlot {
            private int selected = -1;

            public BiomeList() {
                super(GuiCustomizeBuffetWorld.this.mc, GuiCustomizeBuffetWorld.this.width, GuiCustomizeBuffetWorld.this.height, 80, GuiCustomizeBuffetWorld.this.height - 32, 38);
            }

            @Override
            protected int getSize() {
                return GuiCustomizeBuffetWorld.this.biomesArray.length;
            }

            @Override
            protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
                this.selected = slotIndex;
                GuiCustomizeBuffetWorld.this.updateValidity();
            }

            @Override
            protected boolean isSelected(int slotIndex) {
                return this.selected == slotIndex;
            }

            @Override
            protected void drawBackground() {}

            @Override
            protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
                GuiCustomizeBuffetWorld.this.fontRenderer.drawString(this.getBiome(slotIndex).getBiomeName(), xPos + 10, yPos + 14, 0xFFFFFF);
            }

            private int getBiomeId(int index) {
                if (index < 0 || GuiCustomizeBuffetWorld.this.biomesArray.length <= index) index = 0;
                return GuiCustomizeBuffetWorld.this.biomesArray[index];
            }

            private Biome getBiome(int index) {
                Biome biome = Biome.getBiome(this.getBiomeId(index));
                if (biome == null) biome = Biomes.OCEAN;
                return biome;
            }
        }
    }

    private static class BiomeProviderBuffet extends BiomeProviderSingle {

        public BiomeProviderBuffet(Biome biomeIn) {
            super(biomeIn);
        }
    }
}
