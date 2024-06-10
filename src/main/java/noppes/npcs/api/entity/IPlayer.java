package noppes.npcs.api.entity;

import net.minecraft.entity.player.*;
import noppes.npcs.api.item.*;
import noppes.npcs.api.*;
import noppes.npcs.api.block.*;
import noppes.npcs.api.entity.data.*;
import noppes.npcs.api.handler.data.*;
import noppes.npcs.api.gui.*;
import net.minecraft.entity.*;
import noppes.npcs.api.overlay.IOverlay;

public interface IPlayer<T extends ServerPlayerEntity> extends IEntityLiving<T>
{
    String getDisplayName();

    boolean hasFinishedQuest(final int p0);

    boolean hasActiveQuest(final int p0);

    void startQuest(final int p0);

    int factionStatus(final int p0);

    void finishQuest(final int p0);

    void stopQuest(final int p0);

    void removeQuest(final int p0);

    boolean hasReadDialog(final int p0);

    void showDialog(final int p0, final String p1);

    void removeDialog(final int p0);

    void addDialog(final int p0);

    void addFactionPoints(final int p0, final int p1);

    int getFactionPoints(final int p0);

    void message(final String p0);

    int getGamemode();

    void setGamemode(final int p0);

    @Deprecated
    int inventoryItemCount(final IItemStack p0);

    @Deprecated
    int inventoryItemCount(final String p0);

    IContainer getInventory();

    IItemStack getInventoryHeldItem();

    boolean removeItem(final IItemStack p0, final int p1);

    boolean removeItem(final String p0, final int p1);

    void removeAllItems(final IItemStack p0);

    boolean giveItem(final IItemStack p0);

    boolean giveItem(final String p0, final int p1);

    void setSpawnpoint(final int p0, final int p1, final int p2);

    void resetSpawnpoint();

    boolean hasAdvancement(final String p0);

    int getExpLevel();

    void setExpLevel(final int p0);

    boolean hasPermission(final String p0);

    Object getPixelmonData();

    ITimers getTimers();

    void closeGui();

    T getMCEntity();

    IBlock getSpawnPoint();

    void setSpawnPoint(final IBlock p0);

    int getHunger();

    void setHunger(final int p0);

    void kick(final String p0);

    void sendNotification(final String p0, final String p1, final int p2);

    void sendMail(final IPlayerMail p0);

    void clearData();

    IQuest[] getActiveQuests();

    IQuest[] getFinishedQuests();

    void updatePlayerInventory();

    void playSound(final String p0, final float p1, final float p2);

    void playMusic(final String p0, final boolean p1, final boolean p2);

    IContainer getOpenContainer();

    boolean canQuestBeAccepted(final int p0);

    void showCustomGui(final ICustomGui p0);

    ICustomGui getCustomGui();

    void trigger(final int p0, final Object... p1);

    void showOverlay(final IOverlay p0);

    void hideOverlay(final int p0);

    void hideAllOverlays();
}
