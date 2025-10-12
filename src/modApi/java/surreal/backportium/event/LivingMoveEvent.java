package surreal.backportium.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.api.entity.EntityState;

public class LivingMoveEvent extends LivingEvent {

    private final EntityState defaultMove;
    private EntityState newMove;

    public LivingMoveEvent(EntityLivingBase entity, @Nullable EntityState defaultMove) {
        super(entity);
        this.defaultMove = defaultMove;
        this.newMove = this.defaultMove;
    }

    @Nullable
    public EntityState getMove() {
        return defaultMove;
    }

    @Nullable
    public EntityState getNewMove() {
        return newMove;
    }

    public void setNewMove(@Nullable EntityState newMove) {
        this.newMove = newMove;
    }
}
