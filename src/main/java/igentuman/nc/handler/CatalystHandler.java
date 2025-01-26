package igentuman.nc.handler;

import igentuman.nc.block.entity.processor.NCProcessorBE;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CatalystHandler extends ItemStackHandler {
    protected NCProcessorBE<?> be;
    public boolean wasUpdated = true;

    public CatalystHandler(NCProcessorBE<?> be) {
        super(1);
        this.be = be;
    }

    @Override
    protected void onContentsChanged(int slot) {
        wasUpdated = true;
        be.setChanged();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        for (Object item : be.getAllowedCatalysts()) {
            if (stack.getItem().equals(item)) {
                return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("is_nc_analyzed");
            }
        }
        return false;
    }
}