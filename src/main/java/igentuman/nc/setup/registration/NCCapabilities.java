package igentuman.nc.setup.registration;

import igentuman.nc.block.entity.BarrelBE;
import igentuman.nc.content.energy.BatteryBlocks;
import igentuman.nc.content.energy.RTGs;
import igentuman.nc.content.energy.SolarPanels;
import igentuman.nc.content.storage.BarrelBlocks;
import igentuman.nc.handler.ItemEnergyHandler;
import igentuman.nc.item.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static igentuman.nc.setup.registration.NCComponents.FLUID_CONTENT;
import static igentuman.nc.setup.registration.NCItems.*;
import static net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import static net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;

public class NCCapabilities {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
//        event.registerEntity(PlayerRadiation, EntityType.PLAYER, ); //TODO
//        event.register(WorldRadiation.class);
//        event.register(PlayerRadiation.class);

        registerItemCapabilities(event);
        registerBlockCapabilities(event);
    }

    private static void registerItemCapabilities(RegisterCapabilitiesEvent event) {
        // Energy Capabilities
        event.registerItem(EnergyStorage.ITEM, (stack, ctx) -> {
            HEVItem hevItem = (HEVItem) stack.getItem();
            return new ItemEnergyHandler.ItemEnergy(stack, hevItem.getEnergyMaxStorage(), 5000, hevItem.getEnergyMaxStorage() / 4);
        }, HEV_HELMET.get(), HEV_CHEST.get(), HEV_PANTS.get(), HEV_BOOTS.get());

        event.registerItem(EnergyStorage.ITEM, (stack, ctx) -> {
            QNP item = (QNP) stack.getItem();
            return new ItemEnergyHandler.ItemEnergy(stack, item.getEnergyMaxStorage(), 0, item.getEnergyMaxStorage() / 4);
        }, QNP.get());

        event.registerItem(EnergyStorage.ITEM, (stack, ctx) -> {
            if (stack.getItem() instanceof BatteryItem item) {
                return new ItemEnergyHandler.ItemEnergy(stack, item.getEnergyMaxStorage(), item.getEnergyMaxStorage(), item.getEnergyMaxStorage());
            } else if (stack.getItem() instanceof BatteryBlockItem item) {
                return new ItemEnergyHandler.ItemEnergy(stack, item.getEnergyMaxStorage(), item.getEnergyMaxStorage(), item.getEnergyMaxStorage());
            } else {
                throw new RuntimeException("Stack of " + stack.getItem().getDescriptionId() + " has no known capability");
            }
        }, Stream.of(List.of(LITHIUM_ION_CELL.get()),
                BatteryBlocks.all().keySet().stream().map(name -> NCEnergyBlocks.BLOCK_ITEMS.get(name).get()).toList()
        ).flatMap(Collection::stream).toArray(ItemLike[]::new));

        // Fluid Capabilities
        event.registerItem(FluidHandler.ITEM, (stack, ctx) -> {
            BarrelBlockItem item = (BarrelBlockItem) stack.getItem();
            return new FluidHandlerItemStack(FLUID_CONTENT, stack, item.getCapacity());
        }, BarrelBlocks.all().keySet().stream().map(name -> NCStorageBlocks.BLOCK_ITEMS.get(name).get()).toArray(ItemLike[]::new));

        // Item Capabilities
    }

    private static void registerBlockCapabilities(RegisterCapabilitiesEvent event) {
        // Barrel Capabilities
        for (var type : BarrelBlocks.all().keySet().stream().map(name -> NCStorageBlocks.STORAGE_BE.get(name)).toList()) {
            event.registerBlockEntity(FluidHandler.BLOCK, type.get(), (entity, context) -> ((BarrelBE) entity).getFluidHandler().get());
        }

        // NCEnergy Capabilities
        for (var type : Stream.of(List.of("decay_generator"),
                SolarPanels.all().keySet().stream().map(name -> "solar_panel/" + name).toList(),
                BatteryBlocks.all().keySet(),
                RTGs.all().keySet()
        ).flatMap(Collection::stream).map(name -> NCEnergyBlocks.ENERGY_BE.get(name)).toList()) {
            event.registerBlockEntity(EnergyStorage.BLOCK, type.get(), (entity, context) -> entity.getEnergy().get());
        }
    }
}