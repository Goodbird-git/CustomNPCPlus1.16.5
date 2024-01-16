package noppes.npcs.api.wrapper;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;
import noppes.npcs.api.*;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.ServerCloneController;
import ru.noxus.rpghud.utils.StatusHelper;

import java.util.*;

public class EntityWrapper<T extends Entity> implements IEntity
{
    protected T entity;
    private Map<String, Object> tempData;
    private IWorld levelWrapper;
    private final IData tempdata;
    private final IData storeddata;

    public EntityWrapper(final T entity) {
        this.tempData = new HashMap<>();
        this.tempdata = new IData() {
            public void put(String key, Object value) {
                if (ModList.get().isLoaded("rpg_hud")) {
                    if (value instanceof Number) {
                        String hudKey = key.replaceAll("BuildUp", "");
                        if (Arrays.asList(StatusHelper.statusNames).contains(hudKey)) {
                            StatusHelper.setStatus(entity, hudKey, ((Number) value).intValue());
                        }
                    } else {
                        if (key.equals("StatusResistances")) {
                            Set<Map.Entry<String, Object>> entrySet = ((ScriptObjectMirror) value).entrySet();
                            StatusHelper.setMaxStatus(entity, entrySet);
                        }
                    }
                }
                EntityWrapper.this.tempData.put(key, value);
            }

            public Object get(String key) {
                return EntityWrapper.this.tempData.get(key);
            }

            public void remove(String key) {
                if (key.equals("StatusResistances")) {
                    StatusHelper.setMaxStatus(entity, null);
                }
                EntityWrapper.this.tempData.remove(key);
            }

            public boolean has(String key) {
                return EntityWrapper.this.tempData.containsKey(key);
            }

            public void clear() {
                EntityWrapper.this.tempData.clear();
            }

            public String[] getKeys() {
                return EntityWrapper.this.tempData.keySet().toArray(new String[EntityWrapper.this.tempData.size()]);
            }
        };
        this.storeddata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                final CompoundNBT compound = this.getStoredCompound();
                if (value instanceof Number) {
                    compound.putDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    compound.putString(key, (String)value);
                }
                this.saveStoredCompound(compound);
            }

            @Override
            public Object get(final String key) {
                final CompoundNBT compound = this.getStoredCompound();
                if (!compound.contains(key)) {
                    return null;
                }
                final INBT base = compound.get(key);
                if (base instanceof NumberNBT) {
                    return ((NumberNBT)base).getAsDouble();
                }
                return base.getAsString();
            }

            @Override
            public void remove(final String key) {
                final CompoundNBT compound = this.getStoredCompound();
                compound.remove(key);
                this.saveStoredCompound(compound);
            }

            @Override
            public boolean has(final String key) {
                return this.getStoredCompound().contains(key);
            }

            @Override
            public void clear() {
                EntityWrapper.this.entity.getPersistentData().remove("CNPCStoredData");
            }

            private CompoundNBT getStoredCompound() {
                return EntityWrapper.this.entity.getPersistentData().getCompound("CNPCStoredData");
            }

            private void saveStoredCompound(final CompoundNBT compound) {
                EntityWrapper.this.entity.getPersistentData().put("CNPCStoredData", compound);
            }

