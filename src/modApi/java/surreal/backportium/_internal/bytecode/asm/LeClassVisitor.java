package surreal.backportium._internal.bytecode.asm;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public abstract class LeClassVisitor extends ClassVisitor implements Opcodes {

    public LeClassVisitor(ClassVisitor cv) { super(ASM5, cv); }

    protected static String[] getInterfaces(@Nullable String[] interfaces, String... additional) {
        if (interfaces == null) return additional;
        else {
            String[] array = new String[interfaces.length + additional.length];
            System.arraycopy(interfaces, 0, array, 0, interfaces.length);
            System.arraycopy(additional, 0, array, interfaces.length, additional.length);
            return array;
        }
    }

    protected static String getName(String mcp, String srg) { return FMLLaunchHandler.isDeobfuscatedEnvironment() ? mcp : srg; }

    protected static int setAccess(int access, int newAccess) {
        if ((access & ACC_PUBLIC) != 0) access ^= ACC_PUBLIC;
        if ((access & ACC_PROTECTED) != 0) access ^= ACC_PROTECTED;
        if ((access & ACC_PRIVATE) != 0) access ^= ACC_PRIVATE;
        access |= newAccess;
        return access;
    }
}
