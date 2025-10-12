package surreal.backportium._internal.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public interface Sounds {
    SoundEvent register(String name);
    SoundEvent register(ResourceLocation name);
    <V extends SoundEvent> V register(V entry);
}
