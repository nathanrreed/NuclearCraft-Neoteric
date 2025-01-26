package igentuman.nc.datagen.tags;

import net.minecraft.core.HolderLookup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static igentuman.nc.NuclearCraft.MODID;

public class NCBiomeTags extends TagsProvider<Biome> {

    public NCBiomeTags(DataGenerator generator, GatherDataEvent event) {
        super(generator.getPackOutput(), Registries.BIOME, event.getLookupProvider(), MODID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }

    @Override
    public String getName() {
        return "NuclearCraft Tags";
    }
}