package noppes.npcs.api.overlay;

import net.minecraft.item.*;
import noppes.npcs.api.item.*;

public interface IRenderItemOverlay extends IOverlayComponent
{
    ItemStack getItem();
    
    IRenderItemOverlay setItem(final IItemStack p0);
}
