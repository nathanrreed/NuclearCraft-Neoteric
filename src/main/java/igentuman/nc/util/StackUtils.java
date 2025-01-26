package igentuman.nc.util;

import com.lowdragmc.lowdraglib.misc.ItemHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.setup.registration.NCItems.MULTITOOL;

public final class StackUtils {

    private StackUtils() {
    }

    public static ItemStack size(ItemStack stack, int size) {
        if (size <= 0 || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Nullable
    public static BlockState getStateForPlacement(ItemStack stack, BlockPos pos, Player player) {
        return Block.byItem(stack.getItem()).getStateForPlacement(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false))));
    }


    public static Item getItemByRegistryName(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static final List<Item> allowedTools = new ArrayList<>();

    public static boolean isMultiTool(ItemStack stack) {
        if (allowedTools.isEmpty()) {
            allowedTools.add(MULTITOOL.get());
            Item wrench = getItemByRegistryName("rftoolsbase:smartwrench");
            Item configurator = getItemByRegistryName("mekanism:configurator");
            Item thermal = getItemByRegistryName("thermalfoundation:wrench");
            Item hammer = getItemByRegistryName("immersiveengineering:hammer");
            Item enderIoWrench = getItemByRegistryName("enderio:item_yeta_wrench");
            if (!(wrench instanceof AirItem)) {
                allowedTools.add(wrench);
            }
            if (!(configurator instanceof AirItem)) {
                allowedTools.add(configurator);
            }
            if (!(thermal instanceof AirItem)) {
                allowedTools.add(thermal);
            }
            if (!(hammer instanceof AirItem)) {
                allowedTools.add(hammer);
            }
            if (!(enderIoWrench instanceof AirItem)) {
                allowedTools.add(enderIoWrench);
            }
        }
        return allowedTools.contains(stack.getItem());
    }
}