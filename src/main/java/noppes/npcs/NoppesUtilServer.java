package noppes.npcs;

import noppes.npcs.packets.*;
import net.minecraft.nbt.*;
import noppes.npcs.controllers.*;
import net.minecraft.util.math.*;
import noppes.npcs.shared.common.util.*;
import net.minecraft.network.rcon.*;
import net.minecraft.server.*;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.server.*;
import net.minecraft.command.*;
import java.util.zip.*;
import java.io.*;
import noppes.npcs.constants.*;
import noppes.npcs.packets.server.*;
import java.util.function.*;
import net.minecraft.network.*;
import io.netty.buffer.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.container.*;
import javax.annotation.*;
import net.minecraftforge.fml.network.*;
import noppes.npcs.packets.client.*;
import net.minecraft.entity.item.*;
import noppes.npcs.controllers.data.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.*;
import net.minecraft.block.*;
import net.minecraft.util.text.*;
import net.minecraft.entity.*;
import java.util.*;
import noppes.npcs.entity.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraftforge.registries.*;
import net.minecraft.util.*;

public class NoppesUtilServer
{
    private static HashMap<UUID, Quest> editingQuests;
    private static HashMap<UUID, Quest> editingQuestsClient;

    public static void setEditingNpc(final PlayerEntity player, final EntityNPCInterface npc) {
        final PlayerData data = PlayerData.get(player);
        data.editingNpc = npc;
        if (npc != null) {
            Packets.send((ServerPlayerEntity)player, new PacketNpcEdit(npc.getId()));
        }
    }

    public static EntityNPCInterface getEditingNpc(final PlayerEntity player) {
        final PlayerData data = PlayerData.get(player);
        return data.editingNpc;
    }

    public static void setEditingQuest(final PlayerEntity player, final Quest quest) {
        if (player.level.isClientSide) {
            NoppesUtilServer.editingQuestsClient.put(player.getUUID(), quest);
        }
        else {
            NoppesUtilServer.editingQuests.put(player.getUUID(), quest);
        }
    }

    public static Quest getEditingQuest(final PlayerEntity player) {
        if (player.level.isClientSide) {
            return NoppesUtilServer.editingQuestsClient.get(player.getUUID());
        }
        return NoppesUtilServer.editingQuests.get(player.getUUID());
    }

