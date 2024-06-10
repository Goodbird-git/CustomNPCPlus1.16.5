package noppes.npcs.client.gui.custom.components;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.*;
import net.minecraft.util.registry.Registry;
import noppes.npcs.client.gui.custom.interfaces.*;
import noppes.npcs.client.gui.custom.*;
import net.minecraft.util.*;
import java.util.*;
import noppes.npcs.api.wrapper.gui.*;
import net.minecraft.util.text.*;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import noppes.npcs.api.gui.*;
import noppes.npcs.packets.server.*;
import noppes.npcs.packets.*;

public class CustomGuiButton extends Button implements IGuiComponent
{
    GuiCustom parent;
    ResourceLocation texture;
    public int textureX;
    public int textureY;
    boolean hovered;
    String label;
    String soundPath;
    int colour;
    boolean centered;
    List<TranslationTextComponent> hoverText;
    public int id;

    public CustomGuiButton(final int buttonId, final String buttonText, final int x, final int y, final int width, final int height, final CustomGuiButtonWrapper component) {
        super(GuiCustom.guiLeft + x, GuiCustom.guiTop + y, width, height, new TranslationTextComponent(buttonText), button -> Packets.sendServer(new SPacketCustomGuiButton(buttonId)));
        this.colour = 16777215;
        this.id = buttonId;
        if (component.hasTexture()) {
            this.textureX = component.getTextureX();
            this.textureY = component.getTextureY();
            this.texture = new ResourceLocation(component.getTexture());
        }
        this.centered = component.isCentered();
        this.label = buttonText;
        this.soundPath = component.getSoundPath();
    }

    public boolean keyPressed(final int p_231046_1_, final int p_231046_2_, final int p_231046_3_) {
        return false;
    }

    public void setParent(final GuiCustom parent) {
        this.parent = parent;
    }

    public int getID() {
        return this.id;
    }

    public void onRender(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        matrixStack.pushPose();
        final Minecraft mc = Minecraft.getInstance();
        final FontRenderer font = mc.font;
        if (this.texture == null) {
            mc.getTextureManager().bind(CustomGuiButton.WIDGETS_LOCATION);
            this.hovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
            final int i = this.getYImage(this.hovered);
            this.blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBg(matrixStack, mc, mouseX, mouseY);
            int j = 14737632;
            if (this.colour != 0) {
                j = this.colour;
            }
            else if (!this.active) {
                j = 10526880;
            }
            else if (this.hovered) {
                j = 16777120;
            }
            matrixStack.translate(0.0, 0.0, 0.10000000149011612);
            if(centered) {
                drawCenteredString(matrixStack, font, this.label, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            }else{
                Minecraft.getInstance().font.draw(matrixStack, this.label, 4+(float)this.x, this.y + (this.height - 8) / 2, j);
            }
            if (this.hovered && this.hoverText != null && this.hoverText.size() > 0) {
                this.parent.hoverText = this.hoverText;
            }
        }
        else {
            mc.getTextureManager().bind(this.texture);
            this.hovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
            final int i = this.hoverState(this.hovered);
            this.blit(matrixStack, this.x, this.y, this.textureX, this.textureY + i * this.height, this.width, this.height);
            if(centered) {
                drawCenteredString(matrixStack, font, this.label, this.x + this.width / 2, this.y + (this.height - 8) / 2, this.colour);
            }else{

            }
            if (this.hovered && this.hoverText != null && this.hoverText.size() > 0) {
                this.parent.hoverText = this.hoverText;
            }
        }
        matrixStack.popPose();
    }

    public ICustomGuiComponent toComponent() {
        final CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(this.id, this.label, this.x, this.y, this.width, this.height, this.texture.toString(), this.textureX, this.textureY);
        component.setHoverText(this.hoverText);
        component.setEnabled(this.active);
        return component;
    }

    public static CustomGuiButton fromComponent(final CustomGuiButtonWrapper component) {
        CustomGuiButton btn;
        if (component.getWidth() >= 0 && component.getHeight() >= 0) {
            btn = new CustomGuiButton(component.getID(), component.getLabel(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), component);
        }
        else {
            btn = new CustomGuiButton(component.getID(), component.getLabel(), component.getPosX(), component.getPosY(), 200, 20, component);
        }
        if (component.hasHoverText()) {
            btn.hoverText = component.getHoverTextList();
        }
        btn.active = component.getEnabled();
        return btn;
    }

    public void setColour(final int colour) {
        this.colour = colour;
    }

    protected int hoverState(final boolean mouseOver) {
        int i = 0;
        if (mouseOver) {
            i = 1;
        }
        return i;
    }

    public void playDownSound(SoundHandler p_230988_1_) {
        if (soundPath.isEmpty()) return;
        if (Registry.SOUND_EVENT.containsKey(new ResourceLocation(soundPath))) {
            p_230988_1_.play(SimpleSound.forUI(Registry.SOUND_EVENT.get(new ResourceLocation(soundPath)), 1.0F));
        } else {
            p_230988_1_.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
