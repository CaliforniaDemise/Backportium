package surreal.backportium._internal.bytecode.asm;

import org.objectweb.asm.ClassVisitor;

import java.util.function.Function;

public final class Transformer {

    public static Function<ClassVisitor, ClassVisitor> mix(Function<ClassVisitor, ClassVisitor> func1, Function<ClassVisitor, ClassVisitor> func2) {
        if (func1 == null) return func2;
        if (func2 == null) return func1;
        return func1.andThen(func2);
    }

    private Transformer() {}
}
