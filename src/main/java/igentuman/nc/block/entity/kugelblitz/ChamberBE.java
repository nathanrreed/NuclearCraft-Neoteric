package igentuman.nc.block.entity.kugelblitz;

import igentuman.nc.block.entity.NuclearCraftBE;
import igentuman.nc.multiblock.AbstractNCMultiblock;
import igentuman.nc.multiblock.IMultiblockAttachable;
import igentuman.nc.multiblock.kugelblitz.KugelblitzMultiblock;
import igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChamberBE extends NuclearCraftBE implements IMultiblockAttachable {

    @Override
    public void setMultiblock(AbstractNCMultiblock multiblock) {
        this.multiblock = (KugelblitzMultiblock) multiblock;
    }

    @Override
    public ChamberTerminalBE<?> controller() {
        try {
            return (ChamberTerminalBE<?>) multiblock().controller().controllerBE();
        } catch (NullPointerException ignore) {
            return null;
        }
    }

    public KugelblitzMultiblock multiblock() {
        return multiblock;
    }

    @Override
    public boolean canInvalidateCache() {
        return true;
    }

    protected KugelblitzMultiblock multiblock;

    public static String NAME;
    public boolean refreshCacheFlag = true;

    public byte validationRuns = 0;

    public ChamberTerminalBE<?> controller;

    public ChamberBE(BlockPos pPos, BlockState pBlockState, String name) {
        super(KugelblitzRegistration.KUGELBLITZ_BE.get(name).get(), pPos, pBlockState);
    }

    public void invalidateCache() {
        refreshCacheFlag = true;
        validationRuns = 0;
    }

    public void tickClient() {
    }

    public void tickServer() {

    }

    @Override
    public void setRemoved() {
        if (controller() != null) controller().invalidateCache();
        super.setRemoved();
    }

    public boolean isValidating = false;

    public void onNeighborChange(BlockState state, BlockPos pos, BlockPos neighbor) {
        if (multiblock() != null) {
            multiblock().onNeighborChange(state, pos, neighbor);
        }
    }
}
