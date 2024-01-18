package noppes.npcs.client.gui.model.custom;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.AnimationFileUtil;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class GuiModelAnimation extends GuiNPCInterface implements ITextfieldListener {

    @Override
    public void init() {
        super.init();
        int y = guiTop + 44;
        addSelectionBlock(1,y,"Animation File:",npc.display.customModelData.getAnimFile());
        addSelectionBlock(2,y+=23,"Idle:",npc.display.customModelData.getIdleAnim());
        addSelectionBlock(3,y+=23,"Walk:",npc.display.customModelData.getWalkAnim());
        addSelectionBlock(4,y+=23,"Attack:",npc.display.customModelData.getAttackAnim());
        addSelectionBlock(5,y+23,"Hurt:",npc.display.customModelData.getHurtAnim());
        this.addButton(new GuiButtonNop(this, 670, width - 22, 2, 20, 20, "X"));
    }

    public void addSelectionBlock(int id, int y, String label, String value){
        this.addLabel(new GuiLabel(id,label, guiLeft - 85, y + 5,0xffffff));
        addTextField(new GuiTextFieldNop(id,this, guiLeft - 40, y, 200, 20, value));
        this.addButton(new GuiButtonNop(this,id, guiLeft + 163, y, 80, 20, "mco.template.button.select"));
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        if(button.id == 670){
            close();
        }
        if(button.id==1){
            setSubGui(new GuiStringSelection(this,"Selecting geckolib animation file:",
                    AnimationFileUtil.getAnimationFileList(), (name)-> npc.display.customModelData.setAnimFile(name)));
        }
        if(button.id==2){
            setSubGui(new GuiStringSelection(this,"Selecting geckolib idle animation:",
                    AnimationFileUtil.getAnimationList(npc.display.customModelData.getAnimFile()),
                    (name)-> npc.display.customModelData.setIdleAnim(name)));
        }
        if(button.id==3){
            setSubGui(new GuiStringSelection(this,"Selecting geckolib walk animation:",
                    AnimationFileUtil.getAnimationList(npc.display.customModelData.getAnimFile()),
                    (name)-> npc.display.customModelData.setWalkAnim(name)));
        }
        if(button.id==4){
            setSubGui(new GuiStringSelection(this,"Selecting geckolib attack animation:",
                    AnimationFileUtil.getAnimationList(npc.display.customModelData.getAnimFile()),
                    (name)-> npc.display.customModelData.setAnimFile(name)));
        }
        if(button.id==5){
            setSubGui(new GuiStringSelection(this,"Selecting geckolib hurt animation:",
                    AnimationFileUtil.getAnimationList(npc.display.customModelData.getAnimFile()),
                    (name)-> npc.display.customModelData.setHurtAnim(name)));
        }
    }

    public boolean isValidAnimFile(String name){
        return GeckoLibCache.getInstance().getAnimations().containsKey(new ResourceLocation(name));
    }

    public boolean isValidAnimation(String name){
        return AnimationFileUtil.getAnimationList(npc.display.customModelData.getAnimFile()).contains(name);
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if(textfield.id == 1 && isValidAnimFile(textfield.getValue())){
            if(!textfield.isEmpty())
                npc.display.customModelData.setAnimFile(textfield.getValue());
            else
                textfield.setValue(npc.display.customModelData.getAnimFile());
        }
        if(textfield.id == 2 && isValidAnimation(textfield.getValue())){
            if(!textfield.isEmpty())
                npc.display.customModelData.setIdleAnim(textfield.getValue());
            else
                textfield.setValue(npc.display.customModelData.getIdleAnim());
        }
        if(textfield.id == 3 && isValidAnimation(textfield.getValue())){
            if(!textfield.isEmpty())
                npc.display.customModelData.setWalkAnim(textfield.getValue());
            else
                textfield.setValue(npc.display.customModelData.getWalkAnim());
        }
        if(textfield.id == 4 && isValidAnimation(textfield.getValue())){
            if(!textfield.isEmpty())
                npc.display.customModelData.setAttackAnim(textfield.getValue());
            else
                textfield.setValue(npc.display.customModelData.getAttackAnim());
        }
        if(textfield.id == 5 && isValidAnimation(textfield.getValue())){
            if(!textfield.isEmpty())
                npc.display.customModelData.setHurtAnim(textfield.getValue());
            else
                textfield.setValue(npc.display.customModelData.getHurtAnim());
        }
    }
}
