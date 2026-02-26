package com.backportium.registry;

import com.backportium.Tags;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class PotionTypeBuilder {

    private final ResourceLocation location;
    private final List<PotionEffect> effects = new ArrayList<>();

    PotionTypeBuilder(ResourceLocation location) {
        this.location = location;
    }

    PotionTypeBuilder(String name) {
        this(new ResourceLocation(Tags.MOD_ID, name));
    }

    public PotionTypeBuilder add(Potion potion, int duration, int amplifier) {
        effects.add(new PotionEffect(potion, duration, amplifier));
        return this;
    }

    public PotionTypeBuilder add(Potion potion, int duration) {
        return add(potion, duration, 0);
    }

    public PotionTypeBuilder add(Potion potion) {
        return add(potion, 0);
    }

    public PotionType build() {
        PotionType type = new PotionType(location.getNamespace() + "_" + location.getPath(), effects.toArray(new PotionEffect[0]));
        type.setRegistryName(location);
        return type;
    }
}
