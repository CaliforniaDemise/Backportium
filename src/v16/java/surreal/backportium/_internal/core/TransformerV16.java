package surreal.backportium._internal.core;

import org.objectweb.asm.ClassVisitor;
import surreal.backportium._internal.bytecode.traverse.ClassBytes;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;
import surreal.backportium._internal.core.visitor.NewWallVisitor;

import java.util.function.Function;

public class TransformerV16 {

    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.block.BlockWall")) return NewWallVisitor.BlockWallVisitor::new;
        else {
            if (transformedName.equals("vazkii.quark.base.block.BlockQuarkWall")) return NewWallVisitor.QuarkWallVisitor::new;
//            if (transformedName.equals("paulevs.betternether.blocks.BNWall")) return NewWallVisitor.BlockWallVisitor::new;
            if (transformedName.equals("net.minecraft.block.BlockPane")) return NewWallVisitor.BlockPaneVisitor::new;
            int[] constantTable = ClassBytes.getConstantJumpTable(bytes);
            if (ClassTraverser.get().isSuper(bytes, constantTable, "net/minecraft/block/BlockWall")) return NewWallVisitor.BlockWallChildVisitor::new;
        }
        if (transformedName.equals("net.minecraft.block.Block")) return NewWallVisitor.BlockVisitor::new;
        return null;
    }
}
