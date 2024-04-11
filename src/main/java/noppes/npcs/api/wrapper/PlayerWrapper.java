package noppes.npcs.api.wrapper;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.*;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerWrapper<T extends ServerPlayerEntity> extends EntityLivingBaseWrapper<T> implements IPlayer
{
    private IContainer inventory;
    private Object pixelmonPartyStorage;
    private Object pixelmonPCStorage;
    private final IData storeddata;
    private PlayerData data;

    public PlayerWrapper(final T player) {
        super(player);
        this.storeddata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                final CompoundNBT compound = this.getStoredCompound();
                if (value instanceof Number) {
                    compound.putDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    compound.putString(key, (String)value);
                }
            }

            @Override
            public Object get(final String key) {
                final CompoundNBT compound = this.getStoredCompound();
                if (!compound.contains(key)) {
                    return null;
                }
                final INBT base = compound.get(key);
                if (base instanceof NumberNBT) {
                    return ((NumberNBT)base).getAsDouble();
                }
                return base.getAsString();
            }

            @Override
            public void remove(final String key) {
                final CompoundNBT compound = this.getStoredCompound();
                compound.remove(key);
            }

            @Override
            public boolean has(final String key) {
                return this.getStoredCompound().contains(key);
            }

            @Override
            public void clear() {
                final PlayerData data = PlayerData.get(PlayerWrapper.this.entity);
                data.scriptStoreddata = new CompoundNBT();
            }

            private CompoundNBT getStoredCompound() {
                final PlayerData data = PlayerData.get(PlayerWrapper.this.entity);
                return data.scriptStoreddata;
            }

            @Override
            public String[] getKeys() {
                final CompoundNBT compound = this.getStoredCompound();
                return compound.getAllKeys().toArray(new String[compound.getAllKeys().size()]);
            }
        };
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public String getName() {
        return this.entity.getName().getString();
    }

    @Override
    public String getDisplayName() {
        return this.entity.getDisplayName().getString();
    }

    @Override
    public int getHunger() {
        return this.entity.getFoodData().getFoodLevel();
    }

    @Override
    public void setHunger(final int level) {
        this.entity.getFoodData().setFoodLevel(level);
    }

    @Override
    public boolean hasFinishedQuest(final int id) {
        final PlayerQuestData data = this.getData().questData;
        return data.finishedQuests.containsKey(id);
    }

    @Override
    public boolean hasActiveQuest(final int id) {
        final PlayerQuestData data = this.getData().questData;
        return data.activeQuests.containsKey(id);
    }

    @Override
    public IQuest[] getActiveQuests() {
        final PlayerQuestData data = this.getData().questData;
        final List<IQuest> quests = new ArrayList<>();
        for (final int id : data.activeQuests.keySet()) {
            final IQuest quest = QuestController.instance.quests.get(id);
            if (quest != null) {
                quests.add(quest);
            }
        }
        return quests.toArray(new IQuest[quests.size()]);
    }

    @Override
    public IQuest[] getFinishedQuests() {
        final PlayerQuestData data = this.getData().questData;
        final List<IQuest> quests = new ArrayList<>();
        for (final int id : data.finishedQuests.keySet()) {
            final IQuest quest = QuestController.instance.quests.get(id);
            if (quest != null) {
                quests.add(quest);
            }
        }
        return quests.toArray(new IQuest[quests.size()]);
    }

    @Override
    public void startQuest(final int id) {
        final Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        final QuestData questdata = new QuestData(quest);
        final PlayerData data = this.getData();
        data.questData.activeQuests.put(id, questdata);
        Packets.send(this.entity, new PacketAchievement(new TranslationTextComponent("quest.newquest"), new TranslationTextComponent(quest.title), 2));
        final ITextComponent text = new TranslationTextComponent("quest.newquest").append(":").append(new TranslationTextComponent(quest.title));
        Packets.send(this.entity, new PacketChat(text));
        data.updateClient = true;
    }

    @Override
    public void sendNotification(final String title, final String msg, final int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Wrong type value given " + type);
        }
        Packets.send(this.entity, new PacketAchievement(new TranslationTextComponent(title), new TranslationTextComponent(msg), type));
    }

    @Override
    public void finishQuest(final int id) {
        final Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        final PlayerData data = this.getData();
        data.questData.finishedQuests.put(id, System.currentTimeMillis());
        data.updateClient = true;
    }

    @Override
    public void stopQuest(final int id) {
        final Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        final PlayerData data = this.getData();
        data.questData.activeQuests.remove(id);
        data.updateClient = true;
    }

    @Override
    public void removeQuest(final int id) {
        final Quest quest = QuestController.instance.quests.get(id);
        if (quest == null) {
            return;
        }
        final PlayerData data = this.getData();
        data.questData.activeQuests.remove(id);
        data.questData.finishedQuests.remove(id);
        data.updateClient = true;
    }

    @Override
    public boolean hasReadDialog(final int id) {
        final PlayerDialogData data = this.getData().dialogData;
        return data.dialogsRead.contains(id);
    }

    @Override
    public void showDialog(final int id, final String name) {
        final Dialog dialog = DialogController.instance.dialogs.get(id);
        if (dialog == null) {
            throw new CustomNPCsException("Unknown Dialog id: " + id);
        }
        if (!dialog.availability.isAvailable(this.entity)) {
            return;
        }
        final EntityDialogNpc npc = new EntityDialogNpc(this.getWorld().getMCWorld());
        npc.display.setName(name);
        EntityUtil.Copy(this.entity, npc);
        final DialogOption option = new DialogOption();
        option.dialogId = id;
        option.title = dialog.title;
        npc.dialogs.put(0, option);
        NoppesUtilServer.openDialog(this.entity, npc, dialog);
    }

    public void showDialog(int id, ICustomNpc npc) {
        Dialog dialog = DialogController.instance.dialogs.get(id);
        if (dialog == null) {
            throw new CustomNPCsException("Unknown Dialog id: " + id);
        } else if (dialog.availability.isAvailable(this.entity)) {
            DialogOption option = new DialogOption();
            option.dialogId = id;
            option.title = dialog.title;
            NoppesUtilServer.openDialog(this.entity, (EntityNPCInterface) npc.getMCEntity(), dialog);
        }
    }

    @Override
    public void addFactionPoints(final int faction, final int points) {
        final PlayerData data = this.getData();
        data.factionData.increasePoints(this.entity, faction, points);
        data.updateClient = true;
    }

    @Override
    public int getFactionPoints(final int faction) {
        return this.getData().factionData.getFactionPoints(this.entity, faction);
    }

    @Override
    public float getRotation() {
        return this.entity.yRot;
    }

    @Override
    public void setRotation(final float rotation) {
        this.entity.yRot = rotation;
    }

    @Override
    public void message(final String message) {
        this.entity.sendMessage(new TranslationTextComponent(NoppesStringUtils.formatText(message, this.entity)), Util.NIL_UUID);
    }

    @Override
    public int getGamemode() {
        return this.entity.gameMode.getGameModeForPlayer().getId();
    }

    @Override
    public void setGamemode(final int type) {
        this.entity.setGameMode(GameType.byId(type));
    }

    @Override
    public int inventoryItemCount(final IItemStack item) {
        int count = 0;
        for (int i = 0; i < this.entity.inventory.getContainerSize(); ++i) {
            final ItemStack is = this.entity.inventory.getItem(i);
            if (this.isItemEqual(item.getMCItemStack(), is)) {
                count += is.getCount();
            }
        }
        return count;
    }

    private boolean isItemEqual(final ItemStack stack, final ItemStack other) {
        return !other.isEmpty() && stack.getItem() == other.getItem();
    }

    @Override
    public int inventoryItemCount(final String id) {
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) {
            throw new CustomNPCsException("Unknown item id: " + id);
        }
        return this.inventoryItemCount(NpcAPI.Instance().getIItemStack(new ItemStack(item, 1)));
    }

    @Override
    public IContainer getInventory() {
        if (this.inventory == null) {
            this.inventory = new ContainerWrapper(this.entity.inventory);
        }
        return this.inventory;
    }

    @Override
    public IItemStack getInventoryHeldItem() {
        return NpcAPI.Instance().getIItemStack(this.entity.inventory.getCarried());
    }

    @Override
    public boolean removeItem(final IItemStack item, int amount) {
        final int count = this.inventoryItemCount(item);
        if (amount > count) {
            return false;
        }
        if (count == amount) {
            this.removeAllItems(item);
        }
        else {
            for (int i = 0; i < this.entity.inventory.getContainerSize(); ++i) {
                final ItemStack is = this.entity.inventory.getItem(i);
                if (this.isItemEqual(item.getMCItemStack(), is)) {
                    if (amount < is.getCount()) {
                        is.split(amount);
                        break;
                    }
                    this.entity.inventory.setItem(i, ItemStack.EMPTY);
                    amount -= is.getCount();
                }
            }
        }
        this.updatePlayerInventory();
        return true;
    }

    @Override
    public boolean removeItem(final String id, final int amount) {
        final Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) {
            throw new CustomNPCsException("Unknown item id: " + id);
        }
        return this.removeItem(NpcAPI.Instance().getIItemStack(new ItemStack(item, 1)), amount);
    }

    @Override
    public boolean giveItem(final IItemStack item) {
        final ItemStack mcItem = item.getMCItemStack();
        if (mcItem.isEmpty()) {
            return false;
        }
        final boolean bo = this.entity.inventory.add(mcItem.copy());
        if (bo) {
            NoppesUtilServer.playSound(this.entity, SoundEvents.ITEM_PICKUP, 0.2f, ((this.entity.getRandom().nextFloat() - this.entity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            this.updatePlayerInventory();
        }
        return bo;
    }

    @Override
    public boolean giveItem(final String id, final int amount) {
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) {
            return false;
        }
        final ItemStack mcStack = new ItemStack(item);
        final IItemStack itemStack = NpcAPI.Instance().getIItemStack(mcStack);
        itemStack.setStackSize(amount);
        return this.giveItem(itemStack);
    }

    @Override
    public void updatePlayerInventory() {
        this.entity.inventoryMenu.broadcastChanges();
        this.entity.connection.send(new SSetSlotPacket(-2, this.entity.inventory.selected, this.entity.inventory.getItem(this.entity.inventory.selected)));
        final PlayerQuestData playerdata = this.getData().questData;
        playerdata.checkQuestCompletion(this.entity, 0);
    }

    @Override
    public IBlock getSpawnPoint() {
        final BlockPos pos = this.entity.getSleepingPos().orElse(null);
        if (pos == null) {
            return this.getWorld().getSpawnPoint();
        }
        return NpcAPI.Instance().getIBlock(this.entity.level, pos);
    }

    @Override
    public void setSpawnPoint(final IBlock block) {
        this.setSpawnpoint(block.getX(), block.getY(), block.getZ());
    }

    @Override
    public void setSpawnpoint(int x, int y, int z) {
        x = ValueUtil.CorrectInt(x, -30000000, 30000000);
        z = ValueUtil.CorrectInt(z, -30000000, 30000000);
        y = ValueUtil.CorrectInt(y, 0, 256);
        this.entity.setRespawnPosition(this.getWorld().getMCWorld().dimension(), new BlockPos(x, y, z), 0.0f, true, false);
    }

    @Override
    public void resetSpawnpoint() {
        this.entity.setRespawnPosition(this.getWorld().getMCWorld().dimension(), null, 0.0f, true, false);
    }

    @Override
    public void removeAllItems(final IItemStack item) {
        for (int i = 0; i < this.entity.inventory.getContainerSize(); ++i) {
            final ItemStack is = this.entity.inventory.getItem(i);
            if (is.sameItem(item.getMCItemStack())) {
                this.entity.inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean hasAdvancement(final String achievement) {
        final Advancement advancement = this.entity.getServer().getAdvancements().getAdvancement(new ResourceLocation(achievement));
        if (advancement == null) {
            throw new CustomNPCsException("Advancement doesnt exist");
        }
        final AdvancementProgress progress = this.entity.getServer().getPlayerList().getPlayerAdvancements(this.entity).getOrStartProgress(advancement);
        return progress.isDone();
    }

    @Override
    public int getExpLevel() {
        return this.entity.experienceLevel;
    }

    @Override
    public void setExpLevel(final int level) {
        this.entity.giveExperienceLevels(level - this.entity.experienceLevel);
    }

    @Override
    public void setPosition(final double x, final double y, final double z) {
        SPacketDimensionTeleport.teleportPlayer(this.entity, x, y, z, this.entity.level.dimension());
    }

    @Override
    public void setPos(final IPos pos) {
        SPacketDimensionTeleport.teleportPlayer(this.entity, pos.getX(), pos.getY(), pos.getZ(), this.entity.level.dimension());
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean typeOf(final int type) {
        return type == 1 || super.typeOf(type);
    }

    @Override
    public boolean hasPermission(final String permission) {
        return CustomNpcsPermissions.hasPermissionString(this.entity, permission);
    }

    @Override
    public IPixelmonPlayerData getPixelmonData() {
        if (!PixelmonHelper.Enabled) {
            throw new CustomNPCsException("Pixelmon isnt installed");
        }
        return new IPixelmonPlayerData() {
            @Override
            public Object getParty() {
                if (PlayerWrapper.this.pixelmonPartyStorage == null) {
                    PlayerWrapper.this.pixelmonPartyStorage = PixelmonHelper.getParty(PlayerWrapper.this.entity);
                }
                return PlayerWrapper.this.pixelmonPartyStorage;
            }

            @Override
            public Object getPC() {
                if (PlayerWrapper.this.pixelmonPCStorage == null) {
                    PlayerWrapper.this.pixelmonPCStorage = PixelmonHelper.getPc(PlayerWrapper.this.entity);
                }
                return PlayerWrapper.this.pixelmonPCStorage;
            }
        };
    }

    private PlayerData getData() {
        if (this.data == null) {
            this.data = PlayerData.get(this.entity);
        }
        return this.data;
    }

    @Override
    public ITimers getTimers() {
        return this.getData().timers;
    }

    @Override
    public void removeDialog(final int id) {
        final PlayerData data = this.getData();
        data.dialogData.dialogsRead.remove(id);
        data.updateClient = true;
    }

    @Override
    public void addDialog(final int id) {
        final PlayerData data = this.getData();
        data.dialogData.dialogsRead.add(id);
        data.updateClient = true;
    }

    @Override
    public void closeGui() {
        this.entity.closeContainer();
        Packets.send(this.entity, new PacketGuiClose(new CompoundNBT()));
    }

    @Override
    public int factionStatus(final int factionId) {
        final Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null) {
            throw new CustomNPCsException("Unknown faction: " + factionId);
        }
        return faction.playerStatus(this);
    }

    @Override
    public void kick(final String message) {
        this.entity.connection.disconnect(new TranslationTextComponent(message));
    }

    @Override
    public boolean canQuestBeAccepted(final int questId) {
        return PlayerQuestController.canQuestBeAccepted(this.entity, questId);
    }

    @Override
    public void showCustomGui(final ICustomGui gui) {
        NoppesUtilServer.openContainerGui(this.getMCEntity(), EnumGuiType.CustomGui, buf -> buf.writeInt(gui.getSlots().size()));
        ((ContainerCustomGui)this.getMCEntity().containerMenu).setGui((CustomGuiWrapper)gui, this.entity);
        Packets.sendDelayed(this.getMCEntity(), new PacketGuiData(((CustomGuiWrapper)gui).toNBT()), 100);
    }

    @Override
    public ICustomGui getCustomGui() {
        if (this.entity.containerMenu instanceof ContainerCustomGui) {
            return ((ContainerCustomGui)this.entity.containerMenu).customGui;
        }
        return null;
    }

    @Override
    public void clearData() {
        final PlayerData data = this.getData();
        data.setNBT(new CompoundNBT());
        data.save(true);
    }

    @Override
    public IContainer getOpenContainer() {
        return NpcAPI.Instance().getIContainer(this.entity.containerMenu);
    }

    @Override
    public void playSound(final String sound, final float volume, final float pitch) {
        final BlockPos pos = this.entity.blockPosition();
        Packets.send(this.entity, new PacketPlaySound(sound, pos, volume, pitch));
    }

    @Override
    public void playMusic(final String sound, final boolean background, final boolean loops) {
        Packets.send(this.entity, new PacketPlayMusic(sound, !background, loops));
    }

    @Override
    public void sendMail(final IPlayerMail mail) {
        final PlayerData data = this.getData();
        data.mailData.playermail.add(((PlayerMail)mail).copy());
        data.save(false);
    }

    @Override
    public void trigger(final int id, final Object... arguments) {
        EventHooks.onScriptTriggerEvent(PlayerData.get(this.entity).scriptData, id, this.getWorld(), this.getPos(), null, arguments);
    }

    public String getSkinType(int type) {
        return PlayerSkinController.getInstance().get(entity, type);
    }

    public void setSkinType(String location, int type) {
        PlayerSkinController.getInstance().set(entity, location, type);
    }

    public void setSkin(boolean isSmallArms, int body, int bodyColor, int hair, int hairColor, int face, int eyesColor, int leg, int jacket, int shoes, int ... peculiarities) {
        PlayerSkinController.getInstance().set(entity, isSmallArms, body, bodyColor, hair, hairColor, face, eyesColor, leg, jacket, shoes, peculiarities);
    }
}
