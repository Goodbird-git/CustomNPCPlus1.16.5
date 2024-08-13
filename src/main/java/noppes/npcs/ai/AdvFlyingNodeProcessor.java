package noppes.npcs.ai;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.Set;

public class AdvFlyingNodeProcessor extends FlyingNodeProcessor {
    private final Long2ObjectMap<PathNodeType> pathTypesByPosCache = new Long2ObjectOpenHashMap<>();

    @Override
    public void done() {
        super.done();
        this.pathTypesByPosCache.clear();
    }

    public PathPoint getStart() {
        int i;
        if (this.canFloat() && this.mob.isInWater()) {
            i = MathHelper.floor(this.mob.getY());
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.mob.getX(), (double)i, this.mob.getZ());

            for(Block block = this.level.getBlockState(blockpos$mutable).getBlock(); block == Blocks.WATER; block = this.level.getBlockState(blockpos$mutable).getBlock()) {
                ++i;
                blockpos$mutable.set(this.mob.getX(), (double)i, this.mob.getZ());
            }
        } else {
            i = MathHelper.floor(this.mob.getY() + 0.5D);
        }

        BlockPos blockpos1 = this.mob.blockPosition();
        PathNodeType pathnodetype1 = this.getBlockPathType(this.mob, blockpos1.getX(), i, blockpos1.getZ());
        if (this.mob.getPathfindingMalus(pathnodetype1) < 0.0F) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.mob.getBoundingBox().minX, (double)i, this.mob.getBoundingBox().minZ));
            set.add(new BlockPos(this.mob.getBoundingBox().minX, (double)i, this.mob.getBoundingBox().maxZ));
            set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)i, this.mob.getBoundingBox().minZ));
            set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)i, this.mob.getBoundingBox().maxZ));

            for(BlockPos blockpos : set) {
                PathNodeType pathnodetype = this.getBlockPathType(this.mob, blockpos);
                if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F) {
                    return super.getNode(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }
            }
        }

        return super.getNode(blockpos1.getX(), i, blockpos1.getZ());
    }

    @Nullable
    protected PathPoint getNode(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
        PathPoint pathpoint = null;
        PathNodeType pathnodetype = this.getBlockPathType(this.mob, p_176159_1_, p_176159_2_, p_176159_3_);
        float f = this.mob.getPathfindingMalus(pathnodetype);
        if (f >= 0.0F) {
            pathpoint = super.getNode(p_176159_1_, p_176159_2_, p_176159_3_);
            pathpoint.type = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            if (pathnodetype == PathNodeType.WALKABLE) {
                ++pathpoint.costMalus;
            }
        }

        return pathnodetype != PathNodeType.OPEN && pathnodetype != PathNodeType.WALKABLE ? pathpoint : pathpoint;
    }


    private PathNodeType getBlockPathType(MobEntity p_192559_1_, BlockPos p_192559_2_) {
        return this.getBlockPathType(p_192559_1_, p_192559_2_.getX(), p_192559_2_.getY(), p_192559_2_.getZ());
    }

    private PathNodeType getBlockPathType(MobEntity p_192558_1_, int p_192558_2_, int p_192558_3_, int p_192558_4_) {
        return this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(p_192558_2_, p_192558_3_, p_192558_4_), (p_237229_5_) -> {
            return this.getBlockPathType(this.level, p_192558_2_, p_192558_3_, p_192558_4_, p_192558_1_, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
        });
    }
}
