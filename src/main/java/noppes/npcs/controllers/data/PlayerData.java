package noppes.npcs.controllers.data;

import java.io.File;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.NBTJsonUtil;

public class PlayerData implements ICapabilityProvider {
    @CapabilityInject(PlayerData.class)
    public static Capability<PlayerData> PLAYERDATA_CAPABILITY = null;
    public BlockPos scriptBlockPos;
    private LazyOptional<PlayerData> instance;
    public PlayerDialogData dialogData;
    public PlayerBankData bankData;
    public PlayerQuestData questData;
    public PlayerTransportData transportData;
    public PlayerFactionData factionData;
    public PlayerItemGiverData itemgiverData;
    public PlayerMailData mailData;
    public PlayerSkinData skinData;
    public PlayerScriptData scriptData;
    public CompoundNBT scriptStoreddata;
    public DataTimers timers;
    public EntityNPCInterface editingNpc;
    public CompoundNBT cloned;
    public PlayerEntity player;
    public String playername;
    public String uuid;
    private EntityNPCInterface activeCompanion;
    public int companionID;
    public int playerLevel;
    public boolean updateClient;
    public int dialogId;
    public ItemStack prevHeldItem;
    public Entity mounted;
    public UUID iAmStealingYourDatas;
    private static final ResourceLocation key = new ResourceLocation("customnpcs", "playerdata");
    private static PlayerData backup = new PlayerData();

    public PlayerData() {
        this.scriptBlockPos = BlockPos.ZERO;
        this.instance = LazyOptional.of(() -> this);
        this.dialogData = new PlayerDialogData();
        this.bankData = new PlayerBankData();
        this.questData = new PlayerQuestData();
        this.transportData = new PlayerTransportData();
        this.factionData = new PlayerFactionData();
        this.itemgiverData = new PlayerItemGiverData();
        this.mailData = new PlayerMailData();
        this.skinData = new PlayerSkinData();
        this.scriptStoreddata = new CompoundNBT();
        this.timers = new DataTimers(this);
        this.playername = "";
        this.uuid = "";
        this.activeCompanion = null;
        this.companionID = 0;
        this.playerLevel = 0;
        this.updateClient = false;
        this.dialogId = -1;
        this.prevHeldItem = ItemStack.EMPTY;
        this.iAmStealingYourDatas = UUID.randomUUID();
    }

    public void setNBT(CompoundNBT data) {
        this.dialogData.loadNBTData(data);
        this.bankData.loadNBTData(data);
        this.questData.loadNBTData(data);
        this.transportData.loadNBTData(data);
        this.factionData.loadNBTData(data);
        this.itemgiverData.loadNBTData(data);
        this.mailData.loadNBTData(data);
        this.skinData.loadNBTData(data);
        this.timers.load(data);
        if (this.player != null) {
            this.playername = this.player.getName().getString();
            this.uuid = this.player.getUUID().toString();
        } else {
            this.playername = data.getString("PlayerName");
            this.uuid = data.getString("UUID");
        }

        this.companionID = data.getInt("PlayerCompanionId");
        if (data.contains("PlayerCompanion") && !this.hasCompanion() && this.player != null) {
            EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.player.level);
            npc.readAdditionalSaveData(data.getCompound("PlayerCompanion"));
            npc.setPos(this.player.getX(), this.player.getY(), this.player.getZ());
            if (npc.role.getType() == 6) {
                ((RoleCompanion)npc.role).setSitting(false);
                npc.forcedLoading = true;
                this.player.level.addFreshEntity(npc);
                this.setCompanion(npc);
            }
        }

