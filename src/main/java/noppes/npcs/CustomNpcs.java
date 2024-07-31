package noppes.npcs;

import net.minecraftforge.fml.common.*;
import java.io.*;
import noppes.npcs.config.*;
import net.minecraft.server.*;
import net.minecraftforge.fml.javafmlmod.*;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.loading.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;

import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.*;
import net.minecraftforge.common.*;
import noppes.npcs.api.*;
import net.minecraft.entity.ai.attributes.*;
import noppes.npcs.controllers.data.*;
import nikedemos.markovnames.generators.*;
import noppes.npcs.api.wrapper.*;
import net.minecraftforge.eventbus.api.*;
import noppes.npcs.controllers.*;
import net.minecraftforge.fml.event.server.*;
import net.minecraft.world.*;
import noppes.npcs.entity.*;
import net.minecraftforge.common.util.*;
import net.minecraft.world.server.*;
import net.minecraft.entity.player.*;
import noppes.npcs.mixin.*;
import com.google.common.collect.*;
import net.minecraft.network.play.server.*;
import net.minecraft.scoreboard.*;
import java.util.*;
import net.minecraftforge.event.*;
import noppes.npcs.command.*;
import net.minecraft.client.*;
import net.minecraft.world.storage.*;
import noppes.npcs.shared.common.util.*;
import java.lang.reflect.*;

import noppes.npcs.client.*;
import net.minecraftforge.fml.*;

@Mod("customnpcs")
public class CustomNpcs
{
    public static final String MODID = "customnpcs";
    public static final String VERSION = "1.16";
    @ConfigProp(info = "Whether scripting is enabled or not")
    public static boolean EnableScripting;
    @ConfigProp(info = "Arguments given to the Nashorn scripting library")
    public static String NashorArguments;
    @ConfigProp(info = "Disable Chat Bubbles")
    public static boolean EnableChatBubbles;
    @ConfigProp(info = "Navigation search range for NPCs. Not recommended to increase if you have a slow pc or on a server")
    public static int NpcNavRange;
    @ConfigProp(info = "Limit too how many npcs can be in one chunk for natural spawning")
    public static int NpcNaturalSpawningChunkLimit;
    @ConfigProp(info = "Set to true if you want the dialog command option to be able to use op commands like tp etc")
    public static boolean NpcUseOpCommands;
    @ConfigProp(info = "If set to true only opped people can use the /noppes command")
    public static boolean NoppesCommandOpOnly;
    @ConfigProp
    public static boolean InventoryGuiEnabled;
    public static boolean FixUpdateFromPre_1_12;
    @ConfigProp(info = "If you are running sponge and you want to disable the permissions set this to true")
    public static boolean DisablePermissions;
    @ConfigProp
    public static boolean SceneButtonsEnabled;
    @ConfigProp
    public static boolean EnableDefaultEyes;
    public static long ticks;
    public static CommonProxy proxy;
    @ConfigProp(info = "Enables CustomNpcs startup update message")
    public static boolean EnableUpdateChecker;
    public static CustomNpcs instance;
    public static boolean FreezeNPCs;
    @ConfigProp(info = "Only ops can create and edit npcs")
    public static boolean OpsOnly;
    @ConfigProp(info = "Default interact line. Leave empty to not have one")
    public static String DefaultInteractLine;
    @ConfigProp(info = "Number of chunk loading npcs that can be active at the same time")
    public static int ChuckLoaders;
    public static File Dir;
    @ConfigProp(info = "Enables leaves decay")
    public static boolean LeavesDecayEnabled;
    @ConfigProp(info = "Enables Vine Growth")
    public static boolean VineGrowthEnabled;
    @ConfigProp(info = "Enables Ice Melting")
    public static boolean IceMeltsEnabled;
    @ConfigProp(info = "Normal players can use soulstone on animals")
    public static boolean SoulStoneAnimals;
    @ConfigProp(info = "Normal players can use soulstone on all npcs")
    public static boolean SoulStoneNPCs;
    @ConfigProp(info = "Type 0 = Normal, Type 1 = Solid")
    public static int HeadWearType;
    @ConfigProp(info = "When set to Minecraft it will use minecrafts font, when Default it will use OpenSans. Can only use fonts installed on your PC")
    public static String FontType;
    @ConfigProp(info = "Font size for custom fonts (doesn't work with minecrafts font)")
    public static int FontSize;
    @ConfigProp(info = "On some servers or with certain plugins, it doesnt work, so you can disable it here")
    public static boolean EnableInvisibleNpcs;
    @ConfigProp
    public static boolean NpcSpeachTriggersChatEvent;
    @ConfigProp(info = "A comma separated list of event packages that will be included into forge events")
    public static String EnabledForgeEventPackages;
    @ConfigProp(info = "Enable new dialog and quest GUI which allow accepting and rejecting quests")
    public static boolean EnableNewDialogSystem;
    public static ConfigLoader Config;
    public static boolean VerboseDebug;
    public static MinecraftServer Server;

