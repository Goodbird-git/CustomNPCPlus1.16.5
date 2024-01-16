package noppes.npcs.packets.client;

import noppes.npcs.shared.common.*;
import net.minecraft.network.*;
import net.minecraft.client.*;
import noppes.npcs.entity.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.controllers.*;
import net.minecraft.entity.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraft.entity.player.*;
import noppes.npcs.client.gui.player.*;
import noppes.npcs.*;
import net.minecraft.client.gui.screen.*;

public class PacketDialog extends PacketBasic
{
    private final int entityId;
    private final int dialogId;

    public PacketDialog(final int entityId, final int dialogId) {
        this.entityId = entityId;
        this.dialogId = dialogId;
    }

    public static void encode(final PacketDialog msg, final PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.dialogId);
    }

    public static PacketDialog decode(final PacketBuffer buf) {
        return new PacketDialog(buf.readInt(), buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle() {
        final Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
        if (!(entity instanceof EntityNPCInterface)) {
            return;
        }
        final Dialog dialog = DialogController.instance.dialogs.get(this.dialogId);
        openDialog(dialog, (EntityNPCInterface)entity, this.player);
    }

    public static void openDialog(final Dialog dialog, final EntityNPCInterface npc, final PlayerEntity player) {
        final Screen gui = Minecraft.getInstance().screen;
        if (!(gui instanceof GuiDialogInteract)) {
            CustomNpcs.proxy.openGui(player, new GuiDialogInteract(npc, dialog));
        }
        else {
            final GuiDialogInteract dia = (GuiDialogInteract)gui;
            dia.appendDialog(dialog);
        }
    }
}
