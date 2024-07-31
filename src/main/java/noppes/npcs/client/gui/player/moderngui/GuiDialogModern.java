//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package noppes.npcs.client.gui.player.moderngui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Quest;
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

public class GuiDialogModern extends GuiNPCInterface implements IGuiClose {
    private Dialog dialog;
    private int selected = -1;
    private List<Integer> options = new ArrayList<>();
    private final ResourceLocation decomposed = new ResourceLocation("customnpcs", "textures/gui/dialog_menu_decomposed.png");
    private boolean isGrabbed = false;

    private boolean useCenteredText = false;

    public GuiDialogModern(EntityNPCInterface npc, Dialog dialog) {
        super(npc);
        this.dialog = dialog;
        this.appendDialog(dialog);
        this.imageHeight = 238;
        this.useCenteredText = npc.ais.textoCentrado;
    }

    public void init() {
        super.init();
        this.isGrabbed = false;
        this.grabMouse(this.dialog.showWheel);
        this.guiTop = this.height - this.imageHeight;
        this.useCenteredText = this.npc.ais.textoCentrado;
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
        double wcoeff = this.width / 960d;
        double hcoeff = this.height / 509d;
        if (!this.dialog.hideNPC) {
            if (!(npc instanceof EntityDialogNpc)) {
                drawNpc(npc, -210 + (int) (300 * (1 - wcoeff)), 350 - (int) (100 * (1 - hcoeff)), (float) (9.5F * hcoeff), -20);
            }
        }
        int screenWidth = this.width; // Obtén el ancho de la pantalla
        int textBlockWidth = (int) (screenWidth * 0.7); // Ajusta el ancho del cuadro de texto al 70% del ancho de la pantalla
        int lineCount = getLineCount(dialog.text, textBlockWidth);
        int gap = Math.max(16, Math.min((int) (2.6f * (float) lineCount), 32));
        int textPartHeight = 23 + 3 + lineCount * ClientProxy.Font.height(null) + 2 * gap;
        this.fillGradient(matrixStack, 0, height - textPartHeight, this.width, this.height, 0xbb000000, 0xbb000000);
        drawLine(matrixStack, 23, height - textPartHeight + 23, width - 23);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.5, 200.06500244140625);
        matrixStack.scale(1.5f, 1.5f, 1);
        AbstractGui.drawString(matrixStack, font, npc.getDisplayName(), (int) (47 / 1.5), (int) ((height - textPartHeight + 5) / 1.5), -1);
        matrixStack.scale(1 / 1.5f, 1 / 1.5f, 1);
        int textX = (width - textBlockWidth) / 2; // Centra el texto horizontalmente
        int textY = height - textPartHeight + 23 + 3 + gap;  //auto escalado de temaño de interfaz
        drawTextBlock(matrixStack, dialog.text, textX, textY, textBlockWidth);  //auto escalado de temaño de interfaz
        selected = -1;
        matrixStack.scale((float) wcoeff, (float) wcoeff, (float) wcoeff);
        int accumulatedHeight = 0;  // Altura acumulada de las opciones anteriores
        for (int i = 0; i < this.options.size(); i++) {
            int optionNum = options.get(i);
            DialogOption option = dialog.options.get(optionNum);
            int optionHeight = (int) (220 * hcoeff + accumulatedHeight);

            // Calcular el ancho máximo necesario para todas las líneas del título
            String[] titleLines = option.title.split("\\\\n");
            int optionWidth = 0;
            for (String line : titleLines) {
                int lineWidth = this.font.width(line);
                optionWidth = Math.max(optionWidth, lineWidth);
            }
            optionWidth += 32; // Ajustar según necesidades visuales

            if (mouseX >= 723 * wcoeff && mouseX <= 946 * wcoeff && mouseY >= optionHeight * wcoeff && mouseY <= (optionHeight + 13) * wcoeff) {
                selected = i;
            }
            RenderSystem.enableBlend();
            this.minecraft.getTextureManager().bind(decomposed);
            this.blit(matrixStack, 723, optionHeight, 0, i == selected ? 13 : 0, optionWidth, 13);
            RenderSystem.disableBlend();
            if (getQuestByOptionId(optionNum) != null) {
                drawString(matrixStack, this.font, "!", 727, optionHeight + 3, 0x76e85b);
            } else {
                drawString(matrixStack, this.font, ">", 727, optionHeight + 3, -1);
            }

            // Renderizar cada línea del título
            int lineOffset = 0;
            for (String line : titleLines) {
                if (!line.isEmpty()) {
                    drawString(matrixStack, this.font, line, 735, optionHeight + 3 + lineOffset, option.optionColor);
                    lineOffset += 12; // Ajustar según el tamaño de fuente y el espaciado deseado
                }
            }
            accumulatedHeight += 13 + 6 + lineOffset;
        }
        matrixStack.popPose();
    }


    public Quest getQuestByOptionId(int id) {
        DialogOption option = dialog.options.get(id);
        if (option != null && option.getDialog() != null && option.getDialog().hasQuest()) {
            return option.getDialog().getQuest();
        }
        return null;
    }

    public void drawLine(MatrixStack stack, int x, int y, int width) {
        fill(stack, x, y, width, y + 1, 0xff8d3800);
        fill(stack, x, y + 1, width, y + 2, 0xfffea53b);
        fill(stack, x, y + 2, width, y + 3, 0xffae5301);
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
        RenderSystem.runAsFancy(() -> {
            lvt_16_1_.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack, lvt_17_1_, 15728880);
        });
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
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0x66000000, 0x66000000);
    }

    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (key == InputMappings.getKey("key.keyboard.enter").getValue() || key == InputMappings.getKey("key.keyboard.keypad.enter").getValue()) {
            if (this.selected == -1 && this.options.isEmpty() || this.selected >= 0) {
                this.handleDialogSelection();
            }
        }

        if (this.closeOnEsc && (key == InputMappings.getKey("key.keyboard.escape").getValue() || this.isInventoryKey(key))) {
            Packets.sendServer(new SPacketDialogSelected(this.dialog.id, -1));
            this.closed();
            this.onClose();
        }

        return true;
    }

    public boolean mouseClicked(double i, double j, int k) {
        if ((this.selected == -1 && this.options.isEmpty() || this.selected >= 0) && k == 0) {
            this.handleDialogSelection();
        }

        return true;
    }

    private void handleDialogSelection() {
        int optionId = -1;
        if (this.dialog.showWheel) {
            optionId = this.selected;
        } else if (!this.options.isEmpty()) {
            optionId = this.options.get(this.selected);
        }
        if (getQuestByOptionId(optionId) == null) {
            Packets.sendServer(new SPacketDialogSelected(this.dialog.id, optionId));
        } else {
            Minecraft.getInstance().setScreen(new GuiQuestModern(npc, getQuestByOptionId(optionId), dialog, optionId));
        }

        if (this.dialog != null && this.dialog.hasOtherOptions() && !this.options.isEmpty()) {
            DialogOption option = this.dialog.options.get(optionId);
            if (option != null && option.optionType == 1) {
                NoppesUtil.clickSound();
            } else {
                if (this.closeOnEsc) {
                    this.closed();
                    this.onClose();
                }

            }
        } else {
            if (this.closeOnEsc) {
                this.closed();
                this.onClose();
            }

        }
    }

    private void closed() {
        this.grabMouse(false);
        Packets.sendServer(new SPacketQuestCompletionCheckAll());
    }
    // Método para manejar el evento de un botón u otro evento que cambie la configuración

    public void toggleTextAlignment() {
        useCenteredText = !useCenteredText;
        npc.ais.textoCentrado = useCenteredText;
    }

    public void drawTextBlock(MatrixStack stack, String text, int x, int y, int width) {
        TextBlockClient block = new TextBlockClient("", text, width, -1, player, npc);

        int count = 0;
        for (Iterator<ITextComponent> iterator = block.lines.iterator(); iterator.hasNext(); count++) {
            ITextComponent line = iterator.next();
            int height = y + count * ClientProxy.Font.height(null);

            if (useCenteredText) {
                AbstractGui.drawCenteredString(stack, font, line, x + width / 2, height, -1);
            } else {
                AbstractGui.drawString(stack, font, line, x, height, -1);
            }
        }
    }


    public int getLineCount(String text, int width) {
        TextBlockClient block = new TextBlockClient("", text, width, -1, player, npc);
        return block.lines.size();
    }

    public void appendDialog(Dialog dialog) {
        this.closeOnEsc = !dialog.disableEsc;
        this.dialog = dialog;
        this.options = new ArrayList<>();
        if (dialog.sound != null && !dialog.sound.isEmpty()) {
            MusicController.Instance.stopMusic();
            BlockPos pos = this.npc.blockPosition();
            MusicController.Instance.playSound(SoundCategory.VOICE, dialog.sound, pos, 1.0F, 1.0F);
        }

        for (int slot : dialog.options.keySet()) {
            DialogOption option = dialog.options.get(slot);
            if (option != null && option.isAvailable(this.player)) {
                this.options.add(slot);
            }
        }
        this.grabMouse(dialog.showWheel);
    }


    public void setClose(CompoundNBT data) {
        this.grabMouse(false);
    }

}
