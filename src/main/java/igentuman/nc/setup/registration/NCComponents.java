package igentuman.nc.setup.registration;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;

import static igentuman.nc.setup.registration.Registries.DATA_COMPONENTS;

public class NCComponents {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DATA_COMPONENTS.registerComponentType("fluid_content", builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static void init() {
    }
}
