package noppes.npcs.api.gui;

import noppes.npcs.api.item.IItemStack;

public interface IItemRenderer extends ICustomGuiComponent {
    boolean hasStack();

    IItemStack getStack();

    IItemRenderer setStack(final IItemStack p0);

    int getWidth();

    int getHeight();

    IItemRenderer setHoverBox(int width, int height);

    float getScale();

    IItemRenderer setScale(float scaleFactor);
}
