package noppes.npcs.entity.data;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.api.wrapper.BlockPosWrapper;

import java.util.ArrayList;
import java.util.List;

import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.roles.JobFarmer;

public class DataAI implements INPCAi {
    private EntityNPCInterface npc;
    public int onAttack;
    public int doorInteract;
    public int findShelter;
    public boolean canSwim;
    public boolean reactsToFire;
    public boolean avoidsWater;
    public boolean avoidsSun;
    public boolean returnToStart;
    public boolean directLOS;
    public boolean canLeap;
    public boolean canSprint;
    public boolean stopAndInteract;
    public boolean attackInvisible;
    public int movementType;
    public int animationType;
    private int standingType;
    private int movingType;
    public boolean npcInteracting;
    public int orientation;
    public float bodyOffsetX;
    public float bodyOffsetY;
    public float bodyOffsetZ;
    public int walkingRange;
    private int moveSpeed;
    private List<int[]> movingPath;
    private BlockPos startPos;
    public int movingPos;
    public int movingPattern;
    public boolean movingPause;

    public boolean textoCentrado;

    public boolean mountControl;

    public boolean lookAtTarget;

    public DataAI(final EntityNPCInterface npc) {
        this.onAttack = 0;
        this.doorInteract = 2;
        this.findShelter = 2;
        this.canSwim = true;
        this.reactsToFire = false;
        this.avoidsWater = false;
        this.avoidsSun = false;
        this.returnToStart = true;
        this.directLOS = true;
        this.canLeap = false;
        this.canSprint = false;
        this.stopAndInteract = true;
        this.attackInvisible = false;
        this.movementType = 0;
        this.animationType = 0;
        this.standingType = 0;
        this.movingType = 0;
        this.npcInteracting = true;
        this.orientation = 0;
        this.bodyOffsetX = 5.0f;
        this.bodyOffsetY = 5.0f;
        this.bodyOffsetZ = 5.0f;
        this.walkingRange = 10;
        this.moveSpeed = 5;
        this.movingPath = new ArrayList<>();
        this.startPos = BlockPos.ZERO;
        this.movingPos = 0;
        this.movingPattern = 0;
        this.movingPause = true;
        this.mountControl = false;
        this.textoCentrado = false;
        this.lookAtTarget = false;
        this.npc = npc;
    }

    public void readToNBT(final CompoundNBT compound) {
        this.canSwim = compound.getBoolean("CanSwim");
        this.reactsToFire = compound.getBoolean("ReactsToFire");
        this.setAvoidsWater(compound.getBoolean("AvoidsWater"));
        this.avoidsSun = compound.getBoolean("AvoidsSun");
        this.returnToStart = compound.getBoolean("ReturnToStart");
        this.onAttack = compound.getInt("OnAttack");
        this.doorInteract = compound.getInt("DoorInteract");
        this.findShelter = compound.getInt("FindShelter");
        this.directLOS = compound.getBoolean("DirectLOS");
        this.canLeap = compound.getBoolean("CanLeap");
        this.canSprint = compound.getBoolean("CanSprint");
        this.movingPause = compound.getBoolean("MovingPause");
        this.npcInteracting = compound.getBoolean("npcInteracting");
        this.stopAndInteract = compound.getBoolean("stopAndInteract");
        this.movementType = compound.getInt("MovementType");
        this.animationType = compound.getInt("MoveState");
        this.standingType = compound.getInt("StandingState");
        this.movingType = compound.getInt("MovingState");
        this.orientation = compound.getInt("Orientation");
        this.bodyOffsetY = compound.getFloat("PositionOffsetY");
        this.bodyOffsetZ = compound.getFloat("PositionOffsetZ");
        this.bodyOffsetX = compound.getFloat("PositionOffsetX");
        this.walkingRange = compound.getInt("WalkingRange");
        this.setWalkingSpeed(compound.getInt("MoveSpeed"));
        this.setMovingPath(NBTTags.getIntegerArraySet(compound.getList("MovingPathNew", 10)));
        this.movingPos = compound.getInt("MovingPos");
        this.movingPattern = compound.getInt("MovingPatern");
        this.attackInvisible = compound.getBoolean("AttackInvisible");
        if (compound.contains("StartPosNew")) {
            final int[] startPos = compound.getIntArray("StartPosNew");
            this.setStartPos(new BlockPos(startPos[0], startPos[1], startPos[2]));
        }
        this.mountControl = compound.getBoolean("MountControl");
        this.textoCentrado = compound.getBoolean("TextoCentrado");
        this.lookAtTarget = compound.getBoolean("LookAtTarget");
    }

