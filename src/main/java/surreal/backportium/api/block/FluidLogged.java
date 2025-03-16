package surreal.backportium.api.block;

import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Optional;
import surreal.backportium.util.IntegrationHelper;

// TODO Fix issues with other liquids that use WATER material.
// TODO Implement waterlogging and apply IFluidloggable interface with transformers.
@Optional.Interface(modid = IntegrationHelper.FLUIDLOGGED_MODID, iface = "git.jbredwards.fluidlogged_api.api.block.IFluidloggable")
public interface FluidLogged extends IFluidloggable {

    // Used when Fluidlogged API isn't present
    default Fluid getFluid() {
        return FluidRegistry.WATER;
    }
}
