package surreal.backportium._internal.registry;

import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class RegistryPotionType extends Registry<PotionType> implements PotionTypes {

    protected RegistryPotionType(RegistryManager manager) {
        super(manager);
    }

    @Override
    public PotionType register(String name, Consumer<PotionTypeBuilder> consumer) {
        return register(new ResourceLocation(manager.getModId(), name), consumer);
    }

    @Override
    public PotionType register(ResourceLocation name, Consumer<PotionTypeBuilder> consumer) {
        PotionTypeBuilder builder = PotionTypeBuilder.create();
        consumer.accept(builder);
        return builder.build().setRegistryName(name);
    }

    public static class Builder {


    }
}
