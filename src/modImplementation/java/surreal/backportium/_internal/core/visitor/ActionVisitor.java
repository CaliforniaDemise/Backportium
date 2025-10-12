package surreal.backportium._internal.core.visitor;

import net.minecraft.client.model.ModelBiped;
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

public final class ActionVisitor {

    private static final String HOOKS = "surreal/backportium/_internal/core/visitor/ActionVisitor$Hooks";

    @Nullable
    public static Function<ClassVisitor, ClassVisitor> getClassVisitor(String name, String transformedName) {
        if (transformedName.equals("net.minecraft.client.model.ModelBiped")) return ModelBipedVisitor::new;
        return null;
    }

    public static class ModelBipedVisitor extends LeClassVisitor {

        public ModelBipedVisitor(ClassVisitor cv) {
            super(cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals(getName("setRotationAngles", "func_78087_a"))) return new SetRotationAnglesVisitor(mv);
            return mv;
        }

        private static class SetRotationAnglesVisitor extends MethodVisitor {

            public SetRotationAnglesVisitor(MethodVisitor mv) {
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
    public static class Hooks {

        @SideOnly(Side.CLIENT)
        public static void ModelBiped$setRotationAngles(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
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


    }

    private ActionVisitor() {}
}
