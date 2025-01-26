package igentuman.nc.datagen.blockstates;

import igentuman.nc.setup.registration.NCFluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static igentuman.nc.setup.registration.NCFluids.ALL_FLUID_ENTRIES;
import static igentuman.nc.setup.registration.NCFluids.FluidEntry.CLIENT_FLUIDTYPE_EXTENSIONS;

public class NCFluidBlockStates extends ExtendedBlockstateProvider {

    public NCFluidBlockStates(DataGenerator gen, GatherDataEvent event) {
        super(gen, event.getExistingFileHelper());
    }

    @Override
    protected void registerStatesAndModels() {
        for (var fluidClient : CLIENT_FLUIDTYPE_EXTENSIONS) {
            NCFluids.FluidEntry entry = ALL_FLUID_ENTRIES.get(fluidClient.getName());
            ModelFile model = models().getBuilder("block/fluid/" + BuiltInRegistries.FLUID.getKey(entry.getStill()).getPath())
                    .texture("particle", fluidClient.getStillTexture());
            getVariantBuilder(entry.getBlock()).partialState().setModels(new ConfiguredModel(model));
        }
    }
}
