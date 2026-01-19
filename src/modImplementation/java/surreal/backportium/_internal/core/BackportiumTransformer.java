package surreal.backportium._internal.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import surreal.backportium._internal.core.visitor.*;
import surreal.backportium._internal.core.visitor.BetterShoulderEntities;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.function.Function;

import static surreal.backportium._internal.bytecode.asm.Transformer.*;

@SuppressWarnings("unused")
public final class BackportiumTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) return null;
        if (transformedName.startsWith("surreal.backportium.")) return bytes;
        Function<ClassVisitor, ClassVisitor> function = null;
        function = mix(function, getVisitors(name, transformedName, bytes));
        function = mix(function, MoreBlockStates.getClassVisitor(name, transformedName));
        function = mix(function, ActionAnimation.getClassVisitor(name, transformedName));
        function = mix(function, CustomSplashTexts.getClassVisitor(name, transformedName));
        function = mix(function, ModelOverride.getClassVisitor(name, transformedName));
        function = mix(function, EntityStates.getClassVisitor(name, transformedName));
        function = mix(function, MoreBiomeOverride.getClassVisitor(name, transformedName));
        function = mix(function, Tags.getVisitor(name, transformedName, bytes));
        function = mix(function, getAdditionalVisitors(name, transformedName, bytes));
        return apply(function, bytes, 3);
    }

    private static Function<ClassVisitor, ClassVisitor> getVisitors(String name, String transformedName, byte[] bytes) {
        Function<ClassVisitor, ClassVisitor> function = null;
        function = TransformerV13.getVisitor(name, transformedName, bytes);
        function = mix(function, TransformerV15.getVisitor(name, transformedName, bytes));
        function = mix(function, TransformerV16.getVisitor(name, transformedName, bytes));
        return function;
    }

    private static Function<ClassVisitor, ClassVisitor> getAdditionalVisitors(String name, String transformedName, byte[] bytes) {
        Function<ClassVisitor, ClassVisitor> function = null;
        function = mix(function, BetterShoulderEntities.visit(name, transformedName, bytes));
        return function;
    }

    private static byte[] apply(Function<ClassVisitor, ClassVisitor> newVisitor, byte[] bytes, int writerOptions) {
        if (newVisitor == null) return bytes;
        ClassWriter writer = new ClassWriter(writerOptions);
        ClassReader reader = new ClassReader(bytes);
        ClassVisitor cv = newVisitor.apply(writer);
        reader.accept(cv, 0);
        byte[] outBytes = writer.toByteArray();
        if (FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            String name;
            {
                ClassReader r = new ClassReader(outBytes);
                name = r.getClassName();
            }
            File file = new File("classOut/" + name + ".class");
            file.getParentFile().mkdirs();
            try (OutputStream stream = Files.newOutputStream(file.toPath())) {
                stream.write(outBytes);
            }
            catch (IOException e) { throw new RuntimeException("Problem occurred while trying to write class", e); }
        }
        return outBytes;
    }
}
