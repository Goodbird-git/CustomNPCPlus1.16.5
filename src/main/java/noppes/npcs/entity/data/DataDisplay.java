package noppes.npcs.entity.data;

import noppes.npcs.api.entity.data.*;
import com.mojang.authlib.*;
import noppes.npcs.controllers.data.*;
import net.minecraft.world.*;
import nikedemos.markovnames.generators.*;
import net.minecraft.nbt.*;
import java.util.*;
import noppes.npcs.util.*;
import noppes.npcs.controllers.*;
import net.minecraft.server.*;
import javax.annotation.*;
import net.minecraft.tileentity.*;
import com.mojang.authlib.properties.*;
import com.google.common.collect.*;
import noppes.npcs.shared.client.util.*;
import noppes.npcs.api.*;
import noppes.npcs.entity.*;
import noppes.npcs.constants.*;
import noppes.npcs.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraftforge.registries.*;
import noppes.npcs.api.entity.*;
import net.minecraft.entity.player.*;

public class DataDisplay implements INPCDisplay
{
    EntityNPCInterface npc;
    private String name;
    private String title;
    private int markovGeneratorId;
    private int markovGender;
    public byte skinType;
    private String url;
    public GameProfile playerProfile;
    private String texture;
    private String cloakTexture;
    private String glowTexture;
    private int visible;
    public Availability availability;
    private int modelSize;
    private int showName;
    private int skinColor;
    private boolean disableLivingAnimation;
    private byte hitboxState;
    private byte showBossBar;
    private BossInfo.Color bossColor;
    public CustomModelData customModelData;
    public DataDisplay(final EntityNPCInterface npc) {
        this.name = "Noppes";
        this.title = "";
        this.markovGeneratorId = 8;
        this.markovGender = 0;
        this.skinType = 0;
        this.url = "";
        this.texture = "customnpcs:textures/entity/humanmale/steve.png";
        this.cloakTexture = "";
        this.glowTexture = "";
        this.visible = 0;
        this.availability = new Availability();
        this.modelSize = 5;
        this.showName = 0;
        this.skinColor = 16777215;
        this.disableLivingAnimation = false;
        this.hitboxState = 0;
        this.showBossBar = 0;
        this.bossColor = BossInfo.Color.PINK;
        this.npc = npc;
        if (!npc.isClientSide()) {
            this.markovGeneratorId = new Random().nextInt(10);
            this.name = this.getRandomName();
        }
        if (npc.getRandom().nextInt(10) == 0) {
            final DataPeople.Person p = DataPeople.get();
            this.name = p.name;
            this.title = p.title;
            if (!p.skin.isEmpty()) {
                this.texture = p.skin;
            }
        }
        customModelData = new CustomModelData();
    }

    public String getRandomName() {
        return MarkovGenerator.fetch(this.markovGeneratorId, this.markovGender);
    }

    public CompoundNBT save(final CompoundNBT nbttagcompound) {
        nbttagcompound.putString("Name", this.name);
        nbttagcompound.putInt("MarkovGeneratorId", this.markovGeneratorId);
        nbttagcompound.putInt("MarkovGender", this.markovGender);
        nbttagcompound.putString("Title", this.title);
        nbttagcompound.putString("SkinUrl", this.url);
        nbttagcompound.putString("Texture", this.texture);
        nbttagcompound.putString("CloakTexture", this.cloakTexture);
        nbttagcompound.putString("GlowTexture", this.glowTexture);
        nbttagcompound.putByte("UsingSkinUrl", this.skinType);
        if (this.playerProfile != null) {
            final CompoundNBT nbttagcompound2 = new CompoundNBT();
            NBTUtil.writeGameProfile(nbttagcompound2, this.playerProfile);
            nbttagcompound.put("SkinUsername", nbttagcompound2);
        }
        nbttagcompound.putInt("Size", this.modelSize);
        nbttagcompound.putInt("ShowName", this.showName);
        nbttagcompound.putInt("SkinColor", this.skinColor);
        nbttagcompound.putInt("NpcVisible", this.visible);
        nbttagcompound.put("VisibleAvailability", this.availability.save(new CompoundNBT()));
        nbttagcompound.putBoolean("NoLivingAnimation", this.disableLivingAnimation);
        nbttagcompound.putByte("IsStatue", this.hitboxState);
        nbttagcompound.putByte("BossBar", this.showBossBar);
        nbttagcompound.putInt("BossColor", this.bossColor.ordinal());
        customModelData.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }

