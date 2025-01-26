package igentuman.nc.registry;

import igentuman.nc.util.annotation.NothingNullByDefault;

import java.util.function.Supplier;

@NothingNullByDefault
public class WrappedRegistryObject<T> implements Supplier<T>, INamedEntry {

    protected Supplier<T> registryObject;

    protected WrappedRegistryObject(Supplier<T> registryObject) {
        this.registryObject = registryObject;
    }

    @Override
    public T get() {
        return registryObject.get();
    }

    @Override
    public String getInternalRegistryName() {
        return registryObject.get().toString(); //TODO FIX
    }
}