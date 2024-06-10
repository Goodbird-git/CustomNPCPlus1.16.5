package noppes.npcs.api.gui;

public interface IColoredLine extends ICustomGuiComponent {
    int getColor();

    IColoredLine setColor(int color);

    int getXEnd();

    int getYEnd();

    IColoredLine setEnd(int x, int y);

    float getThickness();

    IColoredLine setThickness(float thickness);
}
