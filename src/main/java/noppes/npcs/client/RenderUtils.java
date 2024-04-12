package noppes.npcs.client;

import com.mojang.blaze3d.matrix.*;
import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import net.minecraft.client.gui.*;
import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.platform.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;

public class RenderUtils
{
    public static void renderString(final MatrixStack matrixStack, final String text, final int x, final int y, final int linkSide, final int width, final int height, final IReorderingProcessor s) {
        switch (linkSide) {
            case 1: {
                Minecraft.getInstance().font.drawShadow(matrixStack, s, (float)x, (float)y, 16777215);
                break;
            }
            case 2: {
                Minecraft.getInstance().font.drawShadow(matrixStack, s, (width + x - Minecraft.getInstance().font.width(text)) / 2.0f, (float)y, 16777215);
                break;
            }
            case 3: {
                Minecraft.getInstance().font.drawShadow(matrixStack, s, (float)(x + width - Minecraft.getInstance().font.width(text)), (float)y, 16777215);
                break;
            }
            case 4: {
                final FontRenderer font = Minecraft.getInstance().font;
                final float n = (float)x;
                final int n2 = y + height;
                Minecraft.getInstance().font.getClass();
                font.drawShadow(matrixStack, s, n, (n2 - 9) / 2.0f, 16777215);
                break;
            }
            case 5: {
                final FontRenderer font2 = Minecraft.getInstance().font;
                final float n3 = (x + width) / 2.0f - Minecraft.getInstance().font.width(text) / 2.0f;
                final int n4 = y + height;
                Minecraft.getInstance().font.getClass();
                font2.drawShadow(matrixStack, s, n3, (n4 - 9) / 2.0f, 16777215);
                break;
            }
            case 6: {
                final FontRenderer font3 = Minecraft.getInstance().font;
                final float n5 = (float)(x + width - Minecraft.getInstance().font.width(text));
                final int n6 = y + height;
                Minecraft.getInstance().font.getClass();
                font3.drawShadow(matrixStack, s, n5, (n6 - 9) / 2.0f, 16777215);
                break;
            }
            case 7: {
                final FontRenderer font4 = Minecraft.getInstance().font;
                final float n7 = (float)x;
                final int n8 = y + height;
                Minecraft.getInstance().font.getClass();
                font4.drawShadow(matrixStack, s, n7, (float)(n8 - 9), 16777215);
                break;
            }
            case 8: {
                final FontRenderer font5 = Minecraft.getInstance().font;
                final float n9 = (x + width) / 2.0f - Minecraft.getInstance().font.width(text) / 2.0f;
                final int n10 = y + height;
                Minecraft.getInstance().font.getClass();
                font5.drawShadow(matrixStack, s, n9, (float)(n10 - 9), 16777215);
                break;
            }
            case 9: {
                final FontRenderer font6 = Minecraft.getInstance().font;
                final float n11 = (float)(x + width - Minecraft.getInstance().font.width(text));
                final int n12 = y + height;
                Minecraft.getInstance().font.getClass();
                font6.drawShadow(matrixStack, s, n11, (float)(n12 - 9), 16777215);
                break;
            }
        }
    }

