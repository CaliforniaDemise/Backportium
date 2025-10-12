package surreal.backportium._internal.core;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import surreal.backportium._internal.core.visitor.ElytraVisitor;

import java.util.function.Function;

import static surreal.backportium._internal.bytecode.asm.Transformer.mix;

public final class TransformerV15 {

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        Function<ClassVisitor, ClassVisitor> function = null;

        // Fixes
        function = mix(function, ElytraVisitor.visit(name, transformedName, bytes));

        return function;
    }

    private TransformerV15() {}
}
