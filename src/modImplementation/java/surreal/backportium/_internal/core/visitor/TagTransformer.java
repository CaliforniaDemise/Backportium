package surreal.backportium._internal.core.visitor;

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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBiomes;

import java.util.function.Function;

import static surreal.backportium.tag.AllTags.*;

public final class TagTransformer {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/TagTransformer$Hooks";

    public static Function<ClassVisitor, ClassVisitor> getVisitor(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraftforge.registries.GameData": return GameDataVisitor::new;
            case "net.minecraftforge.common.ForgeModContainer": return ForgeModContainerVisitor::new;
        }
        return null;
    }

    private static class GameDataVisitor extends LeClassVisitor {

        public GameDataVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("init")) return new InitVisitor(mv);
            return mv;
        }

        private static class InitVisitor extends MethodVisitor {

            private int count = 0;

            public InitVisitor(MethodVisitor mv) {
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
    private static class ForgeModContainerVisitor extends LeClassVisitor {

        public ForgeModContainerVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("registerAllBiomesAndGenerateEvents")) return new RegisterAllBiomesAndGenerateEvents(mv);
            return mv;
        }

        private static class RegisterAllBiomesAndGenerateEvents extends MethodVisitor {

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
    public static class Hooks {

        public static RegistryBuilder<Block> GameData$addAddCallbackBlock(RegistryBuilder<Block> builder) {
            return builder.add((IForgeRegistry.AddCallback<Block>) (owner, stage, id, obj, oldObj) -> {
                obj.getBlockState().getValidStates().forEach(state -> {
                    if (state.getMaterial() == Material.CORAL) {
                        BLOCK_TAG.add(BLOCK_SEA_PICKLE_GROWABLE, state);
                    }
                    if (obj instanceof BlockPrismarine || obj instanceof BlockSeaLantern) {
                        BLOCK_TAG.add(BLOCK_CONDUIT_BUILDING_BLOCKS, state);
                    }
                    if (state.getMaterial() == Material.SAND) {
                        BLOCK_TAG.add(BLOCK_TURTLE_EGG_HATCHABLE, state);
                    }
                });
            });
        }

        public static RegistryBuilder<EntityEntry> GameData$addAddCallbackEntityEntry(RegistryBuilder<EntityEntry> builder) {
            return builder.add((IForgeRegistry.AddCallback<EntityEntry>) (owner, stage, id, obj, oldObj) -> {
                Class<? extends Entity> entityClass = obj.getEntityClass();
                if (EntityWaterMob.class.isAssignableFrom(entityClass) || EntityGuardian.class.isAssignableFrom(entityClass)) {
                    ENTITY_TAG.add(ENTITY_IMPALING_WHITELIST, obj);
                }
                if (EntityMob.class.isAssignableFrom(entityClass)) {
                    ENTITY_TAG.add(ENTITY_BLOCK_CONDUIT_ATTACK, obj);
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
    }

    private TagTransformer() {}
}
