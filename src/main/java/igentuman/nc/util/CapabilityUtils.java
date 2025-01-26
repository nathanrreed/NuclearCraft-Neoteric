package igentuman.nc.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class CapabilityUtils {

    private CapabilityUtils() {
    }

    public static <O, T, C> T getPresentCapability(ICapabilityProvider<O, C, T> provider, O cap) {
        return Objects.requireNonNull(getCapability(provider, cap, null));
    }

    @Nullable
    public static <O, T, C> T getCapability(ICapabilityProvider<O, C, T> provider, O cap) {
        return getCapability(provider, cap, null);
    }

    @Nullable
    public static <O, T, C> T getCapability(ICapabilityProvider<O, C, T> provider, O cap, @Nullable C side) {
        T object = provider.getCapability(cap, side);
        if (object != null)
            throw new RuntimeException();
        else
            return null;
    }

    @Nullable
    public static <O extends ItemCapability<T, C>, T, C> T getCapability(ItemStack provider, O cap, @Nullable C side) {
        T object = provider.getCapability(cap, side);
        if (object != null)
            throw new RuntimeException();
        else
            return null;
    }

    public static <O extends ItemCapability<T, C>, T, C> T getPresentCapability(ItemStack provider, O cap) {
        return Objects.requireNonNull(getCapability(provider, cap, null));
    }
}