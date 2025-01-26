package igentuman.nc.block.turbine;

import igentuman.nc.block.MultiblockBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TurbineBlock extends MultiblockBlock {

    public TurbineBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    @Deprecated
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentBlockState, @NotNull Direction side) {
        return adjacentBlockState.getBlock().equals(this) && codeID().matches(".*glass.*|.*slope.*");
    }

    private String codeID() {
        return BuiltInRegistries.BLOCK.getKey(this).getPath();
    }
}
