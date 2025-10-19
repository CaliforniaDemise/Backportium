package surreal.backportium.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.entity.EntityState;

public class LivingMoveEvent extends LivingEvent {

    private final EntityState defaultMove;
    private EntityState newMove;

    public LivingMoveEvent(EntityLivingBase entity, @NotNull EntityState defaultMove) {
        super(entity);
        this.defaultMove = defaultMove;
        this.newMove = this.defaultMove;
    }

    @NotNull
    public EntityState getMove() {
        return defaultMove;
    }

    @NotNull
    public EntityState getNewMove() {
        return newMove;
    }

    public void setNewMove(@NotNull EntityState newMove) {
        this.newMove = newMove;
    }
}
