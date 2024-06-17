package noppes.npcs.api.wrapper.gui;

import noppes.npcs.api.gui.*;
import net.minecraft.util.text.*;
import net.minecraft.nbt.*;
import java.util.*;

public abstract class CustomGuiComponentWrapper implements ICustomGuiComponent
{
    int id;
    int posX;
    int posY;
    List<TranslationTextComponent> hoverText;

    public CustomGuiComponentWrapper() {
        this.hoverText = new ArrayList<>();
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
    public ICustomGuiComponent setPos(final int x, final int y) {
        this.posX = x;
        this.posY = y;
        return this;
    }

    @Override
    public boolean hasHoverText() {
        return this.hoverText.size() > 0;
    }

    @Override
    public String[] getHoverText() {
        final String[] ht = new String[this.hoverText.size()];
        for (int i = 0; i < this.hoverText.size(); ++i) {
            ht[i] = this.hoverText.get(i).getKey();
        }
        return ht;
    }

    public List<TranslationTextComponent> getHoverTextList() {
        return this.hoverText;
    }

    @Override
    public ICustomGuiComponent setHoverText(final String text) {
        (this.hoverText = new ArrayList<>()).add(new TranslationTextComponent(text));
        return this;
    }

    @Override
    public ICustomGuiComponent setHoverText(final String[] text) {
        final List<TranslationTextComponent> list = new ArrayList<>();
        for (final String s : text) {
            list.add(new TranslationTextComponent(s));
        }
        this.hoverText = list;
        return this;
    }

    public ICustomGuiComponent setHoverText(final List<TranslationTextComponent> list) {
        this.hoverText = new ArrayList<>();
        for(Object obj:list){
            if(obj instanceof TranslationTextComponent){
                hoverText.add((TranslationTextComponent) obj);
            }else{
                hoverText.add(new TranslationTextComponent(String.valueOf(obj)));
            }
        }
        return this;
    }

    public abstract int getType();

    public CompoundNBT toNBT(final CompoundNBT nbt) {
        nbt.putInt("id", this.id);
        nbt.putIntArray("pos", new int[] { this.posX, this.posY });
        if (this.hoverText != null) {
            final ListNBT list = new ListNBT();
            for (final TranslationTextComponent s : this.hoverText) {
                list.add(StringNBT.valueOf(s.getKey()));
            }
            if (!list.isEmpty()) {
                nbt.put("hover", list);
            }
        }
        nbt.putInt("type", this.getType());
        return nbt;
    }

    public CustomGuiComponentWrapper fromNBT(final CompoundNBT nbt) {
        this.setID(nbt.getInt("id"));
        this.setPos(nbt.getIntArray("pos")[0], nbt.getIntArray("pos")[1]);
        if (nbt.contains("hover")) {
            final ListNBT list = nbt.getList("hover", 8);
            final String[] hoverText = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                hoverText[i] = list.get(i).getAsString();
            }
            this.setHoverText(hoverText);
        }
        return this;
    }

    public static CustomGuiComponentWrapper createFromNBT(final CompoundNBT nbt) {
        switch (nbt.getInt("type")) {
            case 0: {
                return new CustomGuiButtonWrapper().fromNBT(nbt);
            }
            case 1: {
                return new CustomGuiLabelWrapper().fromNBT(nbt);
            }
            case 2: {
                return new CustomGuiTexturedRectWrapper().fromNBT(nbt);
            }
            case 3: {
                return new CustomGuiTextFieldWrapper().fromNBT(nbt);
            }
            case 4: {
                return new CustomGuiScrollWrapper().fromNBT(nbt);
            }
            case 5: {
                return new CustomGuiItemSlotWrapper().fromNBT(nbt);
            }
            case 6: {
                return new CustomGuiTextAreaWrapper().fromNBT(nbt);
            }
            case 7: {
                return new CustomGuiEntityDisplayWrapper().fromNBT(nbt);
            }
            case 8: {
                return new CustomGuiColoredLineWrapper().fromNBT(nbt);
            }
            case 9: {
                return new CustomGuiItemRendererWrapper().fromNBT(nbt);
            }
            default: {
                return null;
            }
        }
    }
}
