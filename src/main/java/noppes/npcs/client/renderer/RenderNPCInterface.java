package noppes.npcs.client.renderer;

import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.entity.*;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import net.minecraft.client.gui.*;
import com.mojang.blaze3d.systems.*;
import net.minecraft.util.math.vector.*;
import noppes.npcs.entity.*;
import noppes.npcs.mixin.*;
import noppes.npcs.shared.common.util.*;
import net.minecraft.util.*;
import net.minecraft.client.resources.*;
import com.mojang.authlib.minecraft.*;
import java.security.*;
import java.io.*;
import java.util.*;
import net.minecraft.client.renderer.texture.*;

public class RenderNPCInterface<T extends EntityNPCInterface, M extends EntityModel<T>> extends LivingRenderer<T, M>
{
    public static int LastTextureTick;
    public static EntityNPCInterface currentNpc;

    public RenderNPCInterface(final EntityRendererManager manager, final M model, final float f) {
        super(manager, model, f);
    }

    public void renderNameTag(final T npc, final ITextComponent text, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int light) {
        if (!this.shouldShowName(npc) || this.entityRenderDispatcher == null) {
            return;
        }
        final double d0 = this.entityRenderDispatcher.distanceToSqr(npc);
        if (d0 > 512.0) {
            return;
        }
        matrixStack.pushPose();
        final Vector3d renderOffset = this.getRenderOffset(npc, 0.0f);
        matrixStack.translate(-renderOffset.x(), -renderOffset.y(), -renderOffset.z());
        if (npc.messages != null) {
            final float height = npc.baseSize.height / 5.0f * npc.display.getSize();
            final float offset = npc.getBbHeight() * (1.2f + (npc.display.showName() ? (npc.display.getTitle().isEmpty() ? 0.15f : 0.25f) : 0.0f));
            matrixStack.translate(0.0, offset, 0.0);
            npc.messages.renderMessages(matrixStack, buffer, 0.666667f * height, npc.isInRange(this.entityRenderDispatcher.camera.getEntity(), 4.0), light);
            matrixStack.translate(0.0, (-offset), 0.0);
        }
        if (npc.display.showName()) {
            this.renderLivingLabel(npc, matrixStack, buffer, light);
        }
        matrixStack.popPose();
    }

    protected void renderLivingLabel(final T npc, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int light) {
        final float scale = npc.baseSize.height / 5.0f * npc.display.getSize();
        final float height = npc.getBbHeight() - 0.06f * scale;
        matrixStack.pushPose();
        final FontRenderer fontrenderer = this.getFont();
        final float f2 = 0.01666667f * scale;
        matrixStack.translate(0.0, height, 0.0);
        matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        final int color = npc.getFaction().color;
        matrixStack.translate(0.0, (scale / 6.5f * 2.0f), 0.0);
        final float f3 = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        final int j = (int)(f3 * 255.0f) << 24;
        matrixStack.scale(-f2, -f2, f2);
        final Matrix4f matrix4f = matrixStack.last().pose();
        float y = 0.0f;
        final boolean nearby = npc.isInRange(this.entityRenderDispatcher.camera.getEntity(), 8.0);
        if (!npc.display.getTitle().isEmpty() && nearby) {
            final ITextComponent title = new StringTextComponent("<").append(new TranslationTextComponent(npc.display.getTitle())).append(">");
            final float f4 = 0.6f;
            matrixStack.translate(0.0, 4.0, 0.0);
            matrixStack.scale(f4, f4, f4);
            fontrenderer.drawInBatch(title, (float)(-fontrenderer.width(title) / 2), 0.0f, color, false, matrix4f, buffer, false, j, light);
            matrixStack.scale(1.0f / f4, 1.0f / f4, 1.0f / f4);
            y = -10.0f;
        }
        final ITextComponent name = npc.getName();
        fontrenderer.drawInBatch(name, (float)(-fontrenderer.width(name) / 2), y, color, false, matrix4f, buffer, nearby, j, light);
        if (nearby) {
            fontrenderer.drawInBatch(name, (float)(-fontrenderer.width(name) / 2), y, color, false, matrix4f, buffer, false, 0, light);
        }
        matrixStack.popPose();
    }

    protected void renderColor(final EntityNPCInterface npc) {
        if (npc.hurtTime <= 0 && npc.deathTime <= 0) {
            final float red = (npc.display.getTint() >> 16 & 0xFF) / 255.0f;
            final float green = (npc.display.getTint() >> 8 & 0xFF) / 255.0f;
            final float blue = (npc.display.getTint() & 0xFF) / 255.0f;
            RenderSystem.color4f(red, green, blue, 1.0f);
        }
    }

