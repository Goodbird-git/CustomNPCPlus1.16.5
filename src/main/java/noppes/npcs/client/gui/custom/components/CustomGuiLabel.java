package noppes.npcs.client.gui.custom.components;

import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.client.gui.custom.interfaces.*;
import noppes.npcs.client.gui.custom.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import net.minecraft.client.gui.*;
import com.mojang.blaze3d.matrix.*;
import noppes.npcs.api.wrapper.gui.*;
import noppes.npcs.api.gui.*;

public class CustomGuiLabel extends GuiLabel implements IGuiComponent
{
    GuiCustom parent;
    String fullLabel;
    int colour;
    List<TranslationTextComponent> hoverText;
    float scale;
    List<IReorderingProcessor> labels;

    public CustomGuiLabel(final String label, final int id, final int x, final int y, final int width, final int height, final int colour) {
        super(id, (ITextComponent)new TranslationTextComponent(label), colour, x, y, width, height);
        this.scale = 1.0f;
        this.x = GuiCustom.guiLeft + x;
        this.y = GuiCustom.guiTop + y;
        this.width = width;
        this.height = height;
        this.fullLabel = label;
        this.colour = colour;
        final FontRenderer font = Minecraft.getInstance().font;
        this.labels = (List<IReorderingProcessor>)font.split((ITextProperties)new TranslationTextComponent(label), width);
    }

    public void setParent(final GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public void onRender(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        matrixStack.pushPose();
        matrixStack.scale(this.scale, this.scale, 0.0f);
        final boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.render(matrixStack, mouseX, mouseY, partialTicks);
        if (hovered && this.hoverText != null && this.hoverText.size() > 0) {
            this.parent.hoverText = this.hoverText;
        }
        matrixStack.popPose();
    }

    @Override
    public int getID() {
        return this.id;
    }

    public void setScale(final float scale) {
        this.scale = scale;
    }

    public static CustomGuiLabel fromComponent(final CustomGuiLabelWrapper component) {
        final CustomGuiLabel lbl = new CustomGuiLabel(component.getText(), component.getID(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), component.getColor());
        lbl.setScale(component.getScale());
        if (component.hasHoverText()) {
            lbl.hoverText = component.getHoverTextList();
        }
        return lbl;
    }

    @Override
    public ICustomGuiComponent toComponent() {
        final CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(this.id, this.fullLabel, this.x, this.y, this.width, this.height, this.colour);
        component.setHoverText(this.hoverText);
        return component;
    }
}
