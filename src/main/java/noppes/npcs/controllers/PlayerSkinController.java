package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPlayerSkinAdd;
import noppes.npcs.packets.client.PacketPlayerSkinGet;

public class PlayerSkinController {

    //NetHandlerPlayClient <- NetHandlerPlayClient <- PlayerList.playerLoggedIn <- SPacketPlayerListItem
    private static PlayerSkinController instance;
    public final Map<UUID, String> playerNames = Maps.newHashMap();
    public final Map<UUID, Map<Type, ResourceLocation>> playerTextures = Maps.newHashMap();
    private String filePath;

    public PlayerSkinController() {
        PlayerSkinController.instance = this;
        this.filePath = CustomNpcs.getWorldSaveDirectory().getAbsolutePath();
        this.loadPlayerSkins();
    }

    public static PlayerSkinController getInstance() {
        if (newInstance()) {
            PlayerSkinController.instance = new PlayerSkinController();
        }
        return PlayerSkinController.instance;
    }

    private static boolean newInstance() {
        if (PlayerSkinController.instance == null) {
            return true;
        }
        File file = CustomNpcs.Dir;
        return file != null && !PlayerSkinController.instance.filePath.equals(file.getName());
    }

    private void loadPlayerSkins() {
        File saveDir = CustomNpcs.Dir;
        if (saveDir == null) {
            return;
        }
//        if (CustomNpcs.VerboseDebug) {
//            CustomNpcs.debugData.startDebug("Common", null, "loadPlayerSkins");
//        }
        this.filePath = saveDir.getName();
        try {
            File file = new File(saveDir, "player_skins.dat");
            if (file.exists()) {
                this.loadPlayerSkins(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //CustomNpcs.debugData.endDebug("Common", null, "loadPlayerSkins");
    }

    private void loadPlayerSkins(File file) {
        try {
            loadPlayerSkins(CompressedStreamTools.readCompressed(new FileInputStream(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlayerSkins(CompoundNBT compound) throws Exception {
        playerNames.clear();
        playerTextures.clear();
        if (compound.contains("Data", 9)) {
            for (int i = 0; i < compound.getList("Data", 10).size(); ++i) {
                this.loadPlayerSkin(compound.getList("Data", 10).getCompound(i));
            }
        }
    }

    public UUID loadPlayerSkin(CompoundNBT nbtSkin) {
        if (nbtSkin == null) {
            return null;
        }
        UUID uuid = nbtSkin.getUUID("UUID");
        playerNames.put(uuid, nbtSkin.getString("Player"));
        if (!playerTextures.containsKey(uuid)) {
            playerTextures.put(uuid, Maps.newEnumMap(Type.class));
        }
        Map<Type, ResourceLocation> skins = playerTextures.get(uuid);
        for (int i = 0; i < nbtSkin.getList("Textures", 10).size(); i++) {
            CompoundNBT nbt = nbtSkin.getList("Textures", 10).getCompound(i);
            Type type;
            switch (nbt.getString("Type").toLowerCase()) {
                case "cape":
                    type = Type.CAPE;
                    break;
                case "elytra":
                    type = Type.ELYTRA;
                    break;
                default:
                    type = Type.SKIN;
                    break;
            }
            skins.put(type, new ResourceLocation(deleteColor(nbt.getString("Location"))));
        }
        playerTextures.put(uuid, skins);
        return uuid;
    }

    public CompoundNBT getNBT() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT listUUIDs = new ListNBT();
        for (UUID uuid : playerTextures.keySet()) {
            CompoundNBT nbtPlayer = new CompoundNBT();
            nbtPlayer.putUUID("UUID", uuid);
            ListNBT listTxrs = new ListNBT();
            for (Type epst : playerTextures.get(uuid).keySet()) {
                ResourceLocation loc = playerTextures.get(uuid).get(epst);
                if (loc == null) {
                    continue;
                }
                CompoundNBT nbtSkin = new CompoundNBT();
                nbtSkin.putString("Type", epst.name());
                nbtSkin.putString("Location", loc.toString());
                listTxrs.add(nbtSkin);
            }
            nbtPlayer.put("Textures", listTxrs);
            nbtPlayer.putString("Player", playerNames.get(uuid) == null ? "null" : playerNames.get(uuid));
            listUUIDs.add(nbtPlayer);
        }
        compound.put("Data", listUUIDs);
        return compound;
    }

    public CompoundNBT getNBT(UUID uuid) {
        CompoundNBT nbtPlayer = new CompoundNBT();
        nbtPlayer.putUUID("UUID", uuid);
        ListNBT listTxrs = new ListNBT();
        for (Type epst : playerTextures.get(uuid).keySet()) {
            ResourceLocation loc = playerTextures.get(uuid).get(epst);
            if (loc == null) {
                continue;
            }
            CompoundNBT nbtSkin = new CompoundNBT();
            nbtSkin.putString("Type", epst.name());
            nbtSkin.putString("Location", loc.toString());
            listTxrs.add(nbtSkin);
        }
        nbtPlayer.put("Textures", listTxrs);
        nbtPlayer.putString("Player", playerNames.get(uuid) == null ? "null" : playerNames.get(uuid));
        return nbtPlayer;
    }

    public void save() { //TODO find place for it
        try {
            CompressedStreamTools.writeCompressed(this.getNBT(), (OutputStream) new FileOutputStream(new File(CustomNpcs.Dir, "player_skins.dat")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logged(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (playerTextures.containsKey(uuid)) {
            playerNames.put(uuid, player.getName().getString());
            sendToAll(player);
        } else {
            Packets.send(player, new PacketPlayerSkinGet());
        }
        for (ServerPlayerEntity pl : player.getServer().getPlayerList().getPlayers()) {
            if (pl.equals(player) || !playerTextures.containsKey(pl.getUUID())) {
                continue;
            }
            Packets.send(player, new PacketPlayerSkinAdd(getNBT(pl.getUUID())));
        }
    }

    public void sendToAll(PlayerEntity player) {
        UUID uuid = player.getUUID();
        if (!playerTextures.containsKey(uuid)) {
            return;
        }
        playerNames.put(uuid, player.getName().getString());
        CompoundNBT nbtPlayer = getNBT(uuid);
        for (ServerPlayerEntity pl : player.getServer().getPlayerList().getPlayers()) {
            Packets.send(pl, new PacketPlayerSkinAdd(nbtPlayer));
        }
    }

    public Map<Type, ResourceLocation> getData(UUID uuid) {
        if (!playerTextures.containsKey(uuid)) {
            playerTextures.put(uuid, Maps.newEnumMap(Type.class));
            playerTextures.get(uuid).put(Type.SKIN, new ResourceLocation("minecraft", (uuid.hashCode() & 1) == 1 ? "textures/entity/alex.png" : "textures/entity/steve.png")); // DefaultPlayerSkin
        }
        return playerTextures.get(uuid);
    }

    public String get(ServerPlayerEntity player, int type) {
        if (type < 0) {
            type *= -1;
        }
        Map<Type, ResourceLocation> data = getData(player.getUUID());
        ResourceLocation loc = data.get(Type.values()[type % Type.values().length]);
        return loc == null ? null : loc.toString();
    }

    public void set(ServerPlayerEntity player, boolean isSmallArms, int body, int bodyColor, int hair, int hairColor, int face, int eyesColor, int leg, int jacket, int shoes, int... peculiarities) {
        UUID uuid = player.getUUID();
        if (!playerTextures.containsKey(uuid)) {
            playerTextures.put(uuid, Maps.newEnumMap(Type.class));
        }
        Map<Type, ResourceLocation> data = getData(player.getUUID());
        String path = "textures/entity/custom/" + (isSmallArms ? "female" : "male") + "_" + body + "_" + bodyColor + "_" + hair + "_" + hairColor + "_" + face + "_" + eyesColor + "_" + leg + "_" + jacket + "_" + shoes;
        for (int id : peculiarities) {
            path += "_" + id;
        }
        path += ".png";
        data.put(Type.SKIN, new ResourceLocation(CustomNpcs.MODID, deleteColor(path)));
        playerTextures.put(uuid, data);
        sendToAll(player);
    }

    public void set(ServerPlayerEntity player, String location, int type) {
        UUID uuid = player.getUUID();
        if (type < 0) {
            type *= -1;
        }
        Type t = Type.values()[type % Type.values().length];
        if (!playerTextures.containsKey(uuid)) {
            playerTextures.put(uuid, Maps.newEnumMap(Type.class));
            playerTextures.get(uuid).put(Type.SKIN, new ResourceLocation("minecraft", (uuid.hashCode() & 1) == 1 ? "textures/entity/alex.png" : "textures/entity/steve.png")); // DefaultPlayerSkin
        }
        Map<Type, ResourceLocation> data = getData(player.getUUID());
        if (location == null || location.isEmpty()) {
            data.remove(t);
        } else {
            data.put(t, new ResourceLocation(deleteColor(location)));
        }
        playerTextures.put(uuid, data);
        sendToAll(player);
    }


    public String deleteColor(String str) {
        if (str == null) { return null; }
        if (str.isEmpty()) { return str; }
        for (int i=0; i<4; i++) {
            String chr = new String(Character.toChars(0x00A7));
            if (i==1) { chr = ""+((char) 167); }
            else if (i==2) { chr = "&"; }
            else if (i==3) { chr = ""+((char) 65535); }
            try {
                while (str.indexOf(chr) != (-1)) {
                    int p = str.indexOf(chr);
                    str = (p>0 ? str.substring(0, p) : "" ) + (p+2==str.length() ? "" : str.substring(p + 2));
                }
            } catch (Exception e) { }
        }
        return str;
    }
}
