package noppes.npcs.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerSkinData;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.commons.compress.utils.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;

public class SkinUtil {
    private static final HashSet<ResourceLocation> createdSkins = new HashSet<>();

    public static ResourceLocation createPlayerSkin(PlayerSkinData skin) {
        LogWriter.debug("Check skin: " + skin);

        //PREPARE
        if(createdSkins.contains(skin.getResLoc())){
            return skin.getResLoc();
        }

        String locSkin = String.format("assets/%s/%s", skin.getResLoc().getNamespace(), skin.getResLoc().getPath());
        File file = new File(CustomNpcs.Dir, locSkin);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists() && file.isFile()) {
            return null;
        }

        TextureManager tm = Minecraft.getInstance().getTextureManager();
        IResourceManager rm = Minecraft.getInstance().getResourceManager();

        //LOAD TEXTURES
        List<BufferedImage> listBuffers = Lists.newArrayList();
        //Body texture
        BufferedImage bodyImage = readBufferedImage(rm, skin.getPartResLocByNumber(tm, "torsos", skin.getBody()));
        //Body color
        bodyImage = colorTexture(bodyImage, new Color(skin.getBodyColor()));
        //Hair texture
        BufferedImage hairImage = readBufferedImage(rm, skin.getPartResLocByNumber(tm, "hairs", skin.getHair()));
        //Hair color
        hairImage = colorTexture(hairImage, new Color(skin.getHairColor()));
        //Face texture
        BufferedImage faceImage = readBufferedImage(rm, skin.getPartResLocByNumber(tm, "faces", skin.getFace()));
        //Eyes color
        faceImage = colorTexture(faceImage, new Color(skin.getEyesColor()));
        //Legs texture
        BufferedImage legsImage = readBufferedImage(rm, skin.getPartResLocByNumber(tm, "legs", skin.getLeg()));
        //Jacket texture
        BufferedImage jacketsImage = readBufferedImage(rm, skin.getPartResLocByNumber(tm, "jackets", skin.getJacket()));
        //Shoes texture
        BufferedImage shoesImage = readBufferedImage(rm, skin.getPartResLocByNumber(tm, "shoes", skin.getShoes()));
        //Peculiarities texture
        for(int pec: skin.getPeculiarities()){
            listBuffers.add(readBufferedImage(rm, skin.getPartResLocByNumber(tm, "peculiarities", pec)));
        }

        //COMBINE TEXTURES
        BufferedImage skinImage = combineTextures(bodyImage, readBufferedImage(rm, skin.getPartResLocByNumber(tm, "torsos", -1)));

        skinImage = combineTextures(skinImage, faceImage);
        skinImage = combineTextures(skinImage, legsImage);
        skinImage = combineTextures(skinImage, shoesImage);
        skinImage = combineTextures(skinImage, jacketsImage);
        skinImage = combineTextures(skinImage, faceImage);
        skinImage = combineTextures(skinImage, hairImage);

        if (!listBuffers.isEmpty()) {
            for (BufferedImage buffer : listBuffers) {
                skinImage = combineTextures(skinImage, buffer);
            }
        }

        //SAVE RESULT
        try {
            ImageIO.write(skinImage, "PNG", file);
            tm.bind(skin.getResLoc());
            LogWriter.debug("Create new player skin: " + file.getAbsolutePath());
        } catch (Exception ignored) { }

        SimpleTexture texture = new SimpleTexture(skin.getResLoc());
        TextureUtil.prepareImage(texture.getId(), skinImage.getWidth(), skinImage.getHeight());
        uploadBufferedImageContents(skinImage, texture.getId());
        tm.register(skin.getResLoc(), texture); //TODO check

        createdSkins.add(skin.getResLoc());

