package noppes.npcs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noppes.npcs.CustomNpcs;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CustomNpcs.MODID)
public class OverlayEventHandler {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event){
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (!ForgeIngameGui.renderVignette || !Minecraft.useFancyGraphics()) {
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
        }
        OverlayController.getInstance().renderOverlays(event.getMatrixStack());
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.disableAlphaTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
    }
}
