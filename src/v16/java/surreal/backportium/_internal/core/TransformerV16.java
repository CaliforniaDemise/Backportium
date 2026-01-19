package surreal.backportium._internal.core;

import org.objectweb.asm.ClassVisitor;
import surreal.backportium._internal.core.visitor.NewWallStates;
import surreal.backportium._internal.core.visitor.UnderwaterGrassToDirt;

import java.util.function.Function;

import static surreal.backportium._internal.bytecode.asm.Transformer.mix;

public class TransformerV16 {

    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        Function<ClassVisitor, ClassVisitor> function = null;
        function = mix(function, NewWallStates.visit(name, transformedName, bytes));
        function = mix(function, UnderwaterGrassToDirt.visit(name, transformedName, bytes));
        return function;
    }
}
