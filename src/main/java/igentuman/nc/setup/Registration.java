package igentuman.nc.setup;

import com.mojang.serialization.MapCodec;
import igentuman.nc.effect.RadiationResistance;
import igentuman.nc.multiblock.fission.FissionReactor;
import igentuman.nc.multiblock.fusion.FusionReactor;
import igentuman.nc.multiblock.kugelblitz.KugelblitzRegistration;
import igentuman.nc.multiblock.turbine.TurbineRegistration;
import igentuman.nc.network.PacketHandler;
import igentuman.nc.recipes.NcRecipeType;
import igentuman.nc.setup.registration.*;
import igentuman.nc.world.structure.LaboratoryStructure;
import igentuman.nc.world.structure.PortalStructure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static igentuman.nc.setup.registration.Registries.EFFECTS;
import static igentuman.nc.setup.registration.Registries.STRUCTURES;

public class Registration {

    public static final DeferredHolder<MobEffect, MobEffect> RADIATION_RESISTANCE = EFFECTS.register("radiation_resistance", () -> new RadiationResistance(MobEffectCategory.BENEFICIAL, 0xd4ffFF));

    public static void init() {
        IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
        Registries.init();
        NCComponents.init();
        NCBlocks.init();
        NCStorageBlocks.init();
        NCItems.init();
        FissionFuel.init();
        NCFluids.init();
        WorldGeneration.register(bus);
        NCEnergyBlocks.init();
        NCProcessors.init();
        FissionReactor.init();
        FusionReactor.init();
        KugelblitzRegistration.init();
        TurbineRegistration.init();
        CreativeTabs.init();
//        NcRecipeSerializers.init();
        NcRecipeType.init();
        NCParticleTypes.init();
        NCSounds.init();
    }

    public static final Supplier<StructureType<?>> PORTAL = STRUCTURES.register("portal", () -> typeConvert(PortalStructure.CODEC));

    public static final Supplier<StructureType<?>> LABORATORY = STRUCTURES.register("nc_laboratory", () -> typeConvert(LaboratoryStructure.CODEC));

    private static <S extends Structure> StructureType<S> typeConvert(MapCodec<S> codec) {
        return () -> codec;
    }
}
