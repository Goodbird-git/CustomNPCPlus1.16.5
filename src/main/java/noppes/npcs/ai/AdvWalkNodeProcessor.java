package noppes.npcs.ai;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Region;

public class AdvWalkNodeProcessor extends WalkNodeProcessor {
    private final Long2ObjectMap<PathNodeType> pathTypesByPosCache = new Long2ObjectOpenHashMap<>();

    public PathNodeType getCachedBlockType(MobEntity p_237230_1_, int p_237230_2_, int p_237230_3_, int p_237230_4_) {
        return this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(p_237230_2_, p_237230_3_, p_237230_4_), (p_237229_5_) -> {
            return this.getBlockPathType(this.level, p_237230_2_, p_237230_3_, p_237230_4_, p_237230_1_, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
        });
    }

    public void done() {
        Region level = this.level;
        MobEntity mob = this.mob;
        this.pathTypesByPosCache.clear();
        super.done();
        this.level = level;
        this.mob = mob;
    }
}
