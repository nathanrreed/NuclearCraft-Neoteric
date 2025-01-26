package igentuman.nc.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.setup.registration.NCFluids.NC_GASES;
import static igentuman.nc.setup.registration.NCFluids.NC_MATERIALS;
import static igentuman.nc.setup.registration.Tags.LIQUIDS_TAG;

public class FluidTags extends FluidTagsProvider {
    public FluidTags(DataGenerator gen, GatherDataEvent event) {
        super(gen.getPackOutput(), event.getLookupProvider(), MODID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (String name : NC_MATERIALS.keySet()) {
            tag(LIQUIDS_TAG.get(name)).add(NC_MATERIALS.get(name).getStill());
            tag(LIQUIDS_TAG.get(name)).add(NC_MATERIALS.get(name).getFlowing());
        }
        for (String name : NC_GASES.keySet()) {
            tag(LIQUIDS_TAG.get(name)).add(NC_GASES.get(name).getStill());
            tag(LIQUIDS_TAG.get(name)).add(NC_GASES.get(name).getFlowing());

            tag(Tags.Fluids.GASEOUS).add(NC_GASES.get(name).getStill());
            tag(Tags.Fluids.GASEOUS).add(NC_GASES.get(name).getFlowing());
        }
    }
}
