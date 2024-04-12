package noppes.npcs.api.overlay;

public interface ITexturedRect extends IOverlayComponent
{
    String getTexture();
    
    ITexturedRect setTexture(final String p0);
    
    int getWidth();
    
    ITexturedRect setWidth(final int p0);
    
    int getHeight();
    
    ITexturedRect setHeight(final int p0);
    
    float[] getUV();
    
    ITexturedRect setUV(final float p0, final float p1, final float p2, final float p3);
    
    ITexturedRect setRGB(final float p0, final float p1, final float p2, final float p3);
    
    float[] getRGB();
    
    int getTextureX();
    
    int getTextureY();
    
    int getTextureMaxX();
    
    int getTextureMaxY();
    
    ITexturedRect setTextureOffset(final int p0, final int p1);
    
    ITexturedRect setTextureMaxSize(final int p0, final int p1);
}
