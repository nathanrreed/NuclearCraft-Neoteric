package igentuman.nc.radiation.data;

import igentuman.nc.compat.mekanism.MekanismRadiation;
import igentuman.nc.radiation.FluidRadiation;
import igentuman.nc.radiation.ItemRadiation;
import igentuman.nc.radiation.RadiationCleaningItems;
import igentuman.nc.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.LinkedList;
import java.util.List;

import static igentuman.nc.NuclearCraft.MODID;
import static igentuman.nc.handler.config.RadiationConfig.RADIATION_CONFIG;
import static igentuman.nc.setup.Registration.RADIATION_RESISTANCE;

public class RadiationEvents {

    public static boolean isTracking = false;
    private static List<ItemEntity> droppedRadioactiveItems = new LinkedList<>();

//    public static void attachWorldRadiation(final AttachCapabilitiesEvent<Level> event) {
//        if (!event.getObject().getCapability(WorldRadiationProvider.WORLD_RADIATION).isPresent()) {
//            event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "radiation"), new WorldRadiationProvider());
//            isTracking = true;
//        }
//    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemUse(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        if (stack.isEmpty()) {
            return;
        }
        long radiation = RadiationCleaningItems.byItem(stack.getItem());
        if (radiation == 0) return;
        PlayerRadiation radCap = entity.getCapability(PlayerRadiationProvider.PLAYER_RADIATION);
        if (radCap != null) {
            if (stack.getItem().toString().contains("radaway")) {
                if (entity.hasEffect(RADIATION_RESISTANCE)) {
                    entity.removeEffect(RADIATION_RESISTANCE);
                }
                entity.addEffect(new MobEffectInstance(RADIATION_RESISTANCE, 1200, 1, false, true));
            } else if (stack.getItem().toString().contains("rad_x")) {
                if (entity.hasEffect(RADIATION_RESISTANCE)) {
                    entity.removeEffect(RADIATION_RESISTANCE);
                }
                entity.addEffect(new MobEffectInstance(RADIATION_RESISTANCE, 1200, 2, false, true));
            }
            radCap.setRadiation(Math.max(0, radCap.getRadiation() - radiation / 1000));
            if (ModUtil.isMekanismLoadeed() && RADIATION_CONFIG.MEKANISM_RADIATION_INTEGRATION.get()) {
                MekanismRadiation.addEntityRadiation((Player) entity, -radiation / 10000000);
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ItemEntity) {
            ItemStack stack = ((ItemEntity) entity).getItem();
            if (stack.isEmpty()) {
                return;
            }
            double radiation = ItemRadiation.byItem(stack.getItem());
            if (radiation > 0.001) {
                RadiationManager.get(event.getLevel()).addRadiation(event.getLevel(), stack.getCount() * radiation / 5, entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ());
                droppedRadioactiveItems.add((ItemEntity) entity);
            }
        }
    }


//    public static void attachPlayerRadiation(final AttachCapabilitiesEvent<Entity> event) {
//        if (event.getObject() instanceof Player) {
//            if (!event.getObject().getCapability(PlayerRadiationProvider.PLAYER_RADIATION).isPresent()) {
//                event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "radiation"), new PlayerRadiationProvider());
//            }
//        }
//    }

    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            PlayerRadiation oldStore = event.getOriginal().getCapability(PlayerRadiationProvider.PLAYER_RADIATION);
            if (oldStore != null) {
                PlayerRadiation newStore = event.getEntity().getCapability(PlayerRadiationProvider.PLAYER_RADIATION);
                if (newStore != null) {
                    newStore.copyFrom(oldStore);
                }
            }
        }
    }

    @SubscribeEvent
    public void onFluidPlaced(BlockEvent.FluidPlaceBlockEvent event) {
        BlockState state = event.getState();
        if (!state.isAir()) {
            double radiation = FluidRadiation.byFluid(state.getFluidState().getType());
            if (radiation > 0.001) {
                // RadiationManager.get((Level) event.getLevel()).addRadiation((Level) event.getLevel(), radiation/5, event.getLiquidPos().getX(), event.getLiquidPos().getY(), event.getLiquidPos().getZ());
            }
        }
    }

    public static void onWorldTick(LevelTickEvent.Post event) {
        if (!isTracking) {
            return;
        }
        // Don't do anything client side
        if (event.getLevel().isClientSide) {
            return;
        }

        Level world = event.getLevel();
        RadiationManager manager = RadiationManager.get(world);
        if (!world.isClientSide) {
            int size = droppedRadioactiveItems.size();
            for (int i = 0; i < size; i++) {
                ItemEntity entity = droppedRadioactiveItems.get(i);
                if (entity.isAlive()) {
                    double radiation = ItemRadiation.byItem(entity.getItem().getItem());
                    if (radiation > 0.001) {
                        RadiationManager.get(world).addRadiation(world, radiation / 5, entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ());
                    }
                } else {
                    droppedRadioactiveItems.remove(i);
                    i--;
                    size--;
                }
            }
        }
        manager.tick(world);
    }

    public static void stopTracking() {
        isTracking = false;
    }

    public static void startTracking() {
        isTracking = true;
    }
}
