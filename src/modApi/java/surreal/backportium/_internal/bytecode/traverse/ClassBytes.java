package surreal.backportium._internal.bytecode.traverse;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ClassBytes {

    /**
     * Checks for magical numbers (first 4 bytes). Can be used to know if the file is actually Java class file or not.
     * @return true if the file is a class file.
     **/
    public static boolean checkMagic(byte[] bytes) {
        return checkBytes(bytes, 0, new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
    }

    /**
     * Get class Java major version.
     * @return 50 for Java 6, 52 for Java 8, 65 for Java 21 etc.
     **/
    public static int getMajorVersion(byte[] bytes) {
        return getShort(bytes, 6);
    }

    /**
     * Get class Java minor version.
     * @return 3 on Java 1.1 and 0 on everything else.
     **/
    public static int getMinorVersion(byte[] bytes) { return getShort(bytes, 4); }

    public static int getConstantPoolCount(byte[] bytes) { return getShort(bytes, 8); }

    /**
     * Constant pool jump table. The values are the byte indexes of constants first byte, their tag.
     * The last byte index is the first byte after constant pool.
     **/
    public static int[] getConstantJumpTable(byte[] bytes) {
        int constantPoolCount = getConstantPoolCount(bytes);
        int[] constantJumpTable = new int[constantPoolCount];
        int index = 10;
        int i = 0;
        for (; i < constantPoolCount - 1; i++) {
            final int tag = bytes[index];
            constantJumpTable[i] = index;
            if (tag == CONSTANT_Long || tag == CONSTANT_Double) constantJumpTable[++i] = index;
            index += constantSize(bytes, index, tag);
        }
        constantJumpTable[i] = index;
        return constantJumpTable;
    }

    /**
     * Get the byte index after the constant pool without creating a jump table.
     **/
    public static int getAfterConstantPool(byte[] bytes) {
        int constantPoolCount = getConstantPoolCount(bytes);
        int index = 10;
        for (int i = 0; i < constantPoolCount - 1; i++) {
            index += constantSize(bytes, index, bytes[index]);
        }
        return index;
    }

    public static boolean isInterface(byte[] bytes, int[] constantJumpTable) { return (getAccessFlags(bytes, constantJumpTable) & 0x0200) == 0x0200; }

    public static int getAccessFlags(byte[] bytes) { return getShort(bytes, getAfterConstantPool(bytes)); }
    public static int getAccessFlags(byte[] bytes, int[] constantJumpTable) { return getShort(bytes, constantJumpTable[constantJumpTable.length - 1]); }

    public static String getClassName(byte[] bytes, int[] constantJumpTable) { return getClassUtf8(bytes, constantJumpTable, constantJumpTable[getShort(bytes, constantJumpTable[constantJumpTable.length - 1] + 2) - 1]); }

    public static String getSuperName(byte[] bytes, int[] constantJumpTable) { return getClassUtf8(bytes, constantJumpTable, constantJumpTable[getShort(bytes, constantJumpTable[constantJumpTable.length - 1] + 4) - 1]); }

    public static String[] getInterfaces(byte[] bytes, int[] constantJumpTable) {
        int index = constantJumpTable[constantJumpTable.length - 1] + 6;
        int interfaces_count = getShort(bytes, index);
        if (interfaces_count == 0) return new String[0];
        String[] interfaces = new String[interfaces_count];
        index += 2;
        for (int i = 0; i < interfaces_count; i++) {
            int iface_index = getShort(bytes, index);
            interfaces[i] = getClassUtf8(bytes, constantJumpTable, constantJumpTable[iface_index - 1]);
            index += 2;
        }
        return interfaces;
    }

    public static void getClasses(byte[] bytes, int[] constantJumpTable, boolean recursive, @NotNull Collection<String> classes) { getConstants(bytes, constantJumpTable, recursive, classes, null, null); }
    public static void getMethods(byte[] bytes, int[] constantJumpTable, boolean recursive, @NotNull Collection<String> methods) { getConstants(bytes, constantJumpTable, recursive, null, methods, null); }
    public static void getFields(byte[] bytes, int[] constantJumpTable, boolean recursive, @NotNull Collection<String> fields) { getConstants(bytes, constantJumpTable, recursive, null, null, fields); }
    public static void getConstants(byte[] bytes, int[] constantJumpTable, boolean recursive, @Nullable Collection<String> classes, @Nullable Collection<String> methods, @Nullable Collection<String> fields) {
        if (classes == null && methods == null && fields == null) return;
        if (recursive) {
            String className = getClassName(bytes, constantJumpTable);
            if (className.equals("java/lang/Object")) return;
        }
        for (int index : constantJumpTable) {
            switch (bytes[index]) {
                case CONSTANT_Class: {
                    String classUtf8 = null;
                    if (recursive) {
                        classUtf8 = getClassUtf8(bytes, constantJumpTable, index);
                        try {
                            final byte[] classBytes = getClass(classUtf8);
                            if (classBytes.length != 0)
                                getConstants(classBytes, getConstantJumpTable(classBytes), true, classes, methods, fields);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (classes == null) break;
                    if (classUtf8 == null) classUtf8 = getClassUtf8(bytes, constantJumpTable, index);
                    classes.add(classUtf8);
                    break;
                }
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref: {
                    if (fields == null && methods == null) break;
                    String utf8 = getReferenceUtf8(bytes, constantJumpTable, index);
                    if (bytes[index] == CONSTANT_Fieldref) {
                        if (fields != null) fields.add(utf8);
                    }
                    else if (methods != null) methods.add(utf8);
                }
            }
        }
    }

    public static byte[] getClass(String className) throws IOException {
        if (className.equals("java/lang/Object")) return new byte[0];
        InputStream is = ClassBytes.class.getClassLoader().getResourceAsStream(className + ".class");
        if (is == null) return new byte[0];
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return bytes;
    }

    private static String getClassUtf8(byte[] bytes, int[] constantJumpTable, int index) {
        if (bytes[index] != CONSTANT_Class) throw new IllegalStateException("Expected Class constant (" + CONSTANT_Class + ") but got " + bytes[index] + " instead");
        int name_index = getShort(bytes, index + 1);
        return getUtf8(bytes, constantJumpTable[name_index - 1]);
    }

    private static String getReferenceUtf8(byte[] bytes, int[] constantJumpTable, int index) {
        {
            int tag = bytes[index];
            if (tag != CONSTANT_Fieldref && tag != CONSTANT_Methodref && tag != CONSTANT_InterfaceMethodref) throw new IllegalStateException("Expected Reference constant but got " + tag + " instead");
        }
        int class_index = getShort(bytes, index + 1);
        int name_and_type_index = getShort(bytes, index + 3);
        String classUtf8, nameUtf8, typeUtf8;
        {
            int name_index = getShort(bytes, constantJumpTable[class_index] + 1);
            classUtf8 = getUtf8(bytes, constantJumpTable[name_index]);
        }
        {
            int name_index = getShort(bytes, constantJumpTable[name_and_type_index + 1]);
            int type_index = getShort(bytes, constantJumpTable[name_and_type_index + 3]);
            nameUtf8 = getUtf8(bytes, constantJumpTable[name_index]);
            typeUtf8 = getUtf8(bytes, constantJumpTable[type_index]);
        }
        return classUtf8 + ":" + nameUtf8 + ":" + typeUtf8;
    }

    private static String getUtf8(byte[] bytes, int index) {
        if (bytes[index] != CONSTANT_Utf8) throw new IllegalStateException("Expected Utf8 constant (" + CONSTANT_Utf8 + ") but got " + bytes[index] + " instead");
        int length = getShort(bytes, index + 1);
        byte[] b = new byte[length];
        System.arraycopy(bytes, index + 3, b, 0, length);
        return new String(b);
    }

    private static int constantSize(byte[] bytes, int index, int tag) {
        switch (tag) {
            case CONSTANT_Class: return 3;
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref: return 5;
            case CONSTANT_String: return 3;
            case CONSTANT_Integer:
            case CONSTANT_Float: return 5;
            case CONSTANT_Long:
            case CONSTANT_Double: return 9;
            case CONSTANT_NameAndType: return 5;
            case CONSTANT_Utf8: return 3 + getShort(bytes, index + 1);
            case CONSTANT_MethodHandle: return 4;
            case CONSTANT_MethodType: return 3;
            case CONSTANT_InvokeDynamic: return 5;
            default: throw new RuntimeException("None of the constant tags match with the tag " + tag);
        }
    }

    private static final int CONSTANT_Class = 7;
    private static final int CONSTANT_Fieldref = 9;
    private static final int CONSTANT_Methodref = 10;
    private static final int CONSTANT_InterfaceMethodref = 11;
    private static final int CONSTANT_String = 8;
    private static final int CONSTANT_Integer = 3;
    private static final int CONSTANT_Float = 4;
    private static final int CONSTANT_Long = 5;
    private static final int CONSTANT_Double = 6;
    private static final int CONSTANT_NameAndType = 12;
    private static final int CONSTANT_Utf8 = 1;
    private static final int CONSTANT_MethodHandle = 15;
    private static final int CONSTANT_MethodType = 16;
    private static final int CONSTANT_InvokeDynamic = 18;

    // Utility
    private static int getShort(byte[] bytes, int offset) { return (bytes[offset] & 0xFF) << 8 | (bytes[offset + 1] & 0xFF); }

    private static boolean checkBytes(byte[] bytes, int offset, byte[] toCheck) {
        if (toCheck.length + offset >= bytes.length) return false;
        for (int i = 0; i < toCheck.length; i++) {
            int offsetLen = offset + i;
            if (bytes[offsetLen] != toCheck[i]) return false;
        }
        return true;
    }
}
