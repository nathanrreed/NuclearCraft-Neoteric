package igentuman.nc.block.entity.kugelblitz;

import igentuman.nc.block.entity.NuclearCraftBE;
import igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlackHoleBE extends NuclearCraftBE {
    public static String NAME = "black_hole";

    public BlackHoleBE(BlockPos pPos, BlockState pBlockState) {
        super(KugelblitzRegistration.KUGELBLITZ_BE.get(NAME).get(), pPos, pBlockState);
    }

    public void tickClient() {
    }

    public void tickServer() {
    }
}
