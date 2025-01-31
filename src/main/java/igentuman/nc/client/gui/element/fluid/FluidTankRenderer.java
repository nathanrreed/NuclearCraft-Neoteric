package igentuman.nc.client.gui.element.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import igentuman.nc.client.gui.element.NCGuiElement;
import igentuman.nc.network.toServer.PacketFlushSlotContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static igentuman.nc.handler.event.client.InputEvents.SHIFT_PRESSED;

// CREDIT: https://github.com/mezz/JustEnoughItems by mezz
// Under MIT-License: https://github.com/mezz/JustEnoughItems/blob/1.19/LICENSE.txt
// Includes major rewrites and methods from:
// https://github.com/mezz/JustEnoughItems/blob/1.19/Forge/src/main/java/mezz/jei/forge/platform/FluidHelper.java
public class FluidTankRenderer extends NCGuiElement {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected static final NumberFormat nf = NumberFormat.getIntegerInstance();
    protected static final int TEXTURE_SIZE = 16;
    protected static final int MIN_FLUID_HEIGHT = 1;

    protected final TooltipMode tooltipMode;
    protected boolean canVoid = false;
    protected final FluidTank tank;

    public static FluidTankRenderer tank(FluidTank fluidTank) {
        return new FluidTankRenderer(fluidTank, 16, 16, 0, 0);
    }

    public FluidTankRenderer pos(int[] slotPos) {
        this.x = slotPos[0];
        this.y = slotPos[1];
        return this;
    }

    public FluidTankRenderer canVoid() {
        canVoid = true;
        return this;
    }

    public FluidTankRenderer id(int i) {
        this.slotId = i;
        return this;
    }

    public FluidTankRenderer size(int i, int i1) {
        this.width = i;
        this.height = i1;
        return this;
    }

    public FluidTankRenderer pos(int i, int i1) {
        this.x = i;
        this.y = i1;
        return this;
    }

    public enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }

    public FluidTankRenderer(FluidTank tank, int width, int height, int[] pos) {
        this(tank, TooltipMode.SHOW_AMOUNT_AND_CAPACITY, width, height, pos[0], pos[1]);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (X() <= pMouseX && pMouseX < X() + width && Y() <= pMouseY && pMouseY < Y() + height) {
            if (!tank.isEmpty() && SHIFT_PRESSED) {
                PacketDistributor.sendToServer(new PacketFlushSlotContent(getPosition(), slotId));
            }
        }
        return false;
    }

    public FluidTankRenderer(FluidTank tank, int width, int height, int x, int y) {
        this(tank, TooltipMode.SHOW_AMOUNT_AND_CAPACITY, width, height, x, y);
    }

    public FluidTankRenderer(FluidTank tank, TooltipMode tooltipMode, int width, int height, int x, int y) {
        super(x, y, width, height, Component.empty());

        this.tank = tank;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GuiGraphics graphics, int mX, int mY, float pTicks) {
        super.draw(graphics, mX, mY, pTicks);
        render(graphics, tank.getFluid());
    }

    public void render(GuiGraphics graphics, FluidStack fluidStack) {
        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        {
            graphics.pose().translate(X(), Y(), 0);
            drawFluid(graphics, width, height, fluidStack);
        }
        graphics.pose().popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    private static Float[] colorToFloat(int color) {
        return List.of(FastColor.ARGB32.red(color) / 255.0f, FastColor.ARGB32.green(color) / 255.0f, FastColor.ARGB32.blue(color) / 255.0f, FastColor.ARGB32.alpha(color) / 255.0f).toArray(Float[]::new);
    }

    private static void blitTile(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int width, int height, int tWidth, int tHeight, int color) {
        blitTile(guiGraphics, sprite, 0, 0, width, height, tWidth, tHeight, color);
    }

    private static void blitTile(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int x, int y, int width, int height, int tWidth, int tHeight, int color) {
        final int xTileCount = width / tWidth;
        final int xRemainder = width % tWidth;
        final int yTileCount = height / tHeight;
        final int yRemainder = height % tHeight;

        Float[] c = colorToFloat(color);
        for (int i = 0; i <= xTileCount; i++) {
            for (int j = 0; j <= yTileCount; j++) {
                guiGraphics.blit(x + i * tWidth, y + j * tHeight, 0, ((i + 1) * tWidth < width) ? tWidth : xRemainder, ((j + 1) * tHeight < height) ? tHeight : yRemainder, sprite, c[0], c[1], c[2], c[3]);
            }
        }
    }

    private void drawFluid(GuiGraphics graphics, final int width, final int height, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
        int fluidColor = getColorTint(fluidStack);

        long amount = fluidStack.getAmount();
        int capacity = Math.min(1, tank.getCapacity());

        long scaledAmount = (amount * height) / capacity;

        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }
        blitTile(graphics, fluidStillSprite, width, ((int) scaledAmount), TEXTURE_SIZE, TEXTURE_SIZE, fluidColor);
    }

    private TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);

        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
    }

    private int getColorTint(FluidStack ingredient) {
        Fluid fluid = ingredient.getFluid();
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        return renderProperties.getTintColor(ingredient);
    }

    public List<Component> getTooltips() {
        List<Component> tooltip = new ArrayList<>();

        Fluid fluidType = tank.getFluid().getFluid();
        try {
            if (fluidType.isSame(Fluids.EMPTY)) {
                return tooltip;
            }

            MutableComponent displayName = Component.translatable(tank.getFluid().getDescriptionId());
            tooltip.add(displayName.withStyle(ChatFormatting.AQUA));

            long amount = tank.getFluid().getAmount();
            long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

            if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
                MutableComponent amountString = Component.translatable("gui.nc.fluid_tank_renderer.amount_capacity", nf.format(milliBuckets), nf.format(tank.getCapacity()));
                tooltip.add(amountString.withStyle(ChatFormatting.WHITE));
            } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
                MutableComponent amountString = Component.translatable("gui.nc.fluid_tank_renderer.amount", nf.format(milliBuckets));
                tooltip.add(amountString.withStyle(ChatFormatting.WHITE));
            }
            if (canVoid) {
                tooltip.add(Component.translatable("gui.nc.fluid_tank_renderer.can_void").withStyle(ChatFormatting.GOLD));
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to get tooltip for fluid: " + e);
        }

        return tooltip;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}