package noppes.npcs.client.controllers;

import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.SkinUtil;
import noppes.npcs.controllers.data.PlayerSkinData;

import java.util.HashMap;
import java.util.Map;

public class ClientSkinController {
    private static final Map<String, PlayerSkinData> skinInfo = new HashMap<>();

    public static void addSkinForPlayer(String playerName, PlayerSkinData skinData){
        SkinUtil.createPlayerSkin(skinData);
        skinInfo.put(playerName, skinData);
    }

    public static ResourceLocation getSkinForPlayer(String playerName){
        PlayerSkinData skin = skinInfo.get(playerName);
        if(skin==null) return null;
        return skin.getResLoc();
    }
}
