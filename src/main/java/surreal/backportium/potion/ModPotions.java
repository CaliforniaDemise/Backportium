package surreal.backportium.potion;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.potion.v1_13.PotionConduitPower;
import surreal.backportium.potion.v1_13.PotionSlowFalling;
import surreal.backportium.util.Registrar;

public class ModPotions extends Registrar<Potion> {

    private final ModPotionTypes types = new ModPotionTypes(8);

    @ObjectHolder("backportium:slow_falling") public static final PotionSlowFalling SLOW_FALLING = null;
    @ObjectHolder("backportium:conduit_power") public static final PotionConduitPower CONDUIT_POWER = null;

    @ObjectHolder("backportium:slow_falling") public static final PotionType SLOW_FALLING_TYPE = null;
    @ObjectHolder("backportium:long_slow_falling") public static final PotionType LONG_SLOW_FALLING_TYPE = null;

    @ObjectHolder("backportium:turtle_master") public static final PotionType TURTLE_MASTER_TYPE = null;
    @ObjectHolder("backportium:long_turtle_master") public static final PotionType LONG_TURTLE_MASTER_TYPE = null;
    @ObjectHolder("backportium:strong_turtle_master") public static final PotionType STRONG_TURTLE_MASTER_TYPE = null;

    public ModPotions() {
        super(4);
        this.register();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.types.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        this.types.init(event);
    }

    public void registerTypeEntries(RegistryEvent.Register<PotionType> event) {
        this.types.registerEntries(event);
    }

    @Override
    protected Potion register(@NotNull Potion entry, @NotNull ResourceLocation location) {
        return super.register(entry, location).setRegistryName(location).setPotionName(location.getPath());
    }

    private void register() {
        Potion slowFalling = this.register(new PotionSlowFalling(false, 0xF3CFB9), "slow_falling");
        this.register(new PotionConduitPower(false, 0x1DC2D1), "conduit_power");

        this.types.register(new PotionType(new PotionEffect(slowFalling, 1800, 0)), "slow_falling");
        this.types.register(new PotionType(new PotionEffect(slowFalling, 4800, 0)), "long_slow_falling");

        this.types.register(new PotionType(new PotionEffect(MobEffects.SLOWNESS, 1200, 3), new PotionEffect(MobEffects.STRENGTH, 1200, 2)), "turtle_master");
        this.types.register(new PotionType(new PotionEffect(MobEffects.SLOWNESS, 3600, 3), new PotionEffect(MobEffects.STRENGTH, 3600, 2)), "long_turtle_master");
        this.types.register(new PotionType(new PotionEffect(MobEffects.SLOWNESS, 1200, 5), new PotionEffect(MobEffects.STRENGTH, 1200, 3)), "strong_turtle_master");
    }

    protected static class ModPotionTypes extends Registrar<PotionType> {

        public ModPotionTypes(int size) {
            super(size);
        }

        @Override
        protected PotionType register(@NotNull PotionType entry, @NotNull String path) {
            return super.register(entry, path);
        }

        @Override
        protected PotionType register(@NotNull PotionType entry, @NotNull ResourceLocation location) {
            return super.register(entry, location).setRegistryName(location);
        }
    }
}
