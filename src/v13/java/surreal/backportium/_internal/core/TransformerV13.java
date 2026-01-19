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
        function = mix(function, BubbleColumns.visit(name, transformedName, bytes));
        function = mix(function, AirBarDepletion.visit(name, transformedName, bytes));
        function = mix(function, ItemEntityBuoyancy.visit(name, transformedName, bytes));
        function = mix(function, BlockHugeMushroomAlias.visit(name, transformedName, bytes));
        function = mix(function, ConduitPowerImplementation.visit(name, transformedName, bytes));
        function = mix(function, SlowFallingImplementation.visit(name, transformedName, bytes));
//        function = mix(function, PotionTransformer.visit(name, transformedName, bytes));
        function = mix(function, NewButtonStates.visit(name, transformedName, bytes));
        function = mix(function, WaterLogging.visit(name, transformedName, bytes));
        function = mix(function, BiomeNameTranslation.visit(name, transformedName, bytes));
        function = mix(function, BetterWaterColor.visit(name, transformedName, bytes));
        function = mix(function, TridentImplementation.visit(name, transformedName, bytes));
        function = mix(function, SwimmingState.visit(name, transformedName, bytes));
        function = mix(function, InterpolatedCameraMovement.visit(name, transformedName, bytes));
        function = mix(function, UncarvedPumpkin.visit(name, transformedName, bytes));
        function = mix(function, BedExplosionDeathMessage.visit(name, transformedName, bytes));

        // Fixes
        function = mix(function, FixBannerSound.visit(name, transformedName, bytes));

        return function;
    }

    private TransformerV13() {}
}
