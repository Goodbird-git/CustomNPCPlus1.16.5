package noppes.npcs;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerSkinData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncSkin;

@Mod.EventBusSubscriber(modid = CustomNpcs.MODID)
public class SkinEventHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(PlayerSkinData.needsAnyResync()){
            for(ServerPlayerEntity player: ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
                PlayerData playerData = PlayerData.get(player);
                if(playerData.skinData.isActive() && playerData.skinData.hasChanged()){
                    Packets.sendAll(new PacketSyncSkin(playerData.playername, playerData.skinData));
                    playerData.skinData.markSynced();
                }
            }
            PlayerSkinData.resyncPerformed();
        }
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getPlayer() instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        PlayerData playerData = PlayerData.get(player);

        if(playerData.skinData.isActive()) {
            Packets.sendAll(new PacketSyncSkin(playerData.playername, playerData.skinData));
        }

        for(ServerPlayerEntity otherPlayer: ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            PlayerData otherPlayerData = PlayerData.get(otherPlayer);
            if(otherPlayerData.skinData.isActive()){
                Packets.send(player, new PacketSyncSkin(otherPlayerData.playername, otherPlayerData.skinData));
            }
        }
    }
}
