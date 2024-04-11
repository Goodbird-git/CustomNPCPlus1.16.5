//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package noppes.npcs.client;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabFactions;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import noppes.npcs.CommonProxy;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.fx.EntityEnderFX;
import noppes.npcs.client.gui.GuiBlockBuilder;
import noppes.npcs.client.gui.GuiBlockCopy;
import noppes.npcs.client.gui.GuiBorderBlock;
import noppes.npcs.client.gui.GuiMerchantAdd;
import noppes.npcs.client.gui.GuiNbtBook;
import noppes.npcs.client.gui.GuiNpcDimension;
import noppes.npcs.client.gui.GuiNpcMobSpawner;
import noppes.npcs.client.gui.GuiNpcMobSpawnerMounter;
import noppes.npcs.client.gui.GuiNpcPather;
import noppes.npcs.client.gui.GuiNpcRedstoneBlock;
import noppes.npcs.client.gui.GuiNpcRemoteEditor;
import noppes.npcs.client.gui.GuiNpcWaypoint;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.global.GuiNPCManageBanks;
import noppes.npcs.client.gui.global.GuiNPCManageDialogs;
import noppes.npcs.client.gui.global.GuiNPCManageFactions;
import noppes.npcs.client.gui.global.GuiNPCManageLinkedNpc;
import noppes.npcs.client.gui.global.GuiNPCManageQuest;
import noppes.npcs.client.gui.global.GuiNPCManageTransporters;
import noppes.npcs.client.gui.global.GuiNpcManageRecipes;
import noppes.npcs.client.gui.global.GuiNpcQuestReward;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.mainmenu.GuiNPCInv;
import noppes.npcs.client.gui.mainmenu.GuiNpcAI;
import noppes.npcs.client.gui.mainmenu.GuiNpcAdvanced;
import noppes.npcs.client.gui.mainmenu.GuiNpcDisplay;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import noppes.npcs.client.gui.player.GuiMailbox;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.client.gui.player.GuiNPCTrader;
import noppes.npcs.client.gui.player.GuiNpcCarpentryBench;
import noppes.npcs.client.gui.player.GuiNpcFollower;
import noppes.npcs.client.gui.player.GuiNpcFollowerHire;
import noppes.npcs.client.gui.player.GuiTransportSelection;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem;
import noppes.npcs.client.gui.roles.GuiNpcBankSetup;
import noppes.npcs.client.gui.roles.GuiNpcFollowerSetup;
import noppes.npcs.client.gui.roles.GuiNpcItemGiver;
import noppes.npcs.client.gui.roles.GuiNpcTraderSetup;
import noppes.npcs.client.gui.roles.GuiNpcTransporter;
import noppes.npcs.client.gui.script.GuiScript;
import noppes.npcs.client.gui.script.GuiScriptBlock;
import noppes.npcs.client.gui.script.GuiScriptDoor;
import noppes.npcs.client.gui.script.GuiScriptGlobal;
import noppes.npcs.client.gui.script.GuiScriptItem;
import noppes.npcs.client.model.ModelClassicPlayer;
import noppes.npcs.client.model.ModelNPCGolem;
import noppes.npcs.client.model.ModelNpcCrystal;
import noppes.npcs.client.model.ModelNpcDragon;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.client.model.ModelPlayer64x32;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.client.renderer.RenderNPCPony;
import noppes.npcs.client.renderer.RenderNpcCrystal;
import noppes.npcs.client.renderer.RenderNpcDragon;
import noppes.npcs.client.renderer.RenderNpcSlime;
import noppes.npcs.client.renderer.RenderProjectile;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ArmorLayerMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerSkinSet;
import noppes.npcs.shared.client.util.TrueTypeFont;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.commons.compress.utils.IOUtils;

import javax.imageio.ImageIO;

public class ClientProxy extends CommonProxy {
    public static PlayerData playerData = new PlayerData();
    public static KeyBinding QuestLog;
    public static KeyBinding Scene1;
    public static KeyBinding SceneReset;
    public static KeyBinding Scene2;
    public static KeyBinding Scene3;
    public static FontContainer Font;
    public static ModelData data;
    public static PlayerModel playerModel;
    public static ArmorLayerMixin armorLayer;

    public ClientProxy() {
    }