        this.scriptStoreddata = data.getCompound("ScriptStoreddata");
    }

    public CompoundNBT getSyncNBT() {
        CompoundNBT compound = new CompoundNBT();
        this.dialogData.saveNBTData(compound);
        this.questData.saveNBTData(compound);
        this.factionData.saveNBTData(compound);
        return compound;
    }

    public CompoundNBT getNBT() {
        if (this.player != null) {
            this.playername = this.player.getName().getString();
            this.uuid = this.player.getUUID().toString();
        }

        CompoundNBT compound = new CompoundNBT();
        this.dialogData.saveNBTData(compound);
        this.bankData.saveNBTData(compound);
        this.questData.saveNBTData(compound);
        this.transportData.saveNBTData(compound);
        this.factionData.saveNBTData(compound);
        this.itemgiverData.saveNBTData(compound);
        this.mailData.saveNBTData(compound);
        this.skinData.saveNBTData(compound);
        this.timers.save(compound);
        compound.putString("PlayerName", this.playername);
        compound.putString("UUID", this.uuid);
        compound.putInt("PlayerCompanionId", this.companionID);
        compound.put("ScriptStoreddata", this.scriptStoreddata);
        if (this.hasCompanion()) {
            CompoundNBT nbt = new CompoundNBT();
            if (this.activeCompanion.saveAsPassenger(nbt)) {
                compound.put("PlayerCompanion", nbt);
            }
        }

        return compound;
    }

    public boolean hasCompanion() {
        return this.activeCompanion != null && !this.activeCompanion.removed;
    }

    public void setCompanion(EntityNPCInterface npc) {
        if (npc == null || npc.role.getType() == 6) {
            ++this.companionID;
            this.activeCompanion = npc;
            if (npc != null) {
                ((RoleCompanion)npc.role).companionID = this.companionID;
            }

            this.save(false);
        }
    }

    public void updateCompanion(World level) {
        if (this.hasCompanion() && level != this.activeCompanion.level) {
            RoleCompanion role = (RoleCompanion)this.activeCompanion.role;
            role.owner = this.player;
            if (role.isFollowing()) {
                CompoundNBT nbt = new CompoundNBT();
                this.activeCompanion.saveAsPassenger(nbt);
                this.activeCompanion.removed = true;
                EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
                npc.readAdditionalSaveData(nbt);
                npc.setPos(this.player.getX(), this.player.getY(), this.player.getZ());
                this.setCompanion(npc);
                ((RoleCompanion)npc.role).setSitting(false);
                npc.forcedLoading = true;
                level.addFreshEntity(npc);
            }
        }
    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        return capability == PLAYERDATA_CAPABILITY ? this.instance.cast() : LazyOptional.empty();
    }

    public static void register(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(key, new PlayerData());
        }

    }

    public synchronized void save(boolean update) {
        CompoundNBT compound = this.getNBT();
        String filename = this.uuid + ".json";
        CustomNPCsScheduler.runTack(() -> {
            try {
                File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
                File file = new File(saveDir, filename + "_new");
                File file1 = new File(saveDir, filename);
                NBTJsonUtil.SaveFile(file, compound);
                if (file1.exists()) {
                    file1.delete();
                }

                file.renameTo(file1);
            } catch (Exception var5) {
                LogWriter.except(var5);
            }

        });
        if (update) {
            this.updateClient = true;
        }

    }

    public static CompoundNBT loadPlayerData(String player) {
        File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
        String filename = player;
        if (player.isEmpty()) {
            filename = "noplayername";
        }

        filename = filename + ".json";
        File file = null;

        try {
            file = new File(saveDir, filename);
            if (file.exists()) {
                return NBTJsonUtil.LoadFile(file);
            }
        } catch (Exception var5) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), var5);
        }

        return new CompoundNBT();
    }

    public static PlayerData get(PlayerEntity player) {
        if (player.level.isClientSide) {
            return CustomNpcs.proxy.getPlayerData(player);
        } else {
            PlayerData data = player.getCapability(PLAYERDATA_CAPABILITY, null).orElse(backup);
            if (data.player == null) {
                data.player = player;
                data.playerLevel = player.experienceLevel;
                data.scriptData = new PlayerScriptData(player);
                CompoundNBT compound = loadPlayerData(player.getUUID().toString());
                data.setNBT(compound);
            }

            return data;
        }
    }
}
