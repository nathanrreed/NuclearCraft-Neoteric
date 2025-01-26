package igentuman.nc.block.entity.processor;

import igentuman.nc.content.processors.Processors;
import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.ItemStackIngredient;
import igentuman.nc.recipes.type.NcRecipe;
import igentuman.nc.util.CustomEnergyStorage;
import igentuman.nc.util.annotation.NBTField;
import igentuman.nc.util.annotation.NothingNullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.concurrent.atomic.AtomicInteger;

import static igentuman.nc.handler.config.CommonConfig.ENERGY_GENERATION;

public class SteamTurbineBE extends NCProcessorBE<SteamTurbineBE.Recipe> {
    @NBTField
    protected double efficiency = 0.001;

    public SteamTurbineBE(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, Processors.STEAM_TURBINE);
    }

    @Override
    public String getName() {
        return Processors.STEAM_TURBINE;
    }

    @Override
    public void tickServer() {
        sendOutPower();
        efficiency += 0.001;
        efficiency = Math.max(0.0001, Math.min(10, efficiency));
        if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
            return;
        }
        super.tickServer();
    }

    @Override
    protected void processRecipe() {
        if (!hasRecipe()) {
            updateRecipe();
        }
        if (!hasRecipe()) return;

        recipeInfo.process(speedMultiplier() / efficiency);
        efficiency += 0.0004;
        energyStorage.addEnergy((int) (getEnergyTransferPerTick() * recipe.getEnergy() * ENERGY_GENERATION.GENERATION_MULTIPLIER.get()));
    }

    @Override
    public double speedMultiplier() {
        return super.speedMultiplier();
    }

    @Override
    protected CustomEnergyStorage createEnergy() {
        //todo read config
        return new CustomEnergyStorage(getEnergyMaxStorage(), 0, getEnergyMaxStorage()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public int energyToSend() {
        return Math.min(energyStorage.getEnergyStored(), getEnergyTransferPerTick());
    }

    protected void sendOutPower() {
        if (energyStorage.getEnergyStored() == 0) return;
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(direction), direction.getOpposite());
                if (cap != null && cap.canReceive()) {
                    int received = cap.receiveEnergy(Math.min(capacity.get(), energyToSend()), false);
                    capacity.addAndGet(-received);
                    energyStorage.consumeEnergy(received);
                    setChanged();
                    if (capacity.get() <= 0) return;
                }
            }
        }
    }

    protected int getEnergyMaxStorage() {
        return getEnergyTransferPerTick() * 10;
    }

    protected int getEnergyTransferPerTick() {
        return ENERGY_GENERATION.STEAM_TURBINE.get();
    }

    @NothingNullByDefault
    public static class Recipe extends NcRecipe {
        public Recipe(ItemStackIngredient[] input, ItemStackIngredient[] output,
                      FluidStackIngredient[] inputFluids, FluidStackIngredient[] outputFluids,
                      double timeModifier, double powerModifier, double heatModifier, double rarity) {
            super(input, output, inputFluids, outputFluids, timeModifier, powerModifier, heatModifier, 1);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            //TODO
        }
    }
}
