package surreal.backportium._internal.core.visitor;

import _mod.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockSeaLantern;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBiomes;

import java.util.function.Function;

import static surreal.backportium.tag.AllTags.*;

public final class Tags {

    private static final String HOOKS = Constants.V_TAGS + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraftforge.registries.GameData": return GameData::new;
            case "net.minecraftforge.common.ForgeModContainer": return ForgeModContainer::new;
            default: return null;
        }
    }

    private static final class GameData extends LeClassVisitor {

        public GameData(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("init")) return new Init(mv);
            return mv;
        }

        private static final class Init extends MethodVisitor {

            private int count = 0;

            public Init(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKESTATIC && name.equals("makeRegistry")) {
                    count++;
                    if (count == 1) {
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "GameData$addAddCallbackBlock", "(Lnet/minecraftforge/registries/RegistryBuilder;)Lnet/minecraftforge/registries/RegistryBuilder;", false);
                    }
                    else if (count == 11) {
                        super.visitMethodInsn(INVOKESTATIC, HOOKS, "GameData$addAddCallbackEntityEntry", "(Lnet/minecraftforge/registries/RegistryBuilder;)Lnet/minecraftforge/registries/RegistryBuilder;", false);
                    }
                }
            }
        }
    }
    private static final class ForgeModContainer extends LeClassVisitor {

        public ForgeModContainer(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("registerAllBiomesAndGenerateEvents")) return new RegisterAllBiomesAndGenerateEvents(mv);
            return mv;
        }

        private static final class RegisterAllBiomesAndGenerateEvents extends MethodVisitor {

            public RegisterAllBiomesAndGenerateEvents(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == INVOKESTATIC && name.equals("ensureHasTypes")) {
                    super.visitVarInsn(ALOAD, 1);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ForgeModContainer$initBiome", "(Lnet/minecraft/world/biome/Biome;)V", false);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static RegistryBuilder<Block> GameData$addAddCallbackBlock(RegistryBuilder<Block> builder) {
            return builder.add((IForgeRegistry.AddCallback<Block>) (owner, stage, id, obj, oldObj) -> {
                obj.getBlockState().getValidStates().forEach(state -> {
                    if (state.getMaterial() == Material.CORAL) {
                        BLOCK_TAG.add(BLOCK_CAN_GROW_SEA_PICKLE, state);
                    }
                    if (obj instanceof BlockPrismarine || obj instanceof BlockSeaLantern) {
                        BLOCK_TAG.add(BLOCK_CONDUIT_BUILDING_BLOCKS, state);
                    }
                    if (state.getMaterial() == Material.SAND) {
                        BLOCK_TAG.add(BLOCK_CAN_HATCH_TURTLE_EGG, state);
                    }
                });
            });
        }

        public static RegistryBuilder<EntityEntry> GameData$addAddCallbackEntityEntry(RegistryBuilder<EntityEntry> builder) {
            return builder.add((IForgeRegistry.AddCallback<EntityEntry>) (owner, stage, id, obj, oldObj) -> {
                Class<? extends Entity> entityClass = obj.getEntityClass();
                if (EntityWaterMob.class.isAssignableFrom(entityClass) || EntityGuardian.class.isAssignableFrom(entityClass)) {
                    ENTITY_TAG.add(ENTITY_IMPALING_SENSITIVE, obj);
                }
                if (EntityMob.class.isAssignableFrom(entityClass)) {
                    ENTITY_TAG.add(ENTITY_CONDUIT_ATTACKS, obj);
                }
            });
        }

        public static void ForgeModContainer$initBiome(Biome biome) {
            boolean isOcean = BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN);
            boolean isRiver = BiomeDictionary.hasType(biome, BiomeDictionary.Type.RIVER);
            boolean isSwamp = BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP);
            boolean isFrozen = BiomeDictionary.hasType(biome, ModBiomes.FROZEN);
            boolean isCold = BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD);
            boolean isWarm = BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT);
            if (isOcean && isWarm) {
                BIOME_TAG.add(BIOME_GENERATION_WARM_VEGETATION, biome);
            }
            if (isOcean || isRiver || isSwamp) {
                if (!isFrozen) BIOME_TAG.add(BIOME_GENERATION_SEAGRASS, biome);
                if (!isCold && !isWarm) BIOME_TAG.add(BIOME_GENERATION_KELP, biome);
            }
            if (isOcean && isFrozen) {
                BIOME_TAG.add(BIOME_GENERATION_ICEBERG, biome);
                BIOME_TAG.add(BIOME_GENERATION_ICEBERG_BLUE, biome);
                BIOME_TAG.add(BIOME_GENERATION_BLUE_ICE, biome);
            }
        }

        private Hooks() {}
    }

    private Tags() {}
}
