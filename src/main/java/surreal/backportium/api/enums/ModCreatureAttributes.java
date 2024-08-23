package surreal.backportium.api.enums;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraftforge.common.util.EnumHelper;

public class ModCreatureAttributes {

    public static final EnumCreatureAttribute
            AQUATIC;

    static {
        AQUATIC = EnumHelper.addCreatureAttribute("aquatic");
    }
}
