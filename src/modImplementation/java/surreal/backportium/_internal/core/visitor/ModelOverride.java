package surreal.backportium._internal.core.visitor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.client.renderer.model.ModelBlockDefinitionProvider;

import java.io.Reader;
import java.util.function.Function;

import static _mod.Constants.V_MODEL_OVERRIDE;

public final class ModelOverride extends LeClassVisitor {

    private static final String HOOKS = V_MODEL_OVERRIDE + "$Hooks";

    private ModelOverride(ClassVisitor cv) {
        super(cv);
    }

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        if (transformedName.equals("net.minecraft.client.renderer.block.model.ModelBakery")) return ModelOverride::new;
        return null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        super.visitField(ACC_PRIVATE | ACC_STATIC, "bp$stupidBlock", "Lnet/minecraft/block/Block;", null, null);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("loadBlock")) return new LoadBlock(mv);
        if (name.equals("loadModelBlockDefinition")) return new LoadModelBlockDefinition(mv);
        return mv;
    }

    private static final class LoadBlock extends MethodVisitor {

        public LoadBlock(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitCode() {
            super.visitVarInsn(ALOAD, 2);
            super.visitFieldInsn(PUTSTATIC, "net/minecraft/client/renderer/block/model/ModelBakery", "bp$stupidBlock", "Lnet/minecraft/block/Block;");
        }
    }

    private static final class LoadModelBlockDefinition extends MethodVisitor {

        private boolean check = false;

        public LoadModelBlockDefinition(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (!check && opcode == INVOKESTATIC) {
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/block/model/ModelBakery", getName("resourceManager", "field_177598_f"), "Lnet/minecraft/client/resources/IResourceManager;");                check = true;
                super.visitFieldInsn(GETSTATIC, "net/minecraft/client/renderer/block/model/ModelBakery", "bp$stupidBlock", "Lnet/minecraft/block/Block;");
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/block/model/ModelBakery", getName("blockModelShapes", "field_177610_k"), "Lnet/minecraft/client/renderer/BlockModelShapes;");
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "$parseFromReader", "(Ljava/io/Reader;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/block/Block;Lnet/minecraft/client/renderer/BlockModelShapes;)Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;", false);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        private static final Gson gson;

        @SideOnly(Side.CLIENT)
        public static ModelBlockDefinition $parseFromReader(Reader reader, ResourceLocation location, IResourceManager manager, Block block, BlockModelShapes shapes) {
            if (location instanceof ModelResourceLocation) return ModelBlockDefinition.parseFromReader(reader, location);
            if (!(block instanceof ModelBlockDefinitionProvider)) return ModelBlockDefinition.parseFromReader(reader, location);
            return ((ModelBlockDefinitionProvider) block).getModelDefinition(manager, shapes, reader, location, gson);
        }

        static {
            GsonBuilder builder = new GsonBuilder();
            if (FMLLaunchHandler.isDeobfuscatedEnvironment()) builder.setPrettyPrinting();
            gson = builder.disableHtmlEscaping().create();
        }

        private Hooks() {}
    }
}