    public CompoundNBT save(final CompoundNBT compound) {
        compound.putBoolean("CanSwim", this.canSwim);
        compound.putBoolean("ReactsToFire", this.reactsToFire);
        compound.putBoolean("AvoidsWater", this.avoidsWater);
        compound.putBoolean("AvoidsSun", this.avoidsSun);
        compound.putBoolean("ReturnToStart", this.returnToStart);
        compound.putInt("OnAttack", this.onAttack);
        compound.putInt("DoorInteract", this.doorInteract);
        compound.putInt("FindShelter", this.findShelter);
        compound.putBoolean("DirectLOS", this.directLOS);
        compound.putBoolean("CanLeap", this.canLeap);
        compound.putBoolean("CanSprint", this.canSprint);
        compound.putBoolean("MovingPause", this.movingPause);
        compound.putBoolean("npcInteracting", this.npcInteracting);
        compound.putBoolean("stopAndInteract", this.stopAndInteract);
        compound.putInt("MoveState", this.animationType);
        compound.putInt("StandingState", this.standingType);
        compound.putInt("MovingState", this.movingType);
        compound.putInt("MovementType", this.movementType);
        compound.putInt("Orientation", this.orientation);
        compound.putFloat("PositionOffsetX", this.bodyOffsetX);
        compound.putFloat("PositionOffsetY", this.bodyOffsetY);
        compound.putFloat("PositionOffsetZ", this.bodyOffsetZ);
        compound.putInt("WalkingRange", this.walkingRange);
        compound.putInt("MoveSpeed", this.moveSpeed);
        compound.put("MovingPathNew", (INBT) NBTTags.nbtIntegerArraySet(this.movingPath));
        compound.putInt("MovingPos", this.movingPos);
        compound.putInt("MovingPatern", this.movingPattern);
        this.setAvoidsWater(this.avoidsWater);
        compound.putIntArray("StartPosNew", this.getStartArray());
        compound.putBoolean("AttackInvisible", this.attackInvisible);
        compound.putBoolean("MountControl", this.mountControl);
        compound.putBoolean("TextoCentrado", this.textoCentrado);
        compound.putBoolean("LookAtTarget", this.lookAtTarget);
        return compound;
    }

    public List<int[]> getMovingPath() {
        if (this.movingPath.isEmpty() && this.startPos != null) {
            this.movingPath.add(this.getStartArray());
        }
        return this.movingPath;
    }

    public void setMovingPath(final List<int[]> list) {
        this.movingPath = list;
        if (!this.movingPath.isEmpty()) {
            final int[] startPos = this.movingPath.get(0);
            this.setStartPos(new BlockPos(startPos[0], startPos[1], startPos[2]));
        }
    }

    public BlockPos startPos() {
        if (this.startPos == null || this.startPos == BlockPos.ZERO) {
            this.setStartPos(this.npc.blockPosition());
        }
        return this.startPos;
    }

