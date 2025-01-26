package igentuman.nc.handler.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public interface INCConfig {

    String getFileName();

    ModConfigSpec getConfigSpec();

    ModConfig.Type getConfigType();

    default boolean addToContainer() {
        return true;
    }
}