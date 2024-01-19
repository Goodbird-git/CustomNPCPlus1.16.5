package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.entity.EntityCustomModel;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketBasic;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.builder.RawAnimation;

public class PacketSyncAnimation extends PacketBasic {
    private final int id;
    private final AnimationBuilder builder;

    public PacketSyncAnimation(int entityId, AnimationBuilder builder) {
        this.id = entityId;
        this.builder = builder;
    }

    public static void encode(PacketSyncAnimation msg, PacketBuffer buf) {
        buf.writeInt(msg.id);

        CompoundNBT compound = new CompoundNBT();
        ListNBT animList = new ListNBT();
        for(RawAnimation anim: msg.builder.getRawAnimationList()){
            CompoundNBT animTag = new CompoundNBT();
            animTag.putString("name", anim.animationName);
            if(anim.loopType!=null) {
                animTag.putInt("loop", ((ILoopType.EDefaultLoopTypes) anim.loopType).ordinal());
            }else{
                animTag.putInt("loop",1);
            }
            animList.add(animTag);
        }
        compound.put("anims",animList);
        buf.writeNbt(compound);
    }

    public static PacketSyncAnimation decode(PacketBuffer buf) {
        int id = buf.readInt();
        AnimationBuilder builder = new AnimationBuilder();
        CompoundNBT compound = buf.readNbt();
        ListNBT animList = compound.getList("anims",10);
        for(int i=0;i<animList.size();i++){
            CompoundNBT animTag = (CompoundNBT) animList.get(i);
            builder.addAnimation(animTag.getString("name"),
                    ILoopType.EDefaultLoopTypes.values()[animTag.getInt("loop")]);
        }
        return new PacketSyncAnimation(id,builder);
    }

    @OnlyIn(Dist.CLIENT)
    public void handle() {
        Entity entity = Minecraft.getInstance().player.getCommandSenderWorld().getEntity(id);
        if(!(entity instanceof EntityCustomNpc)) return;
        EntityCustomNpc npc = (EntityCustomNpc) entity;
        if(npc.modelData==null || !(npc.modelData.getEntity(npc) instanceof EntityCustomModel)) return;
        EntityCustomModel entityCustomModel = (EntityCustomModel) npc.modelData.getEntity(npc);
        entityCustomModel.manualAnim = builder;
    }
}
