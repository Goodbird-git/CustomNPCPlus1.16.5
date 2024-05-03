//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package noppes.npcs.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.CNpcsNetworkHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.CustomNPCsScheduler;

public class Packets {
    private static final String PROTOCOL = "CNPCS";
    public static SimpleChannel Channel;
    public static int index = 0;

    public Packets() {
    }

    public static void register() {
        Channel = ChannelBuilder.named(new ResourceLocation("customnpcs", "packets")).clientAcceptedVersions("CNPCS"::equals).serverAcceptedVersions("CNPCS"::equals).networkProtocolVersion(() -> "CNPCS").simpleChannel();
        index = 0;
        Channel.registerMessage(index++, PacketAchievement.class, PacketAchievement::encode, PacketAchievement::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketChat.class, PacketChat::encode, PacketChat::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketChatBubble.class, PacketChatBubble::encode, PacketChatBubble::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketConfigFont.class, PacketConfigFont::encode, PacketConfigFont::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketDialog.class, PacketDialog::encode, PacketDialog::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketDialogDummy.class, PacketDialogDummy::encode, PacketDialogDummy::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketEyeBlink.class, PacketEyeBlink::encode, PacketEyeBlink::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiCloneOpen.class, PacketGuiCloneOpen::encode, PacketGuiCloneOpen::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiClose.class, PacketGuiClose::encode, PacketGuiClose::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiData.class, PacketGuiData::encode, PacketGuiData::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiError.class, PacketGuiError::encode, PacketGuiError::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiOpen.class, PacketGuiOpen::encode, PacketGuiOpen::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiScrollData.class, PacketGuiScrollData::encode, PacketGuiScrollData::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiScrollList.class, PacketGuiScrollList::encode, PacketGuiScrollList::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiScrollSelected.class, PacketGuiScrollSelected::encode, PacketGuiScrollSelected::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketGuiUpdate.class, PacketGuiUpdate::encode, PacketGuiUpdate::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketItemUpdate.class, PacketItemUpdate::encode, PacketItemUpdate::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketMarkData.class, PacketMarkData::encode, PacketMarkData::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketNpcDelete.class, PacketNpcDelete::encode, PacketNpcDelete::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketNpcEdit.class, PacketNpcEdit::encode, PacketNpcEdit::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketNpcRole.class, PacketNpcRole::encode, PacketNpcRole::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketNpcUpdate.class, PacketNpcUpdate::encode, PacketNpcUpdate::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketParticle.class, PacketParticle::encode, PacketParticle::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketPlayMusic.class, PacketPlayMusic::encode, PacketPlayMusic::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketPlaySound.class, PacketPlaySound::encode, PacketPlaySound::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketQuestCompletion.class, PacketQuestCompletion::encode, PacketQuestCompletion::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketSync.class, PacketSync::encode, PacketSync::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketSyncRemove.class, PacketSyncRemove::encode, PacketSyncRemove::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketSyncUpdate.class, PacketSyncUpdate::encode, PacketSyncUpdate::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketNpcVisibleFalse.class, PacketNpcVisibleFalse::encode, PacketNpcVisibleFalse::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketNpcVisibleTrue.class, PacketNpcVisibleTrue::encode, PacketNpcVisibleTrue::decode, PacketBasic::handle);
        Channel.registerMessage(index++, PacketSyncSkin.class, PacketSyncSkin::encode, PacketSyncSkin::decode, PacketSyncSkin::handle);

        Channel.registerMessage(index++, SPacketBankGet.class, SPacketBankGet::encode, SPacketBankGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketBankRemove.class, SPacketBankRemove::encode, SPacketBankRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketBankSave.class, SPacketBankSave::encode, SPacketBankSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketBanksGet.class, SPacketBanksGet::encode, SPacketBanksGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketBanksSlotOpen.class, SPacketBanksSlotOpen::encode, SPacketBanksSlotOpen::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketBankUnlock.class, SPacketBankUnlock::encode, SPacketBankUnlock::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketBankUpgrade.class, SPacketBankUpgrade::encode, SPacketBankUpgrade::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCloneList.class, SPacketCloneList::encode, SPacketCloneList::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCloneNameCheck.class, SPacketCloneNameCheck::encode, SPacketCloneNameCheck::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCloneRemove.class, SPacketCloneRemove::encode, SPacketCloneRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCloneSave.class, SPacketCloneSave::encode, SPacketCloneSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCompanionOpenInv.class, SPacketCompanionOpenInv::encode, SPacketCompanionOpenInv::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCompanionTalentExp.class, SPacketCompanionTalentExp::encode, SPacketCompanionTalentExp::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketDialogCategoryRemove.class, SPacketDialogCategoryRemove::encode, SPacketDialogCategoryRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketDialogRemove.class, SPacketDialogRemove::encode, SPacketDialogRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketDialogSelected.class, SPacketDialogSelected::encode, SPacketDialogSelected::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketDimensionsGet.class, SPacketDimensionsGet::encode, SPacketDimensionsGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketDimensionTeleport.class, SPacketDimensionTeleport::encode, SPacketDimensionTeleport::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFactionGet.class, SPacketFactionGet::encode, SPacketFactionGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFactionRemove.class, SPacketFactionRemove::encode, SPacketFactionRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFactionSave.class, SPacketFactionSave::encode, SPacketFactionSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFactionsGet.class, SPacketFactionsGet::encode, SPacketFactionsGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFollowerExtend.class, SPacketFollowerExtend::encode, SPacketFollowerExtend::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFollowerHire.class, SPacketFollowerHire::encode, SPacketFollowerHire::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketFollowerState.class, SPacketFollowerState::encode, SPacketFollowerState::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketGuiOpen.class, SPacketGuiOpen::encode, SPacketGuiOpen::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketLinkedAdd.class, SPacketLinkedAdd::encode, SPacketLinkedAdd::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketLinkedGet.class, SPacketLinkedGet::encode, SPacketLinkedGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketLinkedRemove.class, SPacketLinkedRemove::encode, SPacketLinkedRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketLinkedSet.class, SPacketLinkedSet::encode, SPacketLinkedSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketMailSetup.class, SPacketMailSetup::encode, SPacketMailSetup::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketMenuClose.class, SPacketMenuClose::encode, SPacketMenuClose::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketMenuGet.class, SPacketMenuGet::encode, SPacketMenuGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketMenuSave.class, SPacketMenuSave::encode, SPacketMenuSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNaturalSpawnGet.class, SPacketNaturalSpawnGet::encode, SPacketNaturalSpawnGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNaturalSpawnGetAll.class, SPacketNaturalSpawnGetAll::encode, SPacketNaturalSpawnGetAll::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNaturalSpawnRemove.class, SPacketNaturalSpawnRemove::encode, SPacketNaturalSpawnRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNaturalSpawnSave.class, SPacketNaturalSpawnSave::encode, SPacketNaturalSpawnSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNbtBookBlockSave.class, SPacketNbtBookBlockSave::encode, SPacketNbtBookBlockSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNbtBookEntitySave.class, SPacketNbtBookEntitySave::encode, SPacketNbtBookEntitySave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcDelete.class, SPacketNpcDelete::encode, SPacketNpcDelete::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcDialogRemove.class, SPacketNpcDialogRemove::encode, SPacketNpcDialogRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcDialogSet.class, SPacketNpcDialogSet::encode, SPacketNpcDialogSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcDialogsGet.class, SPacketNpcDialogsGet::encode, SPacketNpcDialogsGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcFactionSet.class, SPacketNpcFactionSet::encode, SPacketNpcFactionSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcJobGet.class, SPacketNpcJobGet::encode, SPacketNpcJobGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcJobSave.class, SPacketNpcJobSave::encode, SPacketNpcJobSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcJobSpawnerSet.class, SPacketNpcJobSpawnerSet::encode, SPacketNpcJobSpawnerSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcMarketSet.class, SPacketNpcMarketSet::encode, SPacketNpcMarketSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcRoleCompanionUpdate.class, SPacketNpcRoleCompanionUpdate::encode, SPacketNpcRoleCompanionUpdate::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcRoleGet.class, SPacketNpcRoleGet::encode, SPacketNpcRoleGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcRoleSave.class, SPacketNpcRoleSave::encode, SPacketNpcRoleSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcTransform.class, SPacketNpcTransform::encode, SPacketNpcTransform::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpcTransportGet.class, SPacketNpcTransportGet::encode, SPacketNpcTransportGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerCloseContainer.class, SPacketPlayerCloseContainer::encode, SPacketPlayerCloseContainer::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerDataGet.class, SPacketPlayerDataGet::encode, SPacketPlayerDataGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerDataRemove.class, SPacketPlayerDataRemove::encode, SPacketPlayerDataRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerKeyPressed.class, SPacketPlayerKeyPressed::encode, SPacketPlayerKeyPressed::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerLeftClicked.class, SPacketPlayerLeftClicked::encode, SPacketPlayerLeftClicked::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerMailDelete.class, SPacketPlayerMailDelete::encode, SPacketPlayerMailDelete::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerMailGet.class, SPacketPlayerMailGet::encode, SPacketPlayerMailGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerMailOpen.class, SPacketPlayerMailOpen::encode, SPacketPlayerMailOpen::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerMailRead.class, SPacketPlayerMailRead::encode, SPacketPlayerMailRead::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerMailSend.class, SPacketPlayerMailSend::encode, SPacketPlayerMailSend::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerTransport.class, SPacketPlayerTransport::encode, SPacketPlayerTransport::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketQuestCategoryRemove.class, SPacketQuestCategoryRemove::encode, SPacketQuestCategoryRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketQuestCompletionCheck.class, SPacketQuestCompletionCheck::encode, SPacketQuestCompletionCheck::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketQuestCompletionCheckAll.class, SPacketQuestCompletionCheckAll::encode, SPacketQuestCompletionCheckAll::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketQuestDialogTitles.class, SPacketQuestDialogTitles::encode, SPacketQuestDialogTitles::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketQuestOpen.class, SPacketQuestOpen::encode, SPacketQuestOpen::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketQuestRemove.class, SPacketQuestRemove::encode, SPacketQuestRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRecipeGet.class, SPacketRecipeGet::encode, SPacketRecipeGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRecipeRemove.class, SPacketRecipeRemove::encode, SPacketRecipeRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRecipeSave.class, SPacketRecipeSave::encode, SPacketRecipeSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRecipesGet.class, SPacketRecipesGet::encode, SPacketRecipesGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRemoteFreeze.class, SPacketRemoteFreeze::encode, SPacketRemoteFreeze::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRemoteMenuOpen.class, SPacketRemoteMenuOpen::encode, SPacketRemoteMenuOpen::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRemoteNpcDelete.class, SPacketRemoteNpcDelete::encode, SPacketRemoteNpcDelete::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRemoteNpcReset.class, SPacketRemoteNpcReset::encode, SPacketRemoteNpcReset::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRemoteNpcsGet.class, SPacketRemoteNpcsGet::encode, SPacketRemoteNpcsGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketRemoteNpcTp.class, SPacketRemoteNpcTp::encode, SPacketRemoteNpcTp::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSceneReset.class, SPacketSceneReset::encode, SPacketSceneReset::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSceneStart.class, SPacketSceneStart::encode, SPacketSceneStart::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSchematicsStore.class, SPacketSchematicsStore::encode, SPacketSchematicsStore::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSchematicsTileBuild.class, SPacketSchematicsTileBuild::encode, SPacketSchematicsTileBuild::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSchematicsTileGet.class, SPacketSchematicsTileGet::encode, SPacketSchematicsTileGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSchematicsTileSave.class, SPacketSchematicsTileSave::encode, SPacketSchematicsTileSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketSchematicsTileSet.class, SPacketSchematicsTileSet::encode, SPacketSchematicsTileSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketScriptGet.class, SPacketScriptGet::encode, SPacketScriptGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTileEntityGet.class, SPacketTileEntityGet::encode, SPacketTileEntityGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTileEntitySave.class, SPacketTileEntitySave::encode, SPacketTileEntitySave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketToolMounter.class, SPacketToolMounter::encode, SPacketToolMounter::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTransportCategoriesGet.class, SPacketTransportCategoriesGet::encode, SPacketTransportCategoriesGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTransportCategoryRemove.class, SPacketTransportCategoryRemove::encode, SPacketTransportCategoryRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTransportCategorySave.class, SPacketTransportCategorySave::encode, SPacketTransportCategorySave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTransportGet.class, SPacketTransportGet::encode, SPacketTransportGet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTransportRemove.class, SPacketTransportRemove::encode, SPacketTransportRemove::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketTransportSave.class, SPacketTransportSave::encode, SPacketTransportSave::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCustomGuiButton.class, SPacketCustomGuiButton::encode, SPacketCustomGuiButton::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCustomGuiTextUpdate.class, SPacketCustomGuiTextUpdate::encode, SPacketCustomGuiTextUpdate::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketCustomGuiScrollClick.class, SPacketCustomGuiScrollClick::encode, SPacketCustomGuiScrollClick::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketNpRandomNameSet.class, SPacketNpRandomNameSet::encode, SPacketNpRandomNameSet::decode, PacketServerBasic::handle);
        Channel.registerMessage(index++, SPacketPlayerSoundPlays.class, SPacketPlayerSoundPlays::encode, SPacketPlayerSoundPlays::decode, PacketServerBasic::handle);
        CNpcsNetworkHelper.addPacket(SPacketScriptSave.class, SPacketScriptSave::new);
        CNpcsNetworkHelper.addPacket(SPacketToolMobSpawner.class, SPacketToolMobSpawner::new);
        CNpcsNetworkHelper.addPacket(SPacketQuestSave.class, SPacketQuestSave::new);
        CNpcsNetworkHelper.addPacket(SPacketQuestCategorySave.class, SPacketQuestCategorySave::new);
        CNpcsNetworkHelper.addPacket(SPacketDialogSave.class, SPacketDialogSave::new);
        CNpcsNetworkHelper.addPacket(SPacketDialogCategorySave.class, SPacketDialogCategorySave::new);
    }

