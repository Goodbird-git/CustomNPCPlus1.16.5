package noppes.npcs.api.wrapper;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.*;

import java.util.Collection;

public class OverlayWrapper implements IOverlay
{
    private final Int2ObjectOpenHashMap<IOverlayComponent> components;
    private int id;
    private int linkSide;
    
    public OverlayWrapper(final int id) {
        this.linkSide = 5;
        this.id = id;
        this.components = (Int2ObjectOpenHashMap<IOverlayComponent>)new Int2ObjectOpenHashMap();
    }
    
    @Override
    public Collection<IOverlayComponent> getComponents() {
        return this.components.values();
    }
    
    @Override
    public ILabel addLabel(final int id, final String text, final int x, final int y) {
        final ILabel label = new OverlayLabelWrapper(id, x, y, text);
        this.components.put(id, label);
        return label;
    }
    
    @Override
    public ITexturedRect addTexturedRect(final int id, final String texture, final int x, final int y, final int width, final int height) {
        final ITexturedRect rect = new OverlayTexturedRectWrapper(id, x, y, texture, width, height);
        this.components.put(id, rect);
        return rect;
    }
    
    @Override
    public ITexturedRect addTexturedRectCrop(final int id, final String texture, final int x, final int y, final int width, final int height, final int textureX, final int textureY) {
        final ITexturedRect rect = new OverlayTexturedRectWrapper(id, x, y, texture, width, height, textureX, textureY);
        this.components.put(id, rect);
        return rect;
    }
    
    @Override
    public ITexturedRect addTexturedRectCrop(final int id, final String texture, final int x, final int y, final int width, final int height, final int textureX, final int textureY, final int textureMaxX, final int textureMaxY) {
        final ITexturedRect rect = new OverlayTexturedRectWrapper(id, x, y, texture, width, height, textureX, textureY, textureMaxX, textureMaxY);
        this.components.put(id, rect);
        return rect;
    }
    
    @Override
    public IOverlayComponent getComponent(final int id) {
        return this.components.get(id);
    }
    
    @Override
    public IRenderItemOverlay addRenderItem(final int id, final int x, final int y, final IItemStack item) {
        final IRenderItemOverlay itemOverlay = new OverlayRenderItemWrapper(id, x, y, item.getMCItemStack());
        this.components.put(id, itemOverlay);
        return itemOverlay;
    }
    
    @Override
    public void removeComponent(final int id) {
        this.components.remove(id);
    }
    
    @Override
    public void clear() {
        this.components.clear();
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void setLinkSide(final int side) {
        this.linkSide = Math.min(9, Math.max(1, side));
    }
    
    @Override
    public int getLinkSide() {
        return this.linkSide;
    }
    
    @Override
    public void fromNbt(final CompoundNBT tagCompound) {
        this.id = tagCompound.getInt("id");
        this.linkSide = tagCompound.getInt("linkSide");
        this.components.clear();
        final ListNBT list = tagCompound.getList("components", 10);
        for (int i = 0; i < list.size(); ++i) {
            final CompoundNBT compound = list.getCompound(i);
            final int type = compound.getInt("type");
            IOverlayComponent component = null;
            switch (type) {
                case 0: {
                    component = new OverlayLabelWrapper(0, 0, 0, "");
                    break;
                }
                case 1: {
                    component = new OverlayTexturedRectWrapper(0, 0, 0, "", 0, 0);
                    break;
                }
                case 2: {
                    component = new OverlayRenderItemWrapper(0, 0, 0, null);
                    break;
                }
                default: {
                    continue;
                }
            }
            component.fromNbt(compound);
            this.components.put(component.getId(), component);
        }
    }
    
    @Override
    public CompoundNBT toNbt() {
        final CompoundNBT compound = new CompoundNBT();
        compound.putInt("id", this.id);
        compound.putInt("linkSide", this.linkSide);
        final ListNBT list = new ListNBT();
        for (final IOverlayComponent component : this.components.values()) {
            final CompoundNBT comp = new CompoundNBT();
            component.toNbt(comp);
            list.add(comp);
        }
        compound.put("components", list);
        return compound;
    }
}