    public CustomNpcs() {
        CustomNpcs.instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void postLoad(final FMLLoadCompleteEvent event) {
        CustomNpcs.proxy.postload();
        CustomItems.registerDispenser();
    }

    private void setup(final FMLCommonSetupEvent event) {
        final File dir = new File(FMLPaths.CONFIGDIR.get().toFile(), "..");
        (CustomNpcs.Dir = new File(dir, "customnpcs")).mkdir();
        (CustomNpcs.Config = new ConfigLoader(this.getClass(), new File(dir, "config"), "CustomNpcs")).loadConfig();
        if (CustomNpcs.NpcNavRange < 16) {
            CustomNpcs.NpcNavRange = 16;
        }
        CapabilityManager.INSTANCE.register(PlayerData.class, new Capability.IStorage() {
            public INBT writeNBT(final Capability capability, final Object instance, final Direction side) {
                return null;
            }

            public void readNBT(final Capability capability, final Object instance, final Direction side, final INBT nbt) {
            }
        },PlayerData::new);
        CapabilityManager.INSTANCE.register(WrapperEntityData.class, new Capability.IStorage() {
            public INBT writeNBT(final Capability capability, final Object instance, final Direction side) {
                return null;
            }

            public void readNBT(final Capability capability, final Object instance, final Direction side, final INBT nbt) {
            }
        }, () -> null);
        CapabilityManager.INSTANCE.register(MarkData.class, new Capability.IStorage() {
            public INBT writeNBT(final Capability capability, final Object instance, final Direction side) {
                return null;
            }

            public void readNBT(final Capability capability, final Object instance, final Direction side, final INBT nbt) {
            }
        }, MarkData::new);
        CapabilityManager.INSTANCE.register(ItemStackWrapper.class, new Capability.IStorage<ItemStackWrapper>() {
            public INBT writeNBT(final Capability capability, final ItemStackWrapper instance, final Direction side) {
                return null;
            }

            public void readNBT(final Capability capability, final ItemStackWrapper instance, final Direction side, final INBT nbt) {
            }
        }, () -> null);
        Packets.register();
        MinecraftForge.EVENT_BUS.register(new ServerEventsHandler());
        MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
        MinecraftForge.EVENT_BUS.register(new CustomEntities());
        MinecraftForge.EVENT_BUS.register(CustomNpcs.proxy);
        MinecraftForge.EVENT_BUS.register(this);
        NpcAPI.Instance().events().register(new AbilityEventHandler());
        CustomNpcs.proxy.load();
        PixelmonHelper.load();
        final ScriptController controller = new ScriptController();
        if (CustomNpcs.EnableScripting && !controller.languages.isEmpty()) {
            MinecraftForge.EVENT_BUS.register(controller);
            MinecraftForge.EVENT_BUS.register(new ScriptPlayerEventHandler().registerForgeEvents());
            MinecraftForge.EVENT_BUS.register(new ScriptItemEventHandler());
        }
        setPrivateValue(RangedAttribute.class, (RangedAttribute)Attributes.MAX_HEALTH, Double.MAX_VALUE, 1);
        new RecipeController();
        new CustomNpcsPermissions();
    }

    @SubscribeEvent
    public void setAboutToStart(final FMLServerAboutToStartEvent event) {
        Availability.scores.clear();
        CustomNpcs.Server = event.getServer();
        MarkovGenerator.load();
        ChunkController.instance.clear();
        FactionController.instance.load();
        new PlayerDataController();
        new TransportController();
        new GlobalDataController();
        new SpawnController();
        new LinkedNpcController();
        new MassBlockController();
        VisibilityController.instance = new VisibilityController();
        ScriptController.Instance.loadCategories();
        ScriptController.Instance.loadStoredData();
        ScriptController.Instance.loadPlayerScripts();
        ScriptController.Instance.loadForgeScripts();
        ScriptController.HasStart = false;
        WrapperNpcAPI.clearCache();
        CmdSchematics.names.clear();
        CmdSchematics.names.addAll(SchematicController.Instance.list());
    }

    @SubscribeEvent
    public void started(final FMLServerStartedEvent event) {
        RecipeController.instance.load();
        new BankController();
        DialogController.instance.load();
        QuestController.instance.load();
        ScriptController.HasStart = true;
        ServerCloneController.Instance = new ServerCloneController();
    }

    @SubscribeEvent
    public void stopped(final FMLServerStoppedEvent event) {
        ServerCloneController.Instance = null;
        CustomNpcs.Server = null;
    }

    @SubscribeEvent
    public void serverstart(final FMLServerStartingEvent event) {
        EntityNPCInterface.ChatEventPlayer = new FakePlayer(event.getServer().getLevel(World.OVERWORLD), EntityNPCInterface.ChatEventProfile);
        EntityNPCInterface.CommandPlayer = new FakePlayer(event.getServer().getLevel(World.OVERWORLD), EntityNPCInterface.CommandProfile);
        EntityNPCInterface.GenericPlayer = new FakePlayer(event.getServer().getLevel(World.OVERWORLD), EntityNPCInterface.GenericProfile);
        for (final ServerWorld level : CustomNpcs.Server.getAllLevels()) {
            ServerScoreboard board = level.getScoreboard();
            board.addDirtyListener(() -> {
                Iterator<String> var1 = Availability.scores.iterator();

                while(true) {
                    ScoreObjective so;
                    do {
                        if (!var1.hasNext()) {
                            return;
                        }

                        String objective = var1.next();
                        so = board.getObjective(objective);
                    } while(so == null);

                    for (ServerPlayerEntity player : Server.getPlayerList().getPlayers()) {
                        if (!board.hasPlayerScore(player.getScoreboardName(), so) && board.getObjectiveDisplaySlotCount(so) == 0) {
                            player.connection.send(new SScoreboardObjectivePacket(so, 0));
                        }

                        ScoreBoardMixin mixin = (ScoreBoardMixin) board;
                        Map<ScoreObjective, Score> map = mixin.getScores().computeIfAbsent(player.getScoreboardName(), (p_197898_0_) -> Maps.newHashMap());
                        Score sco = map.computeIfAbsent(so, (ob) -> new Score(board, ob, player.getScoreboardName()));
                        player.connection.send(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, so.getName(), sco.getOwner(), sco.getScore()));
                    }
                }
            });
            board.addDirtyListener(() -> {
                List<ServerPlayerEntity> players = Server.getPlayerList().getPlayers();

                for (ServerPlayerEntity playerMP : players) {
                    VisibilityController.instance.onUpdate(playerMP);
                }

            });
        }
    }

