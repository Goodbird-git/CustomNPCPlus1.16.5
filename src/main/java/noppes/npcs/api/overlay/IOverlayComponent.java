package noppes.npcs.api.overlay;

import net.minecraft.nbt.*;

public interface IOverlayComponent
{
    int getId();
    
    int getPosX();
    
    int getPosY();
    
    IOverlayComponent setPos(final int p0, final int p1);
    
    int getType();
    
    void toNbt(final CompoundNBT p0);
    
    void fromNbt(final CompoundNBT p0);
}
