package noppes.npcs.packets.client;

import noppes.npcs.client.OverlayController;
import noppes.npcs.shared.common.*;
import net.minecraft.network.*;
import net.minecraftforge.api.distmarker.*;

public class PacketOverlayHide extends PacketBasic
{
    private final int id;

    public PacketOverlayHide(final int id) {
        this.id = id;
    }

    public static void encode(final PacketOverlayHide msg, final PacketBuffer buf) {
        buf.writeInt(msg.id);
    }

    public static PacketOverlayHide decode(final PacketBuffer buf) {
        return new PacketOverlayHide(buf.readInt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle() {
        OverlayController.getInstance().removeOverlay(this.id);
    }
}