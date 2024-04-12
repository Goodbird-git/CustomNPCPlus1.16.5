package noppes.npcs.packets.client;

import noppes.npcs.client.OverlayController;
import noppes.npcs.shared.common.*;
import net.minecraft.network.*;
import net.minecraftforge.api.distmarker.*;

public class PacketHideAllOverlays extends PacketBasic
{
    private final boolean id;

    public PacketHideAllOverlays(final boolean id) {
        this.id = id;
    }

    public static void encode(final PacketHideAllOverlays msg, final PacketBuffer buf) {
        buf.writeBoolean(msg.id);
    }

    public static PacketHideAllOverlays decode(final PacketBuffer buf) {
        return new PacketHideAllOverlays(buf.readBoolean());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle() {
        OverlayController.getInstance().clear();
    }
}

