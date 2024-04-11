package noppes.npcs.packets.client;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlayerSkinGet extends PacketBasic
{

    public PacketPlayerSkinGet() {
    }

    public static void encode(final PacketPlayerSkinGet msg, final PacketBuffer buf) {
    }

    public static PacketPlayerSkinGet decode(final PacketBuffer buf) {
        return new PacketPlayerSkinGet();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle() {
        ClientProxy.sendSkin(player.getUUID());
    }
}
