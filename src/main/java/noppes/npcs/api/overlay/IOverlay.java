package noppes.npcs.api.overlay;

import noppes.npcs.api.item.*;
import java.util.*;
import net.minecraft.nbt.*;

public interface IOverlay
{
    int getId();
    
    void setLinkSide(final int p0);
    
    int getLinkSide();
    
    ILabel addLabel(final int p0, final String p1, final int p2, final int p3);
    
    ITexturedRect addTexturedRect(final int p0, final String p1, final int p2, final int p3, final int p4, final int p5);
    
    ITexturedRect addTexturedRectCrop(final int p0, final String p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    ITexturedRect addTexturedRectCrop(final int p0, final String p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    IOverlayComponent getComponent(final int p0);
    
    IRenderItemOverlay addRenderItem(final int p0, final int p1, final int p2, final IItemStack p3);
    
    Collection<IOverlayComponent> getComponents();
    
    void removeComponent(final int p0);
    
    void clear();
    
    CompoundNBT toNbt();
    
    void fromNbt(final CompoundNBT p0);
}
