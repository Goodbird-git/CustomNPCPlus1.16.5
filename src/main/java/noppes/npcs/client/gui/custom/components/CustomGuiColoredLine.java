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
import org.lwjgl.opengl.GL11;

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
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();

            int color = component.getColor();
            int r = color >> 24 & 0xff;
            int g = color >> 16 & 0xff;
            int b = color >> 8 & 0xff;
            int a = color & 0xff;

            GL11.glLineWidth(component.getThickness());
            BufferBuilder builder = Tessellator.getInstance().getBuilder();
            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            builder.vertex(matrixStack.last().pose(), GuiCustom.guiLeft + this.x, GuiCustom.guiTop + this.y, getBlitOffset()).color(r, g, b, a).endVertex();
            builder.vertex(matrixStack.last().pose(), GuiCustom.guiLeft + this.component.getXEnd(), GuiCustom.guiTop + this.component.getYEnd(), getBlitOffset()).color(r, g, b, a).endVertex();
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