    public int[] getStartArray() {
        final BlockPos pos = this.startPos();
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public int[] getCurrentMovingPath() {
        final List<int[]> list = this.getMovingPath();
        final int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        int pos = this.movingPos;
        if (this.movingPattern == 0 && pos >= size) {
            final boolean movingPos = false;
            this.movingPos = (movingPos ? 1 : 0);
            pos = (movingPos ? 1 : 0);
        }
        if (this.movingPattern == 1) {
            final int size2 = size * 2 - 1;
            if (pos >= size2) {
                final boolean movingPos2 = false;
                this.movingPos = (movingPos2 ? 1 : 0);
                pos = (movingPos2 ? 1 : 0);
            } else if (pos >= size) {
                pos = size2 - pos;
            }
        }
        return list.get(pos);
    }

    public void clearMovingPath() {
        this.movingPath.clear();
        this.movingPos = 0;
    }

    public void setMovingPathPos(int m_pos, final int[] pos) {
        if (m_pos < 0) {
            m_pos = 0;
        }
        this.movingPath.set(m_pos, pos);
    }

    public int[] getMovingPathPos(final int m_pos) {
        return this.movingPath.get(m_pos);
    }

    public void appendMovingPath(final int[] pos) {
        this.movingPath.add(pos);
    }

    public int getMovingPos() {
        return this.movingPos;
    }

    public void setMovingPos(final int pos) {
        this.movingPos = pos;
    }

    public int getMovingPathSize() {
        return this.movingPath.size();
    }

    public void incrementMovingPath() {
        final List<int[]> list = this.getMovingPath();
        if (list.size() == 1) {
            this.movingPos = 0;
            return;
        }
        ++this.movingPos;
        if (this.movingPattern == 0) {
            this.movingPos %= list.size();
        } else if (this.movingPattern == 1) {
            final int size = list.size() * 2 - 1;
            this.movingPos %= size;
        }
    }

    public void decreaseMovingPath() {
        final List<int[]> list = this.getMovingPath();
        if (list.size() == 1) {
            this.movingPos = 0;
            return;
        }
        --this.movingPos;
        if (this.movingPos < 0) {
            if (this.movingPattern == 0) {
                this.movingPos = list.size() - 1;
            } else if (this.movingPattern == 1) {
                this.movingPos = list.size() * 2 - 2;
            }
        }
    }

    public double distanceToSqrToPathPoint() {
        final int[] pos = this.getCurrentMovingPath();
        return this.npc.distanceToSqr(pos[0] + 0.5, (double) pos[1], pos[2] + 0.5);
    }

    public IPos getStartPos() {
        return new BlockPosWrapper(this.startPos());
    }

    public void setStartPos(final BlockPos pos) {
        this.startPos = pos;
        this.npc.restrictTo(this.startPos, this.npc.stats.aggroRange * 2);
    }

    public void setStartPos(final IPos pos) {
        this.setStartPos(pos.getMCBlockPos());
    }

    public void setStartPos(final double x, final double y, final double z) {
        this.setStartPos(new BlockPos(x, y, z));
    }

    @Override
    public void setReturnsHome(final boolean bo) {
        this.returnToStart = bo;
    }

    @Override
    public boolean getReturnsHome() {
        return this.returnToStart;
    }

    public boolean shouldReturnHome() {
        return (this.npc.job.getType() != 10 || !((JobBuilder) this.npc.job).isBuilding()) && (this.npc.job.getType() != 11 || !((JobFarmer) this.npc.job).isPlucking()) && this.returnToStart;
    }

    @Override
    public int getAnimation() {
        return this.animationType;
    }

    @Override
    public int getCurrentAnimation() {
        return this.npc.currentAnimation;
    }

    @Override
    public void setAnimation(final int type) {
        this.animationType = type;
    }

    @Override
    public int getRetaliateType() {
        return this.onAttack;
    }

    @Override
    public void setRetaliateType(final int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Unknown retaliation type: " + type, new Object[0]);
        }
        this.onAttack = type;
        this.npc.updateAI = true;
    }

    @Override
    public int getMovingType() {
        return this.movingType;
    }

