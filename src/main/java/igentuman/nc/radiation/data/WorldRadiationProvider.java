package igentuman.nc.radiation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.CapabilityRegistry;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class WorldRadiationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static CapabilityRegistry<WorldRadiation> WORLD_RADIATION = new CapabilityRegistry<>(WorldRadiation::new);

    private WorldRadiation worldRadiation = createWorldRadiation();
    private final Supplier<WorldRadiation> opt = () -> createWorldRadiation();

    @Nonnull
    private WorldRadiation createWorldRadiation() {
        if (worldRadiation == null) {
            worldRadiation = new WorldRadiation();
        }
        return worldRadiation;
    }

//    @Nonnull
//    @Override
//    public <T> Supplier<T> getCapability(@Nonnull Capability<T> cap) {
//        if (cap == WORLD_RADIATION) {
//            return opt.cast();
//        }
//        return LazyOptional.empty();
//    }

//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        return getCapability(cap);
//    }


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return worldRadiation.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        worldRadiation.deserializeNBT(provider, compoundTag);
    }

    @Override
    public @Nullable Object getCapability(Object o, Object o2) {
        return null;
    }
}