    public static <MSG> void send(ServerPlayerEntity player, MSG msg) {
        Channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG> void sendDelayed(ServerPlayerEntity player, MSG msg, int delay) {
        CustomNPCsScheduler.runTack(() -> Channel.send(PacketDistributor.PLAYER.with(() -> player), msg), delay);
    }

    public static <MSG> void sendNearby(World level, BlockPos pos, int range, MSG msg) {
        Channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, level.dimension())), msg);
    }

    public static <MSG> void sendNearby(Entity entity, MSG msg) {
        Channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    public static <MSG> void sendAll(MSG msg) {
        Channel.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendServer(MSG msg) {
        if (msg instanceof IPacket) {
            Minecraft.getInstance().getConnection().getConnection().send((IPacket)msg);
        } else {
            Channel.sendToServer(msg);
        }

    }

    public static void doExplosion(World level, Explosion explosion, float size) {
        if (!level.isClientSide) {

            for (Object ob : level.players()) {
                ServerPlayerEntity player = (ServerPlayerEntity) ob;
                Vector3d vec = explosion.getPosition();
                SExplosionPacket packet = new SExplosionPacket(vec.x, vec.y, vec.z, size, explosion.getToBlow(), (Vector3d) explosion.getHitPlayers().get(player));
                player.connection.send(packet);
            }

        }
    }
}
