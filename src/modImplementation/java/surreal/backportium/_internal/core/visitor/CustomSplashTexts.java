package surreal.backportium._internal.core.visitor;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.event.MainMenuEvent;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static _mod.Constants.V_CUSTOM_SPLASH_TEXTS;

public final class CustomSplashTexts extends LeClassVisitor {

    private static final String HOOKS = V_CUSTOM_SPLASH_TEXTS + "$Hooks";

    private CustomSplashTexts(ClassVisitor cv) {
        super(cv);
    }

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        if (transformedName.equals("net.minecraft.client.gui.GuiMainMenu")) return CustomSplashTexts::new;
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("<init>")) return new Init(mv);
        return mv;
    }

    public static final class Init extends MethodVisitor {

        private int count = 0;

        public Init(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);
            if (opcode == PUTFIELD) {
                ++this.count;
                if (this.count == 4) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/gui/GuiMainMenu", getName("splashText", "field_73975_c"), "Ljava/lang/String;");
                    super.visitVarInsn(ALOAD, 2);
                    super.visitFieldInsn(GETSTATIC, "net/minecraft/client/gui/GuiMainMenu", getName("RANDOM", "field_175374_h"), "Ljava/util/Random;");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$postEvent", "(Lnet/minecraft/client/gui/GuiMainMenu;Ljava/lang/String;Ljava/util/List;Ljava/util/Random;)Ljava/lang/String;", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/gui/GuiMainMenu", getName("splashText", "field_73975_c"), "Ljava/lang/String;");
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static String $postEvent(GuiMainMenu gui, String splashText, List<String> splashTexts, Random random) {
            MainMenuEvent.SplashText event = new MainMenuEvent.SplashText(gui, splashText, splashTexts, random);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getNewSplashText();
        }

        private Hooks() {}
    }
}
