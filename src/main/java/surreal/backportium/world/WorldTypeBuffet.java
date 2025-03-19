package surreal.backportium.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import surreal.backportium.Backportium;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        if (this.biome == null) this.biome = Biomes.OCEAN;
        WorldInfo info = world.getWorldInfo();
        WorldSettings settings = new WorldSettings(info.getSeed(), info.getGameType(), info.isMapFeaturesEnabled(), info.isHardcoreModeEnabled(), WorldType.CUSTOMIZED);
        if (info.getGeneratorOptions().isEmpty()) settings.setGeneratorOptions("{\"fixedBiome\":" + Biome.getIdForBiome(this.biome) + "}");
        else settings.setGeneratorOptions(info.getGeneratorOptions());
        info.populateFromWorldSettings(settings);
        return new BiomeProviderBuffet(info);
    }

    @SideOnly(Side.CLIENT)
    private static class GuiCustomizeBuffetWorld extends GuiScreen {
        protected final int[] biomesArray;
        private final GuiCreateWorld parent;
        private final WorldTypeBuffet worldType;
        private String title;
        private String generatorType;
        private String biome;
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
            this.generatorType = I18n.format("createWorld.customize.buffet.generatorType");
            this.biome = I18n.format("createWorld.customize.buffet.biome");
            this.list = new BiomeList();
            this.select = this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
            this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
            this.updateValidity();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.list.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
            this.drawCenteredString(this.fontRenderer, this.generatorType, this.width / 2, 30, 10329495);
            this.drawCenteredString(this.fontRenderer, this.biome, this.width / 2, 68, 10329495);
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
            List<Biome> list =  ForgeRegistries.BIOMES.getValues().stream().sorted(Comparator.comparing(Biome::getBiomeName)).collect(Collectors.toList());
            int[] ints = new int[list.size()];
            for (int i = 0; i < ints.length; ++i) {
                ints[i] = Biome.getIdForBiome(list.get(i));
            }
            return ints;
        }

        private class BiomeList extends GuiSlot {
            private int selected = -1;

            public BiomeList() {
                super(GuiCustomizeBuffetWorld.this.mc, GuiCustomizeBuffetWorld.this.width, GuiCustomizeBuffetWorld.this.height, 80, GuiCustomizeBuffetWorld.this.height - 32, 16);
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
                GuiCustomizeBuffetWorld.this.fontRenderer.drawStringWithShadow(this.getBiome(slotIndex).getBiomeName(), xPos + 5, yPos + 2, 0xFFFFFF);
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

    private static class BiomeProviderBuffet extends BiomeProvider {

        private final Biome biome;

        public BiomeProviderBuffet(WorldInfo info) {
            super(info);
            System.out.println(info.getGeneratorOptions());
            WorldSettings settings = new WorldSettings(info.getSeed(), info.getGameType(), info.isMapFeaturesEnabled(), info.isHardcoreModeEnabled(), Backportium.TYPE_BUFFET);
            settings.setGeneratorOptions(info.getGeneratorOptions());
            info.populateFromWorldSettings(settings);
            Biome biome = this.getFixedBiome();
            if (biome == null) biome = Biomes.OCEAN;
            this.biome = biome;
        }

        public Biome @NotNull[] getBiomesForGeneration(Biome @NotNull [] biomes, int x, int z, int width, int height) {
            if (biomes == null || biomes.length < width * height) biomes = new Biome[width * height];
            Arrays.fill(biomes, 0, width * height, this.biome);
            return biomes;
        }

        public Biome @NotNull[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth) {
            if (oldBiomeList == null || oldBiomeList.length < width * depth) oldBiomeList = new Biome[width * depth];
            Arrays.fill(oldBiomeList, 0, width * depth, this.biome);
            return oldBiomeList;
        }

        public Biome @NotNull [] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
            return this.getBiomes(listToReuse, x, z, width, length);
        }

        @Nullable
        public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, @NotNull Random random) {
            return biomes.contains(this.biome) ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1)) : null;
        }

        @Override
        public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
            return allowed.contains(this.biome);
        }
    }
}
