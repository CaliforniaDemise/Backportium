package surreal.backportium._internal.core.visitor;

import com.google.gson.Gson;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.bytecode.traverse.ClassBytes;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;
import surreal.backportium.block.properties.wall.NewWall;
import surreal.backportium.block.state.WallContainer;

import java.util.function.Function;

import static _mod.Constants.A_NEW_WALL;
import static surreal.backportium.block.properties.wall.NewWallProperties.*;

import static _mod.Constants.V_NEW_WALL_STATES;

public final class NewWallStates {

    private static final String HOOKS = V_NEW_WALL_STATES + "$Hooks";
    private static final String NEW_WALL = A_NEW_WALL;

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.BlockWall")) return BlockWall::new;
        else {
            if (transformedName.equals("vazkii.quark.base.block.BlockQuarkWall")) return QuarkWall::new;
//            if (transformedName.equals("paulevs.betternether.blocks.BNWall")) return NewWallVisitor.BlockWallVisitor::new;
            if (transformedName.equals("net.minecraft.block.BlockPane")) return BlockPane::new;
            int[] constantTable = ClassBytes.getConstantJumpTable(bytes);
            if (ClassTraverser.get().isSuper(bytes, constantTable, "net/minecraft/block/BlockWall")) return BlockWallChild::new;
        }
        if (transformedName.equals("net.minecraft.block.Block")) return Block::new;
        return null;
    }

    // I hate you Vazkii
    private static final class QuarkWall extends BlockWall {

        public QuarkWall(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("getIgnoredProperties")) return null;
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getIgnoredProperties", "()[Lnet/minecraft/block/properties/IProperty;", null, null);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            super.visitEnd();
        }
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

    private static class BlockWall extends LeClassVisitor {

        private String className;

        public BlockWall(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            interfaces = getInterfaces(interfaces, NEW_WALL);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(getName("getActualState", "func_176221_a"))) return null;
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("getActualState", "func_176221_a"), "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, this.className, "getActualState_NewWall", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 0);
        }
    }

    private static final class BlockWallChild extends LeClassVisitor {

        private String superName = null;

        public BlockWallChild(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.superName = superName;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getActualState", "func_176221_a"))) return new GetActualState(mv, this.superName);
            return mv;
        }

        private static final class GetActualState extends MethodVisitor {

            private final String superName;
            private boolean hasSuperCall = false;

            public GetActualState(MethodVisitor mv, String superName) {
                super(ASM5, mv);
                this.superName = superName;
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKESPECIAL && name.equals(getName("getActualState", "func_176221_a"))) this.hasSuperCall = true;
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

            @Override
            public void visitInsn(int opcode) {
                if (!hasSuperCall && opcode == ARETURN) {
                    super.visitVarInsn(ASTORE, 1);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitVarInsn(ALOAD, 3);
                    super.visitMethodInsn(INVOKESPECIAL, this.superName, "getActualState_NewWall", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static final class BlockPane extends LeClassVisitor {

        public BlockPane(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("attachesTo", "func_193393_b"))) return new AttachesTo(mv);
            return mv;
        }

        private static final class AttachesTo extends MethodVisitor {

            public AttachesTo(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == IRETURN) {
                    super.visitVarInsn(ALOAD, 6);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockPane$attachesTo", "(ZLnet/minecraft/block/state/BlockFaceShape;)Z", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        private static final Gson gson = new Gson();

        public static BlockStateContainer $createBlockState(BlockStateContainer container, net.minecraft.block.Block block) {
            if (!(block instanceof NewWall)) return container;
            return new WallContainer(block, container, UP, NORTH_NEW, SOUTH_NEW, EAST_NEW, WEST_NEW);
        }

        public static boolean BlockPane$attachesTo(boolean original, BlockFaceShape shape) {
            return original || shape == BlockFaceShape.MIDDLE_POLE_THICK;
        }

        private Hooks() {}
    }

    private NewWallStates() {}
}
