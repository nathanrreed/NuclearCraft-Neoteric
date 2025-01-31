package igentuman.nc.datagen.recipes;

import igentuman.nc.datagen.recipes.recipes.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

public class CustomRecipes extends NCRecipes {
    public CustomRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static RecipeOutput consumer;

    public static void generate(RecipeOutput consumer) {
        CustomRecipes.consumer = consumer;
        FissionRecipes.generate(consumer);
        ManufactoryRecipes.generate(consumer);
        DecayHastenerRecipes.generate(consumer);
        PressurizerRecipes.generate(consumer);
        AlloySmelterRecipes.generate(consumer);
        RockCrusherRecipes.generate(consumer);
        IsotopeSeparatorRecipes.generate(consumer);
        MelterRecipes.generate(consumer);
        IngotFormerRecipes.generate(consumer);
        FuelReprocessorRecipes.generate(consumer);
        ElectrolyzerRecipes.generate(consumer);
        ChemicalReactorRecipes.generate(consumer);
        AssemblerRecipes.generate(consumer);
        CentrifugeRecipes.generate(consumer);
        IrradiatorRecipes.generate(consumer);
        CrystalizerRecipes.generate(consumer);
        SteamTurbineRecipes.generate(consumer);
        FluidInfuserRecipes.generate(consumer);
        SupercoolerRecipes.generate(consumer);
        FluidEnricherRecipes.generate(consumer);
        ExtractorRecipes.generate(consumer);
        PumpRecipes.generate(consumer);
        GasScrubberRecipes.generate(consumer);
        AnalyzerRecipes.generate(consumer);
        LeacherRecipes.generate(consumer);
        OreVeinsRecipes.generate(consumer);
        FusionReactorRecipes.generate(consumer);
        FusionCoolantRecipes.generate(consumer);
        FissionBoilingRecipes.generate(consumer);
        TurbineControllerRecipes.generate(consumer);
    }
}