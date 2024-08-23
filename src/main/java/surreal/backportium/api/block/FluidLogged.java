package surreal.backportium.api.block;

import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Optional;
import surreal.backportium.util.IntegrationHelper;

@Optional.Interface(modid = IntegrationHelper.FLUIDLOGGED, iface = "git.jbredwards.fluidlogged_api.api.block.IFluidloggable")
public interface FluidLogged extends IFluidloggable {

    // Used when Fluidlogged API isn't present
    default Fluid getFluid() {
        return FluidRegistry.WATER;
    }
}
