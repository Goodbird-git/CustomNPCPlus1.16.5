package noppes.npcs.api.wrapper;

import noppes.npcs.api.overlay.*;
import net.minecraft.nbt.*;

public class OverlayLabelWrapper extends OverlayComponentWrapper implements ILabel
{
    private String text;
    private boolean isCenter;
    private float scale;
    
    public OverlayLabelWrapper(final int id, final int x, final int y, final String text) {
        super(id, x, y);
        this.isCenter = false;
        this.scale = 1.0f;
        this.text = text;
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    @Override
    public ILabel setText(final String text) {
        this.text = text;
        return this;
    }
    
    @Override
    public float getScale() {
        return this.scale;
    }
    
    @Override
    public void setScale(final float scale) {
        this.scale = scale;
    }
    
    @Override
    public ILabel setCentered(final boolean centered) {
        this.isCenter = centered;
        return this;
    }
    
    @Override
    public boolean isCentered() {
        return this.isCenter;
    }
    
    @Override
    public int getType() {
        return 0;
    }
    
    @Override
    public void toNbt(final CompoundNBT compound) {
        super.toNbt(compound);
        compound.putString("text", this.text);
        compound.putFloat("scale", this.scale);
        if (this.isCenter) {
            compound.putBoolean("centered", true);
        }
    }
    
    @Override
    public void fromNbt(final CompoundNBT compound) {
        super.fromNbt(compound);
        this.text = compound.getString("text");
        this.scale = compound.getFloat("scale");
        this.isCenter = compound.getBoolean("centered");
    }
}
