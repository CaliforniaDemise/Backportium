package surreal.backportium._internal.core.visitor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBlocks;

import java.util.function.Function;

import static _mod.Constants.V_BLOCK_HUGE_MUSHROOM_ALIAS;

public final class BlockHugeMushroomAlias extends LeClassVisitor {

    private static final String HOOKS = V_BLOCK_HUGE_MUSHROOM_ALIAS + "$Hooks";

    private BlockHugeMushroomAlias(ClassVisitor cv) {
        super(cv);
    }

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.BlockHugeMushroom")) return BlockHugeMushroomAlias::new;
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(getName("getItemDropped", "func_180660_a"))) return new GetItemDropped(mv);
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        { // canSilkHarvest
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("canSilkHarvest", "func_149700_E"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/player/EntityPlayer;)Z", null, null);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(5, 0);
        }
        { // getSilkTouchDrop
            MethodVisitor mv = super.visitMethod(ACC_PROTECTED, getName("getSilkTouchDrop", "func_180643_i"), "(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/item/ItemStack;", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$getSilkTouchDrop", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/item/ItemStack;", true);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 0);
        }
        { // getPickItem
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getPickBlock", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$getSilkTouchDrop", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/item/ItemStack;", true);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(6, 0);
        }
    }

    private static final class GetItemDropped extends MethodVisitor {

        public GetItemDropped(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == ARETURN) {
                super.visitVarInsn(ALOAD, 1);
                super.visitMethodInsn(INVOKESTATIC, HOOKS, "$getItem", "(Lnet/minecraft/item/Item;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/item/Item;", false);
            }
            super.visitInsn(opcode);
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static Item $getItem(Item defaultItem, IBlockState state) {
            BlockHugeMushroom.EnumType type = state.getValue(BlockHugeMushroom.VARIANT);
            if (type == BlockHugeMushroom.EnumType.ALL_STEM || type == BlockHugeMushroom.EnumType.STEM) return Items.AIR;
            return defaultItem;
        }

        public static ItemStack $getSilkTouchDrop(Block block, IBlockState state) {
            BlockHugeMushroom.EnumType type = state.getValue(BlockHugeMushroom.VARIANT);
            if (type == BlockHugeMushroom.EnumType.ALL_STEM || type == BlockHugeMushroom.EnumType.STEM) return new ItemStack(ModBlocks.MUSHROOM_STEM);
            if (block == Blocks.RED_MUSHROOM_BLOCK) return new ItemStack(ModBlocks.RED_MUSHROOM_BLOCK);
            if (block == Blocks.BROWN_MUSHROOM_BLOCK) return new ItemStack(ModBlocks.BROWN_MUSHROOM_BLOCK);
            return ItemStack.EMPTY;
        }

        private Hooks() {}
    }
}
