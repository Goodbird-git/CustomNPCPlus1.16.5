package noppes.npcs.api.wrapper.gui;

import noppes.npcs.controllers.*;
import noppes.npcs.api.wrapper.*;
import noppes.npcs.api.item.*;
import noppes.npcs.api.gui.*;
import java.util.*;
import noppes.npcs.api.entity.*;
import noppes.npcs.containers.*;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.*;
import net.minecraft.nbt.*;

public class CustomGuiWrapper implements ICustomGui
{
    int id;
    int width;
    int height;
    int playerInvX;
    int playerInvY;
    boolean pauseGame;
    boolean showPlayerInv;
    String backgroundTexture;
    ScriptContainer scriptHandler;
    List<ICustomGuiComponent> components;
    List<IItemSlot> slots;

    public CustomGuiWrapper() {
        this.backgroundTexture = "";
        this.components = new ArrayList<ICustomGuiComponent>();
        this.slots = new ArrayList<IItemSlot>();
    }

    public CustomGuiWrapper(final int id, final int width, final int height, final boolean pauseGame) {
        this.backgroundTexture = "";
        this.components = new ArrayList<ICustomGuiComponent>();
        this.slots = new ArrayList<IItemSlot>();
        this.id = id;
        this.width = width;
        this.height = height;
        this.pauseGame = pauseGame;
        this.scriptHandler = ScriptContainer.Current;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public List<ICustomGuiComponent> getComponents() {
        return this.components;
    }

    @Override
    public List<IItemSlot> getSlots() {
        return this.slots;
    }

    public ScriptContainer getScriptHandler() {
        return this.scriptHandler;
    }

    @Override
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void setDoesPauseGame(final boolean pauseGame) {
        this.pauseGame = pauseGame;
    }

    public boolean getDoesPauseGame() {
        return this.pauseGame;
    }

    @Override
    public void setBackgroundTexture(final String resourceLocation) {
        this.backgroundTexture = resourceLocation;
    }

    public String getBackgroundTexture() {
        return this.backgroundTexture;
    }

    @Override
    public IButton addButton(final int id, final String label, final int x, final int y) {
        final CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y);
        this.components.add(component);
        return component;
    }

