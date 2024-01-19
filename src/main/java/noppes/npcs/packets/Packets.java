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
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import noppes.npcs.packets.client.*;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.CustomNPCsScheduler;

public class Packets
{
    public static SimpleChannel Channel;
    public static int index;

    public static void register() {
        Packets.Channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("customnpcs", "packets")).clientAcceptedVersions("CNPCS"::equals).serverAcceptedVersions("CNPCS"::equals).networkProtocolVersion(() -> "CNPCS").simpleChannel();
        Packets.index = 0;
        Packets.Channel.registerMessage(Packets.index++, PacketAchievement.class, PacketAchievement::encode, PacketAchievement::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketChat.class, PacketChat::encode, PacketChat::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketChatBubble.class, PacketChatBubble::encode, PacketChatBubble::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketConfigFont.class, PacketConfigFont::encode, PacketConfigFont::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketDialog.class, PacketDialog::encode, PacketDialog::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketDialogDummy.class, PacketDialogDummy::encode, PacketDialogDummy::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketEyeBlink.class, PacketEyeBlink::encode, PacketEyeBlink::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiCloneOpen.class, PacketGuiCloneOpen::encode, PacketGuiCloneOpen::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiClose.class, PacketGuiClose::encode, PacketGuiClose::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiData.class, PacketGuiData::encode, PacketGuiData::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiError.class, PacketGuiError::encode, PacketGuiError::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiOpen.class, PacketGuiOpen::encode, PacketGuiOpen::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiScrollData.class, PacketGuiScrollData::encode, PacketGuiScrollData::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiScrollList.class, PacketGuiScrollList::encode, PacketGuiScrollList::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiScrollSelected.class, PacketGuiScrollSelected::encode, PacketGuiScrollSelected::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketGuiUpdate.class, PacketGuiUpdate::encode, PacketGuiUpdate::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketItemUpdate.class, PacketItemUpdate::encode, PacketItemUpdate::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketMarkData.class, PacketMarkData::encode, PacketMarkData::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketNpcDelete.class, PacketNpcDelete::encode, PacketNpcDelete::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketNpcEdit.class, PacketNpcEdit::encode, PacketNpcEdit::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketNpcRole.class, PacketNpcRole::encode, PacketNpcRole::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketNpcUpdate.class, PacketNpcUpdate::encode, PacketNpcUpdate::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketParticle.class, PacketParticle::encode, PacketParticle::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketPlayMusic.class, PacketPlayMusic::encode, PacketPlayMusic::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketPlaySound.class, PacketPlaySound::encode, PacketPlaySound::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketQuestCompletion.class, PacketQuestCompletion::encode, PacketQuestCompletion::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketSync.class, PacketSync::encode, PacketSync::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketSyncRemove.class, PacketSyncRemove::encode, PacketSyncRemove::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketSyncUpdate.class, PacketSyncUpdate::encode, PacketSyncUpdate::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketNpcVisibleFalse.class, PacketNpcVisibleFalse::encode, PacketNpcVisibleFalse::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketNpcVisibleTrue.class, PacketNpcVisibleTrue::encode, PacketNpcVisibleTrue::decode, PacketBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBankGet.class, SPacketBankGet::encode, SPacketBankGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBankRemove.class, SPacketBankRemove::encode, SPacketBankRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBankSave.class, SPacketBankSave::encode, SPacketBankSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBanksGet.class, SPacketBanksGet::encode, SPacketBanksGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBanksSlotOpen.class, SPacketBanksSlotOpen::encode, SPacketBanksSlotOpen::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBankUnlock.class, SPacketBankUnlock::encode, SPacketBankUnlock::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketBankUpgrade.class, SPacketBankUpgrade::encode, SPacketBankUpgrade::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCloneList.class, SPacketCloneList::encode, SPacketCloneList::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCloneNameCheck.class, SPacketCloneNameCheck::encode, SPacketCloneNameCheck::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCloneRemove.class, SPacketCloneRemove::encode, SPacketCloneRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCloneSave.class, SPacketCloneSave::encode, SPacketCloneSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCompanionOpenInv.class, SPacketCompanionOpenInv::encode, SPacketCompanionOpenInv::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCompanionTalentExp.class, SPacketCompanionTalentExp::encode, SPacketCompanionTalentExp::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketDialogCategoryRemove.class, SPacketDialogCategoryRemove::encode, SPacketDialogCategoryRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketDialogRemove.class, SPacketDialogRemove::encode, SPacketDialogRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketDialogSelected.class, SPacketDialogSelected::encode, SPacketDialogSelected::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketDimensionsGet.class, SPacketDimensionsGet::encode, SPacketDimensionsGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketDimensionTeleport.class, SPacketDimensionTeleport::encode, SPacketDimensionTeleport::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFactionGet.class, SPacketFactionGet::encode, SPacketFactionGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFactionRemove.class, SPacketFactionRemove::encode, SPacketFactionRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFactionSave.class, SPacketFactionSave::encode, SPacketFactionSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFactionsGet.class, SPacketFactionsGet::encode, SPacketFactionsGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFollowerExtend.class, SPacketFollowerExtend::encode, SPacketFollowerExtend::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFollowerHire.class, SPacketFollowerHire::encode, SPacketFollowerHire::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketFollowerState.class, SPacketFollowerState::encode, SPacketFollowerState::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketGuiOpen.class, SPacketGuiOpen::encode, SPacketGuiOpen::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketLinkedAdd.class, SPacketLinkedAdd::encode, SPacketLinkedAdd::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketLinkedGet.class, SPacketLinkedGet::encode, SPacketLinkedGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketLinkedRemove.class, SPacketLinkedRemove::encode, SPacketLinkedRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketLinkedSet.class, SPacketLinkedSet::encode, SPacketLinkedSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketMailSetup.class, SPacketMailSetup::encode, SPacketMailSetup::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketMenuClose.class, SPacketMenuClose::encode, SPacketMenuClose::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketMenuGet.class, SPacketMenuGet::encode, SPacketMenuGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketMenuSave.class, SPacketMenuSave::encode, SPacketMenuSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNaturalSpawnGet.class, SPacketNaturalSpawnGet::encode, SPacketNaturalSpawnGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNaturalSpawnGetAll.class, SPacketNaturalSpawnGetAll::encode, SPacketNaturalSpawnGetAll::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNaturalSpawnRemove.class, SPacketNaturalSpawnRemove::encode, SPacketNaturalSpawnRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNaturalSpawnSave.class, SPacketNaturalSpawnSave::encode, SPacketNaturalSpawnSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNbtBookBlockSave.class, SPacketNbtBookBlockSave::encode, SPacketNbtBookBlockSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNbtBookEntitySave.class, SPacketNbtBookEntitySave::encode, SPacketNbtBookEntitySave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcDelete.class, SPacketNpcDelete::encode, SPacketNpcDelete::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcDialogRemove.class, SPacketNpcDialogRemove::encode, SPacketNpcDialogRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcDialogSet.class, SPacketNpcDialogSet::encode, SPacketNpcDialogSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcDialogsGet.class, SPacketNpcDialogsGet::encode, SPacketNpcDialogsGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcFactionSet.class, SPacketNpcFactionSet::encode, SPacketNpcFactionSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcJobGet.class, SPacketNpcJobGet::encode, SPacketNpcJobGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcJobSave.class, SPacketNpcJobSave::encode, SPacketNpcJobSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcJobSpawnerSet.class, SPacketNpcJobSpawnerSet::encode, SPacketNpcJobSpawnerSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcMarketSet.class, SPacketNpcMarketSet::encode, SPacketNpcMarketSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcRoleCompanionUpdate.class, SPacketNpcRoleCompanionUpdate::encode, SPacketNpcRoleCompanionUpdate::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcRoleGet.class, SPacketNpcRoleGet::encode, SPacketNpcRoleGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcRoleSave.class, SPacketNpcRoleSave::encode, SPacketNpcRoleSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcTransform.class, SPacketNpcTransform::encode, SPacketNpcTransform::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpcTransportGet.class, SPacketNpcTransportGet::encode, SPacketNpcTransportGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerCloseContainer.class, SPacketPlayerCloseContainer::encode, SPacketPlayerCloseContainer::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerDataGet.class, SPacketPlayerDataGet::encode, SPacketPlayerDataGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerDataRemove.class, SPacketPlayerDataRemove::encode, SPacketPlayerDataRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerKeyPressed.class, SPacketPlayerKeyPressed::encode, SPacketPlayerKeyPressed::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerLeftClicked.class, SPacketPlayerLeftClicked::encode, SPacketPlayerLeftClicked::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerMailDelete.class, SPacketPlayerMailDelete::encode, SPacketPlayerMailDelete::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerMailGet.class, SPacketPlayerMailGet::encode, SPacketPlayerMailGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerMailOpen.class, SPacketPlayerMailOpen::encode, SPacketPlayerMailOpen::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerMailRead.class, SPacketPlayerMailRead::encode, SPacketPlayerMailRead::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerMailSend.class, SPacketPlayerMailSend::encode, SPacketPlayerMailSend::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerTransport.class, SPacketPlayerTransport::encode, SPacketPlayerTransport::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketQuestCategoryRemove.class, SPacketQuestCategoryRemove::encode, SPacketQuestCategoryRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketQuestCompletionCheck.class, SPacketQuestCompletionCheck::encode, SPacketQuestCompletionCheck::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketQuestCompletionCheckAll.class, SPacketQuestCompletionCheckAll::encode, SPacketQuestCompletionCheckAll::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketQuestDialogTitles.class, SPacketQuestDialogTitles::encode, SPacketQuestDialogTitles::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketQuestOpen.class, SPacketQuestOpen::encode, SPacketQuestOpen::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketQuestRemove.class, SPacketQuestRemove::encode, SPacketQuestRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRecipeGet.class, SPacketRecipeGet::encode, SPacketRecipeGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRecipeRemove.class, SPacketRecipeRemove::encode, SPacketRecipeRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRecipeSave.class, SPacketRecipeSave::encode, SPacketRecipeSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRecipesGet.class, SPacketRecipesGet::encode, SPacketRecipesGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRemoteFreeze.class, SPacketRemoteFreeze::encode, SPacketRemoteFreeze::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRemoteMenuOpen.class, SPacketRemoteMenuOpen::encode, SPacketRemoteMenuOpen::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRemoteNpcDelete.class, SPacketRemoteNpcDelete::encode, SPacketRemoteNpcDelete::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRemoteNpcReset.class, SPacketRemoteNpcReset::encode, SPacketRemoteNpcReset::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRemoteNpcsGet.class, SPacketRemoteNpcsGet::encode, SPacketRemoteNpcsGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketRemoteNpcTp.class, SPacketRemoteNpcTp::encode, SPacketRemoteNpcTp::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSceneReset.class, SPacketSceneReset::encode, SPacketSceneReset::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSceneStart.class, SPacketSceneStart::encode, SPacketSceneStart::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSchematicsStore.class, SPacketSchematicsStore::encode, SPacketSchematicsStore::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSchematicsTileBuild.class, SPacketSchematicsTileBuild::encode, SPacketSchematicsTileBuild::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSchematicsTileGet.class, SPacketSchematicsTileGet::encode, SPacketSchematicsTileGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSchematicsTileSave.class, SPacketSchematicsTileSave::encode, SPacketSchematicsTileSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketSchematicsTileSet.class, SPacketSchematicsTileSet::encode, SPacketSchematicsTileSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketScriptGet.class, SPacketScriptGet::encode, SPacketScriptGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTileEntityGet.class, SPacketTileEntityGet::encode, SPacketTileEntityGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTileEntitySave.class, SPacketTileEntitySave::encode, SPacketTileEntitySave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketToolMounter.class, SPacketToolMounter::encode, SPacketToolMounter::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTransportCategoriesGet.class, SPacketTransportCategoriesGet::encode, SPacketTransportCategoriesGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTransportCategoryRemove.class, SPacketTransportCategoryRemove::encode, SPacketTransportCategoryRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTransportCategorySave.class, SPacketTransportCategorySave::encode, SPacketTransportCategorySave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTransportGet.class, SPacketTransportGet::encode, SPacketTransportGet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTransportRemove.class, SPacketTransportRemove::encode, SPacketTransportRemove::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketTransportSave.class, SPacketTransportSave::encode, SPacketTransportSave::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCustomGuiButton.class, SPacketCustomGuiButton::encode, SPacketCustomGuiButton::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCustomGuiTextUpdate.class, SPacketCustomGuiTextUpdate::encode, SPacketCustomGuiTextUpdate::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketCustomGuiScrollClick.class, SPacketCustomGuiScrollClick::encode, SPacketCustomGuiScrollClick::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketNpRandomNameSet.class, SPacketNpRandomNameSet::encode, SPacketNpRandomNameSet::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, SPacketPlayerSoundPlays.class, SPacketPlayerSoundPlays::encode, SPacketPlayerSoundPlays::decode, PacketServerBasic::handle);
        Packets.Channel.registerMessage(Packets.index++, PacketSyncAnimation.class, PacketSyncAnimation::encode, PacketSyncAnimation::decode, PacketSyncAnimation::handle);
        CNpcsNetworkHelper.addPacket(SPacketScriptSave.class, SPacketScriptSave::new);
        CNpcsNetworkHelper.addPacket(SPacketToolMobSpawner.class, SPacketToolMobSpawner::new);
        CNpcsNetworkHelper.addPacket(SPacketQuestSave.class, SPacketQuestSave::new);
        CNpcsNetworkHelper.addPacket(SPacketQuestCategorySave.class, SPacketQuestCategorySave::new);
        CNpcsNetworkHelper.addPacket(SPacketDialogSave.class, SPacketDialogSave::new);
        CNpcsNetworkHelper.addPacket(SPacketDialogCategorySave.class, SPacketDialogCategorySave::new);
    }

    public static <MSG> void send(final ServerPlayerEntity player, final MSG msg) {
        Packets.Channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)msg);
    }

    public static <MSG> void sendDelayed(final ServerPlayerEntity player, final MSG msg, final int delay) {
        CustomNPCsScheduler.runTack(() -> Packets.Channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)msg), delay);
    }

    public static <MSG> void sendNearby(final World level, final BlockPos pos, final int range, final MSG msg) {
        Packets.Channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, level.dimension)), (Object)msg);
    }

    public static <MSG> void sendNearby(final Entity entity, final MSG msg) {
        Packets.Channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), (Object)msg);
    }

    public static <MSG> void sendAll(final MSG msg) {
        Packets.Channel.send(PacketDistributor.ALL.noArg(), (Object)msg);
    }

    public static <MSG> void sendServer(final MSG msg) {
        if (msg instanceof IPacket) {
            Minecraft.getInstance().getConnection().getConnection().send((IPacket)msg);
        }
        else {
            Packets.Channel.sendToServer((Object)msg);
        }
    }

    public static void doExplosion(final World level, final Explosion explosion, final float size) {
        if (level.isClientSide) {
            return;
        }
        for (final Object ob : level.players()) {
            final ServerPlayerEntity player = (ServerPlayerEntity)ob;
            final Vector3d vec = explosion.getPosition();
            final SExplosionPacket packet = new SExplosionPacket(vec.x, vec.y, vec.z, size, explosion.getToBlow(), explosion.getHitPlayers().get(player));
            player.connection.send(packet);
        }
    }

    static {
        Packets.index = 0;
    }
}
