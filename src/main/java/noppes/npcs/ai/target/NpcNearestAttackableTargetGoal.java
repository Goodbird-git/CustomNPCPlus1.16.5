package noppes.npcs.ai.target;

import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import noppes.npcs.entity.*;
import java.util.function.*;
import javax.annotation.*;
import net.minecraft.entity.*;

public class NpcNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
    private int unseenTicks1;

    public NpcNearestAttackableTargetGoal(final EntityNPCInterface npc, final Class<T> c, final int range, final boolean b, final boolean b2, @Nullable final Predicate<LivingEntity> selector) {
        super(npc, c, range, b, b2, selector);
        if (npc.ais.attackInvisible) {
            this.targetConditions.ignoreInvisibilityTesting();
        }
    }

    public void start() {
        unseenTicks1 = 0;
        this.mob.setTarget(this.target);
        super.start();
    }

    public void stop() {
        this.mob.setTarget(null);
        this.targetMob = null;
    }

    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            livingentity = this.targetMob;
        }

        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            Team team = this.mob.getTeam();
            Team team1 = livingentity.getTeam();
            if (team != null && team1 == team) {
                return false;
            } else {
                double d0 = this.getFollowDistance();
                if (this.mob.distanceToSqr(livingentity) > d0 * d0) {
                    return false;
                } else {
                    if (this.mustSee) {
                        if (this.mob.getSensing().canSee(livingentity)) {
                            this.unseenTicks1 = 0;
                        } else if (++this.unseenTicks1 > this.unseenMemoryTicks) {
                            return false;
                        }
                    }

                    if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.invulnerable) {
                        return false;
                    } else {
                        this.mob.setTarget(livingentity);
                        return true;
                    }
                }
            }
        }
    }
}
