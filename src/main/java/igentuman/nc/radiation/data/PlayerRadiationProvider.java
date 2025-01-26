package igentuman.nc.radiation.data;

import igentuman.nc.NuclearCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nonnull;

public class PlayerRadiationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static EntityCapability<PlayerRadiation, @Nullable Void> PLAYER_RADIATION = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(NuclearCraft.MODID, "player_radiation_cap"), PlayerRadiation.class);
    private PlayerRadiation playerRadiation = createPlayerRadiation();
//    private final IPlayerRadiationCapability opt = this::createPlayerRadiation; //TODO

    public static void setRadiation(Player pl, int i) {
        PlayerRadiation playerRadiationCap = pl.getCapability(PlayerRadiationProvider.PLAYER_RADIATION);
        if (playerRadiationCap != null) {
            playerRadiationCap.setRadiation(i);
        }
    }

    @Nonnull
    private PlayerRadiation createPlayerRadiation() {
        if (playerRadiation == null) {
            playerRadiation = new PlayerRadiation();
        }
        return playerRadiation;
    }

    @Override
    public @Nullable IPlayerRadiationCapability getCapability(Object cap, Object side) {
        if (cap == PLAYER_RADIATION) {
            return playerRadiation;
        }
        return null;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return playerRadiation.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        playerRadiation.deserializeNBT(provider, compoundTag);
    }
}
