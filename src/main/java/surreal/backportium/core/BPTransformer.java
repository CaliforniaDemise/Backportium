package surreal.backportium.core;

import net.minecraft.launchwrapper.IClassTransformer;
import surreal.backportium.core.transformers.*;
import surreal.backportium.core.v13.ClassTransformer13;

@SuppressWarnings("unused")
public class BPTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (transformedName.startsWith("surreal.backportium")) return basicClass;
        if (transformedName.startsWith("it.unimi") || transformedName.startsWith("com.google") || transformedName.startsWith("com.ibm") || transformedName.startsWith("com.paulscode")) return basicClass;
        basicClass = ClassTransformer13.transformClass(transformedName, basicClass);
        switch (transformedName) {
            case "net.minecraft.block.BlockGrass":
            case "net.minecraft.block.BlockMycelium": return FixTransformer.transformBlockGrass(basicClass);
            case "net.minecraft.network.NetHandlerPlayServer": return FixTransformer.transformNetHandlerPlayServer(basicClass);
        }
        return basicClass;
    }
}
