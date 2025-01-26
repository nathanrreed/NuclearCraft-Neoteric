package igentuman.nc.handler.event.server;

import igentuman.nc.block.turbine.TurbineBladeBlock;
import igentuman.nc.item.HEVItem;
import igentuman.nc.item.HazmatItem;
import igentuman.nc.multiblock.MultiblockHandler;
import igentuman.nc.radiation.data.RadiationEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.setup.registration.NCItems.HEV_BOOTS;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class WorldEvents {

    public static List<Block> trackingBlocks = new ArrayList<>();

    public WorldEvents() {
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(WorldEvents::onTickServer);
        NeoForge.EVENT_BUS.addListener(WorldEvents::onTickLevel);
        NeoForge.EVENT_BUS.addListener(WorldEvents::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(WorldEvents::onPlayerDamage);
        NeoForge.EVENT_BUS.addListener(WorldEvents::chunkUnloadEvent);
        NeoForge.EVENT_BUS.addListener(WorldEvents::worldLoadEvent);
        NeoForge.EVENT_BUS.addListener(WorldEvents::worldUnloadEvent);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, WorldEvents::onBlockPlace);
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        if (state == null) return;
        if (trackingBlocks.contains(state.getBlock())) {
            MultiblockHandler.trackBlockChange(event.getPos());
        }
        if (state != null && !state.isAir() && state.hasBlockEntity()) {

        }
    }

    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        boolean placed = true;
        BlockState state = event.getState();
        if (state == null) return;
        if (trackingBlocks.contains(state.getBlock())) {
            MultiblockHandler.trackBlockChange(event.getPos());
        }
        if (state.getBlock() instanceof TurbineBladeBlock) {
            placed = TurbineBladeBlock.processBlockPlace(event.getLevel(), event.getPos(), event.getPlacedBlock(), state, event.getPlacedAgainst());
        }
        if (!placed) {
            event.setCanceled(true);
        }
    }

    public static void chunkUnloadEvent(ChunkEvent.Unload event) {
    }

    public static void worldUnloadEvent(LevelEvent.Unload event) {
    }

    public static void worldLoadEvent(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
        }
    }

    public static void onTickServer(ServerTickEvent.Post event) {
        MultiblockHandler.tick();
    }

    public static void onTickLevel(LevelTickEvent.Post event) {
        RadiationEvents.onWorldTick(event);
    }

    public static int getHEVProtectionRate(Player player) {
        int rate = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if ((stack.getItem() instanceof HEVItem) && isCharged(stack)) {
                rate++;
            }
        }
        return rate;
    }

    public static boolean isFullyEquipped(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (!(stack.getItem() instanceof HazmatItem) && !(stack.getItem() instanceof HEVItem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCharged(ItemStack item) {
        IEnergyStorage handler = item.getCapability(Capabilities.EnergyStorage.ITEM);
        return handler != null && handler.getEnergyStored() > 0;
    }


    public static void onPlayerDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            DamageSource source = event.getSource();
            if (source.is(DamageTypes.MAGIC)) {
                if (isFullyEquipped(player)) {
                    event.setNewDamage(event.getOriginalDamage() / 10F);
                }
            }
            if (source.is(DamageTypes.FALL)) {
                player.getArmorSlots().forEach(stack -> {
                    if (stack.getItem().equals(HEV_BOOTS.get()) && isCharged(stack)) {
                        consumeEnergy(stack, 1000);
                        event.setNewDamage(0);
                        return;
                    }
                });
            }
            int protectionRate = getHEVProtectionRate(player);
            if (protectionRate > 0) {
                event.setNewDamage(event.getNewDamage() - (event.getNewDamage() * (protectionRate * 0.1F)));
                for (ItemStack stack : player.getArmorSlots()) {
                    consumeEnergy(stack, 1000);
                }
            }
        }
    }

    private static void consumeEnergy(ItemStack stack, int i) {
        IEnergyStorage handler = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (handler != null) {
            handler.extractEnergy(i, false);
        }
    }
}