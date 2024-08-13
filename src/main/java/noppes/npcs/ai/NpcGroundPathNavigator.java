package noppes.npcs.ai;


import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class NpcGroundPathNavigator extends GroundPathNavigator {

    public NpcGroundPathNavigator(MobEntity p_i45875_1_, World p_i45875_2_) {
        super(p_i45875_1_, p_i45875_2_);
    }

    protected PathFinder createPathFinder(int p_179679_1_) {
        this.nodeEvaluator = new AdvWalkNodeProcessor();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, p_179679_1_);
    }

    protected void followThePath()
    {
        Vector3d vec3d = this.getTempMobPos();
        int i = this.path.getNodeCount();

        for (int j = this.path.getNextNodeIndex(); j < this.path.getNodeCount(); ++j)
        {
            if ((double)this.path.getNode(j).y != Math.floor(vec3d.y))
            {
                i = j;
                break;
            }
        }

        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
        Vector3i vec3i = this.path.getNextNodePos();

        if (MathHelper.abs((float)(this.mob.getX() - (vec3i.getX() + 0.5D))) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.mob.getZ() - (vec3i.getZ() + 0.5D))) < this.maxDistanceToWaypoint && Math.abs(this.mob.getY() - vec3i.getY()) < 1.0D)
        {
            this.path.advance();
        }

        int k = MathHelper.ceil(this.mob.getBbWidth());
        int l = MathHelper.ceil(this.mob.getBbHeight());
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.path.getNextNodeIndex(); --j1)
        {
            if (isDirectPathBetweenPoints(vec3d, this.path.getEntityPosAtNode(this.mob, j1), k, l, i1))
            {
                this.path.setNextNodeIndex(j1);
                break;
            }
        }

        this.doStuckDetection(vec3d);
    }

    protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ)
    {
        int i = MathHelper.floor(posVec31.x);
        int j = MathHelper.floor(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D)
        {
            return false;
        }
        else
        {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1))
            {
                return false;
            }
            else
            {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)i - posVec31.x;
                double d7 = (double)j - posVec31.z;

                if (d0 >= 0.0D)
                {
                    ++d6;
                }

                if (d1 >= 0.0D)
                {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor(posVec32.x);
                int j1 = MathHelper.floor(posVec32.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0)
                {
                    if (d6 < d7)
                    {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    }
                    else
                    {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }


    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d p_179692_7_, double p_179692_8_, double p_179692_10_)
    {
        for (BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1)))
        {
            double d0 = (double)blockpos.getX() + 0.5D - p_179692_7_.x;
            double d1 = (double)blockpos.getZ() + 0.5D - p_179692_7_.z;

            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D)
            {
                if (!this.level.getBlockState(blockpos).isPathfindable(this.level, blockpos, PathType.LAND))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d vec31, double p_179683_8_, double p_179683_10_)
    {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;

        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_))
        {
            return false;
        }
        else
        {
            for (int k = i; k < i + sizeX; ++k)
            {
                for (int l = j; l < j + sizeZ; ++l)
                {
                    double d0 = (double)k + 0.5D - vec31.x;
                    double d1 = (double)l + 0.5D - vec31.z;

                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D)
                    {
                        PathNodeType pathnodetype = ((AdvWalkNodeProcessor)nodeEvaluator).getCachedBlockType(this.mob, k, y - 1, l);

                        if (pathnodetype == PathNodeType.WATER)
                        {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.LAVA)
                        {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.OPEN)
                        {
                            return false;
                        }

                        pathnodetype = ((AdvWalkNodeProcessor)nodeEvaluator).getCachedBlockType(this.mob, k, y, l);
                        float f = this.mob.getPathfindingMalus(pathnodetype);

                        if (f < 0.0F || f >= 8.0F)
                        {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER)
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }
}
