package surreal.backportium._internal.core.visitor;

import com.google.gson.Gson;
import net.minecraft.block.state.BlockStateContainer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.bytecode.traverse.ClassBytes;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;
import surreal.backportium.block.properties.button.NewButton;
import surreal.backportium.block.state.ButtonContainer;

import java.util.function.Function;

import static _mod.Constants.C_NEW_BUTTON;
import static surreal.backportium.block.properties.button.NewButtonProperties.*;
import static _mod.Constants.V_NEW_BUTTON_STATES;

public final class NewButtonStates {

    private static final String HOOKS = V_NEW_BUTTON_STATES + "$Hooks";
    private static final String NEW_BUTTON = C_NEW_BUTTON;

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.Block")) return Block::new;
        if (transformedName.equals("net.minecraft.block.BlockButton")) return BlockButton::new;
        else {
            int[] constantTable = ClassBytes.getConstantJumpTable(bytes);
            if (ClassTraverser.get().isSuper(bytes, constantTable, "net/minecraft/block/BlockButton")) return BlockButtonChild::new;
        }
        return null;
    }

    private static final class Block extends LeClassVisitor {

        private boolean check = false;

        public Block(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (!check && name.equals("<init>")) {
                check = true;
                return new Init(mv);
            }
            return mv;
        }

        private static final class Init extends MethodVisitor {

            public Init(MethodVisitor mv) {
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


    private static final class BlockButton extends LeClassVisitor {

        private String className;

        public BlockButton(ClassVisitor cv) {
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
            if (name.equals(getName("getBoundingBox", "func_185496_a"))) return new GetBoundingBox(mv);
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

        private static final class GetBoundingBox extends MethodVisitor {

            public GetBoundingBox(MethodVisitor mv) {
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

    private static final class BlockButtonChild extends LeClassVisitor {

        private String superName = null;

        public BlockButtonChild(ClassVisitor cv) {
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
            if (name.equals(getName("getStateForPlacement", "func_180642_a"))) return new GetStateForPlacement(mv, this.superName);
            return mv;
        }

        private static final class GetStateForPlacement extends MethodVisitor {

            private final String superName;
            private boolean hasSuperCall = false;

            public GetStateForPlacement(MethodVisitor mv, String superName) {
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
    public static final class Hooks {

        private static final Gson gson = new Gson();

        public static BlockStateContainer $createBlockState(BlockStateContainer oldContainer, net.minecraft.block.Block block) {
           if (!(block instanceof NewButton)) return oldContainer;
           return new ButtonContainer(block, oldContainer, POWERED, FACE, FACING);
        }

        private Hooks() {}
    }

    private NewButtonStates() {}
}
