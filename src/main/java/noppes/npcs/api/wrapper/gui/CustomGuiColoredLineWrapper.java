package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.gui.IColoredLine;

public class CustomGuiColoredLineWrapper extends CustomGuiComponentWrapper implements IColoredLine {
    int xEnd, yEnd;
    int color;
    float thickness;

    public CustomGuiColoredLineWrapper() {
    }

    public CustomGuiColoredLineWrapper(int id, int xStart, int yStart, int xEnd, int yEnd, int color, float thickness) {
        this.id = id;
        this.setPos(xStart, yStart);
        this.setEnd(xEnd, yEnd);
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public IColoredLine setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public int getXEnd() {
        return xEnd;
    }

    @Override
    public int getYEnd() {
        return yEnd;
    }

    @Override
    public IColoredLine setEnd(int x, int y) {
        this.xEnd = x;
        this.yEnd = y;
        return this;
    }

    @Override
    public float getThickness() {
        return thickness;
    }

    @Override
    public IColoredLine setThickness(float thickness) {
        this.thickness = thickness;
        return this;
    }

    @Override
    public int getType() {
        return 8;
    }

    public CompoundNBT toNBT(CompoundNBT compound) {
        super.toNBT(compound);
        compound.putInt("xEnd", xEnd);
        compound.putInt("yEnd", yEnd);
        compound.putInt("color", color);
        compound.putFloat("thickness", thickness);
        return compound;
    }

    public CustomGuiComponentWrapper fromNBT(CompoundNBT compound) {
        super.fromNBT(compound);
        setColor(compound.getInt("color"));
        setThickness(compound.getFloat("thickness"));
        setEnd(compound.getInt("xEnd"), compound.getInt("yEnd"));
        return this;
    }
}
