package surreal.backportium._internal.core.visitor;

import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;

import java.util.Arrays;
import java.util.function.Function;

public final class MoreStates {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/MoreStates$Hooks";
    private static final String NIBBLEST_ARRAY = "surreal/backportium/_internal/core/visitor/MoreStates$NibblestArray";
    private static final int MAX_STATES = 255;
    private static final int MAX_ID = 1048575;

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        if (FMLLaunchHandler.isDeobfuscatedEnvironment() && transformedName.equals("net.minecraftforge.registries.GameData")) return cv -> cv;
        switch (transformedName) {
            case "net.minecraft.block.Block": return BlockVisitor::new;
            case "net.minecraftforge.registries.GameData$BlockCallbacks": return GameData$BlockCallbacksVisitor::new;
            case "net.minecraft.client.network.NetHandlerPlayClient": return NetHandlerPlayClientVisitor::new;
            case "net.minecraft.world.chunk.ChunkPrimer": return ChunkPrimerVisitor::new;
            case "net.minecraft.world.chunk.NibbleArray": return NibbleArrayVisitor::new;
            case "net.minecraft.world.chunk.storage.AnvilChunkLoader": return AnvilChunkLoaderVisitor::new;
            case "net.minecraft.world.chunk.BlockStateContainer": return BlockStateContainerVisitor::new;
        }
        return null;
    }

    public static class BlockVisitor extends LeClassVisitor {

        private boolean initCheck = false;

        public BlockVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (!initCheck && name.equals("<init>")) { initCheck = true; return new InitVisitor(mv); }
            if (name.equals(getName("getStateById", "func_176220_d"))) return new GetStateByIdVisitor(mv);
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == BIPUSH && operand == 16) {
                    opcode = SIPUSH;
                    operand = MAX_STATES + 1;
                }
                super.visitIntInsn(opcode, operand);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/block/Block", "harvestLevel", "[I");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$initHarvestLevelArray", "([I)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class GetStateByIdVisitor extends MethodVisitor {

            public GetStateByIdVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == BIPUSH && operand == 15) {
                    opcode = SIPUSH;
                    operand = MAX_STATES;
                }
                super.visitIntInsn(opcode, operand);
            }
        }
    }

    public static class GameData$BlockCallbacksVisitor extends LeClassVisitor {

        public GameData$BlockCallbacksVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("onAdd")) return new OnAddVisitor(mv);
            return mv;
        }

        private static class OnAddVisitor extends MethodVisitor {

            public OnAddVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == BIPUSH && operand == 16) {
                    opcode = SIPUSH;
                    operand = MAX_STATES + 1;
                }
                super.visitIntInsn(opcode, operand);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode != ICONST_4) super.visitInsn(opcode);
                else super.visitIntInsn(BIPUSH, 8);
            }
        }
    }

    public static class NetHandlerPlayClientVisitor extends LeClassVisitor {

        public NetHandlerPlayClientVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("handleSpawnObject", "func_147235_a"))) return new HandleSpawnObjectVisitor(mv);
            return mv;
        }

        private static class HandleSpawnObjectVisitor extends MethodVisitor {

            public HandleSpawnObjectVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (cst.equals(65535)) cst = MAX_ID;
                super.visitLdcInsn(cst);
            }
        }
    }

    public static class ChunkPrimerVisitor extends LeClassVisitor {

        public ChunkPrimerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (name.equals(getName("data", "field_177860_a"))) desc = "[I";
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>")) return new ArrayReplaceVisitor(mv);
            if (name.equals(getName("getBlockState", "func_177856_a"))) return new ArrayReplaceVisitor(mv);
            if (name.equals(getName("setBlockState", "func_177855_a"))) return new ArrayReplaceVisitor(mv);
            if (name.equals(getName("findGroundBlockIdx", "func_186138_a"))) return new ArrayReplaceVisitor(mv);
            return mv;
        }

        private static class ArrayReplaceVisitor extends MethodVisitor {

            public ArrayReplaceVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == NEWARRAY && operand == T_CHAR) operand = T_INT;
                super.visitIntInsn(opcode, operand);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == PUTFIELD || opcode == GETFIELD) desc = "[I";
                super.visitFieldInsn(opcode, owner, name, desc);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == I2C) return;
                switch (opcode) {
                    case CALOAD: opcode = IALOAD; break;
                    case CASTORE: opcode = IASTORE; break;
                }
                super.visitInsn(opcode);
            }
        }
    }

    public static class NibbleArrayVisitor extends LeClassVisitor {

        public NibbleArrayVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>") && desc.startsWith("([B")) return new InitVisitor(mv);
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == SIPUSH && operand == 2048) operand = 4096;
                super.visitIntInsn(opcode, operand);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if (opcode == IF_ICMPEQ) opcode = IF_ICMPLE;
                super.visitJumpInsn(opcode, label);
            }
        }
    }

    private static class AnvilChunkLoaderVisitor extends LeClassVisitor {

        public AnvilChunkLoaderVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("writeChunkToNBT", "func_75820_a"))) return new WriteChunkToNBTVisitor(mv);
            if (name.equals(getName("readChunkFromNBT", "func_75823_a"))) return new ReadChunkFromNBT(mv);
            return mv;
        }

        private static class WriteChunkToNBTVisitor extends MethodVisitor {


            public WriteChunkToNBTVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (opcode == NEW && type.endsWith("NibbleArray")) {
                    super.visitTypeInsn(NEW, NIBBLEST_ARRAY);
                    return;
                }
                super.visitTypeInsn(opcode, type);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKESPECIAL && owner.endsWith("NibbleArray")) {
                    super.visitMethodInsn(INVOKESPECIAL, NIBBLEST_ARRAY, "<init>", "()V", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private static class ReadChunkFromNBT extends MethodVisitor {

            private boolean check = false;

            public ReadChunkFromNBT(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (!check && opcode == NEW && type.endsWith("NibbleArray")) type = NIBBLEST_ARRAY;
                super.visitTypeInsn(opcode, type);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (!check && opcode == INVOKESPECIAL && owner.endsWith("NibbleArray")) {
                    check = true;
                    owner = NIBBLEST_ARRAY;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    public static class BlockStateContainerVisitor extends LeClassVisitor {

        public BlockStateContainerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getDataForNBT", "func_186017_a"))) return new GetDataForNBTVisitor(mv);
            if (name.equals(getName("setDataFromNBT", "func_186019_a"))) return new SetDataFromNBTVisitor(mv);
            return mv;
        }

        private static class GetDataForNBTVisitor extends MethodVisitor {

            private int count = 0;

            public GetDataForNBTVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == BIPUSH) {
                    if (this.count == 4 && operand == 15) {
                        opcode = SIPUSH;
                        operand = MAX_STATES;
                    }
                    if (operand == 12) this.count++;
                    if (this.count > 0) {
                        if (operand == 15) this.count++;
                        else if (operand == 12) operand += 4;
                    }
                }
                super.visitIntInsn(opcode, operand);
            }

            @Override
            public void visitInsn(int opcode) {
                if (this.count > 0 && opcode == ICONST_4) {
                    super.visitIntInsn(BIPUSH, 8);
                    return;
                }
                super.visitInsn(opcode);
            }
        }

        private static class SetDataFromNBTVisitor extends MethodVisitor {

            private boolean saw12 = false;

            public SetDataFromNBTVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitIntInsn(int opcode, int operand) {
                if (opcode == BIPUSH) {
                    if (operand == 12) {
                        this.saw12 = true;
                        operand += 4;
                    }
                }
                super.visitIntInsn(opcode, operand);
            }

            @Override
            public void visitInsn(int opcode) {
                if (this.saw12 && opcode == ICONST_4) {
                    super.visitIntInsn(BIPUSH, 8);
                    return;
                }
                super.visitInsn(opcode);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static void $initHarvestLevelArray(int[] harvestLevel) {
            Arrays.fill(harvestLevel, -1);
        }
    }

    @SuppressWarnings("unused")
    public static class NibblestArray extends NibbleArray {

        public NibblestArray() {
            super(new byte[4096]);
        }

        public NibblestArray(byte[] bytes) {
            super(bytes);
        }

        @Override
        public int getFromIndex(int index) {
            return this.getData()[index];
        }

        @Override
        public void setIndex(int index, int value) {
            this.getData()[index] = (byte) value;
        }
    }

    private MoreStates() {}
}
