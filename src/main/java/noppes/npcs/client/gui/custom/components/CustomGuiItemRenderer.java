package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
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

public class CustomGuiItemRenderer  extends Widget implements IGuiComponent {
    private GuiCustom parent;
    public CustomGuiItemRendererWrapper component;
    private ItemStack stack;
    public int id;
    Minecraft minecraft;
    public CustomGuiItemRenderer(GuiCustom parent, CustomGuiItemRendererWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), new StringTextComponent(""));
        this.component = component;
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.init();
    }

    public void init() {
        this.id = this.component.getID();
        this.x = this.component.getPosX();
        this.y = this.component.getPosY();
        this.setWidth(this.component.getWidth());
        this.setHeight(this.component.getHeight());
        if(component.hasStack()){
            this.stack = component.getStack().getMCItemStack();
        }else {
            this.stack = ItemStack.EMPTY;
        }

        this.active = true;
        this.visible = true;
    }

    public int getID() {
        return this.id;
    }

    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            int x = GuiCustom.guiLeft + this.x;
            int y = GuiCustom.guiTop + this.y;

            if (!NoppesUtilServer.IsItemStackNull(stack)) {
                double scale = component.getScale();
                RenderSystem.pushMatrix();
                RenderSystem.scaled(scale,scale, 1);
                minecraft.getItemRenderer().blitOffset = id;
                minecraft.getItemRenderer().renderAndDecorateItem(stack, (int) (x/scale), (int) (y/scale));
                minecraft.getItemRenderer().renderGuiItemDecorations(minecraft.font, stack, (int) (x/scale), (int) (y/scale));
                minecraft.getItemRenderer().blitOffset = 0;
                RenderSystem.popMatrix();
            }

            boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
            if (hovered && this.component.hasHoverText()) {
                this.parent.hoverText = this.component.getHoverTextList();
            }

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

    public static CustomGuiEntityDisplay fromComponent(GuiCustom parent, CustomGuiEntityDisplayWrapper component) {
        CustomGuiEntityDisplay btn = new CustomGuiEntityDisplay(parent, component);
        return btn;
    }

    public ICustomGuiComponent component() {
        return this.component;
    }
}
