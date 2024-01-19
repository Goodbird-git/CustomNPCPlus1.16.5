package noppes.npcs.api.wrapper;

import noppes.npcs.entity.*;
import noppes.npcs.api.entity.data.*;
import net.minecraft.util.math.*;
import noppes.npcs.util.*;
import noppes.npcs.api.item.*;
import noppes.npcs.api.*;
import net.minecraft.entity.player.*;
import noppes.npcs.api.handler.data.*;
import noppes.npcs.controllers.data.*;
import net.minecraft.entity.*;
import noppes.npcs.*;
import noppes.npcs.controllers.*;
import noppes.npcs.api.entity.*;

public class NPCWrapper<T extends EntityNPCInterface> extends EntityLivingWrapper<T> implements ICustomNpc
{
    public NPCWrapper(final T npc) {
        super(npc);
    }

    @Override
    public void setMaxHealth(final float health) {
        if ((int)health == this.entity.stats.maxHealth) {
            return;
        }
        super.setMaxHealth(health);
        this.entity.stats.maxHealth = (int)health;
        this.entity.updateClient = true;
    }

    @Override
    public INPCDisplay getDisplay() {
        return this.entity.display;
    }

    @Override
    public INPCInventory getInventory() {
        return this.entity.inventory;
    }

    @Override
    public INPCAi getAi() {
        return this.entity.ais;
    }

    @Override
    public INPCAdvanced getAdvanced() {
        return this.entity.advanced;
    }

    @Override
    public INPCStats getStats() {
        return this.entity.stats;
    }

    @Override
    public IFaction getFaction() {
        return this.entity.faction;
    }

    @Override
    public ITimers getTimers() {
        return this.entity.timers;
    }

    @Override
    public void setFaction(final int id) {
        final Faction faction = FactionController.instance.getFaction(id);
        if (faction == null) {
            throw new CustomNPCsException("Unknown faction id: " + id);
        }
        this.entity.setFaction(id);
    }

    @Override
    public INPCRole getRole() {
        return this.entity.role;
    }

    @Override
    public INPCJob getJob() {
        return this.entity.job;
    }

    @Override
    public int getHomeX() {
        return this.entity.ais.startPos().getX();
    }

    @Override
    public int getHomeY() {
        return this.entity.ais.startPos().getY();
    }

    @Override
    public int getHomeZ() {
        return this.entity.ais.startPos().getZ();
    }

    @Override
    public void setHome(final int x, final int y, final int z) {
        this.entity.ais.setStartPos(new BlockPos(x, y, z));
    }

    public int getOffsetX() {
        return (int)this.entity.ais.bodyOffsetX;
    }

    public int getOffsetY() {
        return (int)this.entity.ais.bodyOffsetY;
    }

    public int getOffsetZ() {
        return (int)this.entity.ais.bodyOffsetZ;
    }

    public void setOffset(final int x, final int y, final int z) {
        this.entity.ais.bodyOffsetX = ValueUtil.correctFloat((float)x, 0.0f, 9.0f);
        this.entity.ais.bodyOffsetY = ValueUtil.correctFloat((float)y, 0.0f, 9.0f);
        this.entity.ais.bodyOffsetZ = ValueUtil.correctFloat((float)z, 0.0f, 9.0f);
        this.entity.updateClient = true;
    }

    @Override
    public void say(final String message) {
        this.entity.saySurrounding(new Line(message));
    }

    @Override
    public void sayTo(final IPlayer player, final String message) {
        this.entity.say(player.getMCEntity(), new Line(message));
    }

    @Override
    public void reset() {
        this.entity.reset();
    }

    @Override
    public long getAge() {
        return this.entity.totalTicksAlive;
    }

    @Override
    public IProjectile shootItem(final IEntityLiving target, final IItemStack item, int accuracy) {
        if (item == null) {
            throw new CustomNPCsException("No item was given");
        }
        if (target == null) {
            throw new CustomNPCsException("No target was given");
        }
        accuracy = ValueUtil.CorrectInt(accuracy, 1, 100);
        return (IProjectile)NpcAPI.Instance().getIEntity(this.entity.shoot(target.getMCEntity(), accuracy, item.getMCItemStack(), false));
    }

    @Override
    public IProjectile shootItem(final double x, final double y, final double z, final IItemStack item, int accuracy) {
        if (item == null) {
            throw new CustomNPCsException("No item was given");
        }
        accuracy = ValueUtil.CorrectInt(accuracy, 1, 100);
        return (IProjectile)NpcAPI.Instance().getIEntity(this.entity.shoot(x, y, z, accuracy, item.getMCItemStack(), false));
    }

    @Override
    public void giveItem(final IPlayer player, final IItemStack item) {
        this.entity.givePlayerItem(player.getMCEntity(), item.getMCItemStack());
    }

    @Override
    public String executeCommand(final String command) {
        if (!this.entity.getServer().isCommandBlockEnabled()) {
            throw new CustomNPCsException("Command blocks need to be enabled to executeCommands");
        }
        return NoppesUtilServer.runCommand(this.entity, this.entity.getName().getString(), command, null);
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public String getName() {
        return this.entity.display.getName();
    }

    @Override
    public void setName(final String name) {
        this.entity.display.setName(name);
    }

    @Override
    public void setRotation(final float rotation) {
        super.setRotation(rotation);
        final int r = (int)rotation;
        if (this.entity.ais.orientation != r) {
            this.entity.ais.orientation = r;
            this.entity.updateClient = true;
        }
    }

    @Override
    public boolean typeOf(final int type) {
        return type == 2 || super.typeOf(type);
    }

    @Override
    public void setDialog(final int slot, final IDialog dialog) {
        if (slot < 0 || slot > 11) {
            throw new CustomNPCsException("Slot needs to be between 0 and 11");
        }
        if (dialog == null) {
            this.entity.dialogs.remove(slot);
        }
        else {
            final DialogOption option = new DialogOption();
            option.dialogId = dialog.getId();
            option.title = dialog.getName();
            this.entity.dialogs.put(slot, option);
        }
    }

    @Override
    public IDialog getDialog(final int slot) {
        if (slot < 0 || slot > 11) {
            throw new CustomNPCsException("Slot needs to be between 0 and 11");
        }
        final DialogOption option = this.entity.dialogs.get(slot);
        if (option == null || !option.hasDialog()) {
            return null;
        }
        return option.getDialog();
    }

    @Override
    public void updateClient() {
        this.entity.updateClient();
    }

    @Override
    public IEntityLiving getOwner() {
        final LivingEntity owner = this.entity.getOwner();
        if (owner != null) {
            return (IEntityLiving)NpcAPI.Instance().getIEntity(owner);
        }
        return null;
    }

    @Override
    public void trigger(final int id, final Object... arguments) {
        EventHooks.onScriptTriggerEvent(this.entity.script, id, this.getWorld(), this.getPos(), null, arguments);
    }
}
