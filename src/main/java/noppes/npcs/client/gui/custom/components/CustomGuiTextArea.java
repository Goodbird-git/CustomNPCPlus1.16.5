package noppes.npcs.client.gui.custom.components;

import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.client.gui.custom.interfaces.*;
import noppes.npcs.client.gui.custom.*;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.nbt.*;
import noppes.npcs.packets.server.*;
import noppes.npcs.packets.*;
import noppes.npcs.api.gui.*;
import noppes.npcs.api.wrapper.gui.*;

public class CustomGuiTextArea extends GuiTextArea implements IGuiComponent
{
    GuiCustom parent;
    CustomGuiTextFieldWrapper component;

    public CustomGuiTextArea(final int id, final int x, final int y, final int width, final int height) {
        super(id, GuiCustom.guiLeft + x, GuiCustom.guiTop + y, width, height, "");
    }

    @Override
    public void onRender(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        matrixStack.pushPose();
        final boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        super.render(matrixStack, mouseX, mouseY);
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
        matrixStack.popPose();
    }

    public void setParent(final GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public boolean keyPressed(final int p_keyPressed_1_, final int p_keyPressed_2_, final int p_keyPressed_3_) {
        final String text = this.getText();
        final boolean bo = super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        if (!text.equals(this.getText())) {
            this.component.setText(this.getText());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(this.id, this.component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    @Override
    public boolean charTyped(final char c, final int i) {
        final String text = this.getText();
        final boolean bo = super.charTyped(c, i);
        if (!text.equals(this.getText())) {
            this.component.setText(this.getText());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(this.id, this.component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    @Override
    public ICustomGuiComponent toComponent() {
        this.component.setText(this.getText());
        return this.component;
    }

    public static CustomGuiTextArea fromComponent(final CustomGuiTextAreaWrapper component) {
        final CustomGuiTextArea txt = new CustomGuiTextArea(component.getID(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight());
        txt.component = component;
        if (component.getText() != null && !component.getText().isEmpty()) {
            txt.setText(component.getText());
        }
        txt.enabled = component.getEnabled();
        return txt;
    }
}