    @Override
    public void setMovingType(final int type) {
        if (type < 0 || type > 2) {
            throw new CustomNPCsException("Unknown moving type: " + type, new Object[0]);
        }
        this.movingType = type;
        this.npc.updateAI = true;
    }

    @Override
    public int getStandingType() {
        return this.standingType;
    }

    @Override
    public void setStandingType(final int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Unknown standing type: " + type);
        }
        this.standingType = type;
        this.npc.updateAI = true;
    }

    @Override
    public boolean getAttackInvisible() {
        return this.attackInvisible;
    }

    @Override
    public void setAttackInvisible(final boolean attack) {
        this.attackInvisible = attack;
    }

    @Override
    public int getWanderingRange() {
        return this.walkingRange;
    }

    @Override
    public void setWanderingRange(final int range) {
        if (range < 1 || range > 50) {
            throw new CustomNPCsException("Bad wandering range: " + range);
        }
        this.walkingRange = range;
    }

    @Override
    public boolean getInteractWithNPCs() {
        return this.npcInteracting;
    }

    @Override
    public void setInteractWithNPCs(final boolean interact) {
        this.npcInteracting = interact;
    }

    @Override
    public boolean getStopOnInteract() {
        return this.stopAndInteract;
    }

    @Override
    public void setStopOnInteract(final boolean stopOnInteract) {
        this.stopAndInteract = stopOnInteract;
    }

    @Override
    public int getWalkingSpeed() {
        return this.moveSpeed;
    }

    @Override
    public void setWalkingSpeed(final int speed) {
        if (speed < 0 || speed > 10) {
            throw new CustomNPCsException("Wrong speed: " + speed);
        }
        this.moveSpeed = speed;
        this.npc.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.npc.getSpeed());
        this.npc.getAttribute(Attributes.FLYING_SPEED).setBaseValue(this.npc.getSpeed() * 2.0f);
    }

    @Override
    public int getMovingPathType() {
        return this.movingPattern;
    }

    @Override
    public boolean getMovingPathPauses() {
        return this.movingPause;
    }

    @Override
    public void setMovingPathType(final int type, final boolean pauses) {
        if (type < 0 && type > 1) {
            throw new CustomNPCsException("Moving path type: " + type);
        }
        this.movingPattern = type;
        this.movingPause = pauses;
    }

    @Override
    public int getDoorInteract() {
        return this.doorInteract;
    }

    @Override
    public void setDoorInteract(final int type) {
        this.doorInteract = type;
        this.npc.updateAI = true;
    }

    @Override
    public boolean getCanSwim() {
        return this.canSwim;
    }

    @Override
    public void setCanSwim(final boolean canSwim) {
        this.canSwim = canSwim;
    }

    @Override
    public int getSheltersFrom() {
        return this.findShelter;
    }

    @Override
    public void setSheltersFrom(final int type) {
        this.findShelter = type;
        this.npc.updateAI = true;
    }

    @Override
    public boolean getAttackLOS() {
        return this.directLOS;
    }

    @Override
    public void setAttackLOS(final boolean enabled) {
        this.directLOS = enabled;
        this.npc.updateAI = true;
    }

    @Override
    public boolean getAvoidsWater() {
        return this.avoidsWater;
    }

    @Override
    public void setAvoidsWater(final boolean enabled) {
        this.npc.setPathfindingMalus(PathNodeType.WATER, (this.movementType != 2 && enabled) ? -1.0f : 0.0f);
        this.avoidsWater = enabled;
    }

    @Override
    public boolean getLeapAtTarget() {
        return this.canLeap;
    }

    @Override
    public void setLeapAtTarget(final boolean leap) {
        this.canLeap = leap;
        this.npc.updateAI = true;
    }

    @Override
    public int getNavigationType() {
        return this.movementType;
    }

    @Override
    public void setNavigationType(final int type) {
        this.movementType = type;
    }
}
