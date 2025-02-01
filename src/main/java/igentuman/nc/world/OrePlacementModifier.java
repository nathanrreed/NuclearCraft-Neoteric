package igentuman.nc.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import igentuman.nc.setup.registration.WorldGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static igentuman.nc.handler.config.OreGenConfig.ORE_CONFIG;

public class OrePlacementModifier extends PlacementModifier {

    public static final MapCodec<OrePlacementModifier> CODEC = Codec.INT.fieldOf("count").xmap(OrePlacementModifier::new, (modifier) -> modifier.count);

    private int count;
    private final HashMap<String, Integer> countMap;
    private final HashMap<String, Integer[]> heightMap;
    private final HashMap<String, List<Integer>> dimensionsMap;
    private String name;

    public OrePlacementModifier(int count) {
        this.countMap = new HashMap<>();
        this.heightMap = new HashMap<>();
        this.dimensionsMap = new HashMap<>();
        for (String name : ORE_CONFIG.ORES.keySet()) {
            boolean register;
            int amount;
            int minHeight;
            int maxHeight;
            List<Integer> dims;
            try {
                register = ORE_CONFIG.ORES.get(name).register.get();
                amount = ORE_CONFIG.ORES.get(name).veinSize.get();
                minHeight = ORE_CONFIG.ORES.get(name).min_height.get();
                maxHeight = ORE_CONFIG.ORES.get(name).max_height.get();
                dims = ORE_CONFIG.ORES.get(name).dimensions.get();
            } catch (Exception e) {
                register = ORE_CONFIG.ORES.get(name).register.getDefault();
                amount = ORE_CONFIG.ORES.get(name).veinSize.getDefault();
                minHeight = ORE_CONFIG.ORES.get(name).min_height.getDefault();
                maxHeight = ORE_CONFIG.ORES.get(name).max_height.getDefault();
                dims = ORE_CONFIG.ORES.get(name).dimensions.getDefault();
            }
            if (!register) {
                amount = 0;
            }
            this.heightMap.put(name, new Integer[]{minHeight, maxHeight});
            this.dimensionsMap.put(name, dims);
            this.countMap.put(name, amount);
        }

        this.count = count;
    }

    @Override
    public PlacementModifierType<?> type() {
        return WorldGeneration.NC_ORE_MODIFIER.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int actualCount = determinePlacementCount(context, random);

        return IntStream.range(0, actualCount).mapToObj(i -> pos);
    }

    private int determinePlacementCount(PlacementContext context, RandomSource random) {
        try {
            name = context.topFeature().get().feature().unwrapKey().get().location().getPath().replace("_ore", "");
            int dimensionId = context.getLevel().getServer().registryAccess().registry(Registries.DIMENSION_TYPE).get().getId(context.getLevel().getLevel().dimensionType());
            int veinSize = countMap.get(name);
            if (!dimensionsMap.get(name).contains(dimensionId)) {
                veinSize = 0;
            }
            if (veinSize == 0) return 0;
            return random.nextInt(veinSize);
        } catch (Exception e) {
            return 0;
        }
    }
}