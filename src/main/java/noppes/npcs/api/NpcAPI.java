package noppes.npcs.api;

import net.minecraft.entity.*;
import noppes.npcs.api.entity.*;
import net.minecraft.util.math.*;
import noppes.npcs.api.block.*;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.*;
import net.minecraft.item.*;
import noppes.npcs.api.item.*;
import net.minecraft.world.server.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import noppes.npcs.api.handler.*;
import net.minecraft.util.*;
import noppes.npcs.api.entity.data.*;
import noppes.npcs.api.gui.*;
import net.minecraftforge.eventbus.api.*;
import java.io.*;
import net.minecraftforge.fml.*;

public abstract class NpcAPI
{
    private static NpcAPI instance;

    public abstract ICustomNpc createNPC(final World p0);

    public abstract ICustomNpc spawnNPC(final World p0, final int p1, final int p2, final int p3);

    public abstract IEntity getIEntity(final Entity p0);

    public abstract IBlock getIBlock(final World p0, final BlockPos p1);

    public abstract IContainer getIContainer(final IInventory p0);

    public abstract IContainer getIContainer(final Container p0);

    public abstract IItemStack getIItemStack(final ItemStack p0);

    public abstract IWorld getIWorld(final ServerWorld p0);

    public abstract IWorld getIWorld(final String p0);

    public abstract IWorld getIWorld(final DimensionType p0);

    public abstract IWorld[] getIWorlds();

    public abstract INbt getINbt(final CompoundNBT p0);

    public abstract IPos getIPos(final double p0, final double p1, final double p2);

    public abstract IFactionHandler getFactions();

    public abstract IRecipeHandler getRecipes();

    public abstract IQuestHandler getQuests();

    public abstract IDialogHandler getDialogs();

    public abstract ICloneHandler getClones();

    public abstract IDamageSource getIDamageSource(final DamageSource p0);

    public abstract INbt stringToNbt(final String p0);

    public abstract IPlayerMail createMail(final String p0, final String p1);

    public abstract ICustomGui createCustomGui(final int p0, final int p1, final int p2, final boolean p3);

    public abstract INbt getRawPlayerData(final String p0);

    public abstract IEventBus events();

    public abstract File getGlobalDir();

    public abstract File getWorldDir();

    public static boolean IsAvailable() {
        return ModList.get().isLoaded("customnpcs");
    }

    public static NpcAPI Instance() {
        if (NpcAPI.instance != null) {
            return NpcAPI.instance;
        }
        if (!IsAvailable()) {
            return null;
        }
        try {
            final Class c = Class.forName("noppes.npcs.api.wrapper.WrapperNpcAPI");
            NpcAPI.instance = (NpcAPI)c.getMethod("Instance", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return NpcAPI.instance;
    }

    public abstract void registerPermissionNode(final String p0, final int p1);

    public abstract boolean hasPermissionNode(final String p0);

    public abstract String executeCommand(final IWorld p0, final String p1);

    public abstract String getRandomName(final int p0, final int p1);

    static {
        NpcAPI.instance = null;
    }
}
