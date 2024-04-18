package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiEntityDisplayWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.entity.EntityNPCInterface;

public class CustomGuiEntityDisplay extends Widget implements IGuiComponent {
    private GuiCustom parent;
    public CustomGuiEntityDisplayWrapper component;
    private Entity entity;
    public int id;

    public CustomGuiEntityDisplay(GuiCustom parent, CustomGuiEntityDisplayWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), new StringTextComponent(""));
        this.component = component;
        this.parent = parent;
        this.init();
    }

    public void init() {
        this.id = this.component.getID();
        this.x = this.component.getPosX();
        this.y = this.component.getPosY();
        this.setWidth(this.component.getWidth());
        this.setHeight(this.component.getHeight());
        if(component.entityId!=-1){
            this.entity = Minecraft.getInstance().player.getCommandSenderWorld().getEntity(component.entityId);
        }else if (!this.component.entityData.getMCNBT().isEmpty()) {
            this.entity = (Entity) EntityType.create(this.component.entityData.getMCNBT(), Minecraft.getInstance().level).orElse(null);
        }

        this.active = true;
        this.visible = true;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public int getID() {
        return this.id;
    }

    public void onRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            if (this.entity != null) {
                drawEntity(this.entity, this.x, this.y, this.component.getScale(), this.component.getRotation() / 2 + 180, mouseX, mouseY, (float)this.width / 2.0F, (float)this.height * 0.9F);
            }

            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (hovered && this.component.hasHoverText()) {
                this.parent.hoverText = this.component.getHoverTextList();
            }

        }
    }

    @Override
    public ICustomGuiComponent toComponent() {
        return null;
    }

    protected int getYImage(boolean p_getYImage_1_) {
        return 0;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        return true;
    }

    public static CustomGuiEntityDisplay fromComponent(GuiCustom parent, CustomGuiEntityDisplayWrapper component) {
        CustomGuiEntityDisplay btn = new CustomGuiEntityDisplay(parent, component);
        return btn;
    }

    public void drawEntity(Entity entity, int x, int y, float zoomed, int rotation, int xMouse, int yMouse, float guiLeft, float guiTop) {
        EntityNPCInterface npc = null;
        if (entity instanceof EntityNPCInterface) {
            npc = (EntityNPCInterface) entity;
        }
        LivingEntity livingEntity = null;
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity) entity;
        }

        float f3 = entity.yRot;
        float f4 = entity.xRot;
        float f2 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        if (livingEntity != null) {
            f2 = livingEntity.yBodyRot;
            f5 = livingEntity.yHeadRotO;
            f6 = livingEntity.yHeadRot;
        }

        float scale = 1.0F;
        if ((double)entity.getBbHeight() > 2.4) {
            scale = 2.0F / entity.getBbHeight();
        }

        float f7 = guiLeft + (float)x - (float)xMouse;
        float f8 = (guiTop + (float)y - 50.0F * scale * zoomed) * (entity.getBbHeight() / entity.getEyeHeight()) - (float)yMouse;
        if(component.isFollowingCursor) {
            entity.yRot = ((float) Math.atan((double) (f7 / 80.0F)) * 40.0F + (float) rotation);
            entity.xRot = (-((float) Math.atan((double) (f8 / 40.0F))) * 20.0F);
        }else{
            entity.yRot = (float) rotation;
            entity.xRot = 0;
        }
        if (livingEntity != null) {
            livingEntity.yHeadRotO = livingEntity.yHeadRot = livingEntity.yBodyRot = entity.yRot;
        }

        int orientation = 0;
        if (npc != null) {
            orientation = npc.ais.orientation;
            npc.ais.orientation = (int)entity.yRot;
        }

        float fs = 30.0F * scale * zoomed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiLeft + (float)x, (float)(guiTop + y), 1050.0f);
        RenderSystem.scalef(1.0f, 1.0f, -1.0f);
        final MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0, 0.0, 1000.0);
        matrixStack.scale(30.0f * scale * zoomed, 30.0f * scale * zoomed, 30.0f * scale * zoomed);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        final EntityRendererManager lvt_16_1_ = Minecraft.getInstance().getEntityRenderDispatcher();
        lvt_16_1_.setRenderShadow(false);
        final IRenderTypeBuffer.Impl lvt_17_1_ = Minecraft.getInstance().renderBuffers().bufferSource();
        matrixStack.mulPose(Vector3f.YN.rotationDegrees((float)rotation));
        RenderSystem.runAsFancy(() -> lvt_16_1_.render((Entity)entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrixStack, (IRenderTypeBuffer)lvt_17_1_, 15728880));
        lvt_17_1_.endBatch();
        lvt_16_1_.setRenderShadow(true);
        RenderSystem.popMatrix();
        entity.yRot = (f3);
        entity.xRot = (f4);
        if (livingEntity != null) {
            livingEntity.yBodyRot = f2;
            livingEntity.yHeadRotO = f5;
            livingEntity.yHeadRot = f6;
        }

        if (npc != null) {
            npc.ais.orientation = orientation;
        }

    }

    public ICustomGuiComponent component() {
        return this.component;
    }
}
