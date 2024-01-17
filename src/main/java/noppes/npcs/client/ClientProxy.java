package noppes.npcs.client;

import noppes.npcs.controllers.data.*;
import net.minecraft.client.settings.*;
import noppes.npcs.mixin.*;
import net.minecraft.client.*;
import net.minecraft.resources.*;
import net.minecraft.client.gui.*;
import net.minecraft.inventory.container.*;
import noppes.npcs.client.gui.custom.*;
import noppes.npcs.client.gui.questtypes.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.client.registry.*;
import noppes.npcs.client.controllers.*;
import noppes.npcs.controllers.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.client.tabs.*;
import java.io.*;
import noppes.npcs.constants.*;
import net.minecraft.network.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.util.text.*;
import noppes.npcs.client.gui.global.*;
import noppes.npcs.client.gui.player.companion.*;
import noppes.npcs.client.gui.mainmenu.*;
import noppes.npcs.client.gui.script.*;
import noppes.npcs.client.gui.roles.*;
import noppes.npcs.client.gui.player.*;
import noppes.npcs.client.gui.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import noppes.npcs.entity.*;
import noppes.npcs.client.fx.*;
import net.minecraft.client.particle.*;
import java.util.*;
import net.minecraft.util.*;
import com.mojang.blaze3d.systems.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.particles.*;
import java.util.function.*;
import noppes.npcs.*;
import java.util.concurrent.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.item.*;
import noppes.npcs.api.*;
import noppes.npcs.api.item.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.model.*;
import noppes.npcs.client.model.*;
import noppes.npcs.client.renderer.*;
import noppes.npcs.shared.client.util.*;
import java.awt.*;
import noppes.npcs.shared.common.util.*;
import com.mojang.blaze3d.matrix.*;

public class ClientProxy extends CommonProxy
{
    public static PlayerData playerData;
    public static KeyBinding QuestLog;
    public static KeyBinding Scene1;
    public static KeyBinding SceneReset;
    public static KeyBinding Scene2;
    public static KeyBinding Scene3;
    public static FontContainer Font;
    public static ModelData data;
    public static PlayerModel playerModel;
    public static ArmorLayerMixin armorLayer;

