package igentuman.nc.handler.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static igentuman.nc.world.dimension.Dimensions.WASTELAIND_ID;

public class WorldConfig {
    public static <T> List<T> toList(Collection<T> vals) {
        return new ArrayList<>(vals);
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final DimensionConfig DIMENSION_CONFIG = new DimensionConfig(BUILDER);
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

    public static class DimensionConfig {
        public ModConfigSpec.ConfigValue<Boolean> registerWasteland;
        public ModConfigSpec.ConfigValue<Integer> wastelandID;

        public DimensionConfig(ModConfigSpec.Builder builder) {
            builder.push("Dimension");
            registerWasteland = builder
                    .comment("Register Wasteland Dimension")
                    .define("wasteland", true);
            wastelandID = builder
                    .comment("Dimension ID for Wasteland")
                    .define("wastelandID", WASTELAIND_ID);
            builder.pop();
        }
    }
}