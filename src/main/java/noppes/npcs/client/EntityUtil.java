package noppes.npcs.client;

import net.minecraft.entity.*;
import net.minecraft.network.datasync.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.boss.dragon.*;
import net.minecraft.entity.passive.*;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.*;
import net.minecraft.world.*;
import net.minecraftforge.registries.*;
import java.util.*;
import net.minecraft.util.*;
import noppes.npcs.shared.common.util.*;

public class EntityUtil
{
    private static HashMap<EntityType<? extends Entity>, Class> entityClasses;

    public static void Copy(final LivingEntity copied, final LivingEntity entity) {
        entity.level = copied.level;
        entity.deathTime = copied.deathTime;
        entity.walkDist = copied.walkDist;
        entity.walkDistO = copied.walkDist;
        entity.moveDist = copied.moveDist;
        entity.zza = copied.zza;
        entity.xxa = copied.xxa;
        entity.setOnGround(copied.isOnGround());
        entity.fallDistance = copied.fallDistance;
        entity.jumping = copied.jumping;
        final List<EntityDataManager.DataEntry<?>> copiedData = copied.getEntityData().getAll();
        final List<EntityDataManager.DataEntry<?>> data = entity.getEntityData().getAll();
        for (final EntityDataManager.DataEntry<?> entry : copiedData) {
            if (data.stream().anyMatch(e -> e.getAccessor() == entry.getAccessor())) {
                entity.getEntityData().set(((EntityDataManager.DataEntry)entry).getAccessor(), entry.getValue());
            }
        }
        entity.xo = copied.xo;
        entity.yo = copied.yo;
        entity.zo = copied.zo;
        entity.setPos(copied.getX(), copied.getY(), copied.getZ());
        entity.xOld = copied.xOld;
        entity.yOld = copied.yOld;
        entity.zOld = copied.zOld;
        entity.setDeltaMovement(copied.getDeltaMovement());
        entity.xRot = copied.xRot;
        entity.yRot = copied.yRot;
        entity.xRotO = copied.xRotO;
        entity.yRotO = copied.yRotO;
        entity.yHeadRot = copied.yHeadRot;
        entity.yHeadRotO = copied.yHeadRotO;
        entity.yBodyRot = copied.yBodyRot;
        entity.yBodyRotO = copied.yBodyRotO;
        entity.useItemRemaining = copied.useItemRemaining;
        entity.animationPosition = copied.animationPosition;
        entity.animStep = copied.animStep;
        entity.animStepO = copied.animStepO;
        entity.swimAmount = copied.swimAmount;
        entity.swimAmountO = copied.swimAmountO;
        entity.swinging = copied.swinging;
        entity.swingTime = copied.swingTime;
        entity.animationSpeed = copied.animationSpeed;
        entity.animationSpeedOld = copied.animationSpeedOld;
        entity.attackAnim = copied.attackAnim;
        entity.oAttackAnim = copied.oAttackAnim;
        entity.tickCount = copied.tickCount;
        entity.setHealth(Math.min(copied.getHealth(), entity.getMaxHealth()));
        entity.getPersistentData().merge(copied.getPersistentData());
        if (entity instanceof PlayerEntity && copied instanceof PlayerEntity) {
            final PlayerEntity ePlayer = (PlayerEntity)entity;
            final PlayerEntity cPlayer = (PlayerEntity)copied;
            ePlayer.bob = cPlayer.bob;
            ePlayer.oBob = cPlayer.oBob;
            ePlayer.xCloakO = cPlayer.xCloakO;
            ePlayer.yCloakO = cPlayer.yCloakO;
            ePlayer.zCloakO = cPlayer.zCloakO;
            ePlayer.xCloak = cPlayer.xCloak;
            ePlayer.yCloak = cPlayer.yCloak;
            ePlayer.zCloak = cPlayer.zCloak;
        }
        for (final EquipmentSlotType slot : EquipmentSlotType.values()) {
            entity.setItemSlot(slot, copied.getItemBySlot(slot));
        }
        if (entity instanceof EnderDragonEntity) {
            entity.xRot += 180.0f;
        }
        entity.removed = copied.removed;
        entity.deathTime = copied.deathTime;
        entity.tickCount = copied.tickCount;
        if (entity instanceof EnderDragonEntity) {
            entity.yRot += 180.0f;
        }
        if (entity instanceof ChickenEntity) {
            ((ChickenEntity)entity).flap = (copied.isOnGround() ? 0.0f : 1.0f);
        }
        for (final EquipmentSlotType slot : EquipmentSlotType.values()) {
            entity.setItemSlot(slot, copied.getItemBySlot(slot));
        }
        if (copied instanceof EntityNPCInterface && entity instanceof EntityNPCInterface) {
            final EntityNPCInterface npc = (EntityNPCInterface)copied;
            final EntityNPCInterface target = (EntityNPCInterface)entity;
            target.textureLocation = npc.textureLocation;
            target.textureGlowLocation = npc.textureGlowLocation;
            target.textureCloakLocation = npc.textureCloakLocation;
            target.display = npc.display;
            target.inventory = npc.inventory;
            if (npc.job.getType() == 9) {
                target.job = npc.job;
            }
            if (target.currentAnimation != npc.currentAnimation) {
                target.currentAnimation = npc.currentAnimation;
                npc.refreshDimensions();
            }
            target.setDataWatcher(npc.getEntityData());
        }
        if (entity instanceof EntityCustomNpc && copied instanceof EntityCustomNpc) {
            final EntityCustomNpc npc2 = (EntityCustomNpc)copied;
            final EntityCustomNpc target2 = (EntityCustomNpc)entity;
            (target2.modelData = npc2.modelData.copy()).setEntity((String)null);
        }
        if(entity instanceof EntityCustomModel && copied instanceof EntityNPCInterface){
            ((EntityCustomModel) entity).textureResLoc= RenderNPCInterface.getNpcTexture((EntityNPCInterface) copied);
            ((EntityCustomModel) entity).modelResLoc=new ResourceLocation(((EntityNPCInterface) copied).display.customModelData.getModel());
            ((EntityCustomModel) entity).animResLoc=new ResourceLocation(((EntityNPCInterface) copied).display.customModelData.getAnimFile());
            ((EntityCustomModel) entity).idleAnim=((EntityNPCInterface) copied).display.customModelData.getIdleAnim();
            if(((EntityNPCInterface) copied).inventory.getLeftHand()!=null){
                ((EntityCustomModel) entity).leftHeldItem=((EntityNPCInterface) copied).inventory.getLeftHand().getMCItemStack();
            }
        }
    }

    private <T> void setData(final LivingEntity entity, final List<EntityDataManager.DataEntry<T>> copiedData, final List<EntityDataManager.DataEntry<T>> data) {
        for (final EntityDataManager.DataEntry<?> entry : copiedData) {
            if (data.stream().anyMatch(e -> e.getAccessor() == entry.getAccessor())) {
                entity.getEntityData().set(((EntityDataManager.DataEntry)entry).getAccessor(), entry.getValue());
            }
        }
    }

    public static void setRecentlyHit(final LivingEntity entity) {
        entity.lastHurtByPlayerTime = 100;
    }

    public static HashMap<EntityType<? extends Entity>, Class> getAllEntitiesClasses(final World level) {
        if (!EntityUtil.entityClasses.isEmpty()) {
            return EntityUtil.entityClasses;
        }
        final HashMap<EntityType<? extends Entity>, Class> data = new HashMap<EntityType<? extends Entity>, Class>();
        for (final EntityType<? extends Entity> ent : ForgeRegistries.ENTITIES.getValues()) {
            try {
                final Entity e = ent.create(level);
                if (e == null) {
                    continue;
                }
                if (LivingEntity.class.isAssignableFrom(e.getClass())) {
                    data.put(ent, e.getClass());
                }
                e.remove();
                e.removed = true;
            }
            catch (Exception ex) {}
        }
        return EntityUtil.entityClasses = data;
    }

    public static HashMap<EntityType<? extends Entity>, Class> getAllEntitiesClassesNoNpcs(final World level) {
        final HashMap<EntityType<? extends Entity>, Class> data = new HashMap<>(getAllEntitiesClasses(level));
        final Iterator<Map.Entry<EntityType<? extends Entity>, Class>> ita = data.entrySet().iterator();
        while (ita.hasNext()) {
            final Map.Entry<EntityType<? extends Entity>, Class> entry = ita.next();
            if (EntityNPCInterface.class.isAssignableFrom(entry.getValue()) || !LivingEntity.class.isAssignableFrom(entry.getValue())) {
                ita.remove();
            }
        }
        return data;
    }

    public static HashMap<String, ResourceLocation> getAllEntities(final World level, final boolean withNpcs) {
        final HashMap<String, ResourceLocation> data = new HashMap<>();
        for (final EntityType<? extends Entity> ent : ForgeRegistries.ENTITIES.getValues()) {
            try {
                final Entity e = ent.create(level);
                if (e == null) {
                    continue;
                }
                if (LivingEntity.class.isAssignableFrom(e.getClass()) && (withNpcs || !EntityNPCInterface.class.isAssignableFrom(e.getClass()))) {
                    data.put(ent.getDescriptionId(), ent.getRegistryName());
                }
                e.remove();
                e.removed = true;
            }
            catch (Throwable e2) {
                LogWriter.except(e2);
            }
        }
        return data;
    }

    static {
        EntityUtil.entityClasses = new HashMap<EntityType<? extends Entity>, Class>();
    }
}