    public void load() {
        Font = new FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
        this.createFolders();
        CustomNpcResourceListener listener = new CustomNpcResourceListener();
        ((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
        listener.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
        SimpleReloadableResourceManager rmanager = (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager();
        rmanager.add(new FolderPack(CustomNpcs.Dir));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcPony, (manager) -> {
            return new RenderNPCPony(manager, new ModelPony());
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcCrystal, (manager) -> {
            return new RenderNpcCrystal(manager, new ModelNpcCrystal());
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcDragon, (manager) -> {
            return new RenderNpcDragon(manager, new ModelNpcDragon(), 0.5F);
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcSlime, (manager) -> {
            return new RenderNpcSlime(manager, new ModelNpcSlime(16), new ModelNpcSlime(0), 0.25F);
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityProjectile, (manager) -> {
            return new RenderProjectile(manager);
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityCustomNpc, (manager) -> {
            return new RenderCustomNpc(manager, new PlayerModel(0.0F, false));
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNPC64x32, (manager) -> {
            return new RenderCustomNpc(manager, new ModelPlayer64x32());
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNPCGolem, (manager) -> {
            return new RenderNPCInterface(manager, new ModelNPCGolem(0.0F), 0.0F);
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcAlex, (manager) -> {
            return new RenderCustomNpc(manager, new PlayerModel(0.0F, true));
        });
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcClassicPlayer, (manager) -> {
            return new RenderCustomNpc(manager, new ModelClassicPlayer(0.0F));
        });
        ScreenManager.register(CustomContainer.container_carpentrybench, GuiNpcCarpentryBench::new);
        ScreenManager.register(CustomContainer.container_customgui, GuiCustom::new);
        ScreenManager.register(CustomContainer.container_mail, GuiMailmanWrite::new);
        ScreenManager.register(CustomContainer.container_managebanks, GuiNPCManageBanks::new);
        ScreenManager.register(CustomContainer.container_managerecipes, GuiNpcManageRecipes::new);
        ScreenManager.register(CustomContainer.container_merchantadd, GuiMerchantAdd::new);
        ScreenManager.register(CustomContainer.container_banklarge, GuiNPCBankChest::new);
        ScreenManager.register(CustomContainer.container_banksmall, GuiNPCBankChest::new);
        ScreenManager.register(CustomContainer.container_bankunlock, GuiNPCBankChest::new);
        ScreenManager.register(CustomContainer.container_bankupgrade, GuiNPCBankChest::new);
        ScreenManager.register(CustomContainer.container_companion, GuiNpcCompanionInv::new);
        ScreenManager.register(CustomContainer.container_follower, GuiNpcFollower::new);
        ScreenManager.register(CustomContainer.container_followerhire, GuiNpcFollowerHire::new);
        ScreenManager.register(CustomContainer.container_followersetup, GuiNpcFollowerSetup::new);
        ScreenManager.register(CustomContainer.container_inv, GuiNPCInv::new);
        ScreenManager.register(CustomContainer.container_itemgiver, GuiNpcItemGiver::new);
        ScreenManager.register(CustomContainer.container_questreward, GuiNpcQuestReward::new);
        ScreenManager.register(CustomContainer.container_questtypeitem, GuiNpcQuestTypeItem::new);
        ScreenManager.register(CustomContainer.container_trader, GuiNPCTrader::new);
        ScreenManager.register(CustomContainer.container_tradersetup, GuiNpcTraderSetup::new);
        new MusicController();
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        Minecraft mc = Minecraft.getInstance();
        QuestLog = new KeyBinding("Quest Log", 76, "key.categories.gameplay");
        if (CustomNpcs.SceneButtonsEnabled) {
            Scene1 = new KeyBinding("Scene1 start/pause", 321, "key.categories.gameplay");
            Scene2 = new KeyBinding("Scene2 start/pause", 322, "key.categories.gameplay");
            Scene3 = new KeyBinding("Scene3 start/pause", 323, "key.categories.gameplay");
            SceneReset = new KeyBinding("Scene reset", 320, "key.categories.gameplay");
            ClientRegistry.registerKeyBinding(Scene1);
            ClientRegistry.registerKeyBinding(Scene2);
            ClientRegistry.registerKeyBinding(Scene3);
            ClientRegistry.registerKeyBinding(SceneReset);
        }

        ClientRegistry.registerKeyBinding(QuestLog);
        new PresetController(CustomNpcs.Dir);
        if (CustomNpcs.EnableUpdateChecker) {
            VersionChecker checker = new VersionChecker();
            checker.start();
        }

        PixelmonHelper.loadClient();
    }

    public PlayerData getPlayerData(PlayerEntity player) {
        if (player.getUUID() == Minecraft.getInstance().player.getUUID()) {
            if (playerData.player != player) {
                playerData.player = player;
            }

            return playerData;
        } else {
            return null;
        }
    }

    public void postload() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        if (CustomNpcs.InventoryGuiEnabled) {
            TabRegistry.registerEventListeners(MinecraftForge.EVENT_BUS);
            if (TabRegistry.getTabList().isEmpty()) {
                TabRegistry.registerTab(new InventoryTabVanilla());
            }

            TabRegistry.registerTab(new InventoryTabFactions());
            TabRegistry.registerTab(new InventoryTabQuests());
        }

        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            return 9127187;
        }, new IItemProvider[]{CustomItems.mount, CustomItems.cloner, CustomItems.moving, CustomItems.scripter, CustomItems.wand, CustomItems.teleporter});
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (stack.getItem() == CustomItems.scripted_item) {
                IItemStack item = NpcAPI.Instance().getIItemStack(stack);
                if (!item.isEmpty()) {
                    return ((IItemScripted)item).getColor();
                }
            }

            return -1;
        }, new IItemProvider[]{CustomItems.scripted_item});
    }

    private void createFolders() {
        File file = new File(CustomNpcs.Dir, "assets/customnpcs");
        if (!file.exists()) {
            file.mkdirs();
        }

        File check = new File(file, "sounds");
        if (!check.exists()) {
            check.mkdir();
        }

        File json = new File(file, "sounds.json");
        if (!json.exists()) {
            try {
                json.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(json));
                writer.write("{\n\n}");
                writer.close();
            } catch (IOException var7) {
            }
        }

        File meta = new File(CustomNpcs.Dir, "pack.mcmeta");
        if (!meta.exists()) {
            try {
                meta.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(meta));
                writer.write("{\n    \"pack\": {\n        \"description\": \"customnpcs map resource pack\",\n        \"pack_format\": 6\n    }\n}");
                writer.close();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        check = new File(file, "textures");
        if (!check.exists()) {
            check.mkdir();
        }

    }

    public static Screen getGui(EnumGuiType gui, EntityNPCInterface npc, PacketBuffer buf) {
        try {
            GuiNpcDisplay var3;
            if (gui == EnumGuiType.MainMenuDisplay) {
                if (npc != null) {
                    var3 = new GuiNpcDisplay(npc);
                    return var3;
                }

                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Unable to find npc"), Util.NIL_UUID);
            } else {
                if (gui == EnumGuiType.MainMenuStats) {
                    GuiNpcStats var37 = new GuiNpcStats(npc);
                    return var37;
                }

                if (gui == EnumGuiType.MainMenuAdvanced) {
                    GuiNpcAdvanced var36 = new GuiNpcAdvanced(npc);
                    return var36;
                }

                if (gui == EnumGuiType.MovingPath) {
                    GuiNpcPather var35 = new GuiNpcPather(npc);
                    return var35;
                }

                if (gui == EnumGuiType.ManageFactions) {
                    GuiNPCManageFactions var34 = new GuiNPCManageFactions(npc);
                    return var34;
                }

                if (gui == EnumGuiType.ManageLinked) {
                    GuiNPCManageLinkedNpc var33 = new GuiNPCManageLinkedNpc(npc);
                    return var33;
                }

                if (gui == EnumGuiType.BuilderBlock) {
                    GuiBlockBuilder var32 = new GuiBlockBuilder(buf.readBlockPos());
                    return var32;
                }

                if (gui == EnumGuiType.ManageTransport) {
                    GuiNPCManageTransporters var31 = new GuiNPCManageTransporters(npc);
                    return var31;
                }

                if (gui == EnumGuiType.ManageDialogs) {
                    GuiNPCManageDialogs var30 = new GuiNPCManageDialogs(npc);
                    return var30;
                }

                if (gui == EnumGuiType.ManageQuests) {
                    GuiNPCManageQuest var29 = new GuiNPCManageQuest(npc);
                    return var29;
                }

                if (gui == EnumGuiType.Companion) {
                    GuiNpcCompanionStats var28 = new GuiNpcCompanionStats(npc);
                    return var28;
                }

                if (gui == EnumGuiType.CompanionTalent) {
                    GuiNpcCompanionTalents var27 = new GuiNpcCompanionTalents(npc);
                    return var27;
                }

                if (gui == EnumGuiType.MainMenuGlobal) {
                    GuiNPCGlobalMainMenu var26 = new GuiNPCGlobalMainMenu(npc);
                    return var26;
                }

                if (gui == EnumGuiType.MainMenuAI) {
                    GuiNpcAI var25 = new GuiNpcAI(npc);
                    return var25;
                }

                if (gui == EnumGuiType.PlayerTransporter) {
                    GuiTransportSelection var24 = new GuiTransportSelection(npc);
                    return var24;
                }

                if (gui == EnumGuiType.Script) {
                    GuiScript var23 = new GuiScript(npc);
                    return var23;
                }

                if (gui == EnumGuiType.ScriptBlock) {
                    GuiScriptBlock var22 = new GuiScriptBlock(buf.readBlockPos());
                    return var22;
                }

                if (gui == EnumGuiType.ScriptItem) {
                    GuiScriptItem var21 = new GuiScriptItem(Minecraft.getInstance().player);
                    return var21;
                }

                if (gui == EnumGuiType.ScriptDoor) {
                    GuiScriptDoor var20 = new GuiScriptDoor(buf.readBlockPos());
                    return var20;
                }

                if (gui == EnumGuiType.ScriptPlayers) {
                    GuiScriptGlobal var19 = new GuiScriptGlobal();
                    return var19;
                }

                if (gui == EnumGuiType.SetupTransporter) {
                    GuiNpcTransporter var18 = new GuiNpcTransporter(npc);
                    return var18;
                }

                if (gui == EnumGuiType.SetupBank) {
                    GuiNpcBankSetup var17 = new GuiNpcBankSetup(npc);
                    return var17;
                }

                if (gui == EnumGuiType.NpcRemote && Minecraft.getInstance().screen == null) {
                    GuiNpcRemoteEditor var16 = new GuiNpcRemoteEditor();
                    return var16;
                }

                if (gui == EnumGuiType.PlayerMailbox) {
                    GuiMailbox var15 = new GuiMailbox();
                    return var15;
                }

                if (gui == EnumGuiType.NpcDimensions) {
                    GuiNpcDimension var14 = new GuiNpcDimension();
                    return var14;
                }

                if (gui == EnumGuiType.Border) {
                    GuiBorderBlock var13 = new GuiBorderBlock(buf.readBlockPos());
                    return var13;
                }

                if (gui == EnumGuiType.RedstoneBlock) {
                    GuiNpcRedstoneBlock var12 = new GuiNpcRedstoneBlock(buf.readBlockPos());
                    return var12;
                }

                if (gui == EnumGuiType.MobSpawner) {
                    GuiNpcMobSpawner var11 = new GuiNpcMobSpawner(buf.readBlockPos());
                    return var11;
                }

                if (gui == EnumGuiType.CopyBlock) {
                    GuiBlockCopy var10 = new GuiBlockCopy(buf.readBlockPos());
                    return var10;
                }

                if (gui == EnumGuiType.MobSpawnerMounter) {
                    GuiNpcMobSpawnerMounter var9 = new GuiNpcMobSpawnerMounter();
                    return var9;
                }

                if (gui == EnumGuiType.Waypoint) {
                    GuiNpcWaypoint var8 = new GuiNpcWaypoint(buf.readBlockPos());
                    return var8;
                }

                if (gui == EnumGuiType.NbtBook) {
                    GuiNbtBook var7 = new GuiNbtBook(buf.readBlockPos());
                    return var7;
                }
            }

            var3 = null;
            return var3;
        } finally {
            if (buf != null) {
                buf.release();
            }

        }
    }

    public void openGui(PlayerEntity player, EnumGuiType gui) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == player) {
            Screen screen = getGui(gui, (EntityNPCInterface)null, (PacketBuffer)null);
            if (screen != null) {
                minecraft.setScreen(screen);
            }

        }
    }

    public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(getGui(gui, npc, (PacketBuffer)null));
    }

    public void openGui(PlayerEntity player, Object guiscreen) {
        Minecraft minecraft = Minecraft.getInstance();
        if (player.level.isClientSide && guiscreen instanceof Screen) {
            if (guiscreen != null) {
                minecraft.setScreen((Screen)guiscreen);
            }

        }
    }

    public void spawnParticle(LivingEntity player, String string, Object... ob) {
        if (string.equals("Block")) {
            BlockPos pos = (BlockPos)ob[0];
            BlockState state = (BlockState)ob[1];
            Minecraft.getInstance().particleEngine.destroy(pos, state);
        } else if (string.equals("ModelData")) {
            ModelData data = (ModelData)ob[0];
            ModelPartData particles = (ModelPartData)ob[1];
            EntityCustomNpc npc = (EntityCustomNpc)player;
            Minecraft minecraft = Minecraft.getInstance();
            double height = npc.getMyRidingOffset() + (double)data.getBodyY();
            Random rand = npc.getRandom();

            for(int i = 0; i < 2; ++i) {
                EntityEnderFX fx = new EntityEnderFX(npc, (rand.nextDouble() - 0.5) * (double)player.getBbWidth(), rand.nextDouble() * (double)player.getBbHeight() - height - 0.25, (rand.nextDouble() - 0.5) * (double)player.getBbWidth(), (rand.nextDouble() - 0.5) * 2.0, -rand.nextDouble(), (rand.nextDouble() - 0.5) * 2.0, particles);
                minecraft.particleEngine.add(fx);
            }
        }

    }

    public boolean hasClient() {
        return true;
    }

    public PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void bind(ResourceLocation location) {
        try {
            if (location == null) {
                return;
            }

            TextureManager manager = Minecraft.getInstance().getTextureManager();
            Texture ob = manager.getTexture(location);
            if (ob == null) {
                ob = new SimpleTexture(location);
                manager.register(location, (Texture)ob);
            }

            RenderSystem.bindTexture(((Texture)ob).getId());
        } catch (NullPointerException var3) {
        }

    }

    public void spawnParticle(BasicParticleType particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
        Minecraft mc = Minecraft.getInstance();
        double xx = mc.getCameraEntity().getX() - x;
        double yy = mc.getCameraEntity().getY() - y;
        double zz = mc.getCameraEntity().getZ() - z;
        if (!(xx * xx + yy * yy + zz * zz > 256.0)) {
            Particle fx = mc.particleEngine.createParticle(particle, x, y, z, motionX, motionY, motionZ);
            if (fx != null) {
                if (particle == ParticleTypes.FLAME) {
                    fx.scale(1.0E-5F);
                } else if (particle == ParticleTypes.SMOKE) {
                    fx.scale(1.0E-5F);
                }

            }
        }
    }

    public Item.Properties getItemProperties() {
        Supplier<Callable<ItemStackTileEntityRenderer>> teisr = () -> {
            return () -> {
                return CustomTileEntityItemStackRenderer.i;
            };
        };
        return (new Item.Properties()).setISTER(teisr).tab(CustomTabs.tab);
    }

    public static void resetSkin(UUID uuid) {
        PlayerSkinController pData = PlayerSkinController.getInstance();
        if (uuid == null || !pData.playerTextures.containsKey(uuid) || !pData.playerNames.containsKey(uuid)) { return; }
        NetworkPlayerInfo npi = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
        if (npi == null) { return; }
        Map<MinecraftProfileTexture.Type, ResourceLocation> map = PlayerSkinController.getInstance().playerTextures.get(uuid);
        Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = npi.textureLocations; //ObfuscationHelper.getValue(NetworkPlayerInfo.class, npi, Map.class);
        playerTextures.clear();
        for (MinecraftProfileTexture.Type epst : map.keySet()) {
            ResourceLocation loc = ClientProxy.createPlayerSkin(map.get(epst));
            if (loc == null) { continue; }
            LogWriter.debug("Set skin type: "+epst+" = \""+loc+"\"");
            switch(epst) {
                case CAPE: playerTextures.put(MinecraftProfileTexture.Type.CAPE, loc); break;
                case ELYTRA: playerTextures.put(MinecraftProfileTexture.Type.ELYTRA, loc); break;
                default: {
                    playerTextures.put(MinecraftProfileTexture.Type.SKIN, loc);
                    TextureManager re = Minecraft.getInstance().getTextureManager();
                    ResourceLocation locDynamic = new ResourceLocation("minecraft", "dynamic/skin_"+pData.playerNames.get(uuid));
                    ResourceLocation locSkins = new ResourceLocation("minecraft", "skins/"+pData.playerNames.get(uuid));
                    Texture texture= re.getTexture(loc);
                    Map<ResourceLocation, Texture> mapTextureObjects = re.byPath;// ObfuscationHelper.getValue(TextureManager.class, re, Map.class);
                    mapTextureObjects.put(locDynamic, texture);
                    mapTextureObjects.put(locSkins, texture);
                    re.byPath = mapTextureObjects;
                    //ObfuscationHelper.setValue(TextureManager.class, , mapTextureObjects, Map.class);
                    break;
                }
            }
        }
        if (!playerTextures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            playerTextures.put(MinecraftProfileTexture.Type.SKIN, DefaultPlayerSkin.getDefaultSkin(npi.getProfile().getId()));
        }
        LogWriter.debug("Set skins to player UUID: "+uuid);
    }

//    @Override
//    public void checkTexture(EntityNPCInterface npc) {
//        if (npc.display.skinType != 0) { return; }
//        ClientProxy.createPlayerSkin(new ResourceLocation(npc.display.getSkinTexture()));
//    }

    private static ResourceLocation createPlayerSkin(ResourceLocation skin) {
        LogWriter.debug("Check skin: "+skin);
        if (!skin.getNamespace().equals(CustomNpcs.MODID) ||
                (!skin.getPath().toLowerCase().contains("textures/entity/custom/female_") &&
                        !skin.getPath().toLowerCase().contains("textures/entity/custom/male_"))) { return skin; }

        String locSkin = String.format("%s/%s/%s", "assets", skin.getNamespace(), skin.getPath());
        File file = new File(CustomNpcs.Dir, locSkin);
//CustomNpcs.proxy.getPlayer().sendMessage(new TextComponentString("File: "+file.getParentFile().exists()+", "+file.exists()+" - "+file));
        if (!file.getParentFile().exists()) { file.getParentFile().mkdirs(); }
        if (file.exists() && file.isFile()) { return skin; }

        TextureManager re = Minecraft.getInstance().getTextureManager();
        IResourceManager rm = Minecraft.getInstance().getResourceManager();
        String[] path = skin.getPath().replace(".png", "").split("_");
        String gender = "male";
        BufferedImage bodyImage = null, hairImage = null, faseImage = null, legsImage = null, jacketsImage = null, shoesImage = null;
        List<BufferedImage> listBuffers = Lists.newArrayList();
        for (int i = 0; i < path.length; i++) {
            switch(i) {
                case 0: {
                    if (path[i].toLowerCase().endsWith("female")) { gender = "female"; }
                    break;
                }
                case 1: { // body skin
                    ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/torsos/"+path[i]+".png");
                    re.bind(loc);
                    if (re.getTexture(loc) == null) {
                        loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/torsos/0.png");
                        re.bind(loc);
                    }
                    if (re.getTexture(loc) != null) {
                        try { bodyImage = readBufferedImage(rm.getResource(loc).getInputStream()); }
                        catch (Exception e) {  }
                    }
                    break;
                }
                case 2: { // create body
                    try {
                        int c = Integer.parseInt(path[i]);
                        if (c != 0) { bodyImage = colorTexture(bodyImage, new Color(c), false); }
                    }
                    catch (Exception e) {  }
                    break;
                }
                case 3: { // hair skin
                    ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/hairs/"+path[i]+".png");
                    re.bind(loc);
                    if (re.getTexture(loc) != null) {
                        try { hairImage = readBufferedImage(rm.getResource(loc).getInputStream()); }
                        catch (Exception e) {  }
                    }
                    break;
                }
                case 4: { // create hair
                    try {
                        int c = Integer.parseInt(path[i]);
                        if (c != 0) { hairImage = colorTexture(hairImage, new Color(c), false); }
                    }
                    catch (Exception e) {  }
                    break;
                }
                case 5: { // fase
                    ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/faces/"+path[i]+".png");
                    re.bind(loc);
                    if (re.getTexture(loc) != null) {
                        try { faseImage = readBufferedImage(rm.getResource(loc).getInputStream()); }
                        catch (Exception e) {  }
                    }
                    break;
                }
                case 6: { // create fase
                    try {
                        int c = Integer.parseInt(path[i]);
                        if (c != 0) { faseImage = colorTexture(faseImage, new Color(c), true); }
                    }
                    catch (Exception e) {  }
                    break;
                }
                case 7: { // legs
                    ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/legs/"+path[i]+".png");
                    re.bind(loc);
                    if (re.getTexture(loc) != null) {
                        try { legsImage = readBufferedImage(rm.getResource(loc).getInputStream()); }
                        catch (Exception e) {  }
                    }
                    break;
                }
                case 8: { // jacket
                    ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/jackets/"+path[i]+".png");
                    re.bind(loc);
                    if (re.getTexture(loc) != null) {
                        try { jacketsImage = readBufferedImage(rm.getResource(loc).getInputStream()); }
                        catch (Exception e) {  }
                    }
                    break;
                }
                case 9: { // shoes
                    ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/shoes/"+path[i]+".png");
                    re.bind(loc);
                    if (re.getTexture(loc) != null) {
                        try { shoesImage = readBufferedImage(rm.getResource(loc).getInputStream()); }
                        catch (Exception e) {  }
                    }
                    break;
                }
                default: { break; }
            }
        }
        ByteBuffer buff;
        // combine
        BufferedImage skinImage = null;
        try {
            skinImage = combineTextures(bodyImage, readBufferedImage(rm.getResource(new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/"+gender+"/torsos/-1.png")).getInputStream()));
            if (!listBuffers.isEmpty()) {
                for (BufferedImage buffer : listBuffers) { skinImage = combineTextures(skinImage, buffer); }
            }
            skinImage = combineTextures(skinImage, faseImage);
            skinImage = combineTextures(skinImage, legsImage);
            skinImage = combineTextures(skinImage, shoesImage);
            skinImage = combineTextures(skinImage, jacketsImage);
            skinImage = combineTextures(skinImage, faseImage);
            skinImage = combineTextures(skinImage, hairImage);
        }
        catch (Exception e) {  }

        try {
            ImageIO.write(skinImage, "PNG", file);
            re.bind(skin);
            LogWriter.debug("Create new player skin: "+file.getAbsolutePath());
        }
        catch (Exception e) {  }

		/*if (rm instanceof SimpleReloadableResourceManager) {
			Map<String, FallbackResourceManager> domainResourceManagers = ObfuscationHelper.getValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager) rm, Map.class);
			FallbackResourceManager modf = domainResourceManagers.get(CustomNpcs.MODID);
			if (modf != null) {
				List<IResourcePack> resourcePacks = ObfuscationHelper.getValue(FallbackResourceManager.class, modf, List.class);
				AbstractResourcePack pack = null;
				for (IResourcePack iPack: resourcePacks) {
					if (iPack instanceof FMLFolderResourcePack) {
						pack = (AbstractResourcePack) iPack;
						break;
					}
				}
				if (pack != null) {
					File resourcePackFile = ObfuscationHelper.getValue(AbstractResourcePack.class, pack, File.class);
					try {
						String locSkin = String.format("%s/%s/%s", "assets", skin.getResourceDomain(), skin.getResourcePath());
						ImageIO.write(skinImage, "PNG", new File(resourcePackFile, locSkin));
					}
					catch (Exception e) { e.printStackTrace(); }
				}
			}
		}*/

        Map<ResourceLocation, Texture> mapTextureObjects = re.byPath;// ObfuscationHelper.getValue(TextureManager.class, re, Map.class);
        SimpleTexture texture = new SimpleTexture(skin);
        TextureUtil.prepareImage(texture.getId(), skinImage.getWidth(), skinImage.getHeight());
        uploadBufferedImageContents(skinImage, texture.getId());
        mapTextureObjects.put(skin, texture);
        re.byPath = mapTextureObjects;
        //ObfuscationHelper.setValue(TextureManager.class, re, mapTextureObjects, Map.class);
        return skin;
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage;

        try
        {
            bufferedimage = ImageIO.read(imageStream);
        }
        finally
        {
            IOUtils.closeQuietly(imageStream);
        }

        return bufferedimage;
    }

    public static void uploadBufferedImageContents(BufferedImage bufferedimage, int id) {
        int j = bufferedimage.getWidth();
        int k = bufferedimage.getHeight();
        int[] lvt_8_1_ = new int[j * k];
        bufferedimage.getRGB(0, 0, j, k, lvt_8_1_, 0, j);
        IntBuffer intbuffer = ByteBuffer.allocateDirect(4 * j * k).order(ByteOrder.nativeOrder()).asIntBuffer();
        intbuffer.put(lvt_8_1_);
        intbuffer.flip();

        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(id);
        TextureUtil.initTexture(intbuffer, j, k);
    }

    private static BufferedImage colorTexture(BufferedImage buffer, Color color, boolean onlyGray) {
        if (buffer == null || color == null) { return buffer; }
        for (int v = 0; v < buffer.getHeight(); v++) {
            for (int u = 0; u < buffer.getWidth(); u++) {
                int c = buffer.getRGB(u, v);
                int al = c >> 24 & 255;
                if (al == 0) { continue; }
                if (onlyGray) {
                    Color k = new Color(c);
                    if (k.getRed() != 127 || k.getGreen() != 127 || k.getBlue() != 127) { continue; }
                    buffer.setRGB(u, v, color.getRGB());
                    continue;
                }
                int r0 = c >> 16 & 255, g0 = c >> 8 & 255, b0 = c & 255;
                String a = Integer.toHexString((al + color.getAlpha()) > 255 ? 255 : (al + color.getAlpha()));
                if (a.length() == 1) { a = "0" + a; }
                String r = Integer.toHexString((r0 + color.getRed()) / 2);
                if (r.length() == 1) { r = "0" + r; }
                String g = Integer.toHexString((g0 + color.getGreen()) / 2);
                if (g.length() == 1) { g = "0" + g; }
                String b = Integer.toHexString((b0 + color.getBlue()) / 2);
                if (b.length() == 1) { b = "0" + b; }
                buffer.setRGB(u, v, (int) Long.parseLong(a+r+g+b, 16));
            }
        }
        return buffer;
    }

    private static BufferedImage combineTextures(BufferedImage buffer_0, BufferedImage buffer_1) {
        if (buffer_0 == null) { return buffer_1; }
        if (buffer_1 == null) { return buffer_0; }
        int w0 = buffer_0.getWidth(), w1 = buffer_1.getWidth();
        int h0 = buffer_0.getHeight(), h1 = buffer_1.getHeight();
        int w = w0 >= w1 ? w0 : w1;
        int h = h0 >= h1 ? h0 : h1;
        float sw0 = (float) w0 / (float) w, sh0 = (float) h0 / (float) h;
        float sw1 = (float) w1 / (float) w, sh1 = (float) h1 / (float) h;
        BufferedImage total = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        for (int v = 0; v < h; v++) {
            for (int u = 0; u < w; u++) {
                int c0 = buffer_0.getRGB((int) ((float) u * sw0), (int) ((float) v * sh0));
                int a0 = c0 >> 24 & 255;
                if (a0 != 0) { total.setRGB(u, v, c0); }
                int c1 = buffer_1.getRGB((int) ((float) u * sw1), (int) ((float) v * sh1));
                int a1 = c1 >> 24 & 255;
                if (a1 != 0) {
                    if (a1 == 255) { total.setRGB(u, v, c1); }
                    else {
                        int r0 = c0 >> 16 & 255, g0 = c0 >> 8 & 255, b0 = c0 & 255;
                        int r1 = c1 >> 16 & 255, g1 = c1 >> 8 & 255, b1 = c1 & 255;
                        String a = Integer.toHexString((a0 + a1) > 255 ? 255 : (a0 + a1));
                        if (a.length() == 1) { a = "0" + a; }
                        String r = Integer.toHexString((r0 + r1) / 2);
                        if (r.length() == 1) { r = "0" + r; }
                        String g = Integer.toHexString((g0 + g1) / 2);
                        if (g.length() == 1) { g = "0" + g; }
                        String b = Integer.toHexString((b0 + b1) / 2);
                        if (b.length() == 1) { b = "0" + b; }
                        total.setRGB(u, v, (int) Long.parseLong(a+r+g+b, 16));
                    }
                }
            }
        }
        return total;
    }

    public static void sendSkin(UUID uuid) {
        NetworkPlayerInfo npi = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
        if (npi == null) { return; }
        CompoundNBT nbtPlayer = new CompoundNBT();
        nbtPlayer.putUUID("UUID", uuid);
        ListNBT listTxrs = new ListNBT();
        for (MinecraftProfileTexture.Type t : MinecraftProfileTexture.Type.values()) {
            ResourceLocation loc;
            switch(t) {
                case CAPE: loc = npi.getCapeLocation(); break;
                case ELYTRA: loc = npi.getElytraLocation(); break;
                default: loc = npi.getSkinLocation(); break; // SKIN
            }
            if (loc == null) { continue; }
            CompoundNBT nbtSkin = new CompoundNBT();
            nbtSkin.putString("Type", t.name());
            nbtSkin.putString("Location", loc.toString());
            listTxrs.add(nbtSkin);
        }
        nbtPlayer.put("Textures", listTxrs);
        Packets.sendServer(new SPacketPlayerSkinSet(nbtPlayer));
    }

    public static class FontContainer {
        private TrueTypeFont textFont = null;
        public boolean useCustomFont = true;

        private FontContainer() {
        }

        public FontContainer(String fontType, int fontSize) {
            try {
                this.textFont = new TrueTypeFont(new Font(fontType, 0, fontSize), 1.0F);
                this.useCustomFont = !fontType.equalsIgnoreCase("minecraft");
                if (!this.useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default")) {
                    this.textFont = new TrueTypeFont(new ResourceLocation("customnpcs", "opensans.ttf"), fontSize, 1.0F);
                }
            } catch (Throwable var4) {
                LogWriter.except(var4);
                this.useCustomFont = false;
            }

        }

        public int height(String text) {
            if (this.useCustomFont) {
                return this.textFont.height(text);
            } else {
                Minecraft.getInstance().font.getClass();
                return 9;
            }
        }

        public int width(String text) {
            return this.useCustomFont ? this.textFont.width(text) : Minecraft.getInstance().font.width(text);
        }

        public FontContainer copy() {
            FontContainer font = new FontContainer();
            font.textFont = this.textFont;
            font.useCustomFont = this.useCustomFont;
            return font;
        }

        public void draw(MatrixStack matrixStack, String text, int x, int y, int color) {
            if (this.useCustomFont) {
                this.textFont.draw(text, (float)x, (float)y, color);
            } else {
                Minecraft.getInstance().font.drawShadow(matrixStack, text, (float)x, (float)y, color);
            }

        }

        public String getName() {
            return !this.useCustomFont ? "Minecraft" : this.textFont.getFontName();
        }

        public void clear() {
            if (this.textFont != null) {
                this.textFont.dispose();
            }

        }
    }
}
