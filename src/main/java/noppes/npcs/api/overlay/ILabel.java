package noppes.npcs.api.overlay;

public interface ILabel extends IOverlayComponent
{
    String getText();
    
    ILabel setText(final String p0);
    
    ILabel setCentered(final boolean p0);
    
    boolean isCentered();
    
    float getScale();
    
    void setScale(final float p0);
}
