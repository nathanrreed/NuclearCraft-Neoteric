package igentuman.nc.util;

import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.IFluidTank;

import java.util.List;

@NothingNullByDefault
public class DataHandlerUtils {

    private DataHandlerUtils() {
    }

    /**
     * Helper to read and load a list of containers from a {@link ListTag}
     */
    public static void readContainers(List<? extends INBTSerializable<CompoundTag>> containers, ListTag storedContainers, HolderLookup.Provider registries) {
        readContents(containers, storedContainers, getTagByType(containers), registries);
    }

    /**
     * Helper to read and load a list of containers to a {@link ListTag}
     */
    public static ListTag writeContainers(List<? extends INBTSerializable<CompoundTag>> containers, HolderLookup.Provider registries) {
        return writeContents(containers, getTagByType(containers), registries);
    }

    /**
     * Helper to read and load a list of handler contents from a {@link ListTag}
     */
    public static void readContents(List<? extends INBTSerializable<CompoundTag>> contents, ListTag storedContents, String key, HolderLookup.Provider registries) {
        int size = contents.size();
        for (int tagCount = 0; tagCount < storedContents.size(); tagCount++) {
            CompoundTag tagCompound = storedContents.getCompound(tagCount);
            byte id = tagCompound.getByte(key);
            if (id >= 0 && id < size) {
                contents.get(id).deserializeNBT(registries, tagCompound);
            }
        }
    }

    /**
     * Helper to read and load a list of handler contents to a {@link ListTag}
     */
    public static ListTag writeContents(List<? extends INBTSerializable<CompoundTag>> contents, String key, HolderLookup.Provider registries) {
        ListTag storedContents = new ListTag();
        for (int tank = 0; tank < contents.size(); tank++) {
            CompoundTag tagCompound = contents.get(tank).serializeNBT(registries);
            if (!tagCompound.isEmpty()) {
                tagCompound.putByte(key, (byte) tank);
                storedContents.add(tagCompound);
            }
        }
        return storedContents;
    }

    // keep this only for backwards compat
    private static String getTagByType(List<? extends INBTSerializable<CompoundTag>> containers) {
        if (containers.isEmpty()) {
            return NBTConstants.CONTAINER;
        }
        INBTSerializable<CompoundTag> obj = containers.get(0);
        if (obj instanceof IFluidTank) {
            return NBTConstants.TANK;
        }
        return NBTConstants.CONTAINER;
    }

    /**
     * Helper to calculate what the maximum id is in a list of contents.
     */
    public static int getMaxId(ListTag storedContents, String key) {
        int maxId = -1;
        for (int tagCount = 0; tagCount < storedContents.size(); tagCount++) {
            byte id = storedContents.getCompound(tagCount).getByte(key);
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }
}