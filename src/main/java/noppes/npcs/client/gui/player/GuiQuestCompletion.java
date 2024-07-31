package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;

public class GuiQuestCompletion extends GuiNPCInterface implements ITopButtonListener {
    private IQuest quest;

    public GuiQuestCompletion(IQuest quest) {
        super();
        this.imageWidth = 300;
        this.imageHeight = 170;
        this.quest = quest;
        this.drawDefaultBackground = false;
        this.title = "";
        this.closeOnEsc = false;
    }

    @Override
    public void init() {
        super.init();
        String questTitle = I18n.get(this.quest.getName());
        int left = (this.imageWidth - this.font.width(questTitle)) / 2;
        addLabel(new GuiLabel(0, questTitle, this.guiLeft + left, this.guiTop + 4, 0xFFFFFFFF));
        addButton(new GuiButtonNop((IGuiInterface) this, 0, this.guiLeft + 178, this.guiTop + this.imageHeight - 24, 100, 20, I18n.get("gui.done")));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawBackground(matrixStack); // Dibuja el fondo semitransparente limitado al área específica
        drawQuestText(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks); // Renderiza los botones y otros elementos de interfaz
    }

    private void drawQuestText(MatrixStack matrixStack) {
        TextBlockClient block = new TextBlockClient(this.quest.getCompleteText(), 272, true, this.player);
        int yoffset = this.guiTop + 20;
        for (int i = 0; i < block.lines.size(); i++) {
            ITextComponent line = block.lines.get(i);
            this.font.draw(matrixStack, line, this.guiLeft + 4, this.guiTop + 16 + i * 9, 0xFFFFFFFF);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            Packets.sendServer(new SPacketQuestCompletionCheck(this.quest.getId()));
            close();
        }
    }

    @Override
    public void save() {}

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        // No dibuja el fondo semitransparente por defecto, ya que se controlará en drawBackground()
    }

    private void drawBackground(MatrixStack matrixStack) {
        int xStart = (this.width - this.imageWidth) / 2;
        int yStart = (this.height - this.imageHeight) / 2;
        fill(matrixStack, xStart, yStart, xStart + this.imageWidth, yStart + this.imageHeight, 0xBB000000);
    }
}
