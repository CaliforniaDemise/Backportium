package surreal.backportium._internal.core.visitor;

import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.api.world.biome.Translatable;

import java.util.Objects;
import java.util.function.Function;

/**
 * Make biome names translatable. This doesn't change how biomeName behaves. If I18n doesn't have the key set, it will use default name instead.
 * - Add a field to Biome class called 'translationKey' and add getter and setter for it which is reachable with {@link Translatable}
 * - Set 'translationKey' field with biome registry AddCallback, because we need registry name to properly set the translation key.
 * - Change debug info to show both registry name and biome name.
 * Transforms {@link Biome}, {@link GameData}, {@link GuiOverlayDebug} and {@link org.cyclops.cyclopscore.config.configurable.ConfigurableBiome}
 */
public final class BiomeNameTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/BiomeNameTransformer$Hooks";
    private static final String TRANSLATABLE = "surreal/backportium/api/world/biome/Translatable";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.world.biome.Biome": return BiomeVisitor::new;
            case "net.minecraftforge.registries.GameData": return GameDataVisitor::new;
            case "net.minecraft.client.gui.GuiOverlayDebug": return GuiOverlayDebug::new;
            case "org.cyclops.cyclopscore.config.configurable.ConfigurableBiome": return ConfigurableBiomeVisitor::new;
        }
        return null;
    }

    /**
     * Adds translationKey field and getter and setter for it and adds {@link Translatable} to Biomes interface list.
     */
    private static class BiomeVisitor extends LeClassVisitor {

        public BiomeVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, TRANSLATABLE));
            super.visitField(ACC_PRIVATE, "translationKey", "Ljava/lang/String;", null, null);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>")) return new InitVisitor(mv);
            if (name.equals(getName("getBiomeName", "func_185359_l"))) return new GetBiomeNameVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getTranslationKey
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getTranslationKey", "()Ljava/lang/String;", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/biome/Biome", "translationKey", "Ljava/lang/String;");
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 0);
            }
            { // setTranslationKey
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC | ACC_FINAL, "setTranslationKey", "(Ljava/lang/String;)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/world/biome/Biome", "translationKey", "Ljava/lang/String;");
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
                    super.visitLdcInsn("");
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/world/biome/Biome", "translationKey", "Ljava/lang/String;");
                }
                super.visitInsn(opcode);
            }
        }

        private static class GetBiomeNameVisitor extends MethodVisitor {

            public GetBiomeNameVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ARETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/biome/Biome", "getTranslationKey", "()Ljava/lang/String;", false);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "Biome$getBiomeName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    /**
     * Transform GameData to add a callback to set translation key based on biome registry name
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

    /**
     * Replace basic getBiomeName() call with 'registryName (biomeName)' in debug info gui (F3)
     */
    private static class GuiOverlayDebug extends LeClassVisitor {

        public GuiOverlayDebug(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("call")) return new CallVisitor(mv);
            return mv;
        }

        private static class CallVisitor extends MethodVisitor {

            public CallVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBiomeName", "func_185359_l"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "GuiIngame$getBiomeInfo", "(Lnet/minecraft/world/biome/Biome;)Ljava/lang/String;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    /**
     * Fixes Meneglin and other Cyclops Team mods' biomes translating biome names. Too stupid.
     */
    // From Cyclops Core
    private static class ConfigurableBiomeVisitor extends LeClassVisitor {

        public ConfigurableBiomeVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("constructProperties")) return new ConstructPropertiesVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getTranslationKey
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getTranslationKey", "()Ljava/lang/String;", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "org/cyclops/cyclopscore/config/configurable/ConfigurableBiome", "eConfig", "Lorg/cyclops/cyclopscore/config/extendedconfig/BiomeConfig;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/cyclops/cyclopscore/config/extendedconfig/BiomeConfig", "getTranslationKey", "()Ljava/lang/String;", false);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 0);
            }
        }

        private static class ConstructPropertiesVisitor extends MethodVisitor {

            private boolean check = false;

            public ConstructPropertiesVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (!check && opcode == INVOKESTATIC) {
                    check = true;
                    super.visitMethodInsn(INVOKESTATIC, "net/minecraft/util/text/translation/I18n", getName("translateToFallback", "func_150826_b"), "(Ljava/lang/String;)Ljava/lang/String;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        @SideOnly(Side.CLIENT)
        public static String Biome$getBiomeName(String originalName, String translationKey) {
            if (I18n.hasKey(translationKey)) {
                return I18n.format(translationKey);
            }
            return originalName;
        }

        public static RegistryBuilder<Biome> GameData$addAddCallback(RegistryBuilder<Biome> builder) {
            return builder.add((IForgeRegistry.AddCallback<Biome>) (owner, stage, id, obj, oldObj) -> {
                Translatable extension = Translatable.cast(obj);
                if (extension.getTranslationKey().isEmpty()) {
                    ResourceLocation location = Objects.requireNonNull(obj.getRegistryName());
                    extension.setTranslationKey("biome." + location.getNamespace() + "." + location.getPath() + ".name");
                }
            });
        }

        @SideOnly(Side.CLIENT)
        public static String GuiIngame$getBiomeInfo(Biome biome) {
            return biome.getRegistryName() + " (" + biome.getBiomeName() + ")";
        }
    }

    private BiomeNameTransformer() {}
}