    public void readToNBT(final CompoundNBT nbttagcompound) {
        this.setName(nbttagcompound.getString("Name"));
        this.setMarkovGeneratorId(nbttagcompound.getInt("MarkovGeneratorId"));
        this.setMarkovGender(nbttagcompound.getInt("MarkovGender"));
        this.title = nbttagcompound.getString("Title");
        final int prevSkinType = this.skinType;
        final String prevTexture = this.texture;
        final String prevUrl = this.url;
        final String prevPlayer = this.getSkinPlayer();
        this.url = nbttagcompound.getString("SkinUrl");
        this.skinType = nbttagcompound.getByte("UsingSkinUrl");
        this.texture = nbttagcompound.getString("Texture");
        this.cloakTexture = nbttagcompound.getString("CloakTexture");
        this.glowTexture = nbttagcompound.getString("GlowTexture");
        this.playerProfile = null;
        if (this.skinType == 1) {
            if (nbttagcompound.contains("SkinUsername", 10)) {
                this.playerProfile = NBTUtil.readGameProfile(nbttagcompound.getCompound("SkinUsername"));
            }
            else if (nbttagcompound.contains("SkinUsername", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkinUsername"))) {
                this.playerProfile = new GameProfile((UUID)null, nbttagcompound.getString("SkinUsername"));
            }
            this.loadProfile();
        }
        this.modelSize = ValueUtil.CorrectInt(nbttagcompound.getInt("Size"), 1, 30);
        this.showName = nbttagcompound.getInt("ShowName");
        if (nbttagcompound.contains("SkinColor")) {
            this.skinColor = nbttagcompound.getInt("SkinColor");
        }
        this.visible = nbttagcompound.getInt("NpcVisible");
        this.availability.load(nbttagcompound.getCompound("VisibleAvailability"));
        VisibilityController.instance.trackNpc(this.npc);
        this.disableLivingAnimation = nbttagcompound.getBoolean("NoLivingAnimation");
        this.hitboxState = nbttagcompound.getByte("IsStatue");
        this.setBossbar(nbttagcompound.getByte("BossBar"));
        this.setBossColor(nbttagcompound.getInt("BossColor"));
        if (prevSkinType != this.skinType || !this.texture.equals(prevTexture) || !this.url.equals(prevUrl) || !this.getSkinPlayer().equals(prevPlayer)) {
            this.npc.textureLocation = null;
        }
        this.npc.textureGlowLocation = null;
        this.npc.textureCloakLocation = null;
        this.npc.refreshDimensions();
        this.customModelData.readFromNBT(nbttagcompound);
    }

    public void loadProfile() {
        if (this.playerProfile != null && !StringUtils.isNullOrEmpty(this.playerProfile.getName())) {
            this.playerProfile = getGameprofile(this.npc.getServer(), this.playerProfile);
        }
    }

    private static GameProfile getGameprofile(final MinecraftServer server, @Nullable final GameProfile profile) {
        if (server == null) {
            return SkullTileEntity.updateGameprofile(profile);
        }
        try {
            if (profile == null || StringUtils.isNullOrEmpty(profile.getName()) || (profile.isComplete() && profile.getProperties().containsKey("textures"))) {
                return profile;
            }
            GameProfile gameprofile = server.getProfileCache().get(profile.getName());
            if (gameprofile == null) {
                return profile;
            }
            final Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);
            if (property == null) {
                gameprofile = server.getSessionService().fillProfileProperties(gameprofile, true);
            }
            return gameprofile;
        }
        catch (Exception e) {
            return profile;
        }
    }