    public static void renderItemOverlay(final int linkSide, final ItemRenderer itemRender, final ItemStack item, final int x, final int y, final int width, final int height) {
        switch (linkSide) {
            case 1: {
                itemRender.renderAndDecorateItem(item, x, y);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, x, y);
                break;
            }
            case 2: {
                itemRender.renderAndDecorateItem(item, (width + x) / 2, y);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, (width + x) / 2, y);
                break;
            }
            case 3: {
                itemRender.renderAndDecorateItem(item, width - x, y);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, width - x, y);
                break;
            }
            case 4: {
                itemRender.renderAndDecorateItem(item, x, (height + y) / 2);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, x, (height + y) / 2);
                break;
            }
            case 5: {
                itemRender.renderAndDecorateItem(item, width / 2 + x, (height + y) / 2);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, width / 2 + x, (height + y) / 2);
                break;
            }
            case 6: {
                itemRender.renderAndDecorateItem(item, width - x, (height + y) / 2);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, width - x, (height + y) / 2);
                break;
            }
            case 7: {
                itemRender.renderAndDecorateItem(item, x, height + y);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, x, height + y);
                break;
            }
            case 8: {
                itemRender.renderAndDecorateItem(item, (width + x) / 2, height - y);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, (width + x) / 2, height - y);
                break;
            }
            case 9: {
                itemRender.renderAndDecorateItem(item, width - x, height - y);
                itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, item, width - x, height - y);
                break;
            }
        }
    }

    public static void renderGradientRect(final int id, final int x, final int y, final int linkSide, final int widthScaled, final int heightScaled, final int width, final int height, final int i, final int startColor, final int endColor) {
        switch (linkSide) {
            case 1: {
                drawGradientRect(id, x, y, x + width, y + height, startColor, endColor);
                break;
            }
            case 2: {
                drawGradientRect(id, (widthScaled + x) / 2, y, (widthScaled + x) / 2 + width, y + height, startColor, endColor);
                break;
            }
            case 3: {
                drawGradientRect(id, widthScaled - x, y, widthScaled - x + width, y + height, startColor, endColor);
                break;
            }
            case 4: {
                drawGradientRect(id, x, (heightScaled + y) / 2, x + width, (heightScaled + y) / 2 + height, startColor, endColor);
                break;
            }
            case 5: {
                drawGradientRect(id, i + x, (heightScaled + y) / 2, i + x + width, (heightScaled + y) / 2 + height, startColor, endColor);
                break;
            }
            case 6: {
                drawGradientRect(id, widthScaled - x, (heightScaled + y) / 2, widthScaled - x + width, (heightScaled + y) / 2 + height, startColor, endColor);
                break;
            }
            case 7: {
                drawGradientRect(id, x, heightScaled + y, x + width, heightScaled + y + height, startColor, endColor);
                break;
            }
            case 8: {
                drawGradientRect(id, (widthScaled + x) / 2, heightScaled - y, (widthScaled + x) / 2 + width, heightScaled - y + height, startColor, endColor);
                break;
            }
            case 9: {
                drawGradientRect(id, widthScaled - x, heightScaled - y, widthScaled - x + width, heightScaled - y + height, startColor, endColor);
                break;
            }
        }
    }

    public static void renderRectTexture(final MatrixStack matrixStack, final int x, final int y, final int linkSide, final int widthScaled, final int heightScaled, final int width, final int height, final int i) {
        switch (linkSide) {
            case 1: {
                AbstractGui.blit(matrixStack, x, y, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 2: {
                AbstractGui.blit(matrixStack, (widthScaled + x) / 2, y, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 3: {
                AbstractGui.blit(matrixStack, widthScaled - x, y, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 4: {
                AbstractGui.blit(matrixStack, x, (heightScaled + y) / 2, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 5: {
                AbstractGui.blit(matrixStack, i + x, (heightScaled + y) / 2, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 6: {
                AbstractGui.blit(matrixStack, widthScaled - x, (heightScaled + y) / 2, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 7: {
                AbstractGui.blit(matrixStack, x, heightScaled + y, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 8: {
                AbstractGui.blit(matrixStack, (widthScaled + x) / 2, heightScaled - y, (float)width, (float)height, width, height, width, height);
                break;
            }
            case 9: {
                AbstractGui.blit(matrixStack, widthScaled - x, heightScaled - y, (float)width, (float)height, width, height, width, height);
                break;
            }
        }
    }

    public static void renderRectTextureSize(final MatrixStack matrixStack, final int x, final int y, final int linkSide, final int widthScaled, final int heightScaled, final int width, final int height, final int i, final int textureX, final int textureY) {
        switch (linkSide) {
            case 1: {
                AbstractGui.blit(matrixStack, x, y, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 2: {
                AbstractGui.blit(matrixStack, (widthScaled + x) / 2, y, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 3: {
                AbstractGui.blit(matrixStack, widthScaled - x - textureX, y, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 4: {
                AbstractGui.blit(matrixStack, x, (heightScaled + y) / 2, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 5: {
                AbstractGui.blit(matrixStack, i + x, (heightScaled + y) / 2, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 6: {
                AbstractGui.blit(matrixStack, widthScaled - x - textureX, (heightScaled + y) / 2, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 7: {
                AbstractGui.blit(matrixStack, x, heightScaled + y - textureY, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 8: {
                AbstractGui.blit(matrixStack, i + x, heightScaled - y - textureY, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
            case 9: {
                AbstractGui.blit(matrixStack, widthScaled - x - textureX, heightScaled - y - textureY, (float)width, (float)height, textureX, textureY, 256, 256);
                break;
            }
        }
    }

    public static void renderRectTextureCustomSize(final MatrixStack matrixStack, final int x, final int y, final int linkSide, final int widthScaled, final int heightScaled, final int width, final int height, final int i, final int textureX, final int textureY, final int textureMaxX, final int textureMaxY) {
        switch (linkSide) {
            case 1: {
                AbstractGui.blit(matrixStack, x, y, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 2: {
                AbstractGui.blit(matrixStack, (widthScaled + x) / 2, y, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 3: {
                AbstractGui.blit(matrixStack, widthScaled - x - textureX, y, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 4: {
                AbstractGui.blit(matrixStack, x, (heightScaled + y) / 2, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 5: {
                AbstractGui.blit(matrixStack, i + x, (heightScaled + y) / 2, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 6: {
                AbstractGui.blit(matrixStack, widthScaled - x - textureX, (heightScaled + y) / 2, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 7: {
                AbstractGui.blit(matrixStack, x, heightScaled + y - textureY, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 8: {
                AbstractGui.blit(matrixStack, i + x, heightScaled - y - textureY, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
            case 9: {
                AbstractGui.blit(matrixStack, widthScaled - x - textureX, heightScaled - y - textureY, (float)width, (float)height, textureX, textureY, textureMaxX, textureMaxY);
                break;
            }
        }
    }

    public static void drawGradientRect(final int id, final int left, final int top, final int right, final int bottom, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.vertex(right, top, id).color(f2, f3, f4, f).endVertex();
        bufferbuilder.vertex(left, top, id).color(f2, f3, f4, f).endVertex();
        bufferbuilder.vertex(left, bottom, id).color(f6, f7, f8, f5).endVertex();
        bufferbuilder.vertex(right, bottom, id).color(f6, f7, f8, f5).endVertex();
        tessellator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
}

