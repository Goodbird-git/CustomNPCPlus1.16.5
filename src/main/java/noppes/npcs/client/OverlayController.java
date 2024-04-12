package noppes.npcs.client;

import noppes.npcs.api.overlay.*;
import noppes.npcs.shared.client.gui.components.*;
import it.unimi.dsi.fastutil.ints.*;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import net.minecraft.item.*;
import net.minecraft.client.renderer.*;
import com.mojang.blaze3d.systems.*;
import java.util.*;
import net.minecraft.util.*;

public class OverlayController extends GuiBasic
{
    private static final OverlayController instance;
    private final Int2ObjectOpenHashMap<Overlay> overlays;

    public OverlayController() {
        this.overlays = new Int2ObjectOpenHashMap();
    }

    public static OverlayController getInstance() {
        return OverlayController.instance;
    }

    public void addOverlay(final IOverlay overlay) {
        this.overlays.put(overlay.getId(), new Overlay(overlay));
    }

    public void removeOverlay(final int id) {
        this.overlays.remove(id);
    }

    public void clear() {
        this.overlays.clear();
    }

    public void renderOverlays(final MatrixStack matrixStack) {
        for (final Overlay overlay : this.overlays.values()) {
            overlay.render(matrixStack);
        }
    }

    static {
        instance = new OverlayController();
    }

    static class Overlay
    {
        private final Queue<IOverlayRenderComponent> components;
        private final int linkSide;

        Overlay(final IOverlay overlay) {
            this.components = new ArrayDeque<IOverlayRenderComponent>();
            this.linkSide = overlay.getLinkSide();
            for (final IOverlayComponent component : overlay.getComponents()) {
                if (component instanceof ILabel) {
                    this.components.add(new Label((ILabel)component));
                }
                else if (component instanceof IRenderItemOverlay) {
                    this.components.add(new RenderItem((IRenderItemOverlay)component));
                }
                else {
                    if (!(component instanceof ITexturedRect)) {
                        continue;
                    }
                    this.components.add(new TexturedRect((ITexturedRect)component));
                }
            }
        }

        void render(final MatrixStack matrixStack) {
            matrixStack.pushPose();
            for (final IOverlayRenderComponent component : this.components) {
                component.render(matrixStack, this.linkSide);
            }
            matrixStack.popPose();
        }
    }

    static class Label implements IOverlayRenderComponent
    {
        private final String text;
        private final int x;
        private final int y;
        private final int id;
        private final float scale;

        Label(final ILabel label) {
            final String text = label.getText();
            this.x = label.getPosX();
            this.y = label.getPosY();
            this.id = label.getId();
            this.scale = label.getScale();
            final StringBuilder stringBuilder = new StringBuilder();
            final String[] split;
            final String[] values = split = text.split("&t");
            for (final String s : split) {
                if (I18n.exists(s)) {
                    stringBuilder.append(I18n.get(s, new Object[0]));
                }
                else {
                    stringBuilder.append(s);
                }
            }
            this.text = stringBuilder.toString();
        }

        @Override
        public void render(final MatrixStack matrixStack, final int linkSide) {
            matrixStack.pushPose();
            matrixStack.translate((double)this.x, (double)this.y, (double)this.id);
            matrixStack.scale(this.scale, this.scale, this.scale);
            final int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            final int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            final List<IReorderingProcessor> stringsList = (List<IReorderingProcessor>)Minecraft.getInstance().font.split((ITextProperties)new TranslationTextComponent(this.text), 1000);
            for (final IReorderingProcessor s : stringsList) {
                RenderUtils.renderString(matrixStack, this.text, this.x, this.y, linkSide, width, height, s);
            }
            matrixStack.popPose();
        }
    }

    static class RenderItem implements IOverlayRenderComponent
    {
        private final int x;
        private final int y;
        private final int id;
        private final ItemStack item;
        protected ItemRenderer itemRender;

        RenderItem(final IRenderItemOverlay item) {
            this.x = item.getPosX();
            this.y = item.getPosY();
            this.id = item.getId();
            this.item = item.getItem();
        }

        @Override
        public void render(final MatrixStack matrixStack, final int linkSide) {
            matrixStack.pushPose();
            matrixStack.translate((double)this.x, (double)this.y, (double)this.id);
            matrixStack.scale(1.2f, 1.2f, 1.2f);
            final int width = Minecraft.getInstance().getWindow().getGuiScaledWidth() - 16;
            final int height = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 16;
            RenderSystem.enableRescaleNormal();
            this.itemRender = Minecraft.getInstance().getItemRenderer();
            this.itemRender.blitOffset = 100.0f;
            RenderUtils.renderItemOverlay(linkSide, this.itemRender, this.item, this.x, this.y, width, height);
            this.itemRender.blitOffset = 0.0f;
            RenderSystem.disableRescaleNormal();
            matrixStack.popPose();
        }
    }

    public static class TexturedRect implements IOverlayRenderComponent
    {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final String texture;
        private final int textureX;
        private final int textureY;
        private final int textureMaxX;
        private final int textureMaxY;
        private final int id;

        public TexturedRect(final ITexturedRect component) {
            this.x = component.getPosX();
            this.y = component.getPosY();
            this.id = component.getId();
            this.width = component.getWidth();
            this.height = component.getHeight();
            this.texture = component.getTexture();
            this.textureX = component.getTextureX();
            this.textureY = component.getTextureY();
            this.textureMaxX = component.getTextureMaxX();
            this.textureMaxY = component.getTextureMaxY();
        }

        @Override
        public void render(final MatrixStack matrixStack, final int linkSide) {
            final int width = Minecraft.getInstance().getWindow().getGuiScaledWidth() - this.width;
            final int height = Minecraft.getInstance().getWindow().getGuiScaledHeight() - this.height;
            final int i = width / 2;
            if (Objects.equals(this.texture, "")) {
                RenderUtils.renderGradientRect(this.id, this.x, this.y, linkSide, width, height, this.width, this.height, i, -1072689136, -804253680);
            }
            else {
                Minecraft.getInstance().textureManager.bind(new ResourceLocation(this.texture));
                if (this.textureX >= 0 && this.textureY >= 0) {
                    if (this.textureMaxX >= 0 && this.textureMaxY >= 0) {
                        RenderUtils.renderRectTextureCustomSize(matrixStack, this.x, this.y, linkSide, width, height, this.width, this.height, i, this.textureX, this.textureY, this.textureMaxX, this.textureMaxY);
                    }
                    else {
                        RenderUtils.renderRectTextureSize(matrixStack, this.x, this.y, linkSide, width, height, this.width, this.height, i, this.textureX, this.textureY);
                    }
                }
                else {
                    RenderUtils.renderRectTexture(matrixStack, this.x, this.y, linkSide, width, height, this.width, this.height, i);
                }
            }
        }
    }

    interface IOverlayRenderComponent
    {
        void render(final MatrixStack p0, final int p1);
    }
}