        return skin.getResLoc();
    }

    private static BufferedImage readBufferedImage(IResourceManager rm, ResourceLocation resLoc) {
        if (resLoc == null) return null;
        InputStream imageStream = null;
        try {
            imageStream = rm.getResource(resLoc).getInputStream();
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(imageStream);
        }
    }

    private static void uploadBufferedImageContents(BufferedImage bufferedimage, int id) {
        int j = bufferedimage.getWidth();
        int k = bufferedimage.getHeight();
        int[] lvt_8_1_ = new int[j * k];
        bufferedimage.getRGB(0, 0, j, k, lvt_8_1_, 0, j);
        IntBuffer intbuffer = ByteBuffer.allocateDirect(4 * j * k).order(ByteOrder.nativeOrder()).asIntBuffer();
        intbuffer.put(lvt_8_1_);
        intbuffer.flip();

        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(id);
        TextureUtil.initTexture(intbuffer, j, k);
    }

    private static BufferedImage colorTexture(BufferedImage buffer, Color color) {
        if (buffer == null || color == null) {
            return buffer;
        }
        for (int v = 0; v < buffer.getHeight(); v++) {
            for (int u = 0; u < buffer.getWidth(); u++) {
                int c = buffer.getRGB(u, v);
                int al = c >> 24 & 255;
                if (al == 0) {
                    continue;
                }
                int r0 = c >> 16 & 255, g0 = c >> 8 & 255, b0 = c & 255;
                String a = Integer.toHexString(Math.min((al + color.getAlpha()), 255));
                if (a.length() == 1) {
                    a = "0" + a;
                }
                String r = Integer.toHexString((r0 + color.getRed()) / 2);
                if (r.length() == 1) {
                    r = "0" + r;
                }
                String g = Integer.toHexString((g0 + color.getGreen()) / 2);
                if (g.length() == 1) {
                    g = "0" + g;
                }
                String b = Integer.toHexString((b0 + color.getBlue()) / 2);
                if (b.length() == 1) {
                    b = "0" + b;
                }
                buffer.setRGB(u, v, (int) Long.parseLong(a + r + g + b, 16));
            }
        }
        return buffer;
    }

    private static BufferedImage combineTextures(BufferedImage buffer_0, BufferedImage buffer_1) {
        if (buffer_0 == null) {
            return buffer_1;
        }
        if (buffer_1 == null) {
            return buffer_0;
        }
        int w0 = buffer_0.getWidth(), w1 = buffer_1.getWidth();
        int h0 = buffer_0.getHeight(), h1 = buffer_1.getHeight();
        int w = Math.max(w0, w1);
        int h = Math.max(h0, h1);
        float sw0 = (float) w0 / (float) w, sh0 = (float) h0 / (float) h;
        float sw1 = (float) w1 / (float) w, sh1 = (float) h1 / (float) h;
        BufferedImage total = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        for (int v = 0; v < h; v++) {
            for (int u = 0; u < w; u++) {
                int c0 = buffer_0.getRGB((int) ((float) u * sw0), (int) ((float) v * sh0));
                int a0 = c0 >> 24 & 255;
                if (a0 != 0) {
                    total.setRGB(u, v, c0);
                }
                int c1 = buffer_1.getRGB((int) ((float) u * sw1), (int) ((float) v * sh1));
                int a1 = c1 >> 24 & 255;
                if (a1 != 0) {
                    if (a1 == 255) {
                        total.setRGB(u, v, c1);
                    } else {
                        int r0 = c0 >> 16 & 255, g0 = c0 >> 8 & 255, b0 = c0 & 255;
                        int r1 = c1 >> 16 & 255, g1 = c1 >> 8 & 255, b1 = c1 & 255;
                        String a = Integer.toHexString(Math.min((a0 + a1), 255));
                        if (a.length() == 1) {
                            a = "0" + a;
                        }
                        String r = Integer.toHexString((r0 + r1) / 2);
                        if (r.length() == 1) {
                            r = "0" + r;
                        }
                        String g = Integer.toHexString((g0 + g1) / 2);
                        if (g.length() == 1) {
                            g = "0" + g;
                        }
                        String b = Integer.toHexString((b0 + b1) / 2);
                        if (b.length() == 1) {
                            b = "0" + b;
                        }
                        total.setRGB(u, v, (int) Long.parseLong(a + r + g + b, 16));
                    }
                }
            }
        }
        return total;
     }
}
