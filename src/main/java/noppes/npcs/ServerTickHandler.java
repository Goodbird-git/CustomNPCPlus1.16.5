package noppes.npcs;

import net.minecraftforge.event.*;
import net.minecraftforge.fml.*;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraft.world.server.*;
import noppes.npcs.entity.data.*;
import java.util.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.inventory.container.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.shared.client.util.*;
import noppes.npcs.controllers.*;
import net.minecraft.server.*;
import net.minecraft.scoreboard.*;

public class ServerTickHandler
{
    public int ticks;

    public ServerTickHandler() {
        this.ticks = 0;
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.PlayerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.START) {
            return;
        }
        final PlayerEntity player = event.player;
        final PlayerData data = PlayerData.get(player);
        if (player.getCommandSenderWorld().getDayTime() % 24000L == 1L || player.getCommandSenderWorld().getDayTime() % 240000L == 12001L) {
            VisibilityController.instance.onUpdate((ServerPlayerEntity)player);
        }
        if (data.updateClient) {
            Packets.send((ServerPlayerEntity)player, new PacketSync(8, data.getSyncNBT(), true));
            VisibilityController.instance.onUpdate((ServerPlayerEntity)player);
            data.updateClient = false;
        }
        if (data.prevHeldItem != player.getMainHandItem() && (data.prevHeldItem.getItem() == CustomItems.wand || player.getMainHandItem().getItem() == CustomItems.wand)) {
            VisibilityController.instance.onUpdate((ServerPlayerEntity)player);
        }
        data.prevHeldItem = player.getMainHandItem();
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            NPCSpawning.findChunksForSpawning((ServerWorld)event.world);
        }
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && this.ticks++ >= 20) {
            SchematicController.Instance.updateBuilding();
            MassBlockController.Update();
            this.ticks = 0;
            for (final DataScenes.SceneState state : DataScenes.StartedScenes.values()) {
                if (!state.paused) {
                    final DataScenes.SceneState sceneState = state;
                    ++sceneState.ticks;
                }
            }
            for (final DataScenes.SceneContainer entry : DataScenes.ScenesToRun) {
                entry.update();
            }
            DataScenes.ScenesToRun = new ArrayList<DataScenes.SceneContainer>();
        }
    }

    @SubscribeEvent
    public void playerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        final ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
        final MinecraftServer server = event.getPlayer().getServer();
        PlayerSkinController.getInstance().logged(player);
        for (final ServerWorld level : server.forgeGetWorldMap().values()) {
            final ServerScoreboard board = level.getScoreboard();
            for (final String objective : Availability.scores) {
                final ScoreObjective so = board.getObjective(objective);
                if (so != null) {
                    if (board.getObjectiveDisplaySlotCount(so) == 0) {
                        player.connection.send((IPacket)new SScoreboardObjectivePacket(so, 0));
                    }
                    final Score sco = board.getOrCreatePlayerScore(player.getScoreboardName(), so);
                    player.connection.send((IPacket)new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, sco.getObjective().getName(), sco.getOwner(), sco.getScore()));
                }
            }
        }
        player.inventoryMenu.addSlotListener((IContainerListener)new IContainerListener() {
            public void refreshContainer(final Container container, final NonNullList<ItemStack> itemsList) {
            }

            public void slotChanged(final Container container, final int slotInd, final ItemStack stack) {
                if (player.level.isClientSide) {
                    return;
                }
                final PlayerQuestData playerdata = PlayerData.get((PlayerEntity)player).questData;
                playerdata.checkQuestCompletion((PlayerEntity)player, 0);
            }

            public void setContainerData(final Container container, final int varToUpdate, final int newValue) {
            }
        });
        final PlayerData data = PlayerData.get(event.getPlayer());
        String serverName = "local";
        if (server.isDedicatedServer()) {
            serverName = "server";
        }
        else if (server.isPublished()) {
            serverName = "lan";
        }
        AnalyticsTracking.sendData(data.iAmStealingYourDatas, "join", serverName);
        SyncController.syncPlayer((ServerPlayerEntity)event.getPlayer());
    }
}
