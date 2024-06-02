package noppes.npcs.api.wrapper.gui;

import noppes.npcs.api.gui.*;
import net.minecraft.nbt.*;

public class CustomGuiButtonWrapper extends CustomGuiComponentWrapper implements IButton
{
    int width;
    int height;
    String label;
    String texture;
    int textureX;
    int textureY;
    boolean enabled;
    boolean centered;

    public CustomGuiButtonWrapper() {
        this.height = -1;
        this.textureY = -1;
        this.enabled = true;
        this.centered = true;
    }

    public CustomGuiButtonWrapper(final int id, final String label, final int x, final int y) {
        this.height = -1;
        this.textureY = -1;
        this.enabled = true;
        this.setID(id);
        this.setLabel(label);
        this.setPos(x, y);
        this.centered = true;
    }

    public CustomGuiButtonWrapper(final int id, final String label, final int x, final int y, final int width, final int height) {
        this(id, label, x, y);
        this.setSize(width, height);
    }

    public CustomGuiButtonWrapper(final int id, final String label, final int x, final int y, final int width, final int height, final String texture) {
        this(id, label, x, y, width, height);
        this.setTexture(texture);
    }

    public CustomGuiButtonWrapper(final int id, final String label, final int x, final int y, final int width, final int height, final String texture, final int textureX, final int textureY) {
        this(id, label, x, y, width, height, texture);
        this.setTextureOffset(textureX, textureY);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public IButton setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public IButton setLabel(final String label) {
        this.label = label;
        return this;
    }

    @Override
    public String getTexture() {
        return this.texture;
    }

    @Override
    public boolean hasTexture() {
        return this.texture != null;
    }

    @Override
    public IButton setTexture(final String texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public int getTextureX() {
        return this.textureX;
    }

    @Override
    public int getTextureY() {
        return this.textureY;
    }

    @Override
    public IButton setTextureOffset(final int textureX, final int textureY) {
        this.textureX = textureX;
        this.textureY = textureY;
        return this;
    }

    @Override
    public void setEnabled(final boolean bo) {
        this.enabled = bo;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public ICustomGuiComponent setID(final int id) {
        this.id = id;
        return this;
    }

    @Override
    public int getPosX() {
        return this.posX;
    }

    @Override
    public int getPosY() {
        return this.posY;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public CompoundNBT toNBT(final CompoundNBT nbt) {
        super.toNBT(nbt);
        if (this.width > 0 && this.height > 0) {
            nbt.putIntArray("size", new int[] { this.width, this.height });
        }
        nbt.putString("label", this.label);
        if (this.hasTexture()) {
            nbt.putString("texture", this.texture);
        }
        if (this.textureX >= 0 && this.textureY >= 0) {
            nbt.putIntArray("texPos", new int[] { this.textureX, this.textureY });
        }
        nbt.putBoolean("enabled", this.enabled);
        nbt.putBoolean("centered", this.centered);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(final CompoundNBT nbt) {
        super.fromNBT(nbt);
        if (nbt.contains("size")) {
            this.setSize(nbt.getIntArray("size")[0], nbt.getIntArray("size")[1]);
        }
        this.setLabel(nbt.getString("label"));
        if (nbt.contains("texture")) {
            this.setTexture(nbt.getString("texture"));
        }
        if (nbt.contains("texPos")) {
            this.setTextureOffset(nbt.getIntArray("texPos")[0], nbt.getIntArray("texPos")[1]);
        }
        this.setEnabled(nbt.getBoolean("enabled"));
        if(nbt.contains("centered")){
            this.setCentered(nbt.getBoolean("centered"));
        }else{
            this.setCentered(true);
        }
        return this;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }
}