    public static void openDialog(final PlayerEntity player, final EntityNPCInterface npc, final Dialog dia) {
        final Dialog dialog = dia.copy(player);
        final PlayerData playerdata = PlayerData.get(player);
        if (EventHooks.onNPCDialog(npc, player, dialog)) {
            playerdata.dialogId = -1;
            return;
        }
        playerdata.dialogId = dialog.id;
        if (npc instanceof EntityDialogNpc || dia.id < 0) {
            dialog.hideNPC = true;
            Packets.send((ServerPlayerEntity)player, new PacketDialogDummy(npc.getName().getString(), dialog.save(new CompoundNBT())));
        }
        else {
            Packets.send((ServerPlayerEntity)player, new PacketDialog(npc.getId(), dialog.id));
        }
        dia.factionOptions.addPoints(player);
        if (dialog.hasQuest()) {
            PlayerQuestController.addActiveQuest(dialog.getQuest(), player);
        }
        if (!dialog.command.isEmpty()) {
            runCommand(npc, npc.getName().getString(), dialog.command, player);
        }
        if (dialog.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName().getString(), dialog.mail);
        }
        final PlayerDialogData data = playerdata.dialogData;
        if (!data.dialogsRead.contains(dialog.id) && dialog.id >= 0) {
            data.dialogsRead.add(dialog.id);
            playerdata.updateClient = true;
        }
        setEditingNpc(player, npc);
        playerdata.questData.checkQuestCompletion(player, 1);
    }

    public static String runCommand(final Entity executer, final String name, final String command, final PlayerEntity player) {
        return runCommand(executer.getCommandSenderWorld(), executer.blockPosition(), name, command, player, executer);
    }

    public static String runCommand(final World level, final BlockPos pos, final String name, String command, final PlayerEntity player, final Entity executer) {
        if (!level.getServer().isCommandBlockEnabled()) {
            NotifyOPs("Cant run commands if CommandBlocks are disabled");
            LogWriter.warn("Cant run commands if CommandBlocks are disabled");
            return "Cant run commands if CommandBlocks are disabled";
        }
        if (player != null) {
            command = command.replace("@dp", player.getName().getString());
        }
        command = command.replace("@npc", name);
        final StringTextComponent output = new StringTextComponent("");
        final ICommandSource icommandsender = new RConConsoleSource(level.getServer()) {
            public void sendMessage(final ITextComponent component, final UUID senderUUID) {
                output.append(component);
            }

            public boolean acceptsSuccess() {
                return false;
            }

            public boolean shouldInformAdmins() {
                return level.getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
            }

            public boolean acceptsFailure() {
                return true;
            }
        };
        final int permLvl = CustomNpcs.NpcUseOpCommands ? 4 : 2;
        final Vector3d point = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        final CommandSource commandSource = new CommandSource(icommandsender, point, Vector2f.ZERO, (ServerWorld)level, permLvl, "@CustomNPCs-" + name, new StringTextComponent("@CustomNPCs-" + name), level.getServer(), executer) {
            public void sendFailure(final ITextComponent text) {
                super.sendFailure(text);
                NoppesUtilServer.NotifyOPs(text instanceof TextComponent ? (TextComponent) text : new TranslationTextComponent(text.getString()));
            }
        };
        final Commands icommandmanager = level.getServer().getCommands();
        icommandmanager.performCommand(commandSource, command);
        if (output.getString().isEmpty()) {
            return null;
        }
        return output.getString();
    }

    public static void consumeItemStack(final int i, final PlayerEntity player) {
        final ItemStack item = player.inventory.getSelected();
        if (player.abilities.instabuild || item.isEmpty()) {
            return;
        }
        item.shrink(1);
        if (item.getCount() <= 0) {
            player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    public static DataOutputStream getDataOutputStream(final ByteArrayOutputStream stream) throws IOException {
        return new DataOutputStream(new GZIPOutputStream(stream));
    }

    public static void sendOpenGui(final PlayerEntity player, final EnumGuiType gui, final EntityNPCInterface npc) {
        SPacketGuiOpen.sendOpenGui(player, gui, npc, BlockPos.ZERO);
    }

    private static ContainerType getType(final EnumGuiType gui) {
        if (gui == EnumGuiType.PlayerAnvil) {
            return CustomContainer.container_carpentrybench;
        }
        if (gui == EnumGuiType.CustomGui) {
            return CustomContainer.container_customgui;
        }
        if (gui == EnumGuiType.PlayerBankUnlock) {
            return CustomContainer.container_bankunlock;
        }
        if (gui == EnumGuiType.PlayerBankLarge) {
            return CustomContainer.container_banklarge;
        }
        if (gui == EnumGuiType.PlayerBankUprade) {
            return CustomContainer.container_bankupgrade;
        }
        if (gui == EnumGuiType.PlayerBankSmall) {
            return CustomContainer.container_banksmall;
        }
        if (gui == EnumGuiType.PlayerMailman) {
            return CustomContainer.container_mail;
        }
        if (gui == EnumGuiType.MainMenuInv) {
            return CustomContainer.container_inv;
        }
        if (gui == EnumGuiType.QuestItem) {
            return CustomContainer.container_questtypeitem;
        }
        if (gui == EnumGuiType.QuestReward) {
            return CustomContainer.container_questreward;
        }
        if (gui == EnumGuiType.CompanionInv) {
            return CustomContainer.container_companion;
        }
        if (gui == EnumGuiType.PlayerTrader) {
            return CustomContainer.container_trader;
        }
        if (gui == EnumGuiType.PlayerFollower) {
            return CustomContainer.container_follower;
        }
        if (gui == EnumGuiType.PlayerFollowerHire) {
            return CustomContainer.container_followerhire;
        }
        if (gui == EnumGuiType.SetupTrader) {
            return CustomContainer.container_tradersetup;
        }
        if (gui == EnumGuiType.SetupFollower) {
            return CustomContainer.container_followersetup;
        }
        if (gui == EnumGuiType.SetupItemGiver) {
            return CustomContainer.container_itemgiver;
        }
        if (gui == EnumGuiType.ManageBanks) {
            return CustomContainer.container_managebanks;
        }
        return null;
    }

    public static void openContainerGui(final ServerPlayerEntity player, final EnumGuiType gui, final Consumer<PacketBuffer> extraDataWriter) {
        final ContainerType type = getType(gui);
        final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        extraDataWriter.accept(buffer);
        NetworkHooks.openGui(player, new INamedContainerProvider() {
            @Nullable
            public Container createMenu(final int p_createMenu_1_, final PlayerInventory p_createMenu_2_, final PlayerEntity p_createMenu_3_) {
                return type.create(p_createMenu_1_, p_createMenu_2_, buffer);
            }

            public ITextComponent getDisplayName() {
                return new StringTextComponent(gui.name());
            }
        }, extraDataWriter);
    }

    public static void spawnParticle(final Entity entity, final String particle, final int dimension) {
        Packets.sendNearby(entity, new PacketParticle(entity.getX(), entity.getY(), entity.getZ(), entity.getBbHeight(), entity.getBbWidth(), particle));
    }

    public static void sendScrollData(final ServerPlayerEntity player, final Map<String, Integer> map) {
        Packets.send(player, new PacketGuiScrollData(map));
    }

    public static void sendGuiError(final PlayerEntity player, final int i) {
        Packets.send((ServerPlayerEntity)player, new PacketGuiError(i, new CompoundNBT()));
    }

    public static void sendGuiClose(final ServerPlayerEntity player, final int i, final CompoundNBT comp) {
        Packets.send(player, new PacketGuiClose(comp));
    }

    public static boolean isOp(final PlayerEntity player) {
        return player.getServer().getPlayerList().isOp(player.getGameProfile());
    }

    public static void GivePlayerItem(final Entity entity, final PlayerEntity player, ItemStack item) {
        if (entity.level.isClientSide || item == null || item.isEmpty()) {
            return;
        }
        item = item.copy();
        final float f = 0.7f;
        final double d = entity.level.random.nextFloat() * f + (double)(1.0f - f);
        final double d2 = entity.level.random.nextFloat() * f + (double)(1.0f - f);
        final double d3 = entity.level.random.nextFloat() * f + (double)(1.0f - f);
        final ItemEntity entityitem = new ItemEntity(entity.level, entity.getX() + d, entity.getY() + d2, entity.getZ() + d3, item);
        entityitem.setPickUpDelay(2);
        entity.level.addFreshEntity(entityitem);
        if (player.inventory.add(item)) {
            entity.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.take(entityitem, item.getCount());
            final PlayerQuestData playerdata = PlayerData.get(player).questData;
            playerdata.checkQuestCompletion(player, 0);
            if (item.getCount() <= 0) {
                entityitem.remove();
            }
        }
    }

    public static BlockPos GetClosePos(final BlockPos origin, final World level) {
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                for (int y = 2; y >= -2; --y) {
                    final BlockPos pos = origin.offset(x, y, z);
                    final BlockState state = level.getBlockState(pos.above());
                    if (state.isRedstoneConductor(level, pos) && level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2))) {
                        return pos.above();
                    }
                }
            }
        }
        return level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, origin);
    }

    public static void NotifyOPs(final String message, final Object... obs) {
        NotifyOPs(new TranslationTextComponent(message, obs));
    }

    public static void NotifyOPs(final TextComponent message) {
        final ITextComponent chatcomponenttranslation = message.withStyle(TextFormatting.GRAY, TextFormatting.ITALIC);
        for (final PlayerEntity entityplayer : CustomNpcs.Server.getPlayerList().getPlayers()) {
            if (entityplayer.shouldInformAdmins() && isOp(entityplayer)) {
                entityplayer.sendMessage(chatcomponenttranslation, Util.NIL_UUID);
            }
        }
        if (CustomNpcs.Server.getLevel(World.OVERWORLD).getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            LogWriter.info(chatcomponenttranslation.getString());
        }
    }

    public static void playSound(final LivingEntity entity, final SoundEvent sound, final float volume, final float pitch) {
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.NEUTRAL, volume, pitch);
    }

    public static void playSound(final World level, final BlockPos pos, final SoundEvent sound, final SoundCategory cat, final float volume, final float pitch) {
        level.playSound(null, pos, sound, cat, volume, pitch);
    }

    public static PlayerEntity getPlayer(final MinecraftServer minecraftserver, final UUID id) {
        final List<ServerPlayerEntity> list = minecraftserver.getPlayerList().getPlayers();
        for (final PlayerEntity player : list) {
            if (id.equals(player.getUUID())) {
                return player;
            }
        }
        return null;
    }

    public static Entity GetDamageSourcee(final DamageSource damagesource) {
        Entity entity = damagesource.getEntity();
        if (entity == null) {
            entity = damagesource.getDirectEntity();
        }
        if (entity instanceof EntityProjectile && ((EntityProjectile)entity).getOwner() instanceof LivingEntity) {
            entity = ((AbstractArrowEntity)entity).getOwner();
        }
        else if (entity instanceof ThrowableEntity) {
            entity = ((ThrowableEntity)entity).getOwner();
        }
        return entity;
    }

    public static boolean IsItemStackNull(final ItemStack is) {
        return is == null || is.isEmpty() || is == ItemStack.EMPTY;
    }

    public static ItemStack ChangeItemStack(final ItemStack is, final Item item) {
        final CompoundNBT comp = is.save(new CompoundNBT());
        final ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(item);
        comp.putString("id", (resourcelocation == null) ? "minecraft:air" : resourcelocation.toString());
        return ItemStack.of(comp);
    }

    static {
        NoppesUtilServer.editingQuests = new HashMap<UUID, Quest>();
        NoppesUtilServer.editingQuestsClient = new HashMap<UUID, Quest>();
    }
}
