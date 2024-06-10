package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiColoredLineWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;

public class CustomGuiColoredLine extends Widget implements IGuiComponent {
    private GuiCustom parent;
    public CustomGuiColoredLineWrapper component;
    public int id;

    public CustomGuiColoredLine(GuiCustom parent, CustomGuiColoredLineWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getXEnd() - component.getPosX(), component.getYEnd() - component.getPosY(), new StringTextComponent(""));
        this.component = component;
        this.parent = parent;
        this.init();
    }

    public void init() {
        this.id = this.component.getID();
        this.x = this.component.getPosX();
        this.y = this.component.getPosY();
        this.setWidth(component.getXEnd() - component.getPosX());
        this.setHeight(component.getYEnd() - component.getPosY());
        this.active = true;
        this.visible = true;
    }

    public int getID() {
        return this.id;
    }

    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            float xBeg = GuiCustom.guiLeft + Math.min(this.x, this.component.getXEnd());
            float yBeg = GuiCustom.guiTop + Math.min(this.y, this.component.getYEnd());
            float xEnd = GuiCustom.guiLeft + Math.max(this.x, this.component.getXEnd());
            float yEnd = GuiCustom.guiTop + Math.max(this.y, this.component.getYEnd());
            float xVec = xEnd - xBeg;
            float yVec = yEnd - yBeg;
            float xPerp = 1, yPerp = 1;
            if (xVec != 0) {
                xPerp = -yVec * yPerp / xVec;
            } else {
                yPerp = -xVec * xPerp / yVec;
            }
            float perpLen = (float) Math.sqrt(xPerp * xPerp + yPerp * yPerp);
            xPerp /= perpLen;
            yPerp /= perpLen;
            xPerp *= component.getThickness() / 2;
            yPerp *= component.getThickness() / 2;
            int color = component.getColor();
            int r = color >> 24 & 0xff;
            int g = color >> 16 & 0xff;
            int b = color >> 8 & 0xff;
            int a = color & 0xff;
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            BufferBuilder builder = Tessellator.getInstance().getBuilder();
            builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            builder.vertex(matrixStack.last().pose(), xEnd + xPerp, yEnd + yPerp, getBlitOffset()).color(r, g, b, a).endVertex();
            builder.vertex(matrixStack.last().pose(), xEnd - xPerp, yEnd - yPerp, getBlitOffset()).color(r, g, b, a).endVertex();
            builder.vertex(matrixStack.last().pose(), xBeg - xPerp, yBeg - yPerp, getBlitOffset()).color(r, g, b, a).endVertex();
            builder.vertex(matrixStack.last().pose(), xBeg + xPerp, yBeg + yPerp, getBlitOffset()).color(r, g, b, a).endVertex();
            Tessellator.getInstance().end();
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    @Override
    public ICustomGuiComponent toComponent() {
        return null;
    }

    protected int getYImage(boolean p_getYImage_1_) {
        return 0;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        return true;
    }

    public static CustomGuiColoredLine fromComponent(GuiCustom parent, CustomGuiColoredLineWrapper component) {
        CustomGuiColoredLine line = new CustomGuiColoredLine(parent, component);
        return line;
    }

    public ICustomGuiComponent component() {
        return this.component;
    }
}
