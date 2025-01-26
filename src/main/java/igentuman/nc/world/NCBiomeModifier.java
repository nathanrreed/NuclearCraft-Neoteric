package igentuman.nc.world;

import igentuman.nc.content.materials.Ores;
import igentuman.nc.world.ore.NCOre;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;

import static igentuman.nc.NuclearCraft.rl;
import static igentuman.nc.world.NCPlacedFeatures.PLACED_FEATURES;

public class NCBiomeModifier {

    public static final HashMap<String, ResourceKey<BiomeModifier>> BIOME_MODIFIERS = initBiomeModifiers();

    private static HashMap<String, ResourceKey<BiomeModifier>> initBiomeModifiers() {
        HashMap<String, ResourceKey<BiomeModifier>> map = new HashMap<>();
        for (String name : Ores.all().keySet()) {
            map.put(name, registerKey(name + "_biome_modifier"));
        }
        map.put("glowing_mushroom", registerKey("glowing_mushroom_biome_modifier"));
        return map;
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        for (String name : Ores.registered().keySet()) {
            NCOre ore = Ores.all().get(name);
            if (ore.config().dimensions.contains(0)) {
                context.register(BIOME_MODIFIERS.get(name), new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(PLACED_FEATURES.get(name))),
                        GenerationStep.Decoration.UNDERGROUND_ORES));
            }
            if (ore.config().dimensions.contains(-1)) {
                context.register(BIOME_MODIFIERS.get(name), new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_NETHER),
                        HolderSet.direct(placedFeatures.getOrThrow(PLACED_FEATURES.get(name))),
                        GenerationStep.Decoration.UNDERGROUND_ORES));
            }

            if (ore.config().dimensions.contains(1)) {
                context.register(BIOME_MODIFIERS.get(name), new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_END),
                        HolderSet.direct(placedFeatures.getOrThrow(PLACED_FEATURES.get(name))),
                        GenerationStep.Decoration.UNDERGROUND_ORES));
            }
        }
        context.register(BIOME_MODIFIERS.get("glowing_mushroom"), new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(PLACED_FEATURES.get("glowing_mushroom"))),
                GenerationStep.Decoration.UNDERGROUND_DECORATION));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, rl(name));
    }
}
