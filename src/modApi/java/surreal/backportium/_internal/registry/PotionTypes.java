package surreal.backportium._internal.registry;

import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface PotionTypes {
    PotionType register(String name, Consumer<PotionTypeBuilder> consumer);
    PotionType register(ResourceLocation name, Consumer<PotionTypeBuilder> consumer);
}
