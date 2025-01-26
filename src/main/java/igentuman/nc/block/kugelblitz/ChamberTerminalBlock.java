package igentuman.nc.block.kugelblitz;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import igentuman.nc.block.entity.kugelblitz.ChamberTerminalBE;
import igentuman.nc.block.fission.FissionControllerBlock;
import igentuman.nc.container.ChamberTerminalContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration.KUGELBLITZ_BE;

public class ChamberTerminalBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty HORIZONTAL_FACING = FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final String NAME = "chamber_terminal";

    public ChamberTerminalBlock() {
        this(Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f)
                .requiresCorrectToolForDrops());
    }

    public ChamberTerminalBlock(Properties pProperties) {
        super(pProperties.sound(SoundType.METAL));
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(HORIZONTAL_FACING, Direction.NORTH)
                        .setValue(POWERED, false)
        );
    }

    public static final MapCodec<ChamberTerminalBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(propertiesCodec()).apply(instance, ChamberTerminalBlock::new)
    );

    @Override
    protected MapCodec<? extends ChamberTerminalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
                .add(BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return KUGELBLITZ_BE.get(NAME).get().create(pPos, pState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof ChamberTerminalBE<?>) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable(NAME);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                        return new ChamberTerminalContainer(windowId, pos, playerInventory);
                    }
                };
                player.openMenu(containerProvider, be.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof ChamberTerminalBE<?> tile) {
                    tile.tickClient();
                    level.setBlock(pos, blockState.setValue(POWERED, tile.powered), 3);
                }
            };
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof ChamberTerminalBE<?> tile) {
                tile.tickServer();
            }
        };
    }
}