    @Override
    public IButton addButton(final int id, final String label, final int x, final int y, final int width, final int height) {
        final CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height);
        this.components.add(component);
        return component;
    }

    @Override
    public IButton addTexturedButton(final int id, final String label, final int x, final int y, final int width, final int height, final String texture) {
        final CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture);
        this.components.add(component);
        return component;
    }

    @Override
    public IButton addTexturedButton(final int id, final String label, final int x, final int y, final int width, final int height, final String texture, final int textureX, final int textureY) {
        final CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture, textureX, textureY);
        this.components.add(component);
        return component;
    }

    @Override
    public ILabel addLabel(final int id, final String label, final int x, final int y, final int width, final int height) {
        final CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height);
        this.components.add(component);
        return component;
    }

    @Override
    public ILabel addLabel(final int id, final String label, final int x, final int y, final int width, final int height, final int color) {
        final CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height, color);
        this.components.add(component);
        return component;
    }

    @Override
    public ITextField addTextField(final int id, final int x, final int y, final int width, final int height) {
        final CustomGuiTextFieldWrapper component = new CustomGuiTextFieldWrapper(id, x, y, width, height);
        this.components.add(component);
        return component;
    }

    @Override
    public ITextArea addTextArea(final int id, final int x, final int y, final int width, final int height) {
        final CustomGuiTextAreaWrapper component = new CustomGuiTextAreaWrapper(id, x, y, width, height);
        this.components.add(component);
        return component;
    }

    @Override
    public ITexturedRect addTexturedRect(final int id, final String texture, final int x, final int y, final int width, final int height) {
        final CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height);
        this.components.add(component);
        return component;
    }

    @Override
    public ITexturedRect addTexturedRect(final int id, final String texture, final int x, final int y, final int width, final int height, final int textureX, final int textureY) {
        final CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height, textureX, textureY);
        this.components.add(component);
        return component;
    }

    @Override
    public IItemSlot addItemSlot(final int x, final int y) {
        return this.addItemSlot(x, y, ItemScriptedWrapper.AIR);
    }

    @Override
    public IItemSlot addItemSlot(final int x, final int y, final IItemStack stack) {
        final CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x, y, stack);
        this.slots.add(slot);
        return this.slots.get(this.slots.size() - 1);
    }

    @Override
    public IScroll addScroll(final int id, final int x, final int y, final int width, final int height, final String[] list) {
        final CustomGuiScrollWrapper component = new CustomGuiScrollWrapper(id, x, y, width, height, list);
        this.components.add(component);
        return component;
    }

    public IEntityDisplay addEntityDisplay(int id, int x, int y, int width, int height, IEntity entity){
        IEntityDisplay display = new CustomGuiEntityDisplayWrapper(id, x, y, width, height, entity);
        this.components.add(display);
        return display;
    }

    public IColoredLine addColoredLine(int id, int xStart, int yStart, int xEnd, int yEnd, int color, float thickness){
        IColoredLine line = new CustomGuiColoredLineWrapper(id, xStart, yStart, xEnd, yEnd, color, thickness);
        this.components.add(line);
        return line;
    }

    public IItemRenderer addItemRenderer(int id, final int x, final int y, int width, int height, final IItemStack stack) {
        final CustomGuiItemRendererWrapper rendererWrapper = new CustomGuiItemRendererWrapper(id, x, y, width, height, stack);
        this.components.add(rendererWrapper);
        return rendererWrapper;
    }

    @Override
    public void showPlayerInventory(final int x, final int y) {
        this.showPlayerInv = true;
        this.playerInvX = x;
        this.playerInvY = y;
    }

    @Override
    public ICustomGuiComponent getComponent(final int componentID) {
        for (final ICustomGuiComponent component : this.components) {
            if (component.getID() == componentID) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void removeComponent(final int componentID) {
        this.components.removeIf(c -> c.getID() == componentID);
    }

    @Deprecated
    public void updateComponent(final ICustomGuiComponent component) {
        for (int i = 0; i < this.components.size(); ++i) {
            final ICustomGuiComponent c = this.components.get(i);
            if (c.getID() == component.getID()) {
                this.components.set(i, component);
                return;
            }
        }
    }

    @Override
    public void update(final IPlayer player) {
        if (player.getMCEntity().containerMenu instanceof ContainerCustomGui) {
            Packets.send(player.getMCEntity(), new PacketGuiData(this.toNBT()));
        }
    }
    public void updateSlots(IPlayer player) {
        ((ContainerCustomGui)(player.getMCEntity()).containerMenu).setGui(this, player.getMCEntity());
    }
    public boolean getShowPlayerInv() {
        return this.showPlayerInv;
    }

    public int getPlayerInvX() {
        return this.playerInvX;
    }

    public int getPlayerInvY() {
        return this.playerInvY;
    }

    public ICustomGui fromNBT(final CompoundNBT tag) {
        this.id = tag.getInt("id");
        this.width = tag.getIntArray("size")[0];
        this.height = tag.getIntArray("size")[1];
        this.pauseGame = tag.getBoolean("pause");
        this.backgroundTexture = tag.getString("bgTexture");
        final List<ICustomGuiComponent> components = new ArrayList<>();
        ListNBT list = tag.getList("components", 10);
        for (final INBT b : list) {
            final CustomGuiComponentWrapper component = CustomGuiComponentWrapper.createFromNBT((CompoundNBT)b);
            components.add(component);
        }
        this.components = components;
        final List<IItemSlot> slots = new ArrayList<>();
        list = tag.getList("slots", 10);
        for (final INBT b2 : list) {
            final CustomGuiItemSlotWrapper component2 = (CustomGuiItemSlotWrapper)CustomGuiComponentWrapper.createFromNBT((CompoundNBT)b2);
            slots.add(component2);
        }
        this.slots = slots;
        this.showPlayerInv = tag.getBoolean("showPlayerInv");
        if (this.showPlayerInv) {
            this.playerInvX = tag.getIntArray("pInvPos")[0];
            this.playerInvY = tag.getIntArray("pInvPos")[1];
        }
        return this;
    }

    public CompoundNBT toNBT() {
        final CompoundNBT tag = new CompoundNBT();
        tag.putInt("id", this.id);
        tag.putIntArray("size", new int[] { this.width, this.height });
        tag.putBoolean("pause", this.pauseGame);
        tag.putString("bgTexture", this.backgroundTexture);
        ListNBT list = new ListNBT();
        for (final ICustomGuiComponent c : this.components) {
            list.add(((CustomGuiComponentWrapper)c).toNBT(new CompoundNBT()));
        }
        tag.put("components", list);
        list = new ListNBT();
        for (final ICustomGuiComponent c : this.slots) {
            list.add(((CustomGuiComponentWrapper)c).toNBT(new CompoundNBT()));
        }
        tag.put("slots", list);
        tag.putBoolean("showPlayerInv", this.showPlayerInv);
        if (this.showPlayerInv) {
            tag.putIntArray("pInvPos", new int[] { this.playerInvX, this.playerInvY });
        }
        return tag;
    }
}
