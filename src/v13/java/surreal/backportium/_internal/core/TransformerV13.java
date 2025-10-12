package surreal.backportium._internal.core;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import surreal.backportium._internal.core.visitor.*;

import java.util.function.Function;

import static surreal.backportium._internal.bytecode.asm.Transformer.mix;

public final class TransformerV13 {

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        Function<ClassVisitor, ClassVisitor> function = null;
        function = mix(function, BubbleColumnTransformer.visit(name, transformedName, bytes));
        function = mix(function, AirBarTransformer.visit(name, transformedName, bytes));
        function = mix(function, ItemEntityBuoyancyTransformer.visit(name, transformedName, bytes));
        function = mix(function, BlockHugeMushroomTransformer.visit(name, transformedName, bytes));
        function = mix(function, PotionTransformer.visit(name, transformedName, bytes));
        function = mix(function, NewButtonTransformer.visit(name, transformedName, bytes));
        function = mix(function, LoggingTransformer.visit(name, transformedName, bytes));
        function = mix(function, BiomeNameTransformer.visit(name, transformedName, bytes));
        function = mix(function, WaterColorTransformer.visit(name, transformedName, bytes));
        function = mix(function, TridentTransformer.visit(name, transformedName, bytes));
        function = mix(function, SwimmingTransformer.visit(name, transformedName, bytes));
        function = mix(function, CameraTransformer.visit(name, transformedName, bytes));
        function = mix(function, UncarvedPumpkinTransformer.visit(name, transformedName, bytes));

        // Fixes
        function = mix(function, BannerSoundTransformer.visit(name, transformedName, bytes));

        return function;
    }

    private TransformerV13() {}
}
