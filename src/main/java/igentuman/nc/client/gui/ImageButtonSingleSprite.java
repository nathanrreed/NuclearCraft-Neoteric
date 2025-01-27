package igentuman.nc.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

public class ImageButtonSingleSprite extends ImageButton {
    private final int x;
    private final int y;
    private final int posX;
    private final int posY;
    private final int width;
    private final int height;
    private final ResourceLocation resourcelocation;

    public ImageButtonSingleSprite(int x, int y, int width, int height, int posX, int posY, ResourceLocation resourcelocation, OnPress onPress) {
        super(x, y, width, height, null, onPress);
        this.x = x;
        this.y = y;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.resourcelocation = resourcelocation;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(resourcelocation, this.x, this.y, this.posX, this.posY - (this.isHoveredOrFocused() ? -this.height : 0), this.width, this.height);
    }
}
