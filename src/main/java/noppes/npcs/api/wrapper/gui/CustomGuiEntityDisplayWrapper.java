package noppes.npcs.api.wrapper.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.gui.IEntityDisplay;
import noppes.npcs.api.wrapper.NBTWrapper;

public class CustomGuiEntityDisplayWrapper extends CustomGuiComponentWrapper implements IEntityDisplay {
    public int rotation;
    public boolean isFollowingCursor;
    public float scale;
    public IEntity entity;
    public INbt entityData = new NBTWrapper(new CompoundNBT());
    public int width, height;
    public int entityId;

    public CustomGuiEntityDisplayWrapper(){}

    public CustomGuiEntityDisplayWrapper(final int id, final int x, final int y, final int width, final int height, IEntity entity) {
        this.entityId = -1;
        this.rotation = 0;
        this.scale = 1.0f;
        this.isFollowingCursor = false;
        this.setID(id);
        this.setEntity(entity);
        this.setPos(x, y);
        this.setHoverBox(width, height);
    }

    @Override
    public IEntity getEntity() {
        return entity;
    }

    @Override
    public IEntityDisplay setEntity(IEntity entity) {
        this.entity = entity;
        if (entity == null) {
            this.entityData = new NBTWrapper(new CompoundNBT());
        } else {
            this.entityData = entity.getEntityNbt();
        }
        if(entity.getMCEntity() instanceof PlayerEntity){
            entityId = entity.getMCEntity().getId();
        }

        return this;
    }

    @Override
    public int getRotation() {
        return rotation;
    }

    @Override
    public IEntityDisplay setRotation(int deg) {
        this.rotation = deg;
        return this;
    }

    @Override
    public boolean isFollowingCursor() {
        return isFollowingCursor;
    }

    @Override
    public IEntityDisplay setFollowingCursor(boolean state) {
        this.isFollowingCursor = state;
        return this;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public IEntityDisplay setScale(float scaleFactor) {
        this.scale = scaleFactor;
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public IEntityDisplay setHoverBox(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public int getType() {
        return 7;
    }

    public CompoundNBT toNBT(CompoundNBT compound) {
        super.toNBT(compound);
        compound.put("entity", this.entityData.getMCNBT());
        compound.putInt("rotation", this.rotation);
        compound.putFloat("scale", this.scale);
        compound.putBoolean("followCursor", isFollowingCursor);
        compound.putInt("entityId", entityId);
        return compound;
    }

    public CustomGuiComponentWrapper fromNBT(CompoundNBT compound) {
        super.fromNBT(compound);
        this.entityData = NpcAPI.Instance().getINbt(compound.getCompound("entity"));
        this.setRotation(compound.getInt("rotation"));
        this.setScale(compound.getFloat("scale"));
        this.isFollowingCursor = compound.getBoolean("followCursor");
        this.entityId = compound.getInt("entityId");
        return this;
    }
}
