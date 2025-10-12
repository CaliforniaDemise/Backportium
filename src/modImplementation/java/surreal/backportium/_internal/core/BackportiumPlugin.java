package surreal.backportium._internal.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.Tags;
import surreal.backportium._internal.bytecode.traverse.ClassTraverser;

import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(Tags.MOD_NAME)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-100)
public final class BackportiumPlugin implements IFMLLoadingPlugin {

//    public static final boolean ASSETMOVER;
    public static final boolean FLUIDLOGGED;
    public static final boolean ROUGHLY_ENOUGH_IDS; // TODO Check for JEID

    public BackportiumPlugin() {
        ClassTraverser traverser = new ClassTraverser();
    }

    @Override
    public String getAccessTransformerClass() {
        return "surreal.backportium._internal.core.BackportiumTransformer";
    }

    @Override public String[] getASMTransformerClass() { return null; }
    @Override public String getModContainerClass() { return null; }
    @Override public @Nullable String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}

    private static boolean isLoaded(String className) {
        try {
            Class<?> cls = Launch.classLoader.findClass(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    static {
//        ASSETMOVER = isLoaded("com.cleanroommc.assetmover.AssetMoverCore");
        FLUIDLOGGED = isLoaded("git.jbredwards.fluidlogged_api.mod.asm.ASMHandler");
        ROUGHLY_ENOUGH_IDS = isLoaded("org.dimdev.jeid.core.JEIDLoadingPlugin");
    }
}