    @Override
    public void load() {
        ClientProxy.Font = new FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
        this.createFolders();
        final CustomNpcResourceListener listener = new CustomNpcResourceListener();
        ((IReloadableResourceManager)Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
        listener.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
        final SimpleReloadableResourceManager rmanager = (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager();
        rmanager.add(new FolderPack(CustomNpcs.Dir));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcPony, manager -> new RenderNPCPony(manager, new ModelPony()));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcCrystal, manager -> new RenderNpcCrystal(manager, new ModelNpcCrystal()));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcDragon, manager -> new RenderNpcDragon(manager, new ModelNpcDragon(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcSlime, manager -> new RenderNpcSlime(manager, new ModelNpcSlime(16), new ModelNpcSlime(0), 0.25f));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityProjectile, manager -> new RenderProjectile(manager));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityCustomNpc, manager -> new RenderCustomNpc(manager, new PlayerModel(0.0f, false)));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNPC64x32, manager -> new RenderCustomNpc(manager, new ModelPlayer64x32()));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNPCGolem, manager -> new RenderNPCInterface(manager, new ModelNPCGolem(0.0f), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcAlex, manager -> new RenderCustomNpc(manager, new PlayerModel(0.0f, true)));
        RenderingRegistry.registerEntityRenderingHandler(CustomEntities.entityNpcClassicPlayer, manager -> new RenderCustomNpc(manager, new ModelClassicPlayer(0.0f)));
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
        ClientProxy.QuestLog = new KeyBinding("Quest Log", 76, "key.categories.gameplay");
        if (CustomNpcs.SceneButtonsEnabled) {
            ClientProxy.Scene1 = new KeyBinding("Scene1 start/pause", 321, "key.categories.gameplay");
            ClientProxy.Scene2 = new KeyBinding("Scene2 start/pause", 322, "key.categories.gameplay");
            ClientProxy.Scene3 = new KeyBinding("Scene3 start/pause", 323, "key.categories.gameplay");
            ClientProxy.SceneReset = new KeyBinding("Scene reset", 320, "key.categories.gameplay");
            ClientRegistry.registerKeyBinding(ClientProxy.Scene1);
            ClientRegistry.registerKeyBinding(ClientProxy.Scene2);
            ClientRegistry.registerKeyBinding(ClientProxy.Scene3);
            ClientRegistry.registerKeyBinding(ClientProxy.SceneReset);
        }
        ClientRegistry.registerKeyBinding(ClientProxy.QuestLog);
        new PresetController(CustomNpcs.Dir);
        if (CustomNpcs.EnableUpdateChecker) {
            final VersionChecker checker = new VersionChecker();
            checker.start();
        }
        PixelmonHelper.loadClient();
    }

    @Override
    public PlayerData getPlayerData(final PlayerEntity player) {
        if (player.getUUID() == Minecraft.getInstance().player.getUUID()) {
            if (ClientProxy.playerData.player != player) {
                ClientProxy.playerData.player = player;
            }
            return ClientProxy.playerData;
        }
        return null;
    }

    @Override
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
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> 9127187, new IItemProvider[] { (IItemProvider)CustomItems.mount, (IItemProvider)CustomItems.cloner, (IItemProvider)CustomItems.moving, (IItemProvider)CustomItems.scripter, (IItemProvider)CustomItems.wand, (IItemProvider)CustomItems.teleporter });
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
            if (stack.getItem() == CustomItems.scripted_item) {
                final IItemStack item = NpcAPI.Instance().getIItemStack(stack);
                if (!item.isEmpty()) {
                    return ((IItemScripted)item).getColor();
                }
            }
            return -1;
        }, CustomItems.scripted_item);
    }

    private void createFolders() {
        final File file = new File(CustomNpcs.Dir, "assets/customnpcs");
        if (!file.exists()) {
            file.mkdirs();
        }
        File check = new File(file, "sounds");
        if (!check.exists()) {
            check.mkdir();
        }
        final File json = new File(file, "sounds.json");
        if (!json.exists()) {
            try {
                json.createNewFile();
                final BufferedWriter writer = new BufferedWriter(new FileWriter(json));
                writer.write("{\n\n}");
                writer.close();
            }
            catch (IOException ex) {}
        }
        final File meta = new File(CustomNpcs.Dir, "pack.mcmeta");
        if (!meta.exists()) {
            try {
                meta.createNewFile();
                final BufferedWriter writer2 = new BufferedWriter(new FileWriter(meta));
                writer2.write("{\n    \"pack\": {\n        \"description\": \"customnpcs map resource pack\",\n        \"pack_format\": 6\n    }\n}");
                writer2.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        check = new File(file, "textures");
        if (!check.exists()) {
            check.mkdir();
        }
    }

    public static Screen getGui(final EnumGuiType gui, final EntityNPCInterface npc, final PacketBuffer buf) {
        try {
            if (gui == EnumGuiType.MainMenuDisplay) {
                if (npc != null) {
                    return new GuiNpcDisplay(npc);
                }
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Unable to find npc"), Util.NIL_UUID);
            }
            else {
                if (gui == EnumGuiType.MainMenuStats) {
                    return new GuiNpcStats(npc);
                }
                if (gui == EnumGuiType.MainMenuAdvanced) {
                    return new GuiNpcAdvanced(npc);
                }
                if (gui == EnumGuiType.MovingPath) {
                    return new GuiNpcPather(npc);
                }
                if (gui == EnumGuiType.ManageFactions) {
                    return new GuiNPCManageFactions(npc);
                }
                if (gui == EnumGuiType.ManageLinked) {
                    return new GuiNPCManageLinkedNpc(npc);
                }
                if (gui == EnumGuiType.BuilderBlock) {
                    return new GuiBlockBuilder(buf.readBlockPos());
                }
                if (gui == EnumGuiType.ManageTransport) {
                    return new GuiNPCManageTransporters(npc);
                }
                if (gui == EnumGuiType.ManageDialogs) {
                    return new GuiNPCManageDialogs(npc);
                }
                if (gui == EnumGuiType.ManageQuests) {
                    return new GuiNPCManageQuest(npc);
                }
                if (gui == EnumGuiType.Companion) {
                    return new GuiNpcCompanionStats(npc);
                }
                if (gui == EnumGuiType.CompanionTalent) {
                    return new GuiNpcCompanionTalents(npc);
                }
                if (gui == EnumGuiType.MainMenuGlobal) {
                    return new GuiNPCGlobalMainMenu(npc);
                }
                if (gui == EnumGuiType.MainMenuAI) {
                    return new GuiNpcAI(npc);
                }
                if (gui == EnumGuiType.PlayerTransporter) {
                    return new GuiTransportSelection(npc);
                }
                if (gui == EnumGuiType.Script) {
                    return new GuiScript(npc);
                }
                if (gui == EnumGuiType.ScriptBlock) {
                    return new GuiScriptBlock(buf.readBlockPos());
                }
                if (gui == EnumGuiType.ScriptItem) {
                    return new GuiScriptItem(Minecraft.getInstance().player);
                }
                if (gui == EnumGuiType.ScriptDoor) {
                    return new GuiScriptDoor(buf.readBlockPos());
                }
                if (gui == EnumGuiType.ScriptPlayers) {
                    return new GuiScriptGlobal();
                }
                if (gui == EnumGuiType.SetupTransporter) {
                    return new GuiNpcTransporter(npc);
                }
                if (gui == EnumGuiType.SetupBank) {
                    return new GuiNpcBankSetup(npc);
                }
                if (gui == EnumGuiType.NpcRemote && Minecraft.getInstance().screen == null) {
                    return new GuiNpcRemoteEditor();
                }
                if (gui == EnumGuiType.PlayerMailbox) {
                    return new GuiMailbox();
                }
                if (gui == EnumGuiType.NpcDimensions) {
                    return new GuiNpcDimension();
                }
                if (gui == EnumGuiType.Border) {
                    return new GuiBorderBlock(buf.readBlockPos());
                }
                if (gui == EnumGuiType.RedstoneBlock) {
                    return new GuiNpcRedstoneBlock(buf.readBlockPos());
                }
                if (gui == EnumGuiType.MobSpawner) {
                    return new GuiNpcMobSpawner(buf.readBlockPos());
                }
                if (gui == EnumGuiType.CopyBlock) {
                    return new GuiBlockCopy(buf.readBlockPos());
                }
                if (gui == EnumGuiType.MobSpawnerMounter) {
                    return new GuiNpcMobSpawnerMounter();
                }
                if (gui == EnumGuiType.Waypoint) {
                    return new GuiNpcWaypoint(buf.readBlockPos());
                }
                if (gui == EnumGuiType.NbtBook) {
                    return new GuiNbtBook(buf.readBlockPos());
                }
            }
            return null;
        }
        finally {
            if (buf != null) {
                buf.release();
            }
        }
    }

    @Override
    public void openGui(final PlayerEntity player, final EnumGuiType gui) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != player) {
            return;
        }
        final Screen screen = getGui(gui, null, null);
        if (screen != null) {
            minecraft.setScreen(screen);
        }
    }

    @Override
    public void openGui(final EntityNPCInterface npc, final EnumGuiType gui) {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(getGui(gui, npc, null));
    }

    @Override
    public void openGui(final PlayerEntity player, final Object guiscreen) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (!player.level.isClientSide || !(guiscreen instanceof Screen)) {
            return;
        }
        if (guiscreen != null) {
            minecraft.setScreen((Screen)guiscreen);
        }
    }

    @Override
    public void spawnParticle(final LivingEntity player, final String string, final Object... ob) {
        if (string.equals("Block")) {
            final BlockPos pos = (BlockPos)ob[0];
            final BlockState state = (BlockState)ob[1];
            Minecraft.getInstance().particleEngine.destroy(pos, state);
        }
        else if (string.equals("ModelData")) {
            final ModelData data = (ModelData)ob[0];
            final ModelPartData particles = (ModelPartData)ob[1];
            final EntityCustomNpc npc = (EntityCustomNpc)player;
            final Minecraft minecraft = Minecraft.getInstance();
            final double height = npc.getMyRidingOffset() + data.getBodyY();
            final Random rand = npc.getRandom();
            for (int i = 0; i < 2; ++i) {
                final EntityEnderFX fx = new EntityEnderFX(npc, (rand.nextDouble() - 0.5) * player.getBbWidth(), rand.nextDouble() * player.getBbHeight() - height - 0.25, (rand.nextDouble() - 0.5) * player.getBbWidth(), (rand.nextDouble() - 0.5) * 2.0, -rand.nextDouble(), (rand.nextDouble() - 0.5) * 2.0, particles);
                minecraft.particleEngine.add(fx);
            }
        }
    }

    @Override
    public boolean hasClient() {
        return true;
    }

    @Override
    public PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void bind(final ResourceLocation location) {
        try {
            if (location == null) {
                return;
            }
            final TextureManager manager = Minecraft.getInstance().getTextureManager();
            Texture ob = manager.getTexture(location);
            if (ob == null) {
                ob = new SimpleTexture(location);
                manager.register(location, ob);
            }
            RenderSystem.bindTexture(ob.getId());
        }
        catch (NullPointerException ex) {}
    }

    @Override
    public void spawnParticle(final BasicParticleType particle, final double x, final double y, final double z, final double motionX, final double motionY, final double motionZ, final float scale) {
        final Minecraft mc = Minecraft.getInstance();
        final double xx = mc.getCameraEntity().getX() - x;
        final double yy = mc.getCameraEntity().getY() - y;
        final double zz = mc.getCameraEntity().getZ() - z;
        if (xx * xx + yy * yy + zz * zz > 256.0) {
            return;
        }
        final Particle fx = mc.particleEngine.createParticle(particle, x, y, z, motionX, motionY, motionZ);
        if (fx == null) {
            return;
        }
        if (particle == ParticleTypes.FLAME) {
            fx.scale(1.0E-5f);
        }
        else if (particle == ParticleTypes.SMOKE) {
            fx.scale(1.0E-5f);
        }
    }

    @Override
    public Item.Properties getItemProperties() {
        final Supplier<Callable<ItemStackTileEntityRenderer>> teisr = () -> () -> CustomTileEntityItemStackRenderer.i;
        return new Item.Properties().setISTER(teisr).tab(CustomTabs.tab);
    }

    static {
        ClientProxy.playerData = new PlayerData();
    }

    public static class FontContainer
    {
        private TrueTypeFont textFont;
        public boolean useCustomFont;

        private FontContainer() {
            this.textFont = null;
            this.useCustomFont = true;
        }

        public FontContainer(final String fontType, final int fontSize) {
            this.textFont = null;
            this.useCustomFont = true;
            try {
                this.textFont = new TrueTypeFont(new Font(fontType, 0, fontSize), 1.0f);
                this.useCustomFont = !fontType.equalsIgnoreCase("minecraft");
                if (!this.useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default")) {
                    this.textFont = new TrueTypeFont(new ResourceLocation("customnpcs", "opensans.ttf"), fontSize, 1.0f);
                }
            }
            catch (Throwable e) {
                LogWriter.except(e);
                this.useCustomFont = false;
            }
        }

        public int height(final String text) {
            if (this.useCustomFont) {
                return this.textFont.height(text);
            }
            Minecraft.getInstance().font.getClass();
            return 9;
        }

        public int width(final String text) {
            if (this.useCustomFont) {
                return this.textFont.width(text);
            }
            return Minecraft.getInstance().font.width(text);
        }

        public FontContainer copy() {
            final FontContainer font = new FontContainer();
            font.textFont = this.textFont;
            font.useCustomFont = this.useCustomFont;
            return font;
        }

        public void draw(final MatrixStack matrixStack, final String text, final int x, final int y, final int color) {
            if (this.useCustomFont) {
                this.textFont.draw(text, (float)x, (float)y, color);
            }
            else {
                Minecraft.getInstance().font.drawShadow(matrixStack, text, (float)x, (float)y, color);
            }
        }

        public String getName() {
            if (!this.useCustomFont) {
                return "Minecraft";
            }
            return this.textFont.getFontName();
        }

        public void clear() {
            if (this.textFont != null) {
                this.textFont.dispose();
            }
        }
    }
}
