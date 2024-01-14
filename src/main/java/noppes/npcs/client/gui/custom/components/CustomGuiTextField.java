package noppes.npcs.client.gui.custom.components;

import net.minecraft.client.gui.widget.*;
import noppes.npcs.client.gui.custom.interfaces.*;
import noppes.npcs.client.gui.custom.*;
import noppes.npcs.api.wrapper.gui.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.nbt.*;
import noppes.npcs.packets.server.*;
import noppes.npcs.packets.*;
import noppes.npcs.api.gui.*;

public class CustomGuiTextField extends TextFieldWidget implements IGuiComponent
{
    GuiCustom parent;
    CustomGuiTextFieldWrapper component;
    public int id;

    public CustomGuiTextField(final CustomGuiTextFieldWrapper component) {
        super(Minecraft.getInstance().font, GuiCustom.guiLeft + component.getPosX(), GuiCustom.guiTop + component.getPosY(), component.getWidth(), component.getHeight(), (ITextComponent)new TranslationTextComponent(component.getText()));
        this.id = component.getID();
        this.setMaxLength(500);
        this.component = component;
        if (component.getText() != null && !component.getText().isEmpty()) {
            this.setValue(component.getText());
        }
    }

    public void onRender(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        matrixStack.pushPose();
        final boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
        matrixStack.popPose();
    }

    public void setParent(final GuiCustom parent) {
        this.parent = parent;
    }

    public int getID() {
        return this.id;
    }

    public boolean keyPressed(final int p_keyPressed_1_, final int p_keyPressed_2_, final int p_keyPressed_3_) {
        final String text = this.getValue();
        final boolean bo = super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        if (!text.equals(this.getValue())) {
            this.component.setText(this.getValue());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(this.id, this.component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    public boolean charTyped(final char c, final int i) {
        final String text = this.getValue();
        final boolean bo = super.charTyped(c, i);
        if (!text.equals(this.getValue())) {
            this.component.setText(this.getValue());
            Packets.sendServer(new SPacketCustomGuiTextUpdate(this.id, this.component.toNBT(new CompoundNBT())));
        }
        return bo;
    }

    public ICustomGuiComponent toComponent() {
        this.component.setText(this.getValue());
        return this.component;
    }

    public static CustomGuiTextField fromComponent(final CustomGuiTextFieldWrapper component) {
        final CustomGuiTextField txt = new CustomGuiTextField(component);
        if (component.getText() != null && !component.getText().isEmpty()) {
            txt.setValue(component.getText());
        }
        txt.active = component.getEnabled();
        return txt;
    }
}