    @SubscribeEvent
    public void registerCommand(final RegisterCommandsEvent e) {
        CmdNoppes.register(e.getDispatcher());
    }

    public static File getWorldSaveDirectory() {
        return getWorldSaveDirectory(null);
    }

    public static File getWorldSaveDirectory(final String s) {
        try {
            File dir = new File(".");
            if (CustomNpcs.Server != null) {
                if (!CustomNpcs.Server.isDedicatedServer()) {
                    dir = new File(Minecraft.getInstance().gameDirectory, "saves");
                }
                dir = CustomNpcs.Server.getWorldPath(new FolderName("customnpcs")).toFile();
            }
            if (s != null) {
                dir = new File(dir, s);
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        }
        catch (Exception e) {
            LogWriter.error("Error getting worldsave", e);
            return null;
        }
    }

    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final int fieldIndex) {
        try {
            final Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (IllegalAccessException e) {
            LogWriter.error("setPrivateValue error", e);
        }
    }

    static {
        CustomNpcs.EnableScripting = true;
        CustomNpcs.NashorArguments = "-strict";
        CustomNpcs.EnableChatBubbles = true;
        CustomNpcs.NpcNavRange = 32;
        CustomNpcs.NpcNaturalSpawningChunkLimit = 4;
        CustomNpcs.NpcUseOpCommands = true;
        CustomNpcs.NoppesCommandOpOnly = false;
        CustomNpcs.InventoryGuiEnabled = true;
        CustomNpcs.FixUpdateFromPre_1_12 = false;
        CustomNpcs.DisablePermissions = false;
        CustomNpcs.SceneButtonsEnabled = true;
        CustomNpcs.EnableDefaultEyes = true;
        CustomNpcs.proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        CustomNpcs.EnableUpdateChecker = true;
        CustomNpcs.FreezeNPCs = false;
        CustomNpcs.OpsOnly = false;
        CustomNpcs.DefaultInteractLine = "Hello @p";
        CustomNpcs.ChuckLoaders = 20;
        CustomNpcs.LeavesDecayEnabled = true;
        CustomNpcs.VineGrowthEnabled = true;
        CustomNpcs.IceMeltsEnabled = true;
        CustomNpcs.SoulStoneAnimals = true;
        CustomNpcs.SoulStoneNPCs = false;
        CustomNpcs.HeadWearType = 1;
        CustomNpcs.FontType = "Minecraft";
        CustomNpcs.FontSize = 18;
        CustomNpcs.EnableInvisibleNpcs = true;
        CustomNpcs.NpcSpeachTriggersChatEvent = false;
        CustomNpcs.VerboseDebug = false;
        CustomNpcs.EnabledForgeEventPackages = "net.minecraftforge.event";
        CustomNpcs.EnableNewDialogSystem = true;
    }
}
