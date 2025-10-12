package surreal.backportium._internal.core.visitor;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.bytecode.traverse.ClassBytes;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;
import surreal.backportium.block.properties.button.NewButton;
import surreal.backportium.block.state.ButtonContainer;

import java.util.function.Function;

import static surreal.backportium.block.properties.button.NewButtonProperties.*;

public final class NewButtonTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/NewButtonTransformer$Hooks";
    private static final String NEW_BUTTON = "surreal/backportium/block/properties/button/NewButton";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.Block")) return BlockVisitor::new;
        if (transformedName.equals("net.minecraft.block.BlockButton")) return BlockButtonVisitor::new;
        else {
            int[] constantTable = ClassBytes.getConstantJumpTable(bytes);
            if (ClassTraverser.get().isSuper(bytes, constantTable, "net/minecraft/block/BlockButton")) return BlockButtonChildVisitor::new;
        }
        return null;
    }

    private static class BlockVisitor extends LeClassVisitor {

        private boolean check = false;

        public BlockVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (!check && name.equals("<init>")) {
                check = true;
                return new InitVisitor(mv);
            }
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == PUTFIELD && name.equals(getName("blockState", "field_176227_L"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$createBlockState", "(Lnet/minecraft/block/state/BlockStateContainer;Lnet/minecraft/block/Block;)Lnet/minecraft/block/state/BlockStateContainer;", false);
                }
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
    }


    private static class BlockButtonVisitor extends LeClassVisitor {

        private String className;

        public BlockButtonVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            interfaces = getInterfaces(interfaces, NEW_BUTTON);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(getName("getStateForPlacement", "func_180642_a"))) return null;
            if (name.equals(getName("getMetaFromState", "func_176201_c"))) return null;
            if (name.equals(getName("getStateFromMeta", "func_176203_a"))) return null;
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getBoundingBox", "func_185496_a"))) return new GetBoundingBoxVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getStateForPlacement
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("getStateForPlacement", "func_180642_a"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;FFFILnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/state/IBlockState;", null, null);
                mv.visitInsn(ACONST_NULL);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 8);
                mv.visitMethodInsn(INVOKESTATIC, NEW_BUTTON, "getStateForPlacement_NewButton", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/Block;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/state/IBlockState;", true);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(9, 0);
            }
            { // getMetaFromState
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("getMetaFromState", "func_176201_c"), "(Lnet/minecraft/block/state/IBlockState;)I", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, this.className, "getMetaFromState_NewButton", "(Lnet/minecraft/block/state/IBlockState;)I", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(2, 0);
            }
            { // getStateFromMeta
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("getStateFromMeta", "func_176203_a"), "(I)Lnet/minecraft/block/state/IBlockState;", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, this.className, "getStateFromMeta_NewButton", "(I)Lnet/minecraft/block/state/IBlockState;", false);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(2, 0);
            }
        }

        private static class GetBoundingBoxVisitor extends MethodVisitor {

            public GetBoundingBoxVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ARETURN) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKESTATIC, NEW_BUTTON, "getBoundingBox_NewButton", "(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/AxisAlignedBB;", true);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class BlockButtonChildVisitor extends LeClassVisitor {

        private String superName = null;

        public BlockButtonChildVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superName = superName;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(getName("getMetaFromState", "func_176201_c"))) return null;
            if (name.equals(getName("getStateFromMeta", "func_176203_a"))) return null;
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getStateForPlacement", "func_180642_a"))) return new GetStateForPlacementVisitor(mv, this.superName);
            return mv;
        }

        private static class GetStateForPlacementVisitor extends MethodVisitor {

            private final String superName;
            private boolean hasSuperCall = false;

            public GetStateForPlacementVisitor(MethodVisitor mv, String superName) {
                super(ASM5, mv);
                this.superName = superName;
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKESPECIAL && name.equals(getName("getStateForPlacement", "func_176221_a"))) this.hasSuperCall = true;
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

            @Override
            public void visitInsn(int opcode) {
                if (!hasSuperCall && opcode == ARETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 3);
                    super.visitVarInsn(ALOAD, 8);
                    super.visitMethodInsn(INVOKESTATIC, NEW_BUTTON, "getStateForPlacement_NewButton", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/Block;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/state/IBlockState;", true);
                }
                super.visitInsn(opcode);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        private static final Gson gson = new Gson();

        public static BlockStateContainer $createBlockState(BlockStateContainer oldContainer, Block block) {
           if (!(block instanceof NewButton)) return oldContainer;
           return new ButtonContainer(block, oldContainer, POWERED, FACE, FACING);
        }
    }

    private NewButtonTransformer() {}
}
