package igentuman.nc.world;

import igentuman.nc.content.materials.Ores;
import igentuman.nc.world.ore.NCOre;
import igentuman.nc.world.ore.OreGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.HashMap;
import java.util.List;

import static igentuman.nc.NuclearCraft.rl;
import static igentuman.nc.world.NCConfiguredFeatures.ORE_CONFIGURED_FEATURES;

public class NCPlacedFeatures {
    public static final HashMap<String, ResourceKey<PlacedFeature>> PLACED_FEATURES = initPlaceFeatures();

    private static HashMap<String, ResourceKey<PlacedFeature>> initPlaceFeatures() {
        HashMap<String, ResourceKey<PlacedFeature>> map = new HashMap<>();
        for (String name : Ores.all().keySet()) {
            map.put(name, registerKey(name + "_placed"));
        }
        map.put("glowing_mushroom", registerKey("glowing_mushroom_placed"));
        return map;
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier countPlacement, PlacementModifier heightRange)    {
        return List.of(countPlacement, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        for (String name : Ores.registered().keySet()) {
            NCOre ore = Ores.all().get(name);
            if (ore.dimensions.contains(0)) {
                register(context, PLACED_FEATURES.get(name), configuredFeatures.getOrThrow(ORE_CONFIGURED_FEATURES.get(name)),
                        OreGenerator.orePlacement((CountPlacement.of(ore.config().veinSize)), HeightRangePlacement.uniform(VerticalAnchor.absolute(ore.config().height[0]), VerticalAnchor.absolute(ore.config().height[1]))));
//                        OreGenerator.orePlacement(new OrePlacementModifier(ore.config().veinSize), VerticalAnchor.absolute(ore.config().height[1]))));
            }
            if (ore.dimensions.contains(-1)) {
                register(context, PLACED_FEATURES.get(name), configuredFeatures.getOrThrow(ORE_CONFIGURED_FEATURES.get(name)),
                        OreGenerator.orePlacement(new OrePlacementModifier(ore.config().veinSize),
                                HeightRangePlacement.uniform(VerticalAnchor.absolute(ore.config().config().height[0]), VerticalAnchor.absolute(ore.height[1]))));
            }
            if (ore.dimensions.contains(1)) {
                register(context, PLACED_FEATURES.get(name), configuredFeatures.getOrThrow(ORE_CONFIGURED_FEATURES.get(name)),
                        OreGenerator.orePlacement(new OrePlacementModifier(ore.config().veinSize),
                                HeightRangePlacement.uniform(VerticalAnchor.absolute(ore.config().config().height[0]), VerticalAnchor.absolute(ore.config().height[1]))));
            }
        }

        register(context, PLACED_FEATURES.get("glowing_mushroom"),
                configuredFeatures.getOrThrow(ORE_CONFIGURED_FEATURES.get("glowing_mushroom")),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, rl(name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}