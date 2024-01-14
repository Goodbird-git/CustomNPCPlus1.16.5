package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.gui.widget.*;
import net.minecraft.client.gui.*;
import noppes.npcs.client.*;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;

public class GuiLabel extends Widget implements IGuiEventListener
{
    public int id;
    protected boolean centered;
    public boolean enabled;
    private boolean labelBgEnabled;
    protected final int textColor;
    private int backColor;
    private int ulColor;
    private int brColor;
    private int border;

    public GuiLabel(final int id, final ITextComponent label, final int color, final int x, final int y, final int width, final int height) {
        super(x, y, width, height, label);
        this.centered = false;
        this.enabled = true;
        this.id = id;
        this.textColor = color;
    }

    public GuiLabel(final int id, final String s, final int x, final int y) {
        this(id, (ITextComponent)new TranslationTextComponent(s), CustomNpcResourceListener.DefaultTextColor, x, y, 40, 0);
    }

    public GuiLabel(final int id, final String s, final int x, final int y, final int color) {
        this(id, (ITextComponent)new TranslationTextComponent(s), color, x, y, 40, 0);
    }

    public GuiLabel(final int id, final String s, final int x, final int y, final int width, final int height) {
        this(id, (ITextComponent)new TranslationTextComponent(s), CustomNpcResourceListener.DefaultTextColor, x, y, width, height);
        this.centered = true;
    }

    public GuiLabel(final int id, final String s, final int x, final int y, final int color, final int width, final int height) {
        this(id, (ITextComponent)new TranslationTextComponent(s), color, x, y, width, height);
        this.centered = true;
    }

    public void render(final MatrixStack stack, final int mouseX, final int mouseY, final float partialTick) {
        if (this.enabled) {
            this.drawBox(stack);
            final int i = this.y + this.height / 2 + this.border / 2;
            if (this.centered) {
                Minecraft.getInstance().font.draw(stack, this.getMessage(), this.x + (this.width - Minecraft.getInstance().font.width((ITextProperties)this.getMessage())) / 2.0f, (float)this.y, this.textColor);
            }
            else {
                Minecraft.getInstance().font.draw(stack, this.getMessage(), (float)this.x, (float)this.y, this.textColor);
            }
        }
    }

    protected void drawBox(final MatrixStack stack) {
        if (this.labelBgEnabled) {
            final int i = this.width + this.border * 2;
            final int j = this.height + this.border * 2;
            final int k = this.x - this.border;
            final int l = this.y - this.border;
            fill(stack, k, l, k + i, l + j, this.backColor);
            this.hLine(stack, k, k + i, l, this.ulColor);
            this.hLine(stack, k, k + i, l + j, this.brColor);
            this.vLine(stack, k, l, l + j, this.ulColor);
            this.vLine(stack, k + i, l, l + j, this.brColor);
        }
    }
}
