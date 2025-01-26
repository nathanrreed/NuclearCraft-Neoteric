package igentuman.nc.handler.config;

import igentuman.nc.content.energy.BatteryBlocks;
import igentuman.nc.content.energy.RTGs;
import igentuman.nc.content.energy.SolarPanels;
import igentuman.nc.content.storage.BarrelBlocks;
import igentuman.nc.content.storage.ContainerBlocks;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommonConfig {
    public static <T> List<T> toList(Collection<T> vals) {
        return new ArrayList<>(vals);
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final EnergyGenerationConfig ENERGY_GENERATION = new EnergyGenerationConfig(BUILDER);
    public static final EnergyStorageConfig ENERGY_STORAGE = new EnergyStorageConfig(BUILDER);

    public static final StorageBlocksConfig STORAGE_BLOCKS = new StorageBlocksConfig(BUILDER);
    public static final ModConfigSpec spec = BUILDER.build();
    private static boolean loaded = false;
    private static List<Runnable> loadActions = new ArrayList<>();

    public static void setLoaded() {
        if (!loaded)
            loadActions.forEach(Runnable::run);
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void onLoad(Runnable action) {
        if (loaded)
            action.run();
        else
            loadActions.add(action);
    }

    public static class EnergyGenerationConfig {
        public ModConfigSpec.ConfigValue<List<Boolean>> REGISTER_SOLAR_PANELS;
        public ModConfigSpec.ConfigValue<List<Integer>> SOLAR_PANELS_GENERATION;
        public ModConfigSpec.ConfigValue<List<Boolean>> REGISTER_RTG;
        public ModConfigSpec.ConfigValue<List<Integer>> RTG_GENERATION;
        public ModConfigSpec.ConfigValue<List<Integer>> RTG_RADIATION;
        public ModConfigSpec.ConfigValue<Integer> STEAM_TURBINE;
        public ModConfigSpec.ConfigValue<Integer> DECAY_GENERATOR;
        public ModConfigSpec.ConfigValue<Double> GENERATION_MULTIPLIER;

        public EnergyGenerationConfig(ModConfigSpec.Builder builder) {
            builder.push("Energy");

            GENERATION_MULTIPLIER = builder
                    .comment("Multiplier for all power generation in the mod")
                    .defineInRange("generation_multiplier", 1.0, 0.001, 1000.0);

            REGISTER_SOLAR_PANELS = builder
                    .comment("Allow solar panel registration: " + String.join(", ", SolarPanels.all().keySet()))
                    .define("register_panel", SolarPanels.initialRegistered(), o -> o instanceof ArrayList);

            SOLAR_PANELS_GENERATION = builder
                    .comment("Solar panel power generation: " + String.join(", ", SolarPanels.all().keySet()))
                    .define("panel_power", SolarPanels.initialPower(), o -> o instanceof ArrayList);

            REGISTER_RTG = builder
                    .comment("Allow rtg registration: " + String.join(", ", RTGs.all().keySet()))
                    .define("register_rtg", RTGs.initialRegistered(), o -> o instanceof ArrayList);

            RTG_GENERATION = builder
                    .comment("rtg generation: " + String.join(", ", RTGs.all().keySet()))
                    .define("rtg_power", RTGs.initialPower(), o -> o instanceof ArrayList);

            RTG_RADIATION = builder
                    .comment("rtg radiation: " + String.join(", ", RTGs.all().keySet()))
                    .define("rtg_radiation", RTGs.initialRadiation(), o -> o instanceof ArrayList);

            STEAM_TURBINE = builder
                    .comment("Steam turbine (one block) base power gen")
                    .define("steam_turbine_power_gen", 50);

            DECAY_GENERATOR = builder
                    .comment("Decay Generator base power gen")
                    .define("decay_generator_power_gen", 100);

            builder.pop();
        }
    }

    public static class StorageBlocksConfig {
        public ModConfigSpec.ConfigValue<List<Boolean>> REGISTER_BARREL;
        public ModConfigSpec.ConfigValue<List<Boolean>> REGISTER_CONTAINER;
        public ModConfigSpec.ConfigValue<List<Integer>> BARREL_CAPACITY;

        public StorageBlocksConfig(ModConfigSpec.Builder builder) {

            builder.push("storage_blocks")
                    .comment("Blocks to store items, fluids, etc...");

            REGISTER_CONTAINER = builder
                    .comment("Allow container registration: " + String.join(", ", BarrelBlocks.all().keySet()))
                    .define("container_block_registration", ContainerBlocks.initialRegistered(), o -> o instanceof ArrayList);

            REGISTER_BARREL = builder
                    .comment("Allow barrel registration: " + String.join(", ", BarrelBlocks.all().keySet()))
                    .define("barrel_block_registration", BarrelBlocks.initialRegistered(), o -> o instanceof ArrayList);

            BARREL_CAPACITY = builder
                    .comment("Barrel capacity in Buckets: " + String.join(", ", BarrelBlocks.all().keySet()))
                    .define("barrel_capacity", BarrelBlocks.initialCapacity(), o -> o instanceof ArrayList);

            builder.pop();
        }
    }

    public static class EnergyStorageConfig {
        public ModConfigSpec.ConfigValue<List<Boolean>> REGISTER_ENERGY_BLOCK;
        public ModConfigSpec.ConfigValue<List<Integer>> ENERGY_BLOCK_STORAGE;
        public ModConfigSpec.ConfigValue<Integer> LITHIUM_ION_BATTERY_STORAGE;
        public ModConfigSpec.ConfigValue<Integer> QNP_ENERGY_STORAGE;
        public ModConfigSpec.ConfigValue<Integer> LIGHTNING_ROD_CHARGE;
        public ModConfigSpec.ConfigValue<Integer> QNP_ENERGY_PER_BLOCK;

        public EnergyStorageConfig(ModConfigSpec.Builder builder) {
            builder.push("energy_storage");

            LIGHTNING_ROD_CHARGE = builder
                    .define("ligtning_rod_charge", 1000000);

            REGISTER_ENERGY_BLOCK = builder
                    .comment("Allow block registration: " + String.join(", ", BatteryBlocks.all().keySet()))
                    .define("energy_block_registration", BatteryBlocks.initialRegistered(), o -> o instanceof ArrayList);

            ENERGY_BLOCK_STORAGE = builder
                    .comment("Storage: " + String.join(", ", BatteryBlocks.all().keySet()))
                    .define("energy_block_storage", BatteryBlocks.initialPower(), o -> o instanceof ArrayList);

            LITHIUM_ION_BATTERY_STORAGE = builder
                    .define("lithium_ion_battery_storage", 1000000);

            QNP_ENERGY_STORAGE = builder
                    .define("qnp_energy_storage", 2000000);

            QNP_ENERGY_PER_BLOCK = builder
                    .define("qnp_energy_per_block", 200);

            builder.pop();
        }

        public int getCapacityFor(String code) {
            if (code.equals("lithium_ion_cell")) {
                return LITHIUM_ION_BATTERY_STORAGE.get();
            }
            return BatteryBlocks.all().get(code).config().getStorage();
        }
    }
}