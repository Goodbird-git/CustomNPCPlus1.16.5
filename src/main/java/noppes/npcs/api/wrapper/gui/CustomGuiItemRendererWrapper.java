package noppes.npcs.api.wrapper.gui;

import noppes.npcs.api.gui.*;
import noppes.npcs.api.item.*;
import noppes.npcs.api.wrapper.*;
import net.minecraft.inventory.container.*;
import net.minecraft.nbt.*;
import noppes.npcs.api.*;
import net.minecraft.item.*;

public class CustomGuiItemRendererWrapper extends CustomGuiComponentWrapper implements IItemRenderer
{
    IItemStack stack;
    public int width, height;
    public float scale;

    public CustomGuiItemRendererWrapper() {
        this.stack = ItemStackWrapper.AIR;
    }

    public CustomGuiItemRendererWrapper(int id, final int x, final int y, int width, int height, final IItemStack stack) {
        this.id = id;
        this.stack = ItemStackWrapper.AIR;
        this.scale = 1;
        this.setPos(x, y);
        this.setStack(stack);
        this.setHoverBox(width, height);
    }

    @Override
    public boolean hasStack() {
        return !this.stack.isEmpty();
    }

    @Override
    public IItemStack getStack() {
        return this.stack;
    }

    @Override
    public IItemRenderer setStack(final IItemStack itemStack) {
        if (itemStack == null) {
            this.stack = ItemStackWrapper.AIR;
        }
        else {
            this.stack = itemStack;
        }
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public IItemRenderer setHoverBox(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public IItemRenderer setScale(float scaleFactor) {
        this.scale = scaleFactor;
        return this;
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public CompoundNBT toNBT(final CompoundNBT nbt) {
        super.toNBT(nbt);
        nbt.put("stack", (INBT)this.stack.getMCItemStack().serializeNBT());
        nbt.putFloat("scale", this.scale);
        nbt.putInt("width", width);
        nbt.putInt("height", height);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(final CompoundNBT nbt) {
        super.fromNBT(nbt);
        this.setStack(NpcAPI.Instance().getIItemStack(ItemStack.of(nbt.getCompound("stack"))));
        this.setScale(nbt.getFloat("scale"));
        this.setHoverBox(nbt.getInt("width"), nbt.getInt("height"));
        return this;
    }
}
