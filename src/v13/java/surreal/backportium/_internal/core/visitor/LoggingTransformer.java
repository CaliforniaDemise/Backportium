package surreal.backportium._internal.core.visitor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium._internal.world.LoggedAccess;
import surreal.backportium._internal.world.LoggedWorld;
import surreal.backportium._internal.world.chunk.LoggableChunk;
import surreal.backportium._internal.world.chunk.LoggingMap;
import surreal.backportium.api.block.Loggable;
import surreal.backportium.util.FluidUtil;

import java.util.function.Function;

public final class LoggingTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/LoggingTransformer$Hooks";
    private static final String LOGGED_ACCESS = "surreal/backportium/_internal/world/LoggedAccess";
    private static final String LOGGABLE_CHUNK = "surreal/backportium/_internal/world/chunk/LoggableChunk";
    private static final String LOGGING_MAP = "surreal/backportium/_internal/world/chunk/LoggingMap";

    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.world.chunk.Chunk": return ChunkVisitor::new;
            case "net.minecraft.world.chunk.storage.AnvilChunkLoader": return AnvilChunkLoaderVisitor::new;
            case "net.minecraft.network.play.server.SPacketChunkData": return SPacketChunkDataVisitor::new;
            case "net.minecraft.client.network.NetHandlerPlayClient": return NetHandlerPlayClientVisitor::new;
            case "net.minecraft.world.IBlockAccess": return IBlockAccessVisitor::new;
            case "net.minecraft.world.World": return WorldVisitor::new;
            case "net.minecraft.world.ChunkCache": return ChunkCacheVisitor::new;
            case "net.minecraft.client.renderer.chunk.RenderChunk": return RenderChunkVisitor::new;
            case "net.minecraft.client.renderer.BlockFluidRenderer": return BlockFluidRendererVisitor::new;
            case "net.minecraft.block.BlockLiquid": return BlockLiquidVisitor::new;
            case "net.minecraft.block.BlockDynamicLiquid": return BlockDynamicLiquidVisitor::new;
            case "net.minecraft.block.BlockStaticLiquid": return BlockStaticLiquidVisitor::new;
            case "net.minecraft.world.WorldServer": return WorldServerVisitor::new;
            case "net.minecraft.network.play.server.SPacketBlockChange": return SPacketBlockChangeVisitor::new;
            case "net.minecraft.world.WorldEntitySpawner": return WorldEntitySpawnerVisitor::new;
            case "net.minecraft.entity.Entity": return EntityVisitor::new;
            case "net.minecraft.client.renderer.ActiveRenderInfo": return ActiveRenderInfoVisitor::new;
            case "net.minecraft.client.renderer.EntityRenderer": return EntityRendererVisitor::new;
            case "net.minecraftforge.common.ForgeHooks": return ForgeHooks::new;
        }
        return null;
    }

    private static class ChunkVisitor extends LeClassVisitor {

        public ChunkVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, LOGGABLE_CHUNK));
            super.visitField(ACC_PRIVATE, "bp$loggingData", "L" + LOGGING_MAP + ";", null, null);
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
            { //getLoggingMap
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getLoggingMap", "()L" + LOGGING_MAP + ";", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/chunk/Chunk", "bp$loggingData", "L" + LOGGING_MAP + ";");
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 0);
            }
            { // setLoggingMap
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setLoggingMap", "(L" + LOGGING_MAP + ";)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "net/minecraft/world/chunk/Chunk", "bp$loggingData", "L" + LOGGING_MAP + ";");
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
                    super.visitTypeInsn(NEW, LOGGING_MAP);
                    super.visitInsn(DUP);
                    super.visitMethodInsn(INVOKESPECIAL, LOGGING_MAP, "<init>", "()V", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/world/chunk/Chunk", "bp$loggingData", "L" + LOGGING_MAP + ";");
                }
                super.visitInsn(opcode);
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
            if (name.equals(getName("writeChunkToNBT", "func_75820_a"))) return new WriteChunkToNBT(mv);
            if (name.equals(getName("readChunkFromNBT", "func_75823_a"))) return new ReadChunkFromNBT(mv);
            return mv;
        }

        private static class WriteChunkToNBT extends MethodVisitor {

            public WriteChunkToNBT(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "getLoggingMap", "()L" + LOGGING_MAP + ";", false);
                    super.visitVarInsn(ALOAD, 3);
                    super.visitMethodInsn(INVOKEVIRTUAL, LOGGING_MAP, "writeToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class ReadChunkFromNBT extends MethodVisitor {

            public ReadChunkFromNBT(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ARETURN) {
                    super.visitVarInsn(ALOAD, 5);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "getLoggingMap", "()L" + LOGGING_MAP + ";", false);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitMethodInsn(INVOKEVIRTUAL, LOGGING_MAP, "readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class SPacketChunkDataVisitor extends LeClassVisitor {

        public SPacketChunkDataVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PUBLIC, "bp$loggingData", "L" + LOGGING_MAP + ";", null, null);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>") && !desc.equals("()V")) return new InitVisitor(mv);
            if (name.equals(getName("readPacketData", "func_148837_a"))) return new ReadPacketDataVisitor(mv);
            if (name.equals(getName("writePacketData", "func_148840_b"))) return new WritePacketDataVisitor(mv);
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            public InitVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "getLoggingMap", "()L" + LOGGING_MAP + ";", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/network/play/server/SPacketChunkData", "bp$loggingData", "L" + LOGGING_MAP + ";");
                }
                super.visitInsn(opcode);
            }
        }

        private static class ReadPacketDataVisitor extends MethodVisitor {

            public ReadPacketDataVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/network/play/server/SPacketChunkData", "bp$loggingData", "L" + LOGGING_MAP + ";");
                super.visitVarInsn(ALOAD, 1);
                super.visitMethodInsn(INVOKEVIRTUAL, LOGGING_MAP, "readFromPacket", "(Lnet/minecraft/network/PacketBuffer;)V", false);
            }
        }

        private static class WritePacketDataVisitor extends MethodVisitor {

            public WritePacketDataVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/network/play/server/SPacketChunkData", "bp$loggingData", "L" + LOGGING_MAP + ";");
                super.visitVarInsn(ALOAD, 1);
                super.visitMethodInsn(INVOKEVIRTUAL, LOGGING_MAP, "writeToPacket", "(Lnet/minecraft/network/PacketBuffer;)V", false);
            }
        }
    }

    private static class NetHandlerPlayClientVisitor extends LeClassVisitor {

        public NetHandlerPlayClientVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("handleChunkData", "func_147263_a"))) return new HandleChunkDataVisitor(mv);
            if (name.equals(getName("handleBlockChange", "func_147234_a"))) return new HandleBlockChangeVisitor(mv);
            return mv;
        }

        private static class HandleChunkDataVisitor extends MethodVisitor {

            public HandleChunkDataVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 2);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/network/play/server/SPacketChunkData", "bp$loggingData", "L" + LOGGING_MAP + ";");
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "setLoggingMap", "(L" + LOGGING_MAP + ";)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class HandleBlockChangeVisitor extends MethodVisitor {

            public HandleBlockChangeVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/network/NetHandlerPlayClient", getName("world", "field_147300_g"), "Lnet/minecraft/client/multiplayer/WorldClient;");
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/play/server/SPacketBlockChange", getName("getBlockPosition", "func_179827_b"), "()Lnet/minecraft/util/math/BlockPos;", false);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/play/server/SPacketBlockChange", "getLoggedState", "()Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "setLoggedState", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class IBlockAccessVisitor extends LeClassVisitor {

        public IBlockAccessVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, getInterfaces(interfaces, LOGGED_ACCESS));
        }
    }

    private static class WorldVisitor extends LeClassVisitor {

        public WorldVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE, "IN_LOGGING", "Z", null, false);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("setBlockState", "func_180501_a"))) return new SetBlockStateVisitor(mv);
            if (name.equals(getName("handleMaterialAcceleration", "func_72918_a"))) return new HandleMaterialAccelerationVisitor(mv);
            if (name.equals(getName("neighborChanged", "func_190524_a"))) return new NeighborChangedVisitor(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getLoggedState
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getLoggedState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "World$getLoggedState", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(2, 0);
            }
            { // setLoggedState
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setLoggedState", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "World$setLoggedState", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 0);
            }
        }

        private static class SetBlockStateVisitor extends MethodVisitor {

            public SetBlockStateVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                super.visitVarInsn(opcode, var);
                if (opcode == ASTORE && var == 6) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ALOAD, 6);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitVarInsn(ALOAD, 5);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "World$setBlockState", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;Lnet/minecraftforge/common/util/BlockSnapshot;)Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitVarInsn(ASTORE, 2);
                }
            }
        }

        private static class HandleMaterialAccelerationVisitor extends MethodVisitor {

            private boolean check = false;
            private final Label l_con_start = new Label();
            private Label l_con_check;

            public HandleMaterialAccelerationVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                if (!check && opcode == ALOAD && var == 17) {
                    check = true;
                    super.visitLabel(l_con_start);
//                    super.visitFrame(F_SAME, 0, null, 0, null);
                }
                super.visitVarInsn(opcode, var);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if (opcode == IFLT) {
                    l_con_check = label;
                }
                super.visitJumpInsn(opcode, label);
            }

            @Override
            public void visitLabel(Label label) {
                super.visitLabel(label);
                if (label == l_con_check) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/world/World", "IN_LOGGING", "Z");
                    Label l_con = new Label();
                    super.visitJumpInsn(IFNE, l_con);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ICONST_1);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/world/World", "IN_LOGGING", "Z");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 12);
                    super.visitVarInsn(ALOAD, 16);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getLoggedState", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitVarInsn(ASTORE, 16);
                    super.visitVarInsn(ALOAD, 16);
                    super.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true);
                    super.visitVarInsn(ASTORE, 17);
                    super.visitJumpInsn(GOTO, l_con_start);
                    super.visitLabel(l_con);
