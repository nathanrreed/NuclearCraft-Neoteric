package igentuman.nc.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

public class ImageButtonSingleSprite extends ImageButton {
    private final int x;
    private final int y;
    private final int posX;
    private final int posY;
    private final int size;
    private final ResourceLocation resourcelocation;

    public ImageButtonSingleSprite(int x, int y, int width, int height, int posX, int posY, int size, ResourceLocation resourcelocation, OnPress onPress) {
        super(x, y, width, height, null, onPress);
        this.x = x;
        this.y = y;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.resourcelocation = resourcelocation;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(resourcelocation, 256, 265, this.posX, this.posY, this.x, this.y, 0, this.size, this.size);
    }
}
