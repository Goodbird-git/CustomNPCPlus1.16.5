package noppes.npcs.client.gui.custom.components;

import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.client.gui.custom.interfaces.*;
import noppes.npcs.client.gui.custom.*;
import noppes.npcs.api.wrapper.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.*;
import com.mojang.blaze3d.matrix.*;
import noppes.npcs.api.gui.*;
import java.util.*;

public class CustomGuiScrollComponent extends GuiCustomScroll implements IGuiComponent
{
    GuiCustom parent;
    private CustomGuiScrollWrapper component;

    public CustomGuiScrollComponent(final Screen parent, final CustomGuiScrollWrapper component) {
        super(parent, component.getID(), component.isMultiSelect());
        this.component = component;
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
        this.guiLeft = GuiCustom.guiLeft + component.getPosX();
        this.guiTop = GuiCustom.guiTop + component.getPosY();
        this.setSize(component.getWidth(), component.getHeight());
        this.setUnsortedList(Arrays.asList(component.getList()));
        if (component.getDefaultSelection() >= 0) {
            final int defaultSelect = component.getDefaultSelection();
            if (defaultSelect < this.getList().size()) {
                this.setSelected(this.list.get(defaultSelect));
            }
        }
    }

    public void setParent(final GuiCustom parent) {
        this.parent = parent;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, (double)this.id);
        final boolean hovered = mouseX >= this.guiLeft && mouseY >= this.guiTop && mouseX < this.guiLeft + this.getWidth() && mouseY < this.guiTop + this.getHeight();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
        matrixStack.popPose();
    }

    @Override
    public ICustomGuiComponent toComponent() {
        final List<String> list = this.getList();
        this.component.setList(list.toArray(new String[list.size()]));
        return this.component;
    }
}
