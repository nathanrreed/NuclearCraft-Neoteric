package igentuman.nc.util.insitu_leaching;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.CapabilityRegistry;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nonnull;
import java.security.DrbgParameters;
import java.util.function.Supplier;

public class WorldVeinsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static CapabilityRegistry<WorldVeinOres> VEINS_CAP = new CapabilityRegistry<>(WorldVeinOres::new);
    private WorldVeinOres veinsData = createVeinData();
    private final Supplier<WorldVeinOres> opt = this::createVeinData;

    @Nonnull
    private WorldVeinOres createVeinData() {
        if (veinsData == null) {
            veinsData = new WorldVeinOres();
        }
        return veinsData;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return veinsData.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        veinsData.deserializeNBT(provider, compoundTag);
    }

    @Override
    public @Nullable Object getCapability(Object cap, Object o2) {
        if (cap == VEINS_CAP) {
            return opt;
        }
        return null;
    }
}
