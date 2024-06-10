package noppes.npcs;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import noppes.npcs.api.event.ForgeEvent;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ForgeEventHandler
{
    public static List<String> eventNames;

    @SubscribeEvent
    public void forgeEntity(final Event event) {
        if (CustomNpcs.Server == null || !ScriptController.Instance.forgeScripts.isEnabled()) {
            return;
        }
        try {
            if (event instanceof PlayerEvent) {
                final PlayerEvent ev = (PlayerEvent)event;
                if (!(ev.getPlayer().level instanceof ServerWorld)) {
                    return;
                }
            }
            if (event instanceof EntityEvent) {
                final EntityEvent ev2 = (EntityEvent)event;
                if (ev2.getEntity() == null || !(ev2.getEntity().level instanceof ServerWorld)) {
                    return;
                }
                if (event instanceof PlayerXpEvent) {
                    LogWriter.info(event);
                }
                EventHooks.onForgeEntityEvent(ev2);
            }
            else if (event instanceof WorldEvent) {
                final WorldEvent ev3 = (WorldEvent)event;
                if (!(ev3.getWorld() instanceof ServerWorld)) {
                    return;
                }
                EventHooks.onForgeWorldEvent(ev3);
            }
            else {
                if (event instanceof TickEvent && ((TickEvent)event).side == LogicalSide.CLIENT) {
                    return;
                }
                EventHooks.onForgeEvent(new ForgeEvent(event), event);
            }
        }
        catch (Throwable t) {
            LogWriter.error("Error in " + event.getClass().getName(), t);
        }
    }

    public static String getEventName(final Class c) {
        final String eventName = c.getName();
        final int i = eventName.lastIndexOf(".");
        return StringUtils.uncapitalize(eventName.substring(i + 1).replace("$", ""));
    }

    static {
        ForgeEventHandler.eventNames = new ArrayList<>();
    }
}
