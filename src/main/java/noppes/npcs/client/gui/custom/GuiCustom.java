package noppes.npcs.client.gui.custom;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.*;
import noppes.npcs.client.gui.custom.components.*;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiScrollClick;
import noppes.npcs.shared.client.gui.components.GuiCustomScroll;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiCustom extends ContainerScreen<ContainerCustomGui> implements ICustomScrollListener, IGuiData
{
    CustomGuiWrapper gui;
    int imageWidth;
    int imageHeight;
    public static int guiLeft;
    public static int guiTop;
    ResourceLocation background;
    public List<TranslationTextComponent> hoverText;
    Map<Integer, IGuiComponent> components;

    public GuiCustom(final ContainerCustomGui container, final PlayerInventory inv, final ITextComponent titleIn) {
        super(container, inv, titleIn);
        this.components = new HashMap<>();
    }

    public void init() {
        super.init();
        if (this.gui != null) {
            GuiCustom.guiLeft = (this.width - this.imageWidth) / 2;
            GuiCustom.guiTop = (this.height - this.imageHeight) / 2;
            this.components.clear();
            for (final ICustomGuiComponent c : this.gui.getComponents()) {
                this.addComponent(c);
            }
        }
    }

    public void tick() {
        super.tick();
        for (final IGuiComponent component : this.components.values()) {
            if (component instanceof TextFieldWidget) {
                ((TextFieldWidget)component).tick();
            }
        }
    }

    public void render(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        this.hoverText = null;
        this.renderBackground(matrixStack);
        if (this.background != null) {
            this.drawBackgroundTexture(matrixStack);
        }
        for (final IGuiComponent component : this.components.values()) {
            component.onRender(matrixStack, mouseX, mouseY, partialTicks);
        }
        if (this.hoverText != null && !this.hoverText.isEmpty()) {
            GuiUtils.drawHoveringText(matrixStack, this.hoverText, mouseX, mouseY, this.width, this.height, -1, this.font);
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderBg(final MatrixStack matrixStack, final float partialTicks, final int mouseX, final int mouseY) {
    }

    protected void renderLabels(final MatrixStack matrixStack, final int x, final int y) {
    }

    void drawBackgroundTexture(final MatrixStack matrixStack) {
        this.minecraft.getTextureManager().bind(this.background);
        this.blit(matrixStack, GuiCustom.guiLeft, GuiCustom.guiTop, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void addComponent(final ICustomGuiComponent component) {
        final CustomGuiComponentWrapper c = (CustomGuiComponentWrapper)component;
        switch (c.getType()) {
            case 0: {
                final CustomGuiButton button = CustomGuiButton.fromComponent((CustomGuiButtonWrapper)component);
                button.setParent(this);
                this.components.put(button.getID(), button);
                break;
            }
            case 1: {
                final CustomGuiLabel lbl = CustomGuiLabel.fromComponent((CustomGuiLabelWrapper)component);
                lbl.setParent(this);
                this.components.put(lbl.getID(), lbl);
                break;
            }
            case 3: {
                final CustomGuiTextField textField = CustomGuiTextField.fromComponent((CustomGuiTextFieldWrapper)component);
                textField.setParent(this);
                this.components.put(textField.id, textField);
                break;
            }
            case 6: {
                final CustomGuiTextArea textArea = CustomGuiTextArea.fromComponent((CustomGuiTextAreaWrapper)component);
                textArea.setParent(this);
                this.components.put(textArea.id, textArea);
                break;
            }
            case 2: {
                final CustomGuiTexturedRect rect = CustomGuiTexturedRect.fromComponent((CustomGuiTexturedRectWrapper)component);
                rect.setParent(this);
                this.components.put(rect.getID(), rect);
                break;
            }
            case 4: {
                final CustomGuiScrollComponent scroll = new CustomGuiScrollComponent((Screen)this, (CustomGuiScrollWrapper)component);
                scroll.setParent(this);
                this.components.put(scroll.getID(), scroll);
                break;
            }
        }
    }

    public void scrollClicked(final double i, final double j, final int k, final GuiCustomScroll scroll) {
        Packets.sendServer(new SPacketCustomGuiScrollClick(scroll.id, scroll.getSelectedIndex(), false, this.getScrollSelection((CustomGuiScrollComponent)scroll)));
    }

    public void scrollDoubleClicked(final String selection, final GuiCustomScroll scroll) {
        Packets.sendServer(new SPacketCustomGuiScrollClick(scroll.id, scroll.getSelectedIndex(), true, this.getScrollSelection((CustomGuiScrollComponent)scroll)));
    }

    CompoundNBT getScrollSelection(final CustomGuiScrollComponent scroll) {
        final ListNBT list = new ListNBT();
        if (scroll.multipleSelection) {
            for (final String s : scroll.getSelectedList()) {
                list.add(StringNBT.valueOf(s));
            }
        }
        else {
            list.add(StringNBT.valueOf(scroll.getSelected()));
        }
        final CompoundNBT selection = new CompoundNBT();
        selection.put("selection", list);
        return selection;
    }

    public boolean charTyped(final char typedChar, final int keyCode) {
        for (final IGuiComponent comp : this.components.values()) {
            if (comp instanceof IGuiEventListener) {
                ((IGuiEventListener)comp).charTyped(typedChar, keyCode);
            }
        }
        return super.charTyped(typedChar, keyCode);
    }

    public boolean keyPressed(final int key, final int p_keyPressed_2_, final int p_keyPressed_3_) {
        for (final IGuiComponent comp : this.components.values()) {
            if (comp instanceof IGuiEventListener) {
                ((IGuiEventListener)comp).keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
            }
        }
        return this.minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(key, p_keyPressed_2_)) || super.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
    }

    public boolean mouseClicked(final double mouseX, final double mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (final IGuiComponent comp : this.components.values()) {
            if (comp instanceof IGuiEventListener) {
                ((IGuiEventListener)comp).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        return true;
    }

    public boolean isPauseScreen() {
        return this.gui == null || this.gui.getDoesPauseGame();
    }

    public void setGuiData(final CompoundNBT compound) {
        final Minecraft mc = Minecraft.getInstance();
        final CustomGuiWrapper gui = (CustomGuiWrapper)new CustomGuiWrapper().fromNBT(compound);
        this.menu.setGui(gui, mc.player);
        this.gui = gui;
        this.imageWidth = gui.getWidth();
        this.imageHeight = gui.getHeight();
        if (!gui.getBackgroundTexture().isEmpty()) {
            this.background = new ResourceLocation(gui.getBackgroundTexture());
        }
        this.init();
    }
}
