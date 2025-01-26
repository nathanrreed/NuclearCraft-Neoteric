package igentuman.nc.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;

public class MusicDiscItem extends Item {
    public MusicDiscItem(ResourceKey<JukeboxSong> pSound, Item.Properties pProperties) {
        super(pProperties.rarity(Rarity.RARE).stacksTo(1).jukeboxPlayable(pSound));
    }
}