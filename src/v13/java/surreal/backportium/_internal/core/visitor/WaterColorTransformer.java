package surreal.backportium._internal.core.visitor;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium.Tags;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.api.world.biome.CustomWaterColor;
import surreal.backportium.util.NewMathHelper;
import surreal.backportium.util.WaterUtil;

import java.util.Objects;
import java.util.function.Function;

public final class WaterColorTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/WaterColorTransformer$Hooks";
    private static final String CUSTOM_WATER_COLOR = "surreal/backportium/api/world/biome/CustomWaterColor";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.world.biome.Biome": return BiomeVisitor::new;
            case "net.minecraft.world.biome.BiomeColorHelper$3": return BiomeColorHelper$WaterColorVisitor::new;
            case "net.minecraft.client.renderer.BlockFluidRenderer": return BlockFluidRendererVisitor::new;
            case "net.minecraft.client.renderer.ItemRenderer": return ItemRendererVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return EntityRendererVisitor::new;
            case "net.minecraft.block.BlockLiquid": return BlockLiquidVisitor::new;
            case "net.minecraftforge.registries.GameData": return GameDataVisitor::new;
        }
        return null;
    }

    private static class BiomeVisitor extends LeClassVisitor {

        public BiomeVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, CUSTOM_WATER_COLOR));
            super.visitField(ACC_PRIVATE, "actualWaterColor", "I", null, 0);
            super.visitField(ACC_PRIVATE, "waterFogColor", "I", null, WaterUtil.DEFAULT_WATER_FOG_COLOR);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>")) return new InitVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getActualWaterColor
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getActualWaterColor", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/biome/Biome", "actualWaterColor", "I");
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "Biome$getActualWaterColor", "(Lnet/minecraft/world/biome/Biome;I)I", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(3, 0);
            }
            { // getWaterFogColor
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getWaterFogColor", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/biome/Biome", "waterFogColor", "I");
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "Biome$getWaterFogColor", "(Lnet/minecraft/world/biome/Biome;I)I", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(3, 0);
            }
            { // setActualWaterColor
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC | ACC_FINAL, "setActualWaterColor", "(I)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/world/biome/Biome", "actualWaterColor", "I");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // setWaterFogColor
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC | ACC_FINAL, "setWaterFogColor", "(I)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/world/biome/Biome", "waterFogColor", "I");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
        }

        private static class InitVisitor extends MethodVisitor {

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ICONST_M1);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/world/biome/Biome", "actualWaterColor", "I");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ICONST_M1);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/world/biome/Biome", "waterFogColor", "I");
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class BiomeColorHelper$WaterColorVisitor extends LeClassVisitor {

        public BiomeColorHelper$WaterColorVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(getName("getColorAtPos", "func_180283_a"))) return null;
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getColorAtPos
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("getColorAtPos", "func_180283_a"), "(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;)I", null, null);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, "net/minecraft/client/Minecraft", getName("getMinecraft", "func_71410_x"), "()Lnet/minecraft/client/Minecraft;", false);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/client/Minecraft", getName("world", "field_71441_e"), "Lnet/minecraft/client/multiplayer/WorldClient;");
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/biome/Biome", "getActualWaterColor", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(3, 0);
            }
        }
    }

    private static class BlockFluidRendererVisitor extends LeClassVisitor {

        public BlockFluidRendererVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("initAtlasSprites", "func_178268_a"))) return new InitAtlasSpritesVisitor(mv);
            if (name.equals(getName("renderFluid", "func_178270_a"))) return new RenderFluidVisitor(mv);
            return mv;
        }

        private static class InitAtlasSpritesVisitor extends MethodVisitor {

            public InitAtlasSpritesVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (cst.equals("minecraft:blocks/water_still") || cst.equals("minecraft:blocks/water_flow")) {
                    String str = (String) cst;
                    cst = Tags.MOD_ID + str.substring(9);
                }
                super.visitLdcInsn(cst);
            }
        }

        private static class RenderFluidVisitor extends MethodVisitor {

            private int count = 0;

            public RenderFluidVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (cst.equals(0.5F)) {
                    if (count > 0 && count < 12) {
                        int i = count - 1;
                        super.visitVarInsn(FLOAD, 9 + (i % 3));
                        return;
                    }
                    count++;
                }
                super.visitLdcInsn(cst);
            }
        }
    }

    // HMM
    private static class TileCrucibleRendererVisitor extends LeClassVisitor {

        public TileCrucibleRendererVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

            return mv;
        }

        private static class RenderFluidVisitor extends MethodVisitor {

            public RenderFluidVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getTexture", "func_178122_a"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderFluid$getTexture", "(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)");
                }
            }
        }
    }

    private static class ItemRendererVisitor extends LeClassVisitor {

        public ItemRendererVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("renderWaterOverlayTexture", "func_78448_c"))) return new RenderWaterOverlayTexture(mv);
            return mv;
        }

        private static class RenderWaterOverlayTexture extends MethodVisitor {

            public RenderWaterOverlayTexture(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (cst.equals(0.5F)) cst = 0.1F;
                super.visitLdcInsn(cst);
            }
        }
    }

    private static class EntityRendererVisitor extends LeClassVisitor {

        public EntityRendererVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("updateFogColor", "func_78466_h"))) return new UpdateFogColorVisitor(mv);
            if (name.equals(getName("setupFog", "func_78468_a"))) return new SetupFogVisitor(mv);
            return mv;
        }

        private static class UpdateFogColorVisitor extends MethodVisitor {

            private boolean check = false;

            public UpdateFogColorVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                if (!check && opcode == ASTORE && var == 16) {
                    check = true;
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", getName("mc", "field_78531_r"), "Lnet/minecraft/client/Minecraft;");
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/Minecraft", getName("world", "field_71441_e"), "Lnet/minecraft/client/multiplayer/WorldClient;");
                    super.visitVarInsn(ALOAD, 14);
                    super.visitVarInsn(ALOAD, 15);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityRenderer$getWaterFogColor", "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/Vec3d;", false);
                }
                super.visitVarInsn(opcode, var);
            }
        }

        private static class SetupFogVisitor extends MethodVisitor {

            private int count = 0;

            public SetupFogVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKESTATIC && name.equals(getName("setFogDensity", "func_179095_a"))) {
                    count++;
                    if (count == 4) {
                        super.visitVarInsn(ALOAD, 3);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityRenderer$getFogDensity", "(FLnet/minecraft/entity/Entity;)F", false);
                    }
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    private static class BlockLiquidVisitor extends LeClassVisitor {

        public BlockLiquidVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getLightOpacity
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("getLightOpacity", "func_149717_k"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(2, 0);
            }
        }
    }

    /**
     * Transform GameData to add a callback to set water color based on biome registry name
     */
    private static class GameDataVisitor extends LeClassVisitor {

        public GameDataVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("init")) return new InitVisitor(mv);
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            private int count = 0;

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKESTATIC && name.equals("makeRegistry")) {
                    count++;
                    if (count == 4) {
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "GameData$addAddCallback", "(Lnet/minecraftforge/registries/RegistryBuilder;)Lnet/minecraftforge/registries/RegistryBuilder;", false);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static int Biome$getActualWaterColor(Biome biome, int actualColor) {
            final int default_color_112 = 16777215;
            if (actualColor >= 0) return actualColor;
            if (FMLLaunchHandler.side().isClient()) {
                int modColor = biome.getWaterColor();
                if (modColor != default_color_112) return WaterUtil.emulateLegacyColor(modColor);
            }
            return WaterUtil.DEFAULT_WATER_COLOR;
        }

        public static int Biome$getWaterFogColor(Biome biome, int fogColor) {
            if (fogColor != -1) {
                return fogColor;
            }
            return WaterUtil.DEFAULT_WATER_FOG_COLOR;
        }

        @SideOnly(Side.CLIENT)
        private static int targetFogColor = -1;
        @SideOnly(Side.CLIENT)
        private static int prevFogColor = -1;
        @SideOnly(Side.CLIENT)
        private static long fogAdjustTime = -1;
        @SideOnly(Side.CLIENT)
        public static Vec3d EntityRenderer$getWaterFogColor(Vec3d oldColor, World world, BlockPos pos, IBlockState state) {
            float f6 = 1.0F; // EntityPlayer#getWaterVision() 1.16
            if (state.getMaterial() == Material.WATER) {
                long i = System.nanoTime() / 1000000L;
                int fogColor = CustomWaterColor.cast(world.getBiome(pos)).getWaterFogColor(world, pos);
                if (fogAdjustTime < 0) {
                    targetFogColor = fogColor;
                    prevFogColor = fogColor;
                    fogAdjustTime = i;
                }
                int k = targetFogColor >> 16 & 255;
                int l = targetFogColor >> 8 & 255;
                int i1 = targetFogColor & 255;
                int j1 = prevFogColor >> 16 & 255;
                int k1 = prevFogColor >> 8 & 255;
                int l1 = prevFogColor & 255;
                float f = MathHelper.clamp((float) (i - fogAdjustTime) / 5000.0F, 0.0F, 1.0F);
                float f1 = NewMathHelper.lerp(f, (float) j1, (float) k);
                float f2 = NewMathHelper.lerp(f, (float) k1, (float) l);
                float f3 = NewMathHelper.lerp(f, (float) l1, (float) i1);
                double r = f1 / 255.0;
                double g = f2 / 255.0;
                double b = f3 / 255.0;
                if (targetFogColor != fogColor) {
                    targetFogColor = fogColor;
                    prevFogColor = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
                    fogAdjustTime = i;
                }
                double f9 = Math.min(1.0 / r, Math.min(1.0 / g, 1.0 / b));
                r = r * (1.0 - f6) + r * f9 * f6;
                g = g * (1.0 - f6) + g * f9 * f6;
                b = b * (1.0 - f6) + b * f9 * f6;
                return new Vec3d(r, g, b);
            }
            return oldColor;
        }

        @SideOnly(Side.CLIENT)
        public static float EntityRenderer$getFogDensity(float density, Entity entity) {
            float f6 = 1.0F; // EntityPlayer#getWaterVision() 1.16
            GlStateManager.setFog(GlStateManager.FogMode.EXP2);
            density -= 0.05F;
            Biome biome = entity.world.getBiome(new BlockPos(entity));
            density = CustomWaterColor.cast(biome).getWaterFogDensity((EntityLivingBase) entity, density);
            if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) density += 0.005F;
            density -= f6 * f6 * 0.03F;
            return density;
        }

        public static RegistryBuilder<Biome> GameData$addAddCallback(RegistryBuilder<Biome> builder) {
            return builder.add((IForgeRegistry.AddCallback<Biome>) (owner, stage, id, obj, oldObj) -> {
                CustomWaterColor customWater = CustomWaterColor.cast(obj);
                ResourceLocation location = Objects.requireNonNull(obj.getRegistryName());
                String namespace = location.getNamespace();
                String path = location.getPath();
                if (namespace.equals("minecraft")) {
                    switch (path) {
                        case "mutated_swampland": customWater.setActualWaterColor(6388580); customWater.setWaterFogColor(2302743); break;
                        case "swampland": customWater.setActualWaterColor(6388580); customWater.setWaterFogColor(2302743); break;
                        case "frozen_river": customWater.setActualWaterColor(3750089); break;
                        case "frozen_ocean": customWater.setActualWaterColor(3750089); break;
                        case "cold_beach": customWater.setActualWaterColor(4020182); break;
                        case "taiga_cold": customWater.setActualWaterColor(4020182); break;
                        case "taige_cold_hills": customWater.setActualWaterColor(4020182); break;
                        case "mutated_taiga_cold": customWater.setActualWaterColor(4020182); break;
                    }
                }
                else if (namespace.equals("integrateddynamics") && path.equals("biome_meneglin")) {
                    customWater.setWaterFogColor(5613789);
                }
                else if (namespace.equals("biomesoplenty")) {
                    switch (path) {
                        case "bayou": customWater.setActualWaterColor(0x62AF84); customWater.setWaterFogColor(0x0C211C); break;
                        case "dead_swamp": customWater.setActualWaterColor(0x354762); customWater.setWaterFogColor(0x040511); break;
                        case "mangrove": customWater.setActualWaterColor(0x448FBD); customWater.setWaterFogColor(0x061326); break;
                        case "mystic_grove": customWater.setActualWaterColor(0x9C3FE4); customWater.setWaterFogColor(0x2E0533); break;
                        case "ominous_woods": customWater.setActualWaterColor(0x312346); customWater.setWaterFogColor(0x0A030C); break;
                        case "tropical_rainforest": customWater.setActualWaterColor(0x1FA14A); customWater.setWaterFogColor(0x02271A); break;
                        case "quagmire": customWater.setActualWaterColor(0x433721); customWater.setWaterFogColor(0x0C0C03); break;
                        case "wetland": customWater.setActualWaterColor(0x272179); customWater.setWaterFogColor(0x0C031B); break;
                        case "bog":
                        case "moor": customWater.setActualWaterColor(WaterUtil.DEFAULT_WATER_COLOR); customWater.setWaterFogColor(WaterUtil.DEFAULT_WATER_FOG_COLOR); break;
                    }
                } else if (namespace.equals("thebetweenlands")) {
                    switch (path) {
                        case "swamplands":
                        case "swamplands_clearing": customWater.setActualWaterColor(1589792); customWater.setWaterFogColor(1589792); break;
                        case "coarse_islands": customWater.setActualWaterColor(1784132); customWater.setWaterFogColor(1784132); break;
                        case "deep_waters": customWater.setActualWaterColor(1784132); customWater.setWaterFogColor(1784132); break;
                        case "marsh_0":
                        case "marsh_1": customWater.setActualWaterColor(4742680); customWater.setWaterFogColor(4742680); break;
                        case "patchy_islands": customWater.setActualWaterColor(1589792); customWater.setWaterFogColor(1589792); break;
                        case "raised_isles": customWater.setActualWaterColor(1784132); customWater.setWaterFogColor(1784132); break;
                        case "sludge_plains":
                        case "sludge_plains_clearing": customWater.setActualWaterColor(3813131); customWater.setWaterFogColor(3813131); break;
                    }
                }
                else if (namespace.equals("traverse")) {
                    switch (path) {
                        case "mini_jungle": customWater.setActualWaterColor(0x003320); customWater.setWaterFogColor(0x052721); break;
                        case "green_swamp": customWater.setActualWaterColor(0x617B64); customWater.setWaterFogColor(0x232317); break;
                        case "autumnal_woods":
                        case "woodlands":
                        case "meadow":
                        case "red_desert":
                        case "temperate_rainforest":
                        case "badlands":
                        case "mountainous_desert":
                        case "rocky_plateau":
                        case "forested_hills":
                        case "birch_forested_hills":
                        case "autumnal_wooded_hills":
                        case "cliffs":
                        case "glacier":
                        case "glacier_spikes":
                        case "snowy_coniferous_forest":
                        case "lush_hills":
                        case "desert_shrubland":
                        case "thicket":
                        case "arid_highland":
                        case "rocky_plains": customWater.setActualWaterColor(0x3F76E4); customWater.setWaterFogColor(0x50533); break;
                    }
                }
                else if (namespace.equals("thaumcraft")) {
                   switch (path) {
                       case "magical_forest":
                       case "eerie": customWater.setActualWaterColor(3035999); break;
                   }
                }
            });
        }
    }

    private WaterColorTransformer() {}
}
