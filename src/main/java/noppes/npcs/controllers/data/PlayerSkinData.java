package noppes.npcs.controllers.data;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerSkinData {
    private boolean isSmallArms;
    private int body;
    private int bodyColor;
    private int hair;
    private int hairColor;
    private int face;
    private int eyesColor;
    private int leg;
    private int jacket;
    private int shoes;
    private List<Integer> peculiarities;
    private boolean isActive;

    private ResourceLocation cacheResLoc = null;
    private boolean hasChanged;
    private static boolean skinsNeedResync;

    public boolean isSmallArms() {
        return isSmallArms;
    }

    public PlayerSkinData setSmallArms(boolean smallArms) {
        isSmallArms = smallArms;
        markChanged();
        return this;
    }

    public String getGender(){
        return isSmallArms ? "female" : "male";
    }

    public int getBody() {
        return body;
    }

    public PlayerSkinData setBody(int body) {
        this.body = body;
        markChanged();
        return this;
    }

    public int getBodyColor() {
        return bodyColor;
    }

    public PlayerSkinData setBodyColor(int bodyColor) {
        this.bodyColor = bodyColor;
        markChanged();
        return this;
    }

    public int getHair() {
        return hair;
    }

    public PlayerSkinData setHair(int hair) {
        this.hair = hair;
        markChanged();
        return this;
    }

    public int getHairColor() {
        return hairColor;
    }

    public PlayerSkinData setHairColor(int hairColor) {
        this.hairColor = hairColor;
        markChanged();
        return this;
    }

    public int getFace() {
        return face;
    }

    public PlayerSkinData setFace(int face) {
        this.face = face;
        markChanged();
        return this;
    }

    public int getEyesColor() {
        return eyesColor;
    }

    public PlayerSkinData setEyesColor(int eyesColor) {
        this.eyesColor = eyesColor;
        markChanged();
        return this;
    }

    public int getLeg() {
        return leg;
    }

    public PlayerSkinData setLeg(int leg) {
        this.leg = leg;
        markChanged();
        return this;
    }

    public int getJacket() {
        return jacket;
    }

    public PlayerSkinData setJacket(int jacket) {
        this.jacket = jacket;
        markChanged();
        return this;
    }

    public int getShoes() {
        return shoes;
    }

    public PlayerSkinData setShoes(int shoes) {
        this.shoes = shoes;
        markChanged();
        return this;
    }

    public List<Integer> getPeculiarities() {
        return peculiarities;
    }

    public PlayerSkinData setPeculiarities(List<Integer> peculiarities) {
        this.peculiarities = peculiarities;
        markChanged();
        return this;
    }

    public void markChanged() {
        calculateResLoc();
        skinsNeedResync = true;
        hasChanged = true;
        isActive = true;
    }

    public boolean hasChanged(){
        return hasChanged;
    }

    public void markSynced(){
        hasChanged = false;
    }

    public boolean isActive(){
        return isActive;
    }

    private void calculateResLoc() {
        StringBuilder path = new StringBuilder("textures/entity/custom/");
        path.append(getGender()).append("_");
        path.append(getBody()).append("_");
        path.append(getBodyColor()).append("_");
        path.append(getHair()).append("_");
        path.append(getHairColor()).append("_");
        path.append(getFace()).append("_");
        path.append(getEyesColor()).append("_");
        path.append(getLeg()).append("_");
        path.append(getJacket()).append("_");
        path.append(getShoes());

        for (int id : peculiarities) {
            path.append("_").append(id);
        }
        path.append(".png");
        cacheResLoc = new ResourceLocation(CustomNpcs.MODID, path.toString());
    }

    public ResourceLocation getResLoc() {
        if (cacheResLoc == null) calculateResLoc();
        return cacheResLoc;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getPartResLocByNumber(TextureManager textureManager, String name, int partNum){
        ResourceLocation loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/" + getGender() + "/"+name+"/" + partNum + ".png");
        textureManager.bind(loc);
        if (textureManager.getTexture(loc) == null) {
            loc = new ResourceLocation(CustomNpcs.MODID, "textures/entity/custom/" +  getGender() + "/"+name+"/0.png");
            textureManager.bind(loc);
        }
        if (textureManager.getTexture(loc) != null) {
            return loc;
        }
        return null;
    }

    public CompoundNBT saveNBTData(CompoundNBT tag){
        tag.putBoolean("isSmallArms",isSmallArms);
        tag.putInt("body",body);
        tag.putInt("bodyColor",bodyColor);
        tag.putInt("hair",hair);
        tag.putInt("hairColor",hairColor);
        tag.putInt("face",face);
        tag.putInt("eyesColor",eyesColor);
        tag.putInt("leg",leg);
        tag.putInt("jacket",jacket);
        tag.putInt("shoes",shoes);
        tag.putIntArray("peculiarities",peculiarities);
        tag.putBoolean("isActive", isActive);
        return tag;
    }

    public void loadNBTData(CompoundNBT tag){
        isSmallArms = tag.getBoolean("isSmallArms");
        body = tag.getInt("body");
        bodyColor = tag.getInt("bodyColor");
        hair = tag.getInt("hair");
        hairColor = tag.getInt("hairColor");
        face = tag.getInt("face");
        eyesColor = tag.getInt("eyesColor");
        leg = tag.getInt("leg");
        jacket = tag.getInt("jacket");
        shoes = tag.getInt("shoes");
        peculiarities = Arrays.stream(tag.getIntArray("peculiarities")).boxed().collect(Collectors.toList());
        isActive = tag.getBoolean("isActive");
    }

    public static boolean needsAnyResync(){
        return skinsNeedResync;
    }

    public static void resyncPerformed(){
        skinsNeedResync = false;
    }
}
