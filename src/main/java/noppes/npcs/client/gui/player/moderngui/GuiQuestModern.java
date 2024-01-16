package noppes.npcs.client.gui.player.moderngui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.java.accessibility.util.java.awt.TextComponentTranslator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.player.GuiDialogInteract;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.MouseHelperMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDialogSelected;
import noppes.npcs.packets.server.SPacketQuestCompletionCheckAll;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GuiQuestModern extends GuiNPCInterface implements IGuiClose {
    private final ResourceLocation decomposed = new ResourceLocation("customnpcs", "textures/gui/dialog_menu_decomposed.png");
    private boolean isGrabbed = false;
    private final Dialog prevDialog;
    private final int optionId;
    private final Quest quest;
    private double wcoeff=1;

    public GuiQuestModern(EntityNPCInterface npc, Quest quest, Dialog prevDialog, int optionId) {
        super(npc);
        this.imageHeight = 238;
        this.prevDialog = prevDialog;
        this.quest = quest;
        this.optionId = optionId;
    }

    public void init() {
        super.init();
        this.isGrabbed = false;
        this.grabMouse(false);
        this.guiTop = this.height - this.imageHeight;
        addButton(new ImageButton(720,326,78,20,36,27,22,decomposed,256,256,(button)->{
            Packets.sendServer(new SPacketDialogSelected(prevDialog.id, -1));
            this.closed();
            this.onClose();
        }));
        addButton(new ImageButton(812,326,78,20,36,27,22,decomposed,256,256,(button)->{
            if(optionId!=-2) {
                Packets.sendServer(new SPacketDialogSelected(prevDialog.id, optionId));
            }else{
                CustomNpcs.proxy.openGui(player, new GuiDialogInteract(npc, prevDialog));
            }
        }));
    }

    public void grabMouse(boolean grab) {
        if (grab && !this.isGrabbed) {
            MouseHelperMixin mouse = (MouseHelperMixin) Minecraft.getInstance().mouseHandler;
            mouse.setGrabbed(false);
            double xpos = 0.0;
            double ypos = 0.0;
            mouse.setX(xpos);
            mouse.setY(ypos);
            InputMappings.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, xpos, ypos);
            this.isGrabbed = true;
        } else if (!grab && this.isGrabbed) {
            Minecraft.getInstance().mouseHandler.releaseMouse();
            this.isGrabbed = false;
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0x66000000, 0x66000000);
        wcoeff = this.width/960d;
        double hcoeff = this.height / 509d;
        if(!(npc instanceof EntityDialogNpc)) {
            drawNpc(npc, -210+(int)(300*(1-wcoeff)), 350-(int)(100*(1- hcoeff)), (float)(9.5F* hcoeff), -20);
        }
        int textBlockWidth = 700;
        String takeQuestString=translate("questgui.doyouaccept");
        int lineCount = getLineCount(takeQuestString, textBlockWidth);
        int gap = Math.max(16, Math.min((int) (2.6f * (float) lineCount), 32));
        int textPartHeight = 23 + 3 + lineCount * ClientProxy.Font.height(null) + 2 * gap;
        this.fillGradient(matrixStack, 0, height - textPartHeight, this.width, this.height, 0x99000000, 0x99000000);
        drawLine(matrixStack, 23, height - textPartHeight + 23, width - 23);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.5, 200.06500244140625);
        matrixStack.scale(1.5f, 1.5f, 1);
        AbstractGui.drawString(matrixStack, font, npc.getDisplayName(), (int) (47 / 1.5), (int) ((height - textPartHeight + 5) / 1.5), -1);
        matrixStack.scale(1 / 1.5f, 1 / 1.5f, 1);
        drawTextBlock(matrixStack, takeQuestString, (width - textBlockWidth) / 2, height - textPartHeight + 23 + 3 + gap, textBlockWidth,-1);
        matrixStack.scale((float) wcoeff,(float) wcoeff,(float) wcoeff);

        Map<Integer,QuestData> activeQuests = PlayerData.get(this.player).questData.activeQuests;
        boolean hadQuest = activeQuests.containsKey(quest.id);
        activeQuests.put(quest.id,new QuestData(quest));
        StringBuilder objectiveString = new StringBuilder();
        String[] questType = {translate("questgui.bringitems"), translate("questgui.readdialog"),
                translate("questgui.killmobs"), translate("questgui.findlocation"), translate("questgui.defeat")};
        for (final IQuestObjective objective : quest.questInterface.getObjectives(this.player)) {
            if(objective!=null)
                objectiveString.append("- ").append(questType[quest.getType()]).append(": ").append(objective.getText()).append("\n");
        }
        if(!hadQuest)
            activeQuests.remove(quest.id);
        int questLineCount = getLineCount(quest.logText, 180);
        int objectivesLineCount = getLineCount(objectiveString.toString(),180);
        int topToTextBottom = 40 + 38+questLineCount*ClientProxy.Font.height(null)+20;
        int topToObjectivesBottom = topToTextBottom+19+objectivesLineCount*ClientProxy.Font.height(null)+14;
        int rewardCount = 0;

        List<Integer> facIDs = new ArrayList<>();
        for (Integer facID : new Integer[]{quest.factionOptions.factionId, quest.factionOptions.faction2Id}) {
            if (facID != -1) facIDs.add(facID);
        }
        for (IItemStack reward : quest.getRewards().getItems()) {
            if (!reward.isEmpty()) rewardCount++;
        }
        int topToRewardsBottom = topToObjectivesBottom+(rewardCount==0?0:(36+13));
        int topToExpBottom = topToRewardsBottom+(quest.rewardExp==0?0:(12));
        int topToFactionBottom = topToExpBottom + (facIDs.size() * 15);
        int questBlockHeight = topToFactionBottom+28;
        this.fillGradient(matrixStack, 675, 40, 675+260, questBlockHeight, 0xbb000000, 0xbb000000);
        matrixStack.scale(1.5f, 1.5f, 1);
        AbstractGui.drawString(matrixStack, font, quest.getName(), (int) (692 / 1.5), (int) (50 / 1.5), -1);
        matrixStack.scale(1 / 1.5f, 1 / 1.5f, 1);
        drawLine(matrixStack,686,66,675+260-11);
        drawTextBlock(matrixStack, quest.logText, 715, 80, 180,0xb8b8b8);
        AbstractGui.drawString(matrixStack, font, translate("questgui.objectives"),690, topToTextBottom, -1);
        drawLeftAllignedTextBlock(matrixStack, objectiveString.toString(), 705, topToTextBottom+12, 180,0xb8b8b8);
        if(rewardCount!=0)
            AbstractGui.drawString(matrixStack, font, translate("questgui.rewards"),690, topToObjectivesBottom, -1);
        for(int i=0;i<quest.rewardItems.getContainerSize();i++){
            ItemStack rewardStack = quest.rewardItems.getItem(i);
            if(rewardStack==null || rewardStack.isEmpty()) continue;
            this.minecraft.getTextureManager().bind(decomposed);
            this.blit(matrixStack, 690+26*i, topToObjectivesBottom+16, 0, 27, 24, 24);
            RenderSystem.scaled(wcoeff,wcoeff,wcoeff);
            this.itemRenderer.renderAndDecorateItem(player, rewardStack, (int)((694+26*i)), (int)((topToObjectivesBottom+20)));
            this.itemRenderer.renderGuiItemDecorations(this.font, rewardStack, (int)((694+26*i)),  (int)((topToObjectivesBottom+20)), ""+rewardStack.getCount());
            RenderSystem.scaled(1/wcoeff,1/wcoeff,1/wcoeff);
        }
        if(quest.rewardExp!=0) {
            AbstractGui.drawString(matrixStack, font, translate("questgui.experience"), 690, topToRewardsBottom, 0xb8b8b8);
            int expPosX = 690+ClientProxy.Font.width(translate("questgui.experience")+"     ");
            AbstractGui.drawString(matrixStack, font, ""+quest.rewardExp, expPosX, topToRewardsBottom, -1);
            int expSymbolPosX = expPosX+ClientProxy.Font.width(quest.rewardExp+"  ");
            this.minecraft.getTextureManager().bind(decomposed);
            this.blit(matrixStack, expSymbolPosX, topToRewardsBottom, 26, 27, 8, 8);
        }
        int fac1ID =  quest.factionOptions.factionId;
        if (fac1ID != -1) {
            String fac1Name = FactionController.instance.getFaction(fac1ID).getName();
            String fac1Color = (quest.factionOptions.decreaseFactionPoints) ? "§c-" : "§a+";
            int fac1Point = quest.factionOptions.factionPoints;
            int facIDIndex = facIDs.indexOf(fac1ID);
            AbstractGui.drawString(matrixStack, font, fac1Name + " " + fac1Color + fac1Point, 690, topToExpBottom + (facIDIndex * 12), 0xb8b8b8);
        }
        int fac2ID =  quest.factionOptions.faction2Id;
        if (fac2ID != -1) {
            String fac2Name = FactionController.instance.getFaction(fac2ID).getName();
            String fac2Color = (quest.factionOptions.decreaseFaction2Points) ? "§c-" : "§a+";
            int fac2Point = quest.factionOptions.faction2Points;
            int facIDIndex = facIDs.indexOf(fac2ID);
            AbstractGui.drawString(matrixStack, font, fac2Name + " " + fac2Color + fac2Point, 690, topToExpBottom + (facIDIndex * 12), 0xb8b8b8);
        }
        this.buttons.get(0).y=topToFactionBottom;
        this.buttons.get(1).y=topToFactionBottom;
        super.render(matrixStack, (int) ((double)mouseX/wcoeff), (int) ((double)mouseY/wcoeff), partialTicks);
        String reject = translate("questgui.reject");
        AbstractGui.drawString(matrixStack, font, reject, 753-ClientProxy.Font.width(reject)/2, topToFactionBottom+6, -1);
        String accept = translate("questgui.accept");
        AbstractGui.drawString(matrixStack, font, accept, 847-ClientProxy.Font.width(accept)/2, topToFactionBottom+6, -1);
        matrixStack.popPose();
    }

    public void drawLine(MatrixStack stack, int x, int y, int width) {
        fill(stack, x, y, width, y + 1, 0xff8d3800);
        fill(stack, x, y + 1, width, y + 2, 0xfffea53b);
        fill(stack, x, y + 2, width, y + 3, 0xffae5301);
    }

    public String translate(String key){
        return I18n.get(key);
    }

    public void drawNpc(LivingEntity entity, int x, int y, float zoomed, int rotation) {
        EntityCustomNpc npc = null;
        if (entity instanceof EntityCustomNpc) {
            npc = (EntityCustomNpc) entity;
        }

        float f2 = entity.yBodyRot;
        float f3 = entity.yRot;
        float f4 = entity.xRot;
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        float scale = 1.0F;
        if ((double) entity.getBbHeight() > 2.4) {
            scale = 2.0F / entity.getBbHeight();
        }

        float f7 = (float) (guiLeft + x);
        entity.yBodyRot = 0.0F;
        entity.yRot = (float) Math.atan(f7 / 80.0F) * 40.0F + (float) rotation;
        entity.xRot = 0;
        entity.yHeadRot = 0.0F;
        entity.yHeadRotO = 0.0F;
        int orientation = 0;
        int showName = 0;
        if (npc != null) {
            showName = npc.display.getShowName();
            npc.display.setShowName(1);
            orientation = npc.ais.orientation;
            npc.ais.orientation = rotation;
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) guiLeft + (float) x, (float) (guiTop + y), 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0, 0.0, 1000.0);
        matrixStack.scale(30.0F * scale * zoomed, 30.0F * scale * zoomed, 30.0F * scale * zoomed);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        EntityRendererManager lvt_16_1_ = Minecraft.getInstance().getEntityRenderDispatcher();
        lvt_16_1_.setRenderShadow(false);
        IRenderTypeBuffer.Impl lvt_17_1_ = Minecraft.getInstance().renderBuffers().bufferSource();
        matrixStack.mulPose(Vector3f.YN.rotationDegrees((float) rotation));
        RenderSystem.runAsFancy(() -> lvt_16_1_.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack, lvt_17_1_, 15728880));
        lvt_17_1_.endBatch();
        lvt_16_1_.setRenderShadow(true);
        RenderSystem.popMatrix();
        entity.yBodyRot = f2;
        entity.yRot = f3;
        entity.xRot = f4;
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        if (npc != null) {
            npc.display.setShowName(showName);
            npc.ais.orientation = orientation;
        }
    }

    public void renderBackground(MatrixStack matrixStack, int p_238651_2_) {

    }

    public boolean mouseClicked(double i, double j, int k) {
        super.mouseClicked(i/wcoeff,j/wcoeff,k);
        return true;
    }

    private void closed() {
        this.grabMouse(false);
        Packets.sendServer(new SPacketQuestCompletionCheckAll());
    }

    public void drawTextBlock(MatrixStack stack, String text, int x, int y, int width, int color) {
        TextBlockClient block = new TextBlockClient("", text, width, -1, player, npc);

        int count = 0;
        for (Iterator<ITextComponent> var9 = block.lines.iterator(); var9.hasNext(); count++) {
            ITextComponent line = var9.next();
            int height = y + count * ClientProxy.Font.height(null);
            AbstractGui.drawCenteredString(stack, font, line, x + width / 2, height, color);
        }
    }

    public void drawLeftAllignedTextBlock(MatrixStack stack, String text, int x, int y, int width, int color) {
        TextBlockClient block = new TextBlockClient("", text, width, -1, player, npc);

        int count = 0;
        for (Iterator<ITextComponent> var9 = block.lines.iterator(); var9.hasNext(); count++) {
            ITextComponent line = var9.next();
            int height = y + count * ClientProxy.Font.height(null);
            AbstractGui.drawString(stack, font, line, x, height, color);
        }
    }

    public int getLineCount(String text, int width) {
        TextBlockClient block = new TextBlockClient("", text, width, -1, player, npc);
        return block.lines.size();
    }

    public void setClose(CompoundNBT data) {
        this.grabMouse(false);
    }

}