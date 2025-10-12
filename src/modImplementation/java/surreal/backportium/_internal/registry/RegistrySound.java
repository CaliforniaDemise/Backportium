package surreal.backportium._internal.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class RegistrySound extends Registry<SoundEvent> implements Sounds {

    protected RegistrySound(RegistryManager manager) {
        super(manager);
    }

    @Override
    public SoundEvent register(String name) {
        return this.register(new ResourceLocation(this.manager.getModId(), name));
    }

    @Override
    public SoundEvent register(ResourceLocation name) {
        return this.register(new SoundEvent(name).setRegistryName(name));
    }
}
