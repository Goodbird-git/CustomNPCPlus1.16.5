package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import nikedemos.markovnames.generators.MarkovGenerator;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.*;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LRUHashMap;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WrapperNpcAPI extends NpcAPI
{
    private static final Map<DimensionType, WorldWrapper> worldCache;
    public static final IEventBus EVENT_BUS;
    private static NpcAPI instance;

    public static void clearCache() {
        WrapperNpcAPI.worldCache.clear();
        BlockWrapper.clearCache();
    }

    @Override
    public IEntity getIEntity(final Entity entity) {
        if (entity == null || entity.level.isClientSide) {
            return null;
        }
        if (entity instanceof EntityNPCInterface) {
            return ((EntityNPCInterface)entity).wrappedNPC;
        }
        return WrapperEntityData.get(entity);
    }

    @Override
    public ICustomNpc createNPC(final World level) {
        if (level.isClientSide) {
            return null;
        }
        final EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
        return npc.wrappedNPC;
    }

    @Override
    public void registerPermissionNode(final String permission, final int defaultType) {
        if (defaultType < 0 || defaultType > 2) {
            throw new CustomNPCsException("Default type cant be smaller than 0 or larger than 2");
        }
        if (this.hasPermissionNode(permission)) {
            throw new CustomNPCsException("Permission already exists");
        }
        final DefaultPermissionLevel level = DefaultPermissionLevel.values()[defaultType];
        PermissionAPI.registerNode(permission, level, permission);
    }

    @Override
    public boolean hasPermissionNode(final String permission) {
        return PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(permission);
    }

    @Override
    public ICustomNpc spawnNPC(final World level, final int x, final int y, final int z) {
        if (level.isClientSide) {
            return null;
        }
        final EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
        npc.absMoveTo(x + 0.5, y, z + 0.5, 0.0f, 0.0f);
        npc.ais.setStartPos(x, y, z);
        npc.setHealth(npc.getMaxHealth());
        level.addFreshEntity(npc);
        return npc.wrappedNPC;
    }

    public static NpcAPI Instance() {
        if (WrapperNpcAPI.instance == null) {
            WrapperNpcAPI.instance = new WrapperNpcAPI();
        }
        return WrapperNpcAPI.instance;
    }

    @Override
    public IEventBus events() {
        return WrapperNpcAPI.EVENT_BUS;
    }

    @Override
    public IBlock getIBlock(final World level, final BlockPos pos) {
        return BlockWrapper.createNew(level, pos, level.getBlockState(pos));
    }

    @Override
    public IItemStack getIItemStack(final ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty()) {
            return ItemStackWrapper.AIR;
        }
        return (IItemStack)itemstack.getCapability((Capability)ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, null).orElse(ItemStackWrapper.AIR);
    }

    @Override
    public IWorld getIWorld(final ServerWorld level) {
        WorldWrapper w = WrapperNpcAPI.worldCache.get(level.dimensionType());
        if (w != null) {
            w.level = level;
            return w;
        }
        WrapperNpcAPI.worldCache.put(level.dimensionType(), w = WorldWrapper.createNew(level));
        return w;
    }

    @Override
    public IWorld getIWorld(final DimensionType dimension) {
        for (final ServerWorld level : CustomNpcs.Server.getAllLevels()) {
            if (level.dimensionType() == dimension) {
                return this.getIWorld(level);
            }
        }
        throw new CustomNPCsException("Unknown dimension: " + dimension);
    }

    @Override
    public IWorld getIWorld(final String dimension) {
        final ResourceLocation loc = new ResourceLocation(dimension);
        for (final ServerWorld level : CustomNpcs.Server.getAllLevels()) {
            if (level.dimension().location().equals(loc)) {
                return this.getIWorld(level);
            }
        }
        throw new CustomNPCsException("Unknown dimension: " + loc);
    }

    @Override
    public IContainer getIContainer(final IInventory inventory) {
        return new ContainerWrapper(inventory);
    }

    @Override
    public IContainer getIContainer(final Container container) {
        if (container instanceof ContainerNpcInterface) {
            return ContainerNpcInterface.getOrCreateIContainer((ContainerNpcInterface)container);
        }
        return new ContainerWrapper(container);
    }

    @Override
    public IFactionHandler getFactions() {
        this.checkWorld();
        return FactionController.instance;
    }

    private void checkWorld() {
        if (CustomNpcs.Server == null || CustomNpcs.Server.isStopped()) {
            throw new CustomNPCsException("No world is loaded right now");
        }
    }

    @Override
    public IRecipeHandler getRecipes() {
        this.checkWorld();
        return RecipeController.instance;
    }

    @Override
    public IQuestHandler getQuests() {
        this.checkWorld();
        return QuestController.instance;
    }

    @Override
    public IWorld[] getIWorlds() {
        this.checkWorld();
        final List<IWorld> list = new ArrayList<>();
        for (final ServerWorld level : CustomNpcs.Server.getAllLevels()) {
            list.add(this.getIWorld(level));
        }
        return list.toArray(new IWorld[list.size()]);
    }

    @Override
    public IPos getIPos(final double x, final double y, final double z) {
        return new BlockPosWrapper(new BlockPos(x, y, z));
    }

    @Override
    public File getGlobalDir() {
        return CustomNpcs.Dir;
    }

    @Override
    public File getWorldDir() {
        return CustomNpcs.getWorldSaveDirectory();
    }

    @Override
    public INbt getINbt(final CompoundNBT compound) {
        if (compound == null) {
            return new NBTWrapper(new CompoundNBT());
        }
        return new NBTWrapper(compound);
    }

    @Override
    public INbt stringToNbt(final String str) {
        if (str == null || str.isEmpty()) {
            throw new CustomNPCsException("Cant cast empty string to nbt");
        }
        try {
            return this.getINbt(NBTJsonUtil.Convert(str));
        }
        catch (NBTJsonUtil.JsonException e) {
            throw new CustomNPCsException(e, "Failed converting " + str);
        }
    }

    @Override
    public IDamageSource getIDamageSource(final DamageSource damagesource) {
        return new DamageSourceWrapper(damagesource);
    }

    @Override
    public IDialogHandler getDialogs() {
        return DialogController.instance;
    }

    @Override
    public ICloneHandler getClones() {
        return ServerCloneController.Instance;
    }

    @Override
    public String executeCommand(final IWorld level, final String command) {
        final FakePlayer player = EntityNPCInterface.CommandPlayer;
        player.setLevel(level.getMCWorld());
        player.setPos(0.0, 0.0, 0.0);
        return NoppesUtilServer.runCommand(level.getMCWorld(), BlockPos.ZERO, "API", command, null, player);
    }

    @Override
    public INbt getRawPlayerData(final String uuid) {
        return this.getINbt(PlayerData.loadPlayerData(uuid));
    }

    @Override
    public IPlayerMail createMail(final String sender, final String subject) {
        final PlayerMail mail = new PlayerMail();
        mail.sender = sender;
        mail.subject = subject;
        return mail;
    }

    @Override
    public ICustomGui createCustomGui(final int id, final int width, final int height, final boolean pauseGame) {
        return new CustomGuiWrapper(id, width, height, pauseGame);
    }

    @Override
    public String getRandomName(final int dictionary, final int gender) {
        return MarkovGenerator.fetch(dictionary, gender);
    }

    static {
        worldCache = new LRUHashMap<>(10);
        EVENT_BUS = BusBuilder.builder().build();
        WrapperNpcAPI.instance = null;
    }
}
