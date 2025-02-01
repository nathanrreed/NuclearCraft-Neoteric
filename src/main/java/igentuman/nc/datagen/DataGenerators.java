package igentuman.nc.datagen;

import igentuman.nc.datagen.blockstates.NCBlockStates;
import igentuman.nc.datagen.blockstates.NCFluidBlockStates;
import igentuman.nc.datagen.models.NCItemModels;
import igentuman.nc.datagen.recipes.NCRecipes;
import igentuman.nc.datagen.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static igentuman.nc.NuclearCraft.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    public static boolean isInDataGen = false;

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        isInDataGen = true;
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(NCLootTables::new, LootContextParamSets.BLOCK)), lookupProvider));

        NCBlockTags blockTags = new NCBlockTags(generator, event);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new NCItemTags(generator, blockTags, event));
        generator.addProvider(event.includeServer(), new FluidTags(generator, event));
        generator.addProvider(event.includeServer(), new NCRecipes(generator.getPackOutput(), lookupProvider));
        generator.addProvider(event.includeServer(), new NCBiomeTags(generator, event));
        generator.addProvider(event.includeServer(), new NCStructureSetTags(generator, event));
        generator.addProvider(event.includeClient(), new NCBlockStates(generator, event));
        generator.addProvider(event.includeClient(), new NCFluidBlockStates(generator, event));
        generator.addProvider(event.includeClient(), new NCItemModels(generator, event));
        generator.addProvider(event.includeClient(), new NCLanguageProvider(generator, "en_us"));
        generator.addProvider(event.includeClient(), new EmiLangProvider(generator, "en_gb"));
        generator.addProvider(event.includeServer(), new NCWorldGenProvider(generator, event));
    }
}