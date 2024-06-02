package noppes.npcs.api.gui;

public interface IButton extends ICustomGuiComponent
{
    int getWidth();

    int getHeight();

    IButton setSize(final int p0, final int p1);

    String getLabel();

    IButton setLabel(final String p0);

    String getTexture();

    boolean hasTexture();

    IButton setTexture(final String p0);

    int getTextureX();

    int getTextureY();

    IButton setTextureOffset(final int p0, final int p1);

    void setEnabled(final boolean p0);

    boolean getEnabled();
}
