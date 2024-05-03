package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.controllers.ClientSkinController;
import noppes.npcs.controllers.data.PlayerSkinData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncSkin extends PacketBasic {
    private final String name;
    private final PlayerSkinData skinData;

    public PacketSyncSkin(String name, PlayerSkinData skinData) {
        this.name = name;
        this.skinData = skinData;
    }

    public static void encode(PacketSyncSkin msg, PacketBuffer buf) {
        buf.writeUtf(msg.name);
        CompoundNBT tag = new CompoundNBT();
        msg.skinData.saveNBTData(tag);
        buf.writeNbt(tag);
    }

    public static PacketSyncSkin decode(PacketBuffer buf) {
        String name = buf.readUtf();
        CompoundNBT tag = buf.readNbt();
        PlayerSkinData skinData = new PlayerSkinData();
        skinData.loadNBTData(tag);
        return new PacketSyncSkin(name, skinData);
    }

    @OnlyIn(Dist.CLIENT)
    public void handle() {
        ClientSkinController.addSkinForPlayer(name, skinData);
    }
}
