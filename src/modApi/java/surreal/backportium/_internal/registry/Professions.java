package surreal.backportium._internal.registry;

import net.minecraftforge.fml.common.registry.VillagerRegistry;

public interface Professions {
    <V extends VillagerRegistry.VillagerProfession> V register(V entry);
}