            @Override
            public String[] getKeys() {
                final CompoundNBT compound = this.getStoredCompound();
                return compound.getAllKeys().toArray(new String[0]);
            }
        };
        this.entity = entity;
        this.levelWrapper = NpcAPI.Instance().getIWorld((ServerWorld)entity.level);
    }

    @Override
    public double getX() {
        return this.entity.getX();
    }

    @Override
    public void setX(final double x) {
        this.entity.setPos(x, this.entity.getY(), this.entity.getZ());
    }

    @Override
    public double getY() {
        return this.entity.getY();
    }

    @Override
    public void setY(final double y) {
        this.entity.setPos(this.entity.getX(), y, this.entity.getZ());
    }

    @Override
    public double getZ() {
        return this.entity.getZ();
    }

    @Override
    public void setZ(final double z) {
        this.entity.setPos(this.entity.getX(), this.entity.getY(), z);
    }

    @Override
    public int getBlockX() {
        return MathHelper.floor(this.entity.getX());
    }

    @Override
    public int getBlockY() {
        return MathHelper.floor(this.entity.getY());
    }

    @Override
    public int getBlockZ() {
        return MathHelper.floor(this.entity.getZ());
    }

    @Override
    public String getEntityName() {
        final String s = this.entity.getType().getDescriptionId();
        return LanguageMap.getInstance().getOrDefault(s);
    }

    @Override
    public String getName() {
        return this.entity.getName().getString();
    }

    @Override
    public void setName(final String name) {
        this.entity.setCustomName(new StringTextComponent(name));
    }

    @Override
    public boolean hasCustomName() {
        return this.entity.hasCustomName();
    }

    @Override
    public void setPosition(final double x, final double y, final double z) {
        this.entity.setPos(x, y, z);
    }

    @Override
    public IWorld getWorld() {
        if (this.entity.level != this.levelWrapper.getMCWorld()) {
            this.levelWrapper = NpcAPI.Instance().getIWorld((ServerWorld)this.entity.level);
        }
        return this.levelWrapper;
    }

    @Override
    public boolean isAlive() {
        return this.entity.isAlive();
    }

    @Override
    public IData getTempdata() {
        return this.tempdata;
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public long getAge() {
        return this.entity.tickCount;
    }

    @Override
    public void damage(final float amount) {
        this.entity.hurt(DamageSource.GENERIC, amount);
    }

    @Override
    public void despawn() {
        this.entity.removed = true;
    }

    @Override
    public void spawn() {
        if (this.levelWrapper.getMCWorld().getEntity(this.entity.getUUID()) != null) {
            throw new CustomNPCsException("Entity is already spawned");
        }
        this.entity.removed = false;
        this.levelWrapper.getMCWorld().addFreshEntity(this.entity);
    }

    @Override
    public void kill() {
        this.entity.remove();
    }

    @Override
    public boolean inWater() {
        return this.entity.level.getBlockStates(this.entity.getBoundingBox()).anyMatch(state -> state.getMaterial() == Material.WATER);
    }

    @Override
    public boolean inLava() {
        return this.entity.level.getBlockStates(this.entity.getBoundingBox()).anyMatch(state -> state.getMaterial() == Material.LAVA);
    }

    @Override
    public boolean inFire() {
        return this.entity.level.getBlockStates(this.entity.getBoundingBox()).anyMatch(state -> state.getMaterial() == Material.FIRE);
    }

    @Override
    public boolean isBurning() {
        return this.entity.isOnFire();
    }

    @Override
    public void setBurning(final int ticks) {
        this.entity.setRemainingFireTicks(ticks);
    }

    @Override
    public void extinguish() {
        this.entity.clearFire();
    }

    @Override
    public String getTypeName() {
        return this.entity.getEncodeId();
    }

    @Override
    public IEntityItem dropItem(final IItemStack item) {
        return (IEntityItem)NpcAPI.Instance().getIEntity(this.entity.spawnAtLocation(item.getMCItemStack(), 0.0f));
    }

    @Override
    public IEntity[] getRiders() {
        final List<Entity> list = this.entity.getPassengers();
        final IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            riders[i] = NpcAPI.Instance().getIEntity(list.get(i));
        }
        return riders;
    }

    @Override
    public IRayTrace rayTraceBlock(final double distance, final boolean stopOnLiquid, final boolean ignoreBlockWithoutBoundingBox) {
        final Vector3d vec3d = this.entity.getEyePosition(1.0f);
        final Vector3d vec3d2 = this.entity.getViewVector(1.0f);
        final Vector3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        final RayTraceResult result = this.entity.level.clip(new RayTraceContext(vec3d, vec3d3, RayTraceContext.BlockMode.OUTLINE, stopOnLiquid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, (Entity)this.entity));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return null;
        }
        final BlockRayTraceResult br = (BlockRayTraceResult)result;
        return new RayTraceWrapper(NpcAPI.Instance().getIBlock(this.entity.level, br.getBlockPos()), br.getDirection().get3DDataValue());
    }

    @Override
    public IEntity[] rayTraceEntities(final double distance, final boolean stopOnLiquid, final boolean ignoreBlockWithoutBoundingBox) {
        final Vector3d vec3d = this.entity.getEyePosition(1.0f);
        final Vector3d vec3d2 = this.entity.getViewVector(1.0f);
        Vector3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        final RayTraceResult result = this.entity.level.clip(new RayTraceContext(vec3d, vec3d3, RayTraceContext.BlockMode.COLLIDER, stopOnLiquid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this.entity));
        if (result.getType() != RayTraceResult.Type.MISS) {
            vec3d3 = result.getLocation();
        }
        return this.findEntityOnPath(distance, vec3d, vec3d3);
    }

    private IEntity[] findEntityOnPath(final double distance, final Vector3d vec3d, final Vector3d vec3d1) {
        final List<Entity> list = this.entity.level.getEntities(this.entity, this.entity.getBoundingBox().inflate(distance));
        final List<IEntity> result = new ArrayList<>();
        for (final Entity entity1 : list) {
            if (entity1 != this.entity) {
                final AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
                final Optional<Vector3d> optional = axisalignedbb.clip(vec3d, vec3d1);
                if (!optional.isPresent()) {
                    continue;
                }
                result.add(NpcAPI.Instance().getIEntity(entity1));
            }
        }
        result.sort((o1, o2) -> {
            double d1 = this.entity.distanceToSqr(o1.getMCEntity());
            double d2 = this.entity.distanceToSqr(o2.getMCEntity());
            if (d1 == d2) {
                return 0;
            }
            else {
                return (d1 > d2) ? 1 : -1;
            }
        });
        return result.toArray(new IEntity[result.size()]);
    }

    @Override
    public IEntity[] getAllRiders() {
        final List<Entity> list = new ArrayList<>(this.entity.getIndirectPassengers());
        final IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            riders[i] = NpcAPI.Instance().getIEntity(list.get(i));
        }
        return riders;
    }

    @Override
    public void addRider(final IEntity entity) {
        if (entity != null) {
            entity.getMCEntity().startRiding(this.entity, true);
        }
    }

    @Override
    public void clearRiders() {
        this.entity.ejectPassengers();
    }

    @Override
    public IEntity getMount() {
        return NpcAPI.Instance().getIEntity(this.entity.getVehicle());
    }

    @Override
    public void setMount(final IEntity entity) {
        if (entity == null) {
            this.entity.stopRiding();
        }
        else {
            this.entity.startRiding(entity.getMCEntity(), true);
        }
    }

    @Override
    public void setRotation(final float rotation) {
        this.entity.yRot = rotation;
    }

    @Override
    public float getRotation() {
        return this.entity.yRot;
    }

    @Override
    public void setPitch(final float rotation) {
        this.entity.xRot = rotation;
    }

    @Override
    public float getPitch() {
        return this.entity.xRot;
    }

    @Override
    public void knockback(final int power, final float direction) {
        final float v = direction * 3.1415927f / 180.0f;
        this.entity.push((double)(-MathHelper.sin(v) * power), 0.1 + power * 0.04f, (double)(MathHelper.cos(v) * power));
        this.entity.setDeltaMovement(this.entity.getDeltaMovement().multiply(0.6, 1.0, 0.6));
        this.entity.hurtMarked = true;
    }

    @Override
    public boolean isSneaking() {
        return this.entity.isCrouching();
    }

    @Override
    public boolean isSprinting() {
        return this.entity.isSprinting();
    }

    @Override
    public T getMCEntity() {
        return this.entity;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public boolean typeOf(final int type) {
        return type == this.getType();
    }

    @Override
    public String getUUID() {
        return this.entity.getUUID().toString();
    }

    @Override
    public String generateNewUUID() {
        final UUID id = UUID.randomUUID();
        this.entity.setUUID(id);
        return id.toString();
    }

    @Override
    public INbt getNbt() {
        return NpcAPI.Instance().getINbt(this.entity.getPersistentData());
    }

    @Override
    public void storeAsClone(final int tab, final String name) {
        final CompoundNBT compound = new CompoundNBT();
        if (!this.entity.saveAsPassenger(compound)) {
            throw new CustomNPCsException("Cannot store dead entities");
        }
        ServerCloneController.Instance.addClone(compound, name, tab);
    }

    @Override
    public INbt getEntityNbt() {
        final CompoundNBT compound = new CompoundNBT();
        this.entity.saveWithoutId(compound);
        ResourceLocation resourcelocation = EntityType.getKey(this.entity.getType());
        if (this.getType() == 1) {
            resourcelocation = new ResourceLocation("player");
        }
        compound.putString("id", resourcelocation.toString());
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public void setEntityNbt(final INbt nbt) {
        this.entity.load(nbt.getMCNBT());
    }

    @Override
    public void playAnimation(final int type) {
        this.levelWrapper.getMCWorld().getChunkSource().broadcastAndSend(this.entity, new SAnimateHandPacket(this.entity, type));
    }

    @Override
    public float getHeight() {
        return this.entity.getBbHeight();
    }

    @Override
    public float getEyeHeight() {
        return this.entity.getEyeHeight();
    }

    @Override
    public float getWidth() {
        return this.entity.getBbWidth();
    }

    @Override
    public IPos getPos() {
        return new BlockPosWrapper(this.entity.blockPosition());
    }

    @Override
    public void setPos(final IPos pos) {
        this.entity.setPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
    }

    @Override
    public String[] getTags() {
        return this.entity.getTags().toArray(new String[0]);
    }

    @Override
    public void addTag(final String tag) {
        this.entity.addTag(tag);
    }

    @Override
    public boolean hasTag(final String tag) {
        return this.entity.getTags().contains(tag);
    }

    @Override
    public void removeTag(final String tag) {
        this.entity.removeTag(tag);
    }

    @Override
    public double getMotionX() {
        return this.entity.getDeltaMovement().x;
    }

    @Override
    public double getMotionY() {
        return this.entity.getDeltaMovement().y;
    }

    @Override
    public double getMotionZ() {
        return this.entity.getDeltaMovement().z;
    }

    @Override
    public void setMotionX(final double motion) {
        final Vector3d mo = this.entity.getDeltaMovement();
        if (mo.x == motion) {
            return;
        }
        this.entity.setDeltaMovement(motion, mo.y, mo.z);
        this.entity.hurtMarked = true;
    }

    @Override
    public void setMotionY(final double motion) {
        final Vector3d mo = this.entity.getDeltaMovement();
        if (mo.y == motion) {
            return;
        }
        this.entity.setDeltaMovement(mo.x, motion, mo.z);
        this.entity.hurtMarked = true;
    }

    @Override
    public void setMotionZ(final double motion) {
        final Vector3d mo = this.entity.getDeltaMovement();
        if (mo.z == motion) {
            return;
        }
        this.entity.setDeltaMovement(mo.x, mo.y, motion);
        this.entity.hurtMarked = true;
    }

    public void damage(float damage, IEntity source) {
        if(source.getType() == 1)
            entity.hurt(new EntityDamageSource("player",source.getMCEntity()),damage);
        else
            entity.hurt(new EntityDamageSource(source.getTypeName(),source.getMCEntity()),damage);
    }
}
