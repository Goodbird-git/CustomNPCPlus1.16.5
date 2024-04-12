package noppes.npcs.api.wrapper;

import noppes.npcs.api.overlay.*;
import net.minecraft.item.*;
import noppes.npcs.api.item.*;
import net.minecraft.nbt.*;

public class OverlayRenderItemWrapper extends OverlayComponentWrapper implements IRenderItemOverlay
{
    private ItemStack item;
    
    public OverlayRenderItemWrapper(final int id, final int x, final int y, final ItemStack item) {
        super(id, x, y);
        if (item == null) {
            this.item = ItemStack.EMPTY;
        }
        else {
            this.item = item;
        }
    }
    
    @Override
    public ItemStack getItem() {
        return this.item;
    }
    
    @Override
    public IRenderItemOverlay setItem(final IItemStack item) {
        this.item = item.getMCItemStack();
        return this;
    }
    
    @Override
    public int getType() {
        return 2;
    }
    
    @Override
    public void toNbt(final CompoundNBT compound) {
        super.toNbt(compound);
        compound.put("item", this.item.serializeNBT());
    }
    
    @Override
    public void fromNbt(final CompoundNBT compound) {
        super.fromNbt(compound);
        this.item = ItemStack.of(compound.getCompound("item"));
    }
}