//                    super.visitFrame(F_SAME, 0, null, 0, null);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ICONST_0);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/world/World", "IN_LOGGING", "Z");
                }
            }
        }

        private static class NeighborChangedVisitor extends MethodVisitor {

            public NeighborChangedVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("neighborChanged", "func_189546_a"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getLoggedState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitVarInsn(ASTORE, 4);
                    super.visitVarInsn(ALOAD, 4);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitVarInsn(ALOAD, 3);
                    super.visitMethodInsn(INVOKEINTERFACE, owner, name, desc, true);
                }
            }
        }
    }

    private static class ChunkCacheVisitor extends LeClassVisitor {

        public ChunkCacheVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getLoggedState
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getLoggedState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/ChunkCache", getName("world", "field_72815_e"), "Lnet/minecraft/world/World;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getLoggedState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                mv.visitInsn(ARETURN);
                mv.visitMaxs(2, 0);
            }
            { // setLoggedState
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setLoggedState", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/ChunkCache", getName("world", "field_72815_e"), "Lnet/minecraft/world/World;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "setLoggedState", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 0);
            }
        }
    }

    private static class RenderChunkVisitor extends LeClassVisitor {

        public RenderChunkVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE, "IN_LOGGING", "Z", null, false);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("rebuildChunk", "func_178581_b"))) return new RebuildChunkVisitor(mv);
            return mv;
        }

        private static class RebuildChunkVisitor extends MethodVisitor {

            private boolean check = false;
            private Label label = null;

            public RebuildChunkVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("addTileEntity", "func_178490_a"))) {
                    check = true;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

            @Override
            public void visitLabel(Label label) {
                super.visitLabel(label);
                if (check) {
                    this.label = label;
                    check = false;
                }
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ACONST_NULL) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/chunk/RenderChunk", "IN_LOGGING", "Z");
                    Label l_con = new Label();
                    super.visitJumpInsn(IFNE, l_con);
                    super.visitVarInsn(ALOAD, 16);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderChunk$isLoggable", "(Lnet/minecraft/block/Block;)Z", false);
                    super.visitJumpInsn(IFEQ, l_con);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ICONST_1);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/renderer/chunk/RenderChunk", "IN_LOGGING", "Z");
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/client/renderer/chunk/RenderChunk", getName("worldView", "field_189564_r"), "Lnet/minecraft/world/ChunkCache;");
                    super.visitVarInsn(ALOAD, 14);
                    super.visitVarInsn(ALOAD, 15);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "RenderChunk$getLoggedState", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitVarInsn(ASTORE, 15);
                    super.visitVarInsn(ALOAD, 15);
                    super.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getBlock", "func_177230_c"), "()Lnet/minecraft/block/Block;", true);
                    super.visitVarInsn(ASTORE, 16);
                    super.visitJumpInsn(GOTO, this.label);
                    super.visitLabel(l_con);
                    super.visitFrame(F_SAME, 0, null, 0, null);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ICONST_0);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/client/renderer/chunk/RenderChunk", "IN_LOGGING", "Z");
                }
                super.visitInsn(opcode);
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
            if (name.equals(getName("renderFluid", "func_178270_a"))) return new RenderFluidVisitor(mv);
            return mv;
        }

        private static class RenderFluidVisitor extends MethodVisitor {

            public RenderFluidVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 1);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockFluidRenderer$getWorld", "(Lnet/minecraft/world/IBlockAccess;)Lnet/minecraft/world/IBlockAccess;", false);
                super.visitVarInsn(ASTORE, 1);
            }
        }
    }

    private static class BlockLiquidVisitor extends LeClassVisitor {

        public BlockLiquidVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("modifyAcceleration", "func_176197_a"))) return new ModifyAccelerationVisitor(mv);
            if (name.equals(getName("getFlow", "func_189543_a"))) return new GetFlowVisitor(mv);
            return mv;
        }

        private static class ModifyAccelerationVisitor extends MethodVisitor {

            public ModifyAccelerationVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ARETURN) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getLoggedState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/block/BlockLiquid", getName("getFlow", "func_189543_a"), "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/Vec3d;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/Vec3d", getName("add", "func_178787_e"), "(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static class GetFlowVisitor extends MethodVisitor {

            private int count = 0;

            public GetFlowVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitCode() {
                super.visitCode();
                super.visitVarInsn(ALOAD, 3);
                super.visitMethodInsn(INVOKEINTERFACE, "net/minecraft/block/state/IBlockState", getName("getMaterial", "func_185904_a"), "()Lnet/minecraft/block/material/Material;", true);
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/block/BlockLiquid", getName("material", "field_149764_J"), "Lnet/minecraft/block/material/Material;");
                Label l_con = new Label();
                super.visitJumpInsn(IF_ACMPEQ, l_con);
                super.visitTypeInsn(NEW, "net/minecraft/util/math/Vec3d");
                super.visitInsn(DUP);
                super.visitInsn(DCONST_0);
                super.visitInsn(DCONST_0);
                super.visitInsn(DCONST_0);
                super.visitMethodInsn(INVOKESPECIAL, "net/minecraft/util/math/Vec3d", "<init>", "(DDD)V", false);
                super.visitInsn(ARETURN);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    count++;
                    if (count != 2) {
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getLoggedOrNormal", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                        return;
                    }
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    private static class BlockDynamicLiquidVisitor extends LeClassVisitor {

        public BlockDynamicLiquidVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("placeStaticBlock", "func_180690_f"))) return new PlaceStaticBlockVisitor(mv);
            if (name.equals(getName("updateTick", "func_180650_b"))) return new UpdateTickVisitor(mv);
            if (name.equals(getName("checkAdjacentBlock", "func_176371_a"))) return new CheckAdjacentBlockVisitor(mv);
            return mv;
        }

        private static class PlaceStaticBlockVisitor extends MethodVisitor {

            public PlaceStaticBlockVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("setBlockState", "func_180501_a"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockLiquid$placeBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;ILnet/minecraft/block/Block;)Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        // TODO Handle the checkAdjacentSourceBlocks >= 2 with logging
        private static class UpdateTickVisitor extends MethodVisitor {

            private int setBlockState_counter = 0;
            private int getBlockState_counter = 0;

            public UpdateTickVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL) {
                    if (name.equals(getName("setBlockToAir", "func_175698_g"))) {
                        super.visitVarInsn(ALOAD, 0);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockDynamicLiquid$setBlockToAir", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)Z", false);
                        return;
                    }
                    else if (name.equals(getName("setBlockState", "func_180501_a"))) {
                        setBlockState_counter++;
                        if (setBlockState_counter == 1) {
                            super.visitVarInsn(ALOAD, 0);
                            super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockLiquid$placeBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;ILnet/minecraft/block/Block;)Z", false);
                            return;
                        }
                    }
                    else if (name.equals(getName("getBlockState", "func_180495_p"))) {
                        getBlockState_counter++;
                        if (getBlockState_counter == 1) {
                            super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getLoggedOrNormal", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                            return;
                        }
                    }
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private static class CheckAdjacentBlockVisitor extends MethodVisitor {

            public CheckAdjacentBlockVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getLoggedOrNormal", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    private static class BlockStaticLiquidVisitor extends LeClassVisitor {

        public BlockStaticLiquidVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("updateLiquid", "func_176370_f"))) return new UpdateLiquidVisitor(mv);
            return mv;
        }

        private static class UpdateLiquidVisitor extends MethodVisitor {

            public UpdateLiquidVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("setBlockState", "func_180501_a"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "BlockLiquid$placeBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;ILnet/minecraft/block/Block;)Z", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    private static class WorldServerVisitor extends LeClassVisitor {

        public WorldServerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("updateBlockTick", "func_175654_a"))) return new UpdateBlockTickVisitor(mv);
            if (name.equals(getName("tickUpdates", "func_72955_a"))) return new TickUpdatesVisitor(mv);
            return mv;
        }

        private static class UpdateBlockTickVisitor extends MethodVisitor {

            public UpdateBlockTickVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    super.visitVarInsn(ALOAD, 2);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "World$getStateFromBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)Lnet/minecraft/block/state/IBlockState;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private static class TickUpdatesVisitor extends MethodVisitor {

            public TickUpdatesVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    super.visitVarInsn(ALOAD, 4);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/NextTickListEntry", getName("getBlock", "func_151351_a"), "()Lnet/minecraft/block/Block;", false);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "World$getStateFromBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)Lnet/minecraft/block/state/IBlockState;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    private static class SPacketBlockChangeVisitor extends LeClassVisitor {

        public SPacketBlockChangeVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE, "loggedState", "Lnet/minecraft/block/state/IBlockState;", null, null);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>") && !desc.equals("()V")) return new InitVisitor(mv);
            if (name.equals(getName("readPacketData", "func_148837_a"))) return new ReadPacketDataVisitor(mv);
            if (name.equals(getName("writePacketData", "func_148840_b"))) return new WritePacketData(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getLoggedState
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getLoggedState", "()Lnet/minecraft/block/state/IBlockState;", null, null);
                {
                    AnnotationVisitor av = mv.visitAnnotation("Lnet/minecraftforge/fml/relauncher/SideOnly;", true);
                    av.visitEnum("value", "Lnet/minecraftforge/fml/relauncher/Side;", "CLIENT");
                }
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/network/play/server/SPacketBlockChange", "loggedState", "Lnet/minecraft/block/state/IBlockState;");
                mv.visitInsn(ARETURN);
                mv.visitMaxs(1, 0);
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
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", "getLoggedState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/network/play/server/SPacketBlockChange", "loggedState", "Lnet/minecraft/block/state/IBlockState;");
                }
                super.visitInsn(opcode);
            }
        }

        private static class ReadPacketDataVisitor extends MethodVisitor {

            public ReadPacketDataVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", "readInt", "()I", false);
                    super.visitMethodInsn(INVOKESTATIC, "net/minecraft/block/Block", getName("getStateById", "func_176220_d"), "(I)Lnet/minecraft/block/state/IBlockState;", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/network/play/server/SPacketBlockChange", "loggedState", "Lnet/minecraft/block/state/IBlockState;");
                }
                super.visitInsn(opcode);
            }
        }

        private static class WritePacketData extends MethodVisitor {

            public WritePacketData(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/network/play/server/SPacketBlockChange", "loggedState", "Lnet/minecraft/block/state/IBlockState;");
                    super.visitMethodInsn(INVOKESTATIC, "net/minecraft/block/Block", getName("getStateId", "func_176210_f"), "(Lnet/minecraft/block/state/IBlockState;)I", false);
                    super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer", "writeInt", "(I)Lio/netty/buffer/ByteBuf;", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class WorldEntitySpawnerVisitor extends LeClassVisitor {

        public WorldEntitySpawnerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("canCreatureTypeSpawnBody")) return new CanCreatureTypeSpawnBodyVisitor(mv);
            return mv;
        }

        private static class CanCreatureTypeSpawnBodyVisitor extends MethodVisitor {

            private int isValidEmptySpawnBlock_counter = 0;

            public CanCreatureTypeSpawnBodyVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKEVIRTUAL && name.equals(getName("isNormalCube", "func_185915_l"))) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(ALOAD, 2);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "WorldEntitySpawner$isValidBlockWater", "(ZLnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", false);
                }
                else if (opcode == INVOKESTATIC && name.equals(getName("isValidEmptySpawnBlock", "func_185331_a"))) {
                    isValidEmptySpawnBlock_counter++;
                    if (isValidEmptySpawnBlock_counter == 2) {
                        super.visitVarInsn(ALOAD, 1);
                        super.visitVarInsn(ALOAD, 2);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "WorldEntitySpawner$isValidBlock", "(ZLnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", false);
                    }
                }
            }
        }
    }

    private static class EntityVisitor extends LeClassVisitor {

        public EntityVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("isInsideOfMaterial", "func_70055_a"))) return new IsInsideOfMaterial(mv);
            return mv;
        }

        private static class IsInsideOfMaterial extends MethodVisitor {

            private int iconst0_counter = 0;

            public IsInsideOfMaterial(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ICONST_0) {
                    iconst0_counter++;
                    if (iconst0_counter == 2) {
                        super.visitVarInsn(ALOAD, 0);
                        super.visitVarInsn(ALOAD, 1);
                        super.visitVarInsn(ALOAD, 4);
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "Entity$isInsideOfMaterial", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/material/Material;Lnet/minecraft/util/math/BlockPos;)Z", false);
                        return;
                    }
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static class ForgeHooks extends LeClassVisitor {

        public ForgeHooks(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("isInsideOfMaterial")) return new IsInsideOfMaterialVisitor(mv);
            return mv;
        }

        private static class IsInsideOfMaterialVisitor extends MethodVisitor {

            public IsInsideOfMaterialVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ForgeHooks$getBlockState", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/material/Material;)Lnet/minecraft/block/state/IBlockState;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
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
            return mv;
        }

        private static class UpdateFogColorVisitor extends MethodVisitor {

            public UpdateFogColorVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getLoggedOrNormal2", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    private static class ActiveRenderInfoVisitor extends LeClassVisitor {

        public ActiveRenderInfoVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("getBlockStateAtEntityViewpoint", "func_186703_a"))) return new GetBlockStateAtEntityViewpointVisitor(mv);
            return mv;
        }

        private static class GetBlockStateAtEntityViewpointVisitor extends MethodVisitor {

            public GetBlockStateAtEntityViewpointVisitor(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == INVOKEVIRTUAL && name.equals(getName("getBlockState", "func_180495_p"))) {
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getLoggedOrNormal2", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static IBlockState World$getLoggedState(World world, BlockPos pos) {
            Chunk chunk = world.getChunk(pos);
            LoggingMap map = LoggableChunk.cast(chunk).getLoggingMap();
            return map.getLoggedState(pos.getX(), pos.getY(), pos.getZ());
        }

        public static void World$setLoggedState(World world, BlockPos pos, IBlockState state) {
            Chunk chunk = world.getChunk(pos);
            LoggingMap map = LoggableChunk.cast(chunk).getLoggingMap();
            map.setLoggedState(pos.getX(), pos.getY(), pos.getZ(), state);
        }

        @SideOnly(Side.CLIENT)
        public static boolean RenderChunk$isLoggable(Block block) {
            return block instanceof Loggable;
        }

        public static IBlockState RenderChunk$getLoggedState(IBlockAccess world, BlockPos pos, IBlockState state) {
            return Loggable.cast(state.getBlock()).getLoggedState(world, pos, state);
        }

        public static IBlockState World$setBlockState(World world, BlockPos pos, IBlockState oldState, IBlockState newState, BlockSnapshot snapshot) {
            LoggedAccess logged = LoggedAccess.cast(world);
            Block oldBlock = oldState.getBlock();
            Block newBlock = newState.getBlock();
            IBlockState newLoggedState = null;
            if (oldBlock instanceof Loggable) {
                if (!(newBlock instanceof Loggable)) {
                    if (newBlock.isAir(newState, world, pos))  {
                        newState = logged.getLoggedState(pos);
                    }
                }
                else {
                    IBlockState oldLoggedState = logged.getLoggedState(pos);
                    if (Loggable.cast(newBlock).canLog(world, pos, newState, oldLoggedState)) {
                        newLoggedState = logged.getLoggedState(pos);
                    }
                }
            }
            else {
                Fluid fluid = FluidUtil.getFluid(oldState);
                if (fluid != null) {
                    if (newBlock instanceof Loggable) {
                        if (Loggable.cast(newBlock).canLog(world, pos, newState, oldState)) {
                            if (world.isRemote) logged.setLoggedState(pos, oldState);
                            newLoggedState = oldState;
                        }
                    }
                }
            }
            if (!world.isRemote) {
                logged.setLoggedState(pos, newLoggedState);
            }
            return newState;
        }

        @SideOnly(Side.CLIENT)
        public static IBlockAccess BlockFluidRenderer$getWorld(IBlockAccess parent) {
            return new LoggedWorld(parent);
        }

        public static IBlockState $getLoggedState(IBlockAccess world, BlockPos pos, IBlockState state) {
            if (state.getBlock() instanceof Loggable) {
                return Loggable.cast(state.getBlock()).getLoggedState(world, pos, state);
            }
            return Blocks.AIR.getDefaultState();
        }

        public static IBlockState $getLoggedOrNormal(IBlockAccess world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            if (FluidUtil.getFluid(state) == null) {
                return LoggedAccess.cast(world).getLoggedState(pos);
            }
            return state;
        }

        public static IBlockState $getLoggedOrNormal2(IBlockAccess world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof Loggable) {
                return LoggedAccess.cast(world).getLoggedState(pos);
            }
            return state;
        }

        public static boolean BlockLiquid$placeBlock(World world, BlockPos pos, IBlockState toSet, int flags, Block blockLiquid) {
            IBlockState actualState = world.getBlockState(pos);
            if (actualState.getBlock() != blockLiquid) {
                LoggedAccess logged = LoggedAccess.cast(world);
                logged.setLoggedState(pos, toSet);
                notifyClient(world, pos, flags);
                return true;
            }
            else {
                return world.setBlockState(pos, toSet, flags);
            }
        }

        public static boolean BlockDynamicLiquid$setBlockToAir(World world, BlockPos pos, Block blockLiquid) {
            IBlockState actualState = world.getBlockState(pos);
            if (actualState.getBlock() != blockLiquid) {
                LoggedAccess logged = LoggedAccess.cast(world);
                logged.setLoggedState(pos, null);
                notifyClient(world, pos, 3);
                return true;
            }
            else {
                return world.setBlockToAir(pos);
            }
        }

        public static IBlockState World$getStateFromBlock(World world, BlockPos pos, Block block) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() != block && state.getBlock() instanceof Loggable) {
                state = LoggedAccess.cast(world).getLoggedState(pos);
            }
            return state;
        }

        public static boolean WorldEntitySpawner$isValidBlock(boolean defaultValue, World world, BlockPos pos) {
            IBlockState loggedState = LoggedAccess.cast(world).getLoggedState(pos.down());
            Block loggedBlock = loggedState.getBlock();
            boolean flag = loggedBlock != Blocks.BEDROCK && loggedBlock != Blocks.BARRIER;
            return defaultValue && flag && WorldEntitySpawner.isValidEmptySpawnBlock(loggedState) && WorldEntitySpawner.isValidEmptySpawnBlock(LoggedAccess.cast(world).getLoggedState(pos.up()));
        }

        public static boolean WorldEntitySpawner$isValidBlockWater(boolean defaultValue, World world, BlockPos pos) {
            if (defaultValue) return true;
            LoggedAccess loggedWorld = LoggedAccess.cast(world);
            IBlockState loggedState = loggedWorld.getLoggedState(pos);
            BlockPos upPos = pos.up();
            BlockPos downPos = pos.down();
            return (FluidUtil.getFluid(world.getBlockState(pos)) == FluidRegistry.WATER || FluidUtil.getFluid(loggedState) == FluidRegistry.WATER) && (FluidUtil.getFluid(world.getBlockState(downPos)) == FluidRegistry.WATER || FluidUtil.getFluid(loggedWorld.getLoggedState(downPos)) == FluidRegistry.WATER) && (!world.getBlockState(upPos).isNormalCube() && !loggedWorld.getLoggedState(upPos).isNormalCube());
        }

        public static boolean Entity$isInsideOfMaterial(Entity entity, Material material, BlockPos pos) {
            LoggedAccess logged = LoggedAccess.cast(entity.world);
            IBlockState loggedState = logged.getLoggedState(pos);
            Boolean result = loggedState.getBlock().isEntityInsideMaterial(entity.world, pos, loggedState, entity, entity.posY + (double) entity.getEyeHeight(), material, true);
            if (result != null) return result;
            if (loggedState.getMaterial() == material) {
                return net.minecraftforge.common.ForgeHooks.isInsideOfMaterial(material, entity, pos);
            }
            else {
                return false;
            }
        }

        @SideOnly(Side.CLIENT)
        public static Vec3d EntityRenderer$addLoggedFogColor(Block block, World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
            Vec3d vec3d = block.getFogColor(world, pos, state, entity, originalColor, partialTicks);
            IBlockState loggedState = LoggedAccess.cast(world).getLoggedState(pos);
            return vec3d.add(loggedState.getBlock().getFogColor(world, pos, loggedState, entity, originalColor, partialTicks));
        }

        public static IBlockState ForgeHooks$getBlockState(World world, BlockPos pos, Material material) {
            IBlockState state = world.getBlockState(pos);
            if (state.getMaterial() != material && state.getBlock() instanceof Loggable) {
                state = LoggedAccess.cast(world).getLoggedState(pos);
            }
            return state;
        }

        private static void notifyClient(World world, BlockPos pos, int flags) {
            if (!world.isRemote) {
                world.markAndNotifyBlock(pos, world.getChunk(pos), world.getBlockState(pos), world.getBlockState(pos), flags);
                world.playerEntities.forEach(p -> ((EntityPlayerMP) p).connection.sendPacket(new SPacketBlockChange(world, pos)));
            }
        }
    }

    private LoggingTransformer() {}
}
