package surreal.backportium._internal.registry;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class RegistryPotion extends Registry<Potion> implements Potions {

    protected RegistryPotion(RegistryManager manager) {
        super(manager);
    }

    @Override
    public <V extends Potion> V register(V entry, String name) {
        return this.register(entry, new ResourceLocation(this.manager.getModId(), name));
    }

    @Override
    public <V extends Potion> V register(V entry, ResourceLocation name) {
        entry.setPotionName(name.getNamespace() + "." + name.getPath()).setRegistryName(name);
        return this.register(entry);
    }
}
