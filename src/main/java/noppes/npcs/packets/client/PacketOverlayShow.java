package noppes.npcs.packets.client;


import noppes.npcs.api.wrapper.OverlayWrapper;
import noppes.npcs.client.OverlayController;
import noppes.npcs.shared.common.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraftforge.api.distmarker.*;

public class PacketOverlayShow extends PacketBasic
{
    private final CompoundNBT compound;

    public PacketOverlayShow(final CompoundNBT compound) {
        this.compound = compound;
    }

    public static void encode(final PacketOverlayShow msg, final PacketBuffer buf) {
        buf.writeNbt(msg.compound);
    }

    public static PacketOverlayShow decode(final PacketBuffer buf) {
        return new PacketOverlayShow(buf.readNbt());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle() {
        final OverlayWrapper wrapper = new OverlayWrapper(0);
        wrapper.fromNbt(this.compound);
        OverlayController.getInstance().addOverlay(wrapper);
    }
}
