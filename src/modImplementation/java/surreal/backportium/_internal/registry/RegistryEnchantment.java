package surreal.backportium._internal.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public class RegistryEnchantment extends Registry<Enchantment> implements Enchantments {

    protected RegistryEnchantment(RegistryManager manager) {
        super(manager);
    }

    @Override
    public <V extends Enchantment> V register(V entry, String name) {
        return this.register(entry, new ResourceLocation(this.manager.getModId(), name));
    }

    @Override
    public <V extends Enchantment> V register(V entry, ResourceLocation name) {
        entry.setName(name.getNamespace() + "." + name.getPath()).setRegistryName(name);
        return this.register(entry);
    }
}