    protected void setupRotations(final T npc, final MatrixStack matrixScale, final float f, final float f1, final float f2) {
        if (npc.isAlive() && npc.isSleeping()) {
            matrixScale.mulPose(Vector3f.YP.rotationDegrees((float)npc.ais.orientation));
            matrixScale.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(npc)));
            matrixScale.mulPose(Vector3f.YP.rotationDegrees(270.0f));
        }
        else if (npc.isAlive() && npc.currentAnimation == 7) {
            matrixScale.mulPose(Vector3f.YP.rotationDegrees(270.0f - f1));
            final float scale = (npc).display.getSize() / 5.0f;
            matrixScale.translate((-scale + ((EntityCustomNpc)npc).modelData.getLegsY() * scale), 0.14000000059604645, 0.0);
            matrixScale.mulPose(Vector3f.ZP.rotationDegrees(270.0f));
            matrixScale.mulPose(Vector3f.YP.rotationDegrees(270.0f));
        }
        else {
            super.setupRotations(npc, matrixScale, f, f1, f2);
        }
    }

    protected void scale(final T npc, final MatrixStack matrixScale, final float f) {
        this.renderColor(npc);
        final int size = npc.display.getSize();
        matrixScale.scale(npc.scaleX / 5.0f * size, npc.scaleY / 5.0f * size, npc.scaleZ / 5.0f * size);
    }

    public void render(final T npc, final float entityYaw, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int packedLight) {
        if (npc.isKilled()) {
            this.shadowRadius = 0.0f;
        }
        if (npc.isKilled() && npc.stats.hideKilledBody && npc.deathTime > 20) {
            return;
        }
        float xOffset = 0.0f;
        float yOffset = (npc.currentAnimation == 0) ? (npc.ais.bodyOffsetY / 10.0f - 0.5f) : 0.0f;
        float zOffset = 0.0f;
        if (npc.isAlive()) {
            if (npc.isSleeping()) {
                xOffset = (float)(-Math.cos(Math.toRadians(180 - npc.ais.orientation)));
                zOffset = (float)(-Math.sin(Math.toRadians(npc.ais.orientation)));
                yOffset += 0.14f;
            }
            else if (npc.currentAnimation == 1 || npc.isPassenger()) {
                yOffset -= 0.5f - ((EntityCustomNpc)npc).modelData.getLegsY() * 0.8f;
            }
        }
        xOffset = xOffset / 5.0f * npc.display.getSize();
        yOffset = yOffset / 5.0f * npc.display.getSize();
        zOffset = zOffset / 5.0f * npc.display.getSize();
        if ((npc.display.getBossbar() != 1 && (npc.display.getBossbar() != 2 || !npc.isAttacking())) || npc.isKilled() || npc.deathTime > 20 || npc.canNpcSee((Entity)Minecraft.getInstance().player)) {}
        if (npc.ais.getStandingType() == 3 && !npc.isWalking() && !npc.isInteracting()) {
            final float n = (float)npc.ais.orientation;
            npc.yBodyRot = n;
            npc.yBodyRotO = n;
        }
        this.shadowRadius = npc.getBbWidth() * 0.8f;
        final int stackSize = ((MatrixStackMixin)matrixStack).getStack().size();
        try {
            super.render((T) (RenderNPCInterface.currentNpc = npc), entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
        catch (Throwable e) {
            while (((MatrixStackMixin)matrixStack).getStack().size() > stackSize) {
                matrixStack.popPose();
            }
            LogWriter.except(e);
        }
        finally {
            RenderNPCInterface.currentNpc = null;
        }
    }

    protected float getBob(final T npc, final float limbSwingAmount) {
        if (npc.isKilled() || !npc.display.getHasLivingAnimation()) {
            return 0.0f;
        }
        return super.getBob(npc, limbSwingAmount);
    }

    public ResourceLocation getTextureLocation(final T npc) {
        if (npc.textureLocation == null) {
            if (npc.display.skinType == 0) {
                npc.textureLocation = new ResourceLocation(npc.display.getSkinTexture());
            }
            else {
                if (RenderNPCInterface.LastTextureTick < 5) {
                    return DefaultPlayerSkin.getDefaultSkin();
                }
                if (npc.display.skinType == 1 && npc.display.playerProfile != null) {
                    final Minecraft minecraft = Minecraft.getInstance();
                    final Map map = minecraft.getSkinManager().getInsecureSkinInformation(npc.display.playerProfile);
                    if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                        npc.textureLocation = minecraft.getSkinManager().registerTexture((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                    }
                }
                else if (npc.display.skinType == 2) {
                    try {
                        String size = "";
                        if (npc instanceof EntityCustomNpc && ((EntityCustomNpc)npc).modelData.getEntity(npc) != null) {
                            size = "32";
                        }
                        final MessageDigest digest = MessageDigest.getInstance("MD5");
                        final byte[] hash = digest.digest((npc.display.getSkinUrl() + size).getBytes("UTF-8"));
                        final StringBuilder sb = new StringBuilder(2 * hash.length);
                        for (final byte b : hash) {
                            sb.append(String.format("%02x", b & 0xFF));
                        }
                        this.loadSkin(null, npc.textureLocation = new ResourceLocation("customnpcs", "skins/" + sb + size), npc.display.getSkinUrl(), !size.isEmpty());
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (npc.textureLocation == null) {
            return DefaultPlayerSkin.getDefaultSkin();
        }
        return npc.textureLocation;
    }

    private void loadSkin(final File file, final ResourceLocation resource, final String par1Str, final boolean fix64) {
        final TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        Texture object = texturemanager.getTexture(resource);
        if (object == null) {
            object = new ImageDownloadAlt(file, par1Str, DefaultPlayerSkin.getDefaultSkin(), fix64, () -> {});
            texturemanager.register(resource, object);
        }
    }
}
