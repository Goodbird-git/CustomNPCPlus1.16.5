package noppes.npcs.api.wrapper;

import noppes.npcs.api.overlay.*;
import net.minecraft.nbt.*;

public abstract class OverlayComponentWrapper implements IOverlayComponent
{
    private int id;
    private int x;
    private int y;
    
    public OverlayComponentWrapper(final int id, final int x, final int y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public int getPosX() {
        return this.x;
    }
    
    @Override
    public int getPosY() {
        return this.y;
    }
    
    @Override
    public IOverlayComponent setPos(final int x, final int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    @Override
    public void toNbt(final CompoundNBT compound) {
        compound.putInt("id", this.id);
        compound.putIntArray("pos", new int[] { this.x, this.y });
        compound.putInt("type", this.getType());
    }
    
    @Override
    public void fromNbt(final CompoundNBT compound) {
        final int[] pos = compound.getIntArray("pos");
        this.x = pos[0];
        this.y = pos[1];
        this.id = compound.getInt("id");
    }
}
