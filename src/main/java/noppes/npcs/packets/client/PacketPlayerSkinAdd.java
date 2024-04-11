package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketPlayerSkinAdd extends PacketBasic
{
    CompoundNBT nbt;
    public PacketPlayerSkinAdd(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public static void encode(final PacketPlayerSkinAdd msg, final PacketBuffer buf) {
        buf.writeNbt(msg.nbt);
    }

    public static PacketPlayerSkinAdd decode(final PacketBuffer buf) {
        return new PacketPlayerSkinAdd(buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle() {
        UUID uuid = PlayerSkinController.getInstance().loadPlayerSkin(nbt);
        ClientProxy.resetSkin(uuid);
    }
}
