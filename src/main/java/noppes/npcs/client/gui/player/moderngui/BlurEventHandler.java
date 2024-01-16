package noppes.npcs.client.gui.player.moderngui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BlurEventHandler {

    @SubscribeEvent
    public static void onGuiChange(GuiOpenEvent event) {
        if (Minecraft.getInstance().level != null) {
            GameRenderer er = Minecraft.getInstance().gameRenderer;
            if (er.currentEffect()==null && event.getGui() != null && (event.getGui() instanceof GuiDialogModern || event.getGui() instanceof GuiQuestModern)) {
                er.loadEffect(new ResourceLocation("customnpcs", "shaders/post/blur.json"));
            } else if (er.currentEffect()!=null && event.getGui() == null) {
                er.shutdownEffect();
            }
        }
    }
}
