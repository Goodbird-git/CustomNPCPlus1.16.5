package noppes.npcs.ai.target;

import net.minecraft.entity.ai.goal.*;
import noppes.npcs.entity.*;
import java.util.function.*;
import javax.annotation.*;
import net.minecraft.entity.*;

public class NpcNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
    public NpcNearestAttackableTargetGoal(final EntityNPCInterface npc, final Class<T> c, final int range, final boolean b, final boolean b2, @Nullable final Predicate<LivingEntity> selector) {
        super(npc, c, range, b, b2, selector);
        if (npc.ais.attackInvisible) {
            this.targetConditions.ignoreInvisibilityTesting();
        }
    }
}
