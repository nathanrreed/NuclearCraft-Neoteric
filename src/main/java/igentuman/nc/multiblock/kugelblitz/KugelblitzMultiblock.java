package igentuman.nc.multiblock.kugelblitz;

import igentuman.nc.block.entity.kugelblitz.ChamberTerminalBE;
import igentuman.nc.multiblock.AbstractNCMultiblock;
import igentuman.nc.multiblock.MultiblockHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.List;

import static igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration.CASING_BLOCKS;
import static igentuman.nc.util.TagUtil.getBlocksByTagKey;

public class KugelblitzMultiblock extends AbstractNCMultiblock {

    protected KugelblitzMultiblock(List<Block> validOuterBlocks, List<Block> validInnerBlocks) {
        super(validOuterBlocks, validInnerBlocks);
    }

    public KugelblitzMultiblock(ChamberTerminalBE<?> be) {
        this(getBlocksByTagKey(CASING_BLOCKS.location().toString()), List.of());
        id = "chamber_" + be.getBlockPos().toShortString();
        MultiblockHandler.addMultiblock(this);
        controller = new KugelblitzController(be);
    }

    @Override
    protected void invalidateStats() {

    }

    @Override
    protected Direction getFacing() {
        return null;
    }
}
