package noppes.npcs.entity;

import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;

public abstract class EntityNPCFlying extends EntityNPCInterface
{
    public EntityNPCFlying(final EntityType<? extends CreatureEntity> type, final World world) {
        super(type, world);
    }

    @Override
    public boolean canFly() {
        return this.ais.movementType > 0;
    }

    @Override
    public boolean causeFallDamage(final float distance, final float damageMultiplier) {
        return !this.canFly() && super.causeFallDamage(distance, damageMultiplier);
    }

    protected void checkFallDamage(final double y, final boolean onGroundIn, final BlockState state, final BlockPos pos) {
        if (!this.canFly()) {
            super.checkFallDamage(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void travel(final Vector3d v) {
        if (!this.canFly()) {
            super.travel(v);
            return;
        }
        Vector3d m = this.getDeltaMovement();
        if (!this.isInWater() && this.ais.movementType == 2) {
            m = m.subtract(0.0, 0.15, 0.0);
        }
        if (this.isInWater() && this.ais.movementType == 1) {
            this.moveRelative(0.02f, v);
            this.move(MoverType.SELF, m);
            m = this.getDeltaMovement().scale(0.8);
        }
        else if (this.isInLava()) {
            this.moveRelative(0.02f, v);
            this.move(MoverType.SELF, m);
            m = this.getDeltaMovement().scale(0.5);
        }
        else {
            final BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0, this.getZ());
            float f = 0.91f;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getSlipperiness((IWorldReader)this.level, ground, (Entity)this) * 0.91f;
            }
            final float f2 = 0.16277137f / (f * f * f);
            f = 0.91f;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getSlipperiness((IWorldReader)this.level, ground, (Entity)this) * 0.91f;
            }
            this.moveRelative(this.onGround ? (0.1f * f2) : 0.02f, v);
            this.move(MoverType.SELF, this.getDeltaMovement());
            m = this.getDeltaMovement().scale((double)f);
        }
        this.setDeltaMovement(m);
        this.calculateEntityAnimation((LivingEntity)this, false);
    }

    public boolean onClimbable() {
        return false;
    }
}