    public boolean showName() {
        return !this.npc.isKilled() && (this.showName == 0 || (this.showName == 2 && this.npc.isAttacking()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        if (this.name.equals(name)) {
            return;
        }
        this.name = name;
        this.npc.bossInfo.setName(this.npc.getDisplayName());
        this.npc.updateClient = true;
    }

    @Override
    public int getShowName() {
        return this.showName;
    }

    @Override
    public void setShowName(final int type) {
        if (type == this.showName) {
            return;
        }
        this.showName = ValueUtil.CorrectInt(type, 0, 2);
        this.npc.updateClient = true;
    }

    public int getMarkovGender() {
        return this.markovGender;
    }

    public void setMarkovGender(final int gender) {
        if (this.markovGender == gender) {
            return;
        }
        this.markovGender = ValueUtil.CorrectInt(gender, 0, 2);
    }

    public int getMarkovGeneratorId() {
        return this.markovGeneratorId;
    }

    public void setMarkovGeneratorId(final int id) {
        if (this.markovGeneratorId == id) {
            return;
        }
        this.markovGeneratorId = ValueUtil.CorrectInt(id, 0, 9);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(final String title) {
        if (this.title.equals(title)) {
            return;
        }
        this.title = title;
        this.npc.updateClient = true;
    }

    @Override
    public String getSkinUrl() {
        return this.url;
    }

    @Override
    public void setSkinUrl(final String url) {
        if (this.url.equals(url)) {
            return;
        }
        this.url = url;
        if (url.isEmpty()) {
            this.skinType = 0;
        }
        else {
            this.skinType = 2;
        }
        this.npc.updateClient = true;
    }

    @Override
    public String getSkinPlayer() {
        return (this.playerProfile == null) ? "" : this.playerProfile.getName();
    }

    @Override
    public void setSkinPlayer(final String name) {
        if (name == null || name.isEmpty()) {
            this.playerProfile = null;
            this.skinType = 0;
        }
        else {
            this.playerProfile = new GameProfile(null, name);
            this.skinType = 1;
        }
        this.npc.updateClient = true;
    }

    @Override
    public String getSkinTexture() {
        return NoppesStringUtils.cleanResource(this.texture);
    }

    @Override
    public void setSkinTexture(final String texture) {
        if (texture == null || this.texture.equals(texture)) {
            return;
        }
        this.texture = NoppesStringUtils.cleanResource(texture);
        this.npc.textureLocation = null;
        this.skinType = 0;
        this.npc.updateClient = true;
    }

    @Override
    public String getOverlayTexture() {
        return NoppesStringUtils.cleanResource(this.glowTexture);
    }

    @Override
    public void setOverlayTexture(final String texture) {
        if (this.glowTexture.equals(texture)) {
            return;
        }
        this.glowTexture = NoppesStringUtils.cleanResource(texture);
        this.npc.textureGlowLocation = null;
        this.npc.updateClient = true;
    }

    @Override
    public String getCapeTexture() {
        return NoppesStringUtils.cleanResource(this.cloakTexture);
    }

    @Override
    public void setCapeTexture(final String texture) {
        if (this.cloakTexture.equals(texture)) {
            return;
        }
        this.cloakTexture = NoppesStringUtils.cleanResource(texture);
        this.npc.textureCloakLocation = null;
        this.npc.updateClient = true;
    }

    @Override
    public boolean getHasLivingAnimation() {
        return !this.disableLivingAnimation;
    }

    @Override
    public void setHasLivingAnimation(final boolean enabled) {
        this.disableLivingAnimation = !enabled;
        this.npc.updateClient = true;
    }

    @Override
    public int getBossbar() {
        return this.showBossBar;
    }

    @Override
    public void setBossbar(final int type) {
        if (type == this.showBossBar) {
            return;
        }
        this.showBossBar = (byte)ValueUtil.CorrectInt(type, 0, 2);
        this.npc.bossInfo.setVisible(this.showBossBar == 1);
        this.npc.updateClient = true;
    }

    @Override
    public int getBossColor() {
        return this.bossColor.ordinal();
    }

    @Override
    public void setBossColor(final int color) {
        if (color < 0 || color >= BossInfo.Color.values().length) {
            throw new CustomNPCsException("Invalid Boss Color: " + color);
        }
        this.bossColor = BossInfo.Color.values()[color];
        this.npc.bossInfo.setColor(this.bossColor);
    }

    @Override
    public int getVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(final int type) {
        if (type == this.visible) {
            return;
        }
        this.visible = ValueUtil.CorrectInt(type, 0, 2);
        this.npc.updateClient = true;
    }

    @Override
    public int getSize() {
        return this.modelSize;
    }

    @Override
    public void setSize(final int size) {
        if (this.modelSize == size) {
            return;
        }
        this.modelSize = ValueUtil.CorrectInt(size, 1, 30);
        this.npc.updateClient = true;
    }

    @Override
    public void setModelScale(final int part, final float x, final float y, final float z) {
        final ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        ModelPartConfig model = null;
        if (part == 0) {
            model = modeldata.getPartConfig(EnumParts.HEAD);
        }
        else if (part == 1) {
            model = modeldata.getPartConfig(EnumParts.BODY);
        }
        else if (part == 2) {
            model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
        }
        else if (part == 3) {
            model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
        }
        else if (part == 4) {
            model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
        }
        else if (part == 5) {
            model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);
        }
        if (model == null) {
            throw new CustomNPCsException("Unknown part: " + part);
        }
        model.setScale(x, y, z);
        this.npc.updateClient = true;
    }

    @Override
    public float[] getModelScale(final int part) {
        final ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        ModelPartConfig model = null;
        if (part == 0) {
            model = modeldata.getPartConfig(EnumParts.HEAD);
        }
        else if (part == 1) {
            model = modeldata.getPartConfig(EnumParts.BODY);
        }
        else if (part == 2) {
            model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
        }
        else if (part == 3) {
            model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
        }
        else if (part == 4) {
            model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
        }
        else if (part == 5) {
            model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);
        }
        if (model == null) {
            throw new CustomNPCsException("Unknown part: " + part);
        }
        return new float[] { model.scaleX, model.scaleY, model.scaleZ };
    }

    @Override
    public int getTint() {
        return this.skinColor;
    }

    @Override
    public void setTint(final int color) {
        if (color == this.skinColor) {
            return;
        }
        this.skinColor = color;
        this.npc.updateClient = true;
    }

    @Override
    public void setModel(final String id) {
        final ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        if (id == null) {
            if (modeldata.getEntityName() == null) {
                return;
            }
            modeldata.setEntity((String)null);
            this.npc.updateClient = true;
        }
        else {
            final ResourceLocation resource = new ResourceLocation(id);
            final EntityType type = (EntityType)ForgeRegistries.ENTITIES.getValue(resource);
            if (type == null) {
                throw new CustomNPCsException("Unknown entity id: " + id);
            }
            modeldata.setEntity(id);
            this.npc.updateClient = true;
        }
    }

    @Override
    public String getModel() {
        final ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        if (modeldata.getEntityName() == null) {
            return null;
        }
        return modeldata.getEntityName().toString();
    }

    @Override
    public byte getHitboxState() {
        return this.hitboxState;
    }

    @Override
    public void setHitboxState(final byte state) {
        if (this.hitboxState == state) {
            return;
        }
        this.hitboxState = state;
        this.npc.updateClient = true;
    }

    @Override
    public boolean isVisibleTo(final IPlayer player) {
        return this.isVisibleTo(player);
    }

    public boolean isVisibleTo(final ServerPlayerEntity player) {
        if (this.visible == 1) {
            return !this.availability.isAvailable(player);
        }
        return this.availability.isAvailable(player);
    }
}
