package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerSkinSet extends PacketServerBasic
{
    CompoundNBT nbt;
    public SPacketPlayerSkinSet(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public static void encode(final SPacketPlayerSkinSet msg, final PacketBuffer buf) {
        buf.writeNbt(msg.nbt);
    }

    public static SPacketPlayerSkinSet decode(final PacketBuffer buf) {
        return new SPacketPlayerSkinSet(buf.readNbt());
    }

    @Override
    public void handle() {
        PlayerSkinController pData = PlayerSkinController.getInstance();
        pData.loadPlayerSkin(nbt);
        pData.sendToAll(player);
    }
}