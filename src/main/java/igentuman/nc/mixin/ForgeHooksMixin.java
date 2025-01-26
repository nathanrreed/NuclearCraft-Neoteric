package igentuman.nc.mixin;

import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommonHooks.class)
public abstract class ForgeHooksMixin {

/*    @Inject(method = "filterThunks", at = @At("TAIL"), remap = false, cancellable = true)
    private static void filterThunks(Map<ResourceKey<?>, RegistryResourceAccess.EntryThunk<?>> map, CallbackInfoReturnable<Collection<Map.Entry<ResourceKey<?>, RegistryResourceAccess.EntryThunk<?>>>> cir) {
        Map<ResourceKey<?>, RegistryResourceAccess.EntryThunk<?>> map1 = new HashMap<>();
        for(Map.Entry<ResourceKey<?>, RegistryResourceAccess.EntryThunk<?>> entry : cir.getReturnValue()) {
            String name = entry.getKey().location().getPath();
            if(!entry.getKey().location().getNamespace().equals(MODID) || !name.contains("nc_ores_"))  {
                map1.put(entry.getKey(), entry.getValue());
                continue;
            }
            if(name.contains("nc_ores_")) {
                if(Ores.isRegistered(name.replaceAll("nc_ores_|_deepslate|_nether|_end", ""))) {
                    map1.put(entry.getKey(), entry.getValue());
                }
            }
        }
        cir.setReturnValue(map1.entrySet());
    }*/
}
