package noppes.npcs.items;

import net.minecraft.item.*;
import noppes.npcs.api.wrapper.*;
import noppes.npcs.api.*;
import noppes.npcs.api.item.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import javax.annotation.*;

public class ItemScripted extends Item
{
    public ItemScripted(final Item.Properties props) {
        super(props);
    }

    public static ItemScriptedWrapper GetWrapper(final ItemStack stack) {
        return (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(stack);
    }

    public boolean showDurabilityBar(final ItemStack stack) {
        final IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper) {
            return ((ItemScriptedWrapper)istack).durabilityShow;
        }
        return super.showDurabilityBar(stack);
    }

    public double getDurabilityForDisplay(final ItemStack stack) {
        final IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper) {
            return 1.0 - ((ItemScriptedWrapper)istack).durabilityValue;
        }
        return super.getDurabilityForDisplay(stack);
    }

    public int getRGBDurabilityForDisplay(final ItemStack stack) {
        final IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (!(istack instanceof ItemScriptedWrapper)) {
            return super.getRGBDurabilityForDisplay(stack);
        }
        final int color = ((ItemScriptedWrapper)istack).durabilityColor;
        if (color >= 0) {
            return color;
        }
        return MathHelper.hsvToRgb(Math.max(0.0f, (float)(1.0 - this.getDurabilityForDisplay(stack))) / 3.0f, 1.0f, 1.0f);
    }

    public int getItemStackLimit(final ItemStack stack) {
        final IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper) {
            return istack.getMaxStackSize();
        }
        return super.getItemStackLimit(stack);
    }

    public boolean hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        return true;
    }

    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }

    public CompoundNBT getShareTag(final ItemStack stack) {
        final IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        CompoundNBT generalTag = super.getShareTag(stack);
        if (istack instanceof ItemScriptedWrapper) {
            if(generalTag!=null) {
                return generalTag.merge(((ItemScriptedWrapper) istack).getMCNbt());
            }
            return ((ItemScriptedWrapper) istack).getMCNbt();
        }
        return generalTag;
    }

    public void readShareTag(final ItemStack stack, @Nullable final CompoundNBT nbt) {
        if (nbt == null) {
            return;
        }
        super.readShareTag(stack,nbt);
        final IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ItemScriptedWrapper) {
            ((ItemScriptedWrapper)istack).setMCNbt(nbt);
        }
    }
}
