package surreal.backportium._internal.core.visitor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import surreal.backportium._internal.block.BlockBubbleColumn;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.init.ModBlocks;
import surreal.backportium.init.ModSounds;
import surreal.backportium._internal.entity.RockableBoat;

import java.util.Random;
import java.util.function.Function;

import static _mod.Constants.*;

public final class BubbleColumns {

    private static final String HOOKS = V_BUBBLE_COLUMNS + "$Hooks";
    private static final String BUBBLE_COLUMN_INTERACTABLE = A_BUBBLE_COLUMN_INTERACTABLE;
    private static final String ROCKABLE_BOAT = A_ROCKABLE_BOAT;

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> visit(String name, String transformedName, byte[] bytes) {
        switch (transformedName) {
            case "net.minecraft.block.BlockSoulSand": return cv -> new BubbleColumnPlacerBlock(cv, true);
            case "net.minecraft.block.BlockMagma": return cv -> new BubbleColumnPlacerBlock(cv, false);
            case "net.minecraft.entity.Entity": return Entity::new;
            case "net.minecraft.client.entity.EntityPlayerSP": return EntityPlayerSP::new;
            case "net.minecraft.entity.projectile.EntityThrowable": return EntityThrowable::new;
            case "net.minecraft.entity.item.EntityBoat": return EntityBoat::new;
            case "net.minecraft.client.renderer.entity.RenderBoat": return RenderBoat::new;
            default: return null;
        }
    }

    private static final class BubbleColumnPlacerBlock extends LeClassVisitor {

        private final boolean upwards;

