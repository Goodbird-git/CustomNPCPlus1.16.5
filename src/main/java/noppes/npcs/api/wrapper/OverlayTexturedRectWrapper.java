package noppes.npcs.api.wrapper;

import noppes.npcs.api.overlay.*;
import net.minecraft.nbt.*;

public class OverlayTexturedRectWrapper extends OverlayComponentWrapper implements ITexturedRect
{
    private String texture;
    private int width;
    private int height;
    private float[] uv;
    private float[] rgb;
    int textureX;
    int textureY;
    int textureMaxX;
    int textureMaxY;
    
    public OverlayTexturedRectWrapper(final int id, final int x, final int y, final String texture, final int width, final int height) {
        super(id, x, y);
        this.textureY = -1;
        this.textureMaxY = -1;
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    public OverlayTexturedRectWrapper(final int id, final int x, final int y, final String texture, final int width, final int height, final int textureX, final int textureY) {
        super(id, x, y);
        this.textureY = -1;
        this.textureMaxY = -1;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.setTextureOffset(textureX, textureY);
    }
    
    public OverlayTexturedRectWrapper(final int id, final int x, final int y, final String texture, final int width, final int height, final int textureX, final int textureY, final int textureMaxX, final int textureMaxY) {
        super(id, x, y);
        this.textureY = -1;
        this.textureMaxY = -1;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.setTextureOffset(textureX, textureY);
        this.setTextureMaxSize(textureMaxX, textureMaxY);
    }
    
    @Override
    public int getTextureX() {
        return this.textureX;
    }
    
    @Override
    public int getTextureY() {
        return this.textureY;
    }
    
    @Override
    public int getTextureMaxX() {
        return this.textureMaxX;
    }
    
    @Override
    public int getTextureMaxY() {
        return this.textureMaxY;
    }
    
    @Override
    public ITexturedRect setTextureOffset(final int offsetX, final int offsetY) {
        this.textureX = offsetX;
        this.textureY = offsetY;
        return this;
    }
    
    @Override
    public ITexturedRect setTextureMaxSize(final int textureMaxX, final int textureMaxY) {
        this.textureMaxX = textureMaxX;
        this.textureMaxY = textureMaxY;
        return this;
    }
    
    @Override
    public String getTexture() {
        return this.texture;
    }
    
    @Override
    public ITexturedRect setTexture(final String texture) {
        this.texture = texture;
        return this;
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public ITexturedRect setWidth(final int width) {
        this.width = width;
        return this;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public ITexturedRect setHeight(final int height) {
        this.height = height;
        return this;
    }
    
    @Override
    public int getType() {
        return 1;
    }
    
    @Override
    public ITexturedRect setUV(final float x1, final float y1, final float x2, final float y2) {
        this.uv = new float[] { x1, y1, x2, y2 };
        return this;
    }
    
    @Override
    public ITexturedRect setRGB(final float r, final float g, final float b, final float a) {
        this.rgb = new float[] { r, g, b, a };
        return this;
    }
    
    @Override
    public float[] getRGB() {
        return this.rgb;
    }
    
    @Override
    public float[] getUV() {
        return this.uv;
    }
    
    @Override
    public void toNbt(final CompoundNBT compound) {
        super.toNbt(compound);
        compound.putString("texture", this.texture);
        compound.putInt("width", this.width);
        compound.putInt("height", this.height);
        if (this.uv != null) {
            final int r = (int)(this.uv[0] * 255.0f);
            final int g = (int)(this.uv[1] * 255.0f);
            final int b = (int)(this.uv[2] * 255.0f);
            final int a = (int)(this.uv[3] * 255.0f);
            final int rgb = (r << 24) + (g << 16) + (b << 8) + a;
            compound.putInt("u", rgb);
        }
        if (this.rgb != null) {
            final int r = (int)(this.rgb[0] * 255.0f);
            final int g = (int)(this.rgb[1] * 255.0f);
            final int b = (int)(this.rgb[2] * 255.0f);
            final int a = (int)(this.rgb[3] * 255.0f);
            final int rgb = (r << 24) + (g << 16) + (b << 8) + a;
            compound.putInt("c", rgb);
        }
        if (this.textureX >= 0 && this.textureY >= 0) {
            compound.putIntArray("texPos", new int[] { this.textureX, this.textureY });
        }
        if (this.textureMaxX >= 0 && this.textureMaxY >= 0) {
            compound.putIntArray("texPosMax", new int[] { this.textureMaxX, this.textureMaxY });
        }
    }
    
    @Override
    public void fromNbt(final CompoundNBT compound) {
        super.fromNbt(compound);
        this.texture = compound.getString("texture");
        this.width = compound.getInt("width");
        this.height = compound.getInt("height");
        if (compound.contains("c")) {
            final int uv = compound.getInt("c");
            this.setRGB((uv >> 24 & 0xFF) / 255.0f, (uv >> 16 & 0xFF) / 255.0f, (uv >> 8 & 0xFF) / 255.0f, (uv & 0xFF) / 255.0f);
        }
        else {
            this.setRGB(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (compound.contains("u")) {
            final int uv = compound.getInt("u");
            this.setUV((uv >> 24 & 0xFF) / 255.0f, (uv >> 16 & 0xFF) / 255.0f, (uv >> 8 & 0xFF) / 255.0f, (uv & 0xFF) / 255.0f);
        }
        else {
            this.setUV(0.0f, 0.0f, 1.0f, 1.0f);
        }
        if (compound.contains("texPos")) {
            this.setTextureOffset(compound.getIntArray("texPos")[0], compound.getIntArray("texPos")[1]);
        }
        if (compound.contains("texPosMax")) {
            this.setTextureMaxSize(compound.getIntArray("texPosMax")[0], compound.getIntArray("texPosMax")[1]);
        }
    }
}
