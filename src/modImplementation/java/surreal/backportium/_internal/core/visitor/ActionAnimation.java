package surreal.backportium._internal.core.visitor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import surreal.backportium._internal.bytecode.asm.LeClassVisitor;
import surreal.backportium.api.item.UseAction;
import surreal.backportium.init.ModActions;

import java.util.function.Function;

import static _mod.Constants.V_ACTION_ANIMATION;

public final class ActionAnimation {

    private static final String HOOKS = V_ACTION_ANIMATION + "$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        if (transformedName.equals("net.minecraft.client.model.ModelBiped")) return ModelBiped::new;
        return null;
    }

    public static final class ModelBiped extends LeClassVisitor {

        public ModelBiped(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("setRotationAngles", "func_78087_a"))) return new SetRotationAngles(mv);
            return mv;
        }

        private static final class SetRotationAngles extends MethodVisitor {

            public SetRotationAngles(MethodVisitor mv) {
                super(ASM5, mv);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == RETURN) {
                    super.visitVarInsn(ALOAD, 0);
                    super.visitVarInsn(FLOAD, 1);
                    super.visitVarInsn(FLOAD, 2);
                    super.visitVarInsn(FLOAD, 3);
                    super.visitVarInsn(FLOAD, 4);
                    super.visitVarInsn(FLOAD, 5);
                    super.visitVarInsn(FLOAD, 6);
                    super.visitVarInsn(ALOAD, 7);
                    super.visitMethodInsn(INVOKESTATIC, HOOKS, "ModelBiped$setRotationAngles", "(Lnet/minecraft/client/model/ModelBiped;FFFFFFLnet/minecraft/entity/Entity;)V", false);
                }
                super.visitInsn(opcode);
            }
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        @SideOnly(Side.CLIENT)
        public static void ModelBiped$setRotationAngles(net.minecraft.client.model.ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
            if (entityIn instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entityIn;
                ItemStack stack = living.getActiveItemStack();
                if (stack.isEmpty()) return;
                UseAction action = ModActions.getUseAction(stack);
                if (action != null) {
                    EnumHand hand = living.getActiveHand();
                    action.setRotationAngles(stack, hand, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn, (hand == EnumHand.MAIN_HAND ? living.getPrimaryHand() : living.getPrimaryHand().opposite()) == EnumHandSide.RIGHT);
                }
            }
        }

        private Hooks() {}
    }

    private ActionAnimation() {}
}