        public BubbleColumnPlacerBlock(ClassVisitor cv, boolean upwards) {
            super(cv);
            this.upwards = upwards;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(getName("neighborChanged", "func_189540_a"))) return null;
            if (name.equals(getName("onBlockAdded", "func_176213_c"))) return null;
            if (name.equals(getName("tickRate", "func_149738_a"))) return null;
            if (name.equals(getName("updateTick", "func_180650_b"))) return null;
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>")) return new Init(mv);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // neighborChanged
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("neighborChanged", "func_189540_a"), "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V", null, null);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$neighborChanged", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V" ,false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(6, 0);
            }
            { // onBlockAdded
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("onBlockAdded", "func_176213_c"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$scheduleUpdate", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V" ,false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 0);
            }
            { // tickRate
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("tickRate", "func_149738_a"), "(Lnet/minecraft/world/World;)I", null, null);
                mv.visitIntInsn(BIPUSH, 20);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(2, 0);
            }
            { // updateTick
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getName("updateTick", "func_180650_b"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", null, null);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(upwards ? ICONST_1 : ICONST_0);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$placeColumn", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(5, 0);
            }
        }

        private static final class Init extends MethodVisitor {

            public Init(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == ICONST_1) {
                    super.visitInsn(ICONST_0);
                    return;
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static final class Entity extends LeClassVisitor {

        public Entity(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            interfaces = getInterfaces(interfaces, BUBBLE_COLUMN_INTERACTABLE);
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }

    private static final class EntityPlayerSP extends LeClassVisitor {

        public EntityPlayerSP(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE, "inColumn", "I", null, 0);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnUpdate(mv);
            return mv;
        }

        private static final class OnUpdate extends MethodVisitor {

            public OnUpdate(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                final String mainClass = "net/minecraft/client/entity/EntityPlayerSP";
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, mainClass, "inColumn", "I");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$handleBubbleColumnClient", "(Lnet/minecraft/entity/Entity;I)I", false);
                    super.visitFieldInsn(PUTFIELD, mainClass, "inColumn", "I");
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static final class EntityThrowable extends LeClassVisitor {

        private boolean check = false;

        public EntityThrowable(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE | ACC_FINAL, "bp$newProjectile", "Z", null, false);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (!check && name.equals("<init>")) {
                check = true;
                return new Init(mv);
            }
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnUpdate(mv);
            return mv;
        }

        private static final class Init extends MethodVisitor {

            public Init(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityThrowable$isEligible", "(Lnet/minecraft/entity/Entity;)Z", false);
                    super.visitFieldInsn(PUTFIELD, "net/minecraft/entity/projectile/EntityThrowable", "bp$newProjectile", "Z");
                }
                super.visitInsn(opcode);
            }
        }

        private static final class OnUpdate extends MethodVisitor {

            public OnUpdate(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (name.equals(getName("rayTraceBlocks", "func_72933_a"))) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, "net/minecraft/entity/projectile/EntityThrowable", "bp$newProjectile", "Z");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "EntityThrowable$rayTraceThroughLiquid", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Z)Lnet/minecraft/util/math/RayTraceResult;", false);
                    return;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

            @Override
            public void visitCode() {
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, "net/minecraft/entity/projectile/EntityThrowable", "bp$newProjectile", "Z");
                Label l_con = new Label();
                super.visitJumpInsn(IFEQ, l_con);
                super.visitVarInsn(ALOAD, 0);
                super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/projectile/EntityThrowable", getName("doBlockCollisions", "func_145775_I"), "()V", false);
                super.visitLabel(l_con);
                super.visitFrame(F_SAME, 0, null, 0, null);
                super.visitCode();
            }
        }
    }

    private static final class EntityBoat extends LeClassVisitor {

        private String className;

        public EntityBoat(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            interfaces = getInterfaces(interfaces, ROCKABLE_BOAT);
            super.visit(version, access, name, signature, superName, interfaces);
            super.visitField(ACC_PRIVATE | ACC_STATIC, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;", null, null);
            super.visitField(ACC_PRIVATE, "bp$rocking", "I", null, 0); // 0 - None | 1 - Upwards | 2 - Downwards
            super.visitField(ACC_PRIVATE, "bp$rockingIntensity", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "bp$rockingAngle", "F", null, 0.0F);
            super.visitField(ACC_PRIVATE, "bp$prevRockingAngle", "F", null, 0.0F);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("entityInit", "func_70088_a"))) return new EntityInit(mv, this.className);
            if (name.equals(getName("onUpdate", "func_70071_h_"))) return new OnUpdate(mv);
            if (name.equals("<clinit>")) return new Clinit(mv, this.className);
            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            { // getRockingType
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getRockingType", "()I", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, "bp$rocking", "I");
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // getTicks
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getTicks", "()I", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETSTATIC, this.className, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;");
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$getRockingTicks", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/datasync/DataParameter;)I", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(1, 0);
            }
            { // getIntensity
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getIntensity", "()F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, "bp$rockingIntensity", "F");
                mv.visitInsn(FRETURN);
                mv.visitMaxs(1, 0);
            }
            { // getAngle
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getAngle", "()F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, "bp$rockingAngle", "F");
                mv.visitInsn(FRETURN);
                mv.visitMaxs(1, 0);
            }
            { // getPrevAngle
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "getPrevAngle", "()F", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, "bp$prevRockingAngle", "F");
                mv.visitInsn(FRETURN);
                mv.visitMaxs(1, 0);
            }
            { // setRocking
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setRocking", "(I)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitFieldInsn(PUTFIELD, this.className, "bp$rocking", "I");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // setTicks
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setTicks", "(I)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETSTATIC, this.className, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;");
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$setRockingTicks", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/datasync/DataParameter;I)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // setIntensity
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setIntensity", "(F)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(FLOAD, 1);
                mv.visitFieldInsn(PUTFIELD, this.className, "bp$rockingIntensity", "F");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // setAngle
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setAngle", "(F)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(FLOAD, 1);
                mv.visitFieldInsn(PUTFIELD, this.className, "bp$rockingAngle", "F");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // setPrevAngle
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "setPrevAngle", "(F)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(FLOAD, 1);
                mv.visitFieldInsn(PUTFIELD, this.className, "bp$prevRockingAngle", "F");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 0);
            }
            { // onBubbleColumn
                MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "onBubbleColumn", "(Lnet/minecraft/block/state/IBlockState;Z)V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, getName("rand", "field_70146_Z"), "Ljava/util/Random;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, this.className, getName("getSplashSound", "func_184181_aa"), "()Lnet/minecraft/util/SoundEvent;", false);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitMethodInsn(INVOKESTATIC, HOOKS, "$onBubbleColumn_EntityBoat", "(Lnet/minecraft/entity/Entity;Ljava/util/Random;Lnet/minecraft/util/SoundEvent;Z)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 0);
            }
        }

        private static final class EntityInit extends MethodVisitor {

            private final String className;

            public EntityInit(MethodVisitor mv, String className) {
                super(ASM5, mv);
                this.className = className;
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitFieldInsn(GETSTATIC, this.className, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;");
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$registerBoatData", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/datasync/DataParameter;)V", false);
                }
                super.visitInsn(opcode);
            }
        }

        private static final class OnUpdate extends MethodVisitor {

            private boolean check = false;

            public OnUpdate(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (!check && opcode == INVOKEVIRTUAL && name.equals(getName("doBlockCollisions", "func_145775_I"))) {
                    check = true;
                    super.visitVarInsn(ALOAD, 0);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$onUpdate_EntityBoat", "(Lnet/minecraft/entity/Entity;)V", false);
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private static final class Clinit extends MethodVisitor {

            private final String className;

            public Clinit(MethodVisitor mv, String className) {
                super(ASM5, mv);
                this.className = className;
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitLdcInsn(Type.getType("L" + this.className + ";"));
                    super.visitFieldInsn(GETSTATIC, "net/minecraft/network/datasync/DataSerializers", getName("VARINT", "field_187192_b"), "Lnet/minecraft/network/datasync/DataSerializer;");
                    super.visitMethodInsn(INVOKESTATIC, "net/minecraft/network/datasync/EntityDataManager", getName("createKey", "func_187226_a"), "(Ljava/lang/Class;Lnet/minecraft/network/datasync/DataSerializer;)Lnet/minecraft/network/datasync/DataParameter;", false);
                    super.visitFieldInsn(PUTSTATIC, this.className, "BOAT_ROCKING_TICKS", "Lnet/minecraft/network/datasync/DataParameter;");
                }
                super.visitInsn(opcode);
            }
        }
    }

    private static final class RenderBoat extends LeClassVisitor {

        public RenderBoat(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("setupRotation", "func_188311_a"))) return new SetupRotation(mv);
            return mv;
        }

        private static final class SetupRotation extends MethodVisitor {

            private boolean check = false;

            public SetupRotation(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitLdcInsn(Object cst) {
                if (!check && cst.equals(-1.0F)) {
                    check = true;
                    super.visitVarInsn(ALOAD, 1);
                    super.visitVarInsn(FLOAD, 3);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "$rockBoat", "(Lnet/minecraft/entity/Entity;F)V", false);
                }
                super.visitLdcInsn(cst);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static void $neighborChanged(World world, BlockPos pos, Block block, BlockPos fromPos) {
            if (!world.provider.doesWaterVaporize() && fromPos.getY() > pos.getY() && world.getBlockState(fromPos).getMaterial() == Material.WATER) {
                world.scheduleUpdate(pos, block, block.tickRate(world));
            }
        }

        public static void $scheduleUpdate(World world, BlockPos pos, Block block) {
            if (!world.provider.doesWaterVaporize() && world.getBlockState(pos.up()).getMaterial() == Material.WATER) {
                world.scheduleUpdate(pos, block, block.tickRate(world));
            }
        }

        public static void $placeColumn(World world, BlockPos belowPos, boolean upwards) {
            BlockPos offset = belowPos.up();
            world.setBlockState(offset, ModBlocks.BUBBLE_COLUMN.getDefaultState().withProperty(BlockBubbleColumn.DRAG, upwards), Constants.BlockFlags.SEND_TO_CLIENTS);
        }

        public static int $getRockingTicks(net.minecraft.entity.Entity entity, DataParameter<Integer> parameter) { return entity.getDataManager().get(parameter); }
        public static void $setRockingTicks(net.minecraft.entity.Entity entity, DataParameter<Integer> parameter, int value) { entity.getDataManager().set(parameter, value); }

        public static void $onBubbleColumn_EntityBoat(net.minecraft.entity.Entity entity, Random entityRand, SoundEvent splashSound, boolean downwards) {
            if (!entity.world.isRemote) {
                RockableBoat rockable = (RockableBoat) entity;
                rockable.setRocking(downwards ? 2 : 1);
                if (rockable.getTicks() == 0) rockable.setTicks(60);
            }
            entity.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, entity.posX + (double) entityRand.nextFloat(), entity.posY + 0.7, entity.posZ + (double) entityRand.nextFloat(), 0.0, 0.0, 0.0);
            if (entityRand.nextInt(20) == 0) {
                entity.world.playSound(entity.posX, entity.posY, entity.posZ, splashSound, entity.getSoundCategory(), 1.0F, 0.8F + 0.4F * entityRand.nextFloat(), false);
            }
        }

        public static void $registerBoatData(net.minecraft.entity.Entity entity, DataParameter<Integer> parameter) {
            entity.getDataManager().register(parameter, 0);
        }

        public static void $onUpdate_EntityBoat(net.minecraft.entity.Entity entity) {
            RockableBoat rockable = (RockableBoat) entity;
            if (entity.world.isRemote) {
                int i = rockable.getTicks();
                if (i > 0) rockable.setIntensity(rockable.getIntensity() + 0.05F);
                else rockable.setIntensity(rockable.getIntensity() - 0.1F);
                rockable.setIntensity(MathHelper.clamp(rockable.getIntensity(), 0.0F, 1.0F));
                rockable.setPrevAngle(rockable.getAngle());
                rockable.setAngle(10.0F * (float) Math.sin(0.5F * (float) entity.world.getTotalWorldTime()) * rockable.getIntensity());
            }
            else {
                if (rockable.getRockingType() == 0) rockable.setTicks(0);
                int k = rockable.getTicks();
                if (k > 0) {
                    --k;
                    rockable.setTicks(k);
                    int j = 60 - k - 1;
                    if (j > 0 && k == 0) {
                        rockable.setTicks(0);
                        if (rockable.getRockingType() == 2) {
                            entity.motionY -= 0.7D;
                            entity.removePassengers();
                        }
                        else entity.motionY = isPlayerRiding(entity) ? 2.7D : 0.6D;
                    }
                    rockable.setRocking(0);
                }
            }
        }

        @SideOnly(Side.CLIENT)
        public static void $rockBoat(net.minecraft.entity.Entity entity, float partialTicks) {
            RockableBoat rockable = (RockableBoat) entity;
            float angle = rockable.getPrevAngle() + (rockable.getAngle() - rockable.getPrevAngle()) * partialTicks;
            if (!MathHelper.epsilonEquals(angle, 0.0F)) GlStateManager.rotate(angle, 1.0F, 0.0F, 1.0F);
        }

        @SideOnly(Side.CLIENT)
        public static int $handleBubbleColumnClient(net.minecraft.entity.Entity entity, int inColumn) {
            World world = entity.world;
            BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == ModBlocks.BUBBLE_COLUMN) {
                if (state.getValue(BlockBubbleColumn.DRAG)) {
                    if (inColumn != 1) {
                        world.playSound(entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, SoundCategory.BLOCKS, 0.7F, 1.0F, false);
                        return 1;
                    }
                }
                else {
                    if (inColumn != 2) {
                        world.playSound(entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundCategory.BLOCKS, 0.7F, 1.0F, false);
                        return 2;
                    }
                }
            }
            else return 0;
            return inColumn;
        }

        // TODO A config maybe?
        public static boolean EntityThrowable$isEligible(net.minecraft.entity.Entity entity) {
            return entity.getClass().getName().startsWith("net.minecraft.");
        }

        public static RayTraceResult EntityThrowable$rayTraceThroughLiquid(World world, Vec3d start, Vec3d end, boolean isEligible) {
            if (isEligible) return world.rayTraceBlocks(start, end, false, true, false);
            return world.rayTraceBlocks(start, end);
        }

        private static boolean isPlayerRiding(net.minecraft.entity.Entity entity) {
            for(net.minecraft.entity.Entity passenger : entity.getPassengers()) {
                if (EntityPlayer.class.isAssignableFrom(passenger.getClass())) {
                    return true;
                }
            }
            return false;
        }

        private Hooks() {}
    }

    private BubbleColumns() {}
}
