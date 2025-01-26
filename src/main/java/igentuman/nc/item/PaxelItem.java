package igentuman.nc.item;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import static igentuman.nc.setup.registration.Tags.MINEABLE_WITH_PAXEL;

public class PaxelItem extends DiggerItem {
    public PaxelItem(float pAttackDamageModifier, float pAttackSpeedModifier, Tier pTier, Item.Properties pProperties) {
        super(pTier, MINEABLE_WITH_PAXEL, pProperties.attributes(createAttributes(pTier, pAttackDamageModifier, pAttackSpeedModifier)));
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return this.getTier().getSpeed();
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return super.isCorrectToolForDrops(stack, state);
    }

    //    public boolean isCorrectToolForDrops(BlockState pBlock) { TODO readd
//        TierSortingRegistry
//        if (TierSortingRegistry.isTierSorted(getTier())) {
//            return net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getTier(), pBlock);
//        }
//        int i = this.getTier().getLevel();
//        if (i < 3 && pBlock.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
//            return false;
//        } else if (i < 2 && pBlock.is(BlockTags.NEEDS_IRON_TOOL)) {
//            return false;
//        } else {
//            return i < 1 && pBlock.is(BlockTags.NEEDS_STONE_TOOL) ? false : true;
//        }
//    }
//
//    @Override
//    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
//        return net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
//    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return true;
    }
}