package igentuman.nc.registry;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public class ParticleTypeRegistryObject<PARTICLE extends ParticleOptions, TYPE extends ParticleType<PARTICLE>> extends WrappedRegistryObject<TYPE> {
    public ParticleTypeRegistryObject(Supplier<TYPE> registryObject) {
        super(registryObject);
    }
}