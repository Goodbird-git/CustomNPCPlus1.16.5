package noppes.npcs.containers;

import noppes.npcs.api.wrapper.gui.*;
import net.minecraft.inventory.*;
import noppes.npcs.client.gui.custom.components.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.inventory.container.*;
import noppes.npcs.api.wrapper.*;
import noppes.npcs.api.*;
import net.minecraft.entity.*;
import noppes.npcs.*;
import noppes.npcs.api.gui.*;
import net.minecraft.entity.player.*;
import noppes.npcs.util.*;

public class ContainerCustomGui extends Container
{
    public CustomGuiWrapper customGui;
    public IInventory guiInventory;

    public ContainerCustomGui(final int containerId, final PlayerInventory playerInventory, final int size) {
        super(CustomContainer.container_customgui, containerId);
        this.guiInventory = new Inventory(size);
    }

    public boolean stillValid(final PlayerEntity playerIn) {
        return true;
    }

    public void setGui(final CustomGuiWrapper gui, final PlayerEntity player) {
        this.customGui = gui;
        int index = 0;
        for (final IItemSlot slot : this.customGui.getSlots()) {
            this.addSlot(new CustomGuiSlot(this.guiInventory, index, slot, player));
            this.guiInventory.setItem(index, slot.getStack().getMCItemStack());
            ++index;
        }
        if (this.customGui.getShowPlayerInv()) {
            this.addPlayerInventory(player, this.customGui.getPlayerInvX(), this.customGui.getPlayerInvY());
        }
    }

    public ItemStack quickMoveStack(final PlayerEntity playerIn, final int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.copy();
            if (index < this.guiInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack2, this.guiInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack2, 0, this.guiInventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public ItemStack clicked(final int slotId, final int dragType, final ClickType clickTypeIn, final PlayerEntity player) {
        if (slotId < 0) {
            return super.clicked(slotId, dragType, clickTypeIn, player);
        }
        if (!player.level.isClientSide) {
            if (!EventHooks.onCustomGuiSlotClicked((PlayerWrapper)NpcAPI.Instance().getIEntity(player), ((ContainerCustomGui)player.containerMenu).customGui, slotId, dragType, clickTypeIn.toString())) {
                final ItemStack item = super.clicked(slotId, dragType, clickTypeIn, player);
                final ServerPlayerEntity p = (ServerPlayerEntity)player;
                CustomNPCsScheduler.runTack(() -> p.refreshContainer(this), 10);
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    public void removed(final PlayerEntity player) {
        super.removed(player);
        if (!player.level.isClientSide) {
            EventHooks.onCustomGuiClose((PlayerWrapper)NpcAPI.Instance().getIEntity(player), this.customGui);
        }
    }

    void addPlayerInventory(final PlayerEntity player, final int x, final int y) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(player.inventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (int row = 0; row < 9; ++row) {
            this.addSlot(new Slot(player.inventory, row, x + row * 18, y + 58));
        }
    }

    @Override
    public void setItem(int p_75141_1_, ItemStack p_75141_2_){
        if(slots.size()>p_75141_1_){
            super.setItem(p_75141_1_,p_75141_2_);
        }
    }
}
