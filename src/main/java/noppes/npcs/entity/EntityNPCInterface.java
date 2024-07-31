//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package noppes.npcs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.IChatMessages;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.ai.CombatHandler;
import noppes.npcs.ai.EntityAIAnimation;
import noppes.npcs.ai.EntityAIAttackTarget;
import noppes.npcs.ai.EntityAIAvoidTarget;
import noppes.npcs.ai.EntityAIBustDoor;
import noppes.npcs.ai.EntityAIFindShade;
import noppes.npcs.ai.EntityAIFollow;
import noppes.npcs.ai.EntityAIJob;
import noppes.npcs.ai.EntityAILook;
import noppes.npcs.ai.EntityAIMoveIndoors;
import noppes.npcs.ai.EntityAIMovingPath;
import noppes.npcs.ai.EntityAIPanic;
import noppes.npcs.ai.EntityAIPounceTarget;
import noppes.npcs.ai.EntityAIRangedAttack;
import noppes.npcs.ai.EntityAIReturn;
import noppes.npcs.ai.EntityAIRole;
import noppes.npcs.ai.EntityAISprintToTarget;
import noppes.npcs.ai.EntityAITransform;
import noppes.npcs.ai.EntityAIWander;
import noppes.npcs.ai.EntityAIWatchClosest;
import noppes.npcs.ai.EntityAIWaterNav;
import noppes.npcs.ai.EntityAIWorldLines;
import noppes.npcs.ai.FlyingMoveHelper;
import noppes.npcs.ai.selector.NPCAttackSelector;
import noppes.npcs.ai.target.EntityAIClearTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtByTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtTarget;
import noppes.npcs.ai.target.NpcNearestAttackableTargetGoal;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.DataTransform;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.entity.data.DataAbilities;
import noppes.npcs.entity.data.DataAdvanced;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.entity.data.DataInventory;
import noppes.npcs.entity.data.DataScript;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketChatBubble;
import noppes.npcs.packets.client.PacketNpcUpdate;
import noppes.npcs.packets.client.PacketNpcVisibleFalse;
import noppes.npcs.packets.client.PacketNpcVisibleTrue;
import noppes.npcs.packets.client.PacketPlaySound;
import noppes.npcs.packets.client.PacketQuestCompletion;
import noppes.npcs.roles.JobBard;
import noppes.npcs.roles.JobFollower;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.util.GameProfileAlt;

import javax.annotation.Nullable;

public abstract class EntityNPCInterface extends CreatureEntity implements IEntityAdditionalSpawnData, IRangedAttackMob {
    public static final DataParameter<Boolean> Attacking;
    protected static final DataParameter<Integer> Animation;
    private static final DataParameter<String> RoleData;
    private static final DataParameter<String> JobData;
    private static final DataParameter<Integer> FactionData;
    private static final DataParameter<Boolean> Walking;
    private static final DataParameter<Boolean> Interacting;
    private static final DataParameter<Boolean> IsDead;
    public static final GameProfileAlt CommandProfile;
    public static final GameProfileAlt ChatEventProfile;
    public static final GameProfileAlt GenericProfile;
    public static FakePlayer ChatEventPlayer;
    public static FakePlayer CommandPlayer;
    public static FakePlayer GenericPlayer;
    public ICustomNpc wrappedNPC;
    public final DataAbilities abilities = new DataAbilities(this);
    public DataDisplay display = new DataDisplay(this);
    public DataStats stats = new DataStats(this);
    public DataInventory inventory = new DataInventory(this);
    public final DataAI ais = new DataAI(this);
    public final DataAdvanced advanced = new DataAdvanced(this);
    public final DataScript script = new DataScript(this);
    public final DataTransform transform = new DataTransform(this);
    public final DataTimers timers = new DataTimers(this);
    public CombatHandler combatHandler = new CombatHandler(this);
    public String linkedName = "";
    public long linkedLast = 0L;
    public LinkedNpcController.LinkedData linkedData;
    public EntitySize baseSize = new EntitySize(0.6F, 1.8F, false);
    private static final EntitySize sizeSleep;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    private boolean wasKilled = false;
    public RoleInterface role;
    public JobInterface job;
    public HashMap<Integer, DialogOption> dialogs;
    public boolean hasDied;
    public long killedtime;
    public long totalTicksAlive;
    private int taskCount;
    public int lastInteract;
    public Faction faction;
    private EntityAIRangedAttack aiRange;
    private Goal aiAttackTarget;
    public EntityAILook lookAi;
    public EntityAIAnimation animateAi;
    public List<LivingEntity> interactingEntities;
    public ResourceLocation textureLocation;
    public ResourceLocation textureGlowLocation;
    public ResourceLocation textureCloakLocation;
    public int currentAnimation;
    public int animationStart;
    public int npcVersion;
    public IChatMessages messages;
    public boolean updateClient;
    public boolean updateAI;
    public final ServerBossInfo bossInfo;
    public final HashSet<Integer> tracking;
    public double prevChasingPosX;
    public double prevChasingPosY;
    public double prevChasingPosZ;
    public double chasingPosX;
    public double chasingPosY;
    public double chasingPosZ;
    private double startYPos;

    public EntityNPCInterface(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        this.role = RoleInterface.NONE;
        this.job = JobInterface.NONE;
        this.hasDied = false;
        this.killedtime = 0L;
        this.totalTicksAlive = 0L;
        this.taskCount = 1;
        this.lastInteract = 0;
        this.interactingEntities = new ArrayList();
        this.textureLocation = null;
        this.textureGlowLocation = null;
        this.textureCloakLocation = null;
        this.currentAnimation = 0;
        this.animationStart = 0;
        this.npcVersion = VersionCompatibility.ModRev;
        this.updateClient = false;
        this.updateAI = false;
        this.tracking = new HashSet();
        this.startYPos = -1.0;
        if (!this.isClientSide()) {
            this.wrappedNPC = new NPCWrapper(this);
        }

        this.registerBaseAttributes();
        this.dialogs = new HashMap();
        if (!CustomNpcs.DefaultInteractLine.isEmpty()) {
            this.advanced.interactLines.lines.put(0, new Line(CustomNpcs.DefaultInteractLine));
        }

        this.xpReward = 0;
        this.scaleX = this.scaleY = this.scaleZ = 0.9375F;
        this.faction = this.getFaction();
        this.setFaction(this.faction.id);
        this.updateAI = true;
        this.bossInfo = new ServerBossInfo(this.getDisplayName(), Color.PURPLE, Overlay.PROGRESS);
        this.bossInfo.setVisible(false);
    }

    public boolean canBreatheUnderwater() {
        return this.ais.movementType == 2;
    }

    public boolean isPushedByFluid() {
        return this.ais.movementType != 2;
    }

    @Nullable
    public Entity getControllingPassenger() {
        return (this.getPassengers().isEmpty() || !this.ais.mountControl) ? null : (Entity)this.getPassengers().get(0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.ais.mountControl;
    }

    private void registerBaseAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.stats.maxHealth);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue((double)CustomNpcs.NpcNavRange);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.getSpeed());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)this.stats.melee.getStrength());
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue((double)(this.getSpeed() * 2.0F));
    }

    public static AttributeModifierMap.MutableAttribute createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.FLYING_SPEED).add(Attributes.FOLLOW_RANGE);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RoleData, String.valueOf(""));
        this.entityData.define(JobData, String.valueOf(""));
        this.entityData.define(FactionData, 0);
        this.entityData.define(Animation, 0);
        this.entityData.define(Walking, false);
        this.entityData.define(Interacting, false);
        this.entityData.define(IsDead, false);
        this.entityData.define(Attacking, false);
    }

    public boolean isAlive() {
        return super.isAlive() && !this.isKilled();
    }

    public void tick() {
        super.tick();
        if (this.tickCount % 10 == 0) {
            this.startYPos = this.calculateStartYPos(this.ais.startPos()) + 1.0;
            if (this.startYPos < 0.0 && !this.isClientSide()) {
                this.remove();
            }

            EventHooks.onNPCTick(this);
        }

        this.timers.update();
        if (this.level.isClientSide && this.wasKilled != this.isKilled() && this.wasKilled) {
            this.deathTime = 0;
            this.refreshDimensions();
        }

        this.wasKilled = this.isKilled();
        if (this.currentAnimation == 14) {
            this.deathTime = 19;
        }

    }

    public boolean doHurtTarget(Entity par1Entity) {
        float f = (float)this.stats.melee.getStrength();
        if (this.stats.melee.getDelay() < 10) {
            par1Entity.invulnerableTime = 0;
        }

        if (par1Entity instanceof LivingEntity) {
            NpcEvent.MeleeAttackEvent event = new NpcEvent.MeleeAttackEvent(this.wrappedNPC, (LivingEntity)par1Entity, f);
            if (EventHooks.onNPCAttacksMelee(this, event)) {
                return false;
            }

            f = event.damage;
        }

        boolean var4 = par1Entity.hurt(new NpcDamageSource("mob", this), f);
        if (var4) {
            if (this.getOwner() instanceof PlayerEntity) {
                EntityUtil.setRecentlyHit((LivingEntity)par1Entity);
            }

            if (this.stats.melee.getKnockback() > 0) {
                par1Entity.push((double)(-MathHelper.sin(this.yRot * 3.1415927F / 180.0F) * (float)this.stats.melee.getKnockback() * 0.5F), 0.1, (double)(MathHelper.cos(this.yRot * 3.1415927F / 180.0F) * (float)this.stats.melee.getKnockback() * 0.5F));
                Vector3d motion = this.getDeltaMovement();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (this.role.getType() == 6) {
                ((RoleCompanion)this.role).attackedEntity(par1Entity);
            }
        }

        if (this.stats.melee.getEffectType() != 0) {
            if (this.stats.melee.getEffectType() != 666) {
                ((LivingEntity)par1Entity).addEffect(new EffectInstance(PotionEffectType.getMCType(this.stats.melee.getEffectType()), this.stats.melee.getEffectTime() * 20, this.stats.melee.getEffectStrength()));
            } else {
                par1Entity.setRemainingFireTicks(this.stats.melee.getEffectTime() * 20);
            }
        }

        return var4;
    }

    public void aiStep() {
        if (!CustomNpcs.FreezeNPCs) {
            if (this.isNoAi()) {
                super.aiStep();
            } else {
                ++this.totalTicksAlive;
                this.updateSwingTime();
                if (this.tickCount % 20 == 0) {
                    this.faction = this.getFaction();
                }

                if (!this.level.isClientSide) {
                    if (!this.isKilled() && this.tickCount % 20 == 0) {
                        this.advanced.scenes.update();
                        if (this.getHealth() < this.getMaxHealth()) {
                            if (this.stats.healthRegen > 0 && !this.isAttacking()) {
                                this.heal((float)this.stats.healthRegen);
                            }

                            if (this.stats.combatRegen > 0 && this.isAttacking()) {
                                this.heal((float)this.stats.combatRegen);
                            }
                        }

                        if (this.faction.getsAttacked && !this.isAttacking()) {
                            List<MonsterEntity> list = this.level.getEntitiesOfClass(MonsterEntity.class, this.getBoundingBox().inflate(16.0, 16.0, 16.0));
                            Iterator var2 = list.iterator();

                            while(var2.hasNext()) {
                                MonsterEntity mob = (MonsterEntity)var2.next();
                                if (mob.getTarget() == null && this.canNpcSee(mob)) {
                                    mob.setTarget(this);
                                }
                            }
                        }

                        if (this.linkedData != null && this.linkedData.time > this.linkedLast) {
                            LinkedNpcController.Instance.loadNpcData(this);
                        }

                        if (this.updateClient) {
                            this.updateClient();
                        }

                        if (this.updateAI) {
                            this.updateTasks();
                            this.updateAI = false;
                        }
                    }

                    if (this.getHealth() <= 0.0F && !this.isKilled()) {
                        this.removeAllEffects();
                        this.entityData.set(IsDead, true);
                        this.updateTasks();
                        this.refreshDimensions();
                    }

                    if (this.display.getBossbar() == 2) {
                        this.bossInfo.setVisible(this.getTarget() != null);
                    }

                    this.entityData.set(Walking, !this.getNavigation().isDone());
                    this.entityData.set(Interacting, this.isInteracting());
                    this.combatHandler.update();
                    this.onCollide();
                }

                if (this.wasKilled != this.isKilled() && this.wasKilled) {
                    this.reset();
                }

                if (this.level.isDay() && !this.level.isClientSide && this.stats.burnInSun) {
                    float f = this.getBrightness();
                    if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.level.canSeeSky(this.blockPosition())) {
                        this.setRemainingFireTicks(160);
                    }
                }

                super.aiStep();
                if (this.level.isClientSide) {
                    this.role.clientUpdate();
                    if (this.textureCloakLocation != null) {
                        this.cloakUpdate();
                    }

                    if (this.currentAnimation != (Integer)this.entityData.get(Animation)) {
                        this.currentAnimation = (Integer)this.entityData.get(Animation);
                        this.animationStart = this.tickCount;
                        this.refreshDimensions();
                    }

                    if (this.job.getType() == 1) {
                        ((JobBard)this.job).aiStep();
                    }
                }

                if (this.display.getBossbar() > 0) {
                    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
                }

            }
        }
    }

    public void updateClient() {
        Packets.sendNearby(this, new PacketNpcUpdate(this.getId(), this.writeSpawnData()));
        this.updateClient = false;
    }

    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (this.level.isClientSide) {
            return this.isAttacking() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        } else if (hand != Hand.MAIN_HAND) {
            return ActionResultType.PASS;
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (stack != null) {
                Item item = stack.getItem();
                if (item == CustomItems.cloner || item == CustomItems.wand || item == CustomItems.mount || item == CustomItems.scripter) {
                    this.setTarget((LivingEntity)null);
                    this.setLastHurtByMob((LivingEntity)null);
                    return ActionResultType.SUCCESS;
                }

                if (item == CustomItems.moving) {
                    this.setTarget((LivingEntity)null);
                    stack.addTagElement("NPCID", IntNBT.valueOf(this.getId()));
                    player.sendMessage(new TranslationTextComponent("message.pather.register", new Object[]{this.getName()}), this.getUUID());
                    return ActionResultType.SUCCESS;
                }
            }

            if (EventHooks.onNPCInteract(this, player)) {
                return ActionResultType.FAIL;
            } else if (this.getFaction().isAggressiveToPlayer(player)) {
                return ActionResultType.FAIL;
            } else {
                this.addInteract(player);
                Dialog dialog = this.getDialog(player);
                QuestData data = PlayerData.get(player).questData.getQuestCompletion(player, this);
                if (data != null) {
                    Packets.send((ServerPlayerEntity)player, new PacketQuestCompletion(data.quest.id));
                } else if (dialog != null) {
                    NoppesUtilServer.openDialog(player, this, dialog);
                } else if (this.role.getType() != 0) {
                    this.role.interact(player);
                } else {
                    this.say(player, this.advanced.getInteractLine());
                }

                return ActionResultType.PASS;
            }
        }
    }

    public void addInteract(LivingEntity entity) {
        if (this.ais.stopAndInteract && !this.isAttacking() && entity.isAlive() && !this.isNoAi()) {
            if (this.tickCount - this.lastInteract < 180) {
                this.interactingEntities.clear();
            }

            this.getNavigation().stop();
            this.lastInteract = this.tickCount;
            if (!this.interactingEntities.contains(entity)) {
                this.interactingEntities.add(entity);
            }

        }
    }

    public boolean isInteracting() {
        if (this.tickCount - this.lastInteract < 40 || this.isClientSide() && (Boolean)this.entityData.get(Interacting)) {
            return true;
        } else {
            return this.ais.stopAndInteract && !this.interactingEntities.isEmpty() && this.tickCount - this.lastInteract < 180;
        }
    }

    private Dialog getDialog(PlayerEntity player) {
        Iterator var2 = this.dialogs.values().iterator();

        while(var2.hasNext()) {
            DialogOption option = (DialogOption)var2.next();
            if (option != null && option.hasDialog()) {
                Dialog dialog = option.getDialog();
                if (dialog.availability.isAvailable(player)) {
                    return dialog;
                }
            }
        }

        return null;
    }

    public boolean hurt(DamageSource damagesource, float i) {
        if (!this.level.isClientSide && !CustomNpcs.FreezeNPCs && !damagesource.msgId.equals("inWall")) {
            if (damagesource.msgId.equals("outOfWorld") && this.isKilled()) {
                this.reset();
            }

            i = this.stats.resistances.applyResistance(damagesource, i);
            float var10000 = (float)this.invulnerableTime;
            this.getClass();
            if (var10000 > 20.0F / 2.0F && i <= this.lastHurt) {
                return false;
            } else {
                Entity entity = NoppesUtilServer.GetDamageSourcee(damagesource);
                LivingEntity attackingEntity = null;
                if (entity instanceof LivingEntity) {
                    attackingEntity = (LivingEntity)entity;
                }

                if (attackingEntity != null && attackingEntity == this.getOwner()) {
                    return false;
                } else {
                    if (attackingEntity instanceof EntityNPCInterface) {
                        EntityNPCInterface npc = (EntityNPCInterface)attackingEntity;
                        if (npc.faction.id == this.faction.id) {
                            return false;
                        }

                        if (npc.getOwner() instanceof PlayerEntity) {
                            this.hurtTime = 100;
                        }
                    } else if (attackingEntity instanceof PlayerEntity && this.faction.isFriendlyToPlayer((PlayerEntity)attackingEntity)) {
                        ForgeHooks.onLivingAttack(this, damagesource, i);
                        return false;
                    }

                    NpcEvent.DamagedEvent event = new NpcEvent.DamagedEvent(this.wrappedNPC, entity, i, damagesource);
                    if (EventHooks.onNPCDamaged(this, event)) {
                        ForgeHooks.onLivingAttack(this, damagesource, i);
                        return false;
                    } else {
                        i = event.damage;
                        if (this.isKilled()) {
                            return false;
                        } else if (attackingEntity == null) {
                            return super.hurt(damagesource, i);
                        } else {
                            boolean var13;
                            try {
                                if (this.isAttacking()) {
                                    if (this.getTarget() != null && this.distanceToSqr(this.getTarget()) > this.distanceToSqr(attackingEntity)) {
                                        this.setTarget(attackingEntity);
                                    }

                                    var13 = super.hurt(damagesource, i);
                                    return var13;
                                }

                                if (i > 0.0F) {
                                    List<EntityNPCInterface> inRange = this.level.getEntitiesOfClass(EntityNPCInterface.class, this.getBoundingBox().inflate(32.0, 16.0, 32.0));
                                    Iterator var7 = inRange.iterator();

                                    while(true) {
                                        if (!var7.hasNext()) {
                                            this.setTarget(attackingEntity);
                                            break;
                                        }

                                        EntityNPCInterface npc = (EntityNPCInterface)var7.next();
                                        if (!npc.isKilled() && npc.advanced.defendFaction && npc.faction.id == this.faction.id && (npc.canNpcSee(this) || npc.ais.directLOS || npc.canNpcSee(attackingEntity))) {
                                            npc.onAttack(attackingEntity);
                                        }
                                    }
                                }

                                var13 = super.hurt(damagesource, i);
                            } finally {
                                if (event.clearTarget) {
                                    this.setTarget((LivingEntity)null);
                                    this.setLastHurtByMob((LivingEntity)null);
                                }

                            }

                            return var13;
                        }
                    }
                }
            }
        } else {
            return false;
        }
    }

    protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
        super.actuallyHurt(damageSrc, damageAmount);
        this.combatHandler.damage(damageSrc, damageAmount);
    }

    public void onAttack(LivingEntity entity) {
        if (entity != null && entity != this && !this.isAttacking() && this.ais.onAttack != 3 && entity != this.getOwner()) {
            super.setTarget(entity);
        }
    }

    public void setTarget(LivingEntity entity) {
        if ((!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).abilities.invulnerable) && (entity == null || entity != this.getOwner()) && this.getTarget() != entity) {
            if (entity != null) {
                NpcEvent.TargetEvent event = new NpcEvent.TargetEvent(this.wrappedNPC, entity);
                if (EventHooks.onNPCTarget(this, event)) {
                    return;
                }

                if (event.entity == null) {
                    entity = null;
                } else {
                    entity = event.entity.getMCEntity();
                }
            } else {
                Iterator var4 = this.targetSelector.availableGoals.iterator();

                while(var4.hasNext()) {
                    PrioritizedGoal en = (PrioritizedGoal)var4.next();
                    en.stop();
                }

                if (EventHooks.onNPCTargetLost(this, this.getTarget())) {
                    return;
                }
            }

            if (entity != null && entity != this && this.ais.onAttack != 3 && !this.isAttacking() && !this.isClientSide()) {
                Line line = this.advanced.getAttackLine();
                if (line != null) {
                    this.saySurrounding(Line.formatTarget(line, entity));
                }
            }

            super.setTarget(entity);
        }
    }

    public void performRangedAttack(LivingEntity entity, float f) {
        ItemStack proj = ItemStackWrapper.MCItem(this.inventory.getProjectile());
        if (proj == null) {
            this.updateAI = true;
        } else {
            NpcEvent.RangedLaunchedEvent event = new NpcEvent.RangedLaunchedEvent(this.wrappedNPC, entity, (float)this.stats.ranged.getStrength());

            for(int i = 0; i < this.stats.ranged.getShotCount(); ++i) {
                EntityProjectile projectile = this.shoot(entity, this.stats.ranged.getAccuracy(), proj, f == 1.0F);
                projectile.damage = event.damage;
                projectile.callback = (projectile1, pos, entity1) -> {
                    if (proj.getItem() == CustomItems.soulstoneFull) {
                        Entity e = ItemSoulstoneFilled.Spawn((PlayerEntity)null, proj, this.level, pos);
                        if (e instanceof LivingEntity && entity1 instanceof LivingEntity) {
                            if (e instanceof MobEntity) {
                                ((MobEntity)e).setTarget((LivingEntity)entity1);
                            } else {
                                ((LivingEntity)e).setLastHurtByMob((LivingEntity)entity1);
                            }
                        }
                    }

                    projectile1.playSound(this.stats.ranged.getSoundEvent(entity1 != null ? 1 : 2), 1.0F, 1.2F / (this.getRandom().nextFloat() * 0.2F + 0.9F));
                    return false;
                };
                this.playSound(this.stats.ranged.getSoundEvent(0), 2.0F, 1.0F);
                event.projectiles.add((IProjectile)NpcAPI.Instance().getIEntity(projectile));
            }

            EventHooks.onNPCRangedLaunched(this, event);
        }
    }

    public EntityProjectile shoot(LivingEntity entity, int accuracy, ItemStack proj, boolean indirect) {
        return this.shoot(entity.getX(), entity.getBoundingBox().minY + (double)(entity.getBbHeight() / 2.0F), entity.getZ(), accuracy, proj, indirect);
    }

    public EntityProjectile shoot(double x, double y, double z, int accuracy, ItemStack proj, boolean indirect) {
        EntityProjectile projectile = new EntityProjectile(this.level, this, proj.copy(), true);
        double varX = x - this.getX();
        double varY = y - (this.getY() + (double)this.getEyeHeight());
        double varZ = z - this.getZ();
        float varF = projectile.hasGravity() ? MathHelper.sqrt(varX * varX + varZ * varZ) : 0.0F;
        float angle = projectile.getAngleForXYZ(varX, varY, varZ, (double)varF, indirect);
        float acc = 20.0F - (float)MathHelper.floor((float)accuracy / 5.0F);
        projectile.shoot(varX, varY, varZ, angle, acc);
        this.level.addFreshEntity(projectile);
        return projectile;
    }

    private void clearTasks(GoalSelector tasks) {
        List<PrioritizedGoal> list = new ArrayList(tasks.availableGoals);

        for (PrioritizedGoal entityaitaskentry : list) {
            tasks.removeGoal(entityaitaskentry);
        }

        tasks.availableGoals.clear();
        tasks.lockedFlags.clear();
        tasks.disabledFlags.clear();
    }

    private void updateTasks() {
        if (this.level != null && !this.level.isClientSide && this.level instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld)this.level;
            this.clearTasks(this.goalSelector);
            this.clearTasks(this.targetSelector);
            if (!this.isKilled()) {
                this.targetSelector.addGoal(0, new EntityAIClearTarget(this));
                this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
                this.targetSelector.addGoal(2, new NpcNearestAttackableTargetGoal(this, LivingEntity.class, 4, this.ais.directLOS, false, new NPCAttackSelector(this)));
                this.targetSelector.addGoal(3, new EntityAIOwnerHurtByTarget(this));
                this.targetSelector.addGoal(4, new EntityAIOwnerHurtTarget(this));
                sWorld.navigations.remove(this.getNavigation());
                if (this.ais.movementType == 1) {
                    this.moveControl = new FlyingMoveHelper(this);
                    this.navigation = new FlyingPathNavigator(this, this.level);
                } else if (this.ais.movementType == 2) {
                    this.moveControl = new FlyingMoveHelper(this);
                    this.navigation = new SwimmerPathNavigator(this, this.level);
                } else {
                    this.moveControl = new MovementController(this);
                    this.navigation = new GroundPathNavigator(this, this.level);
                    this.goalSelector.addGoal(0, new EntityAIWaterNav(this));
                }

                sWorld.navigations.add(this.getNavigation());
                this.taskCount = 1;
                this.addRegularEntries();
                this.doorInteractType();
                this.seekShelter();
                this.setResponse();
                this.setMoveType();
            }
        }
    }

    private void setResponse() {
        this.aiAttackTarget = this.aiRange = null;
        if (this.ais.canSprint) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAISprintToTarget(this));
        }

        if (this.ais.onAttack == 1) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAIPanic(this, 1.2F));
        } else if (this.ais.onAttack == 2) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAIAvoidTarget(this));
        } else if (this.ais.onAttack == 0) {
            if (this.ais.canLeap) {
                this.goalSelector.addGoal(this.taskCount++, new EntityAIPounceTarget(this));
            }

            this.goalSelector.addGoal(this.taskCount, this.aiAttackTarget = new EntityAIAttackTarget(this));
            if (this.inventory.getProjectile() != null) {
                this.goalSelector.addGoal(this.taskCount++, this.aiRange = new EntityAIRangedAttack(this));
            }
        } else if (this.ais.onAttack == 3) {
        }

    }

    public boolean canFly() {
        return this.navigation instanceof FlyingPathNavigator;
    }

    public void setMoveType() {
        if (this.ais.getMovingType() == 1) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAIWander(this));
        }

        if (this.ais.getMovingType() == 2) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAIMovingPath(this));
        }

    }

    public void doorInteractType() {
        if (this.navigation instanceof GroundPathNavigator) {
            Goal aiDoor = null;
            if (this.ais.doorInteract == 1) {
                this.goalSelector.addGoal(this.taskCount++, (Goal)(aiDoor = new OpenDoorGoal(this, true)));
            } else if (this.ais.doorInteract == 0) {
                this.goalSelector.addGoal(this.taskCount++, (Goal)(aiDoor = new EntityAIBustDoor(this)));
            }

            ((GroundPathNavigator)this.navigation).setCanOpenDoors(aiDoor != null);
        }

    }

    public void seekShelter() {
        if (this.ais.findShelter == 0) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAIMoveIndoors(this));
        } else if (this.ais.findShelter == 1) {
            if (!this.canFly()) {
                this.goalSelector.addGoal(this.taskCount++, new RestrictSunGoal(this));
            }

            this.goalSelector.addGoal(this.taskCount++, new EntityAIFindShade(this));
        }

    }

    public void addRegularEntries() {
        this.goalSelector.addGoal(this.taskCount++, new EntityAIReturn(this));
        this.goalSelector.addGoal(this.taskCount++, new EntityAIFollow(this));
        if (this.ais.getStandingType() != 1 && this.ais.getStandingType() != 3) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAIWatchClosest(this, LivingEntity.class, 5.0F));
        }

        this.goalSelector.addGoal(this.taskCount++, this.lookAi = new EntityAILook(this));
        this.goalSelector.addGoal(this.taskCount++, new EntityAIWorldLines(this));
        this.goalSelector.addGoal(this.taskCount++, new EntityAIJob(this));
        this.goalSelector.addGoal(this.taskCount++, new EntityAIRole(this));
        this.goalSelector.addGoal(this.taskCount++, this.animateAi = new EntityAIAnimation(this));
        if (this.transform.isValid()) {
            this.goalSelector.addGoal(this.taskCount++, new EntityAITransform(this));
        }

    }

    public float getSpeed() {
        return (float)this.ais.getWalkingSpeed() / 20.0F;
    }

    public float getWalkTargetValue(BlockPos pos) {
        if (this.ais.movementType == 2) {
            return this.level.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F : 0.0F;
        } else {
            float weight = (float)this.level.getLightEmission(pos) - 0.5F;
            if (this.level.getBlockState(pos).isSolidRender(this.level, pos)) {
                weight += 10.0F;
            }

            return weight;
        }
    }

    protected int decreaseAirSupply(int par1) {
        return !this.stats.canDrown ? par1 : super.decreaseAirSupply(par1);
    }

    public CreatureAttribute getMobType() {
        return this.stats == null ? null : this.stats.creatureType;
    }

    public int getAmbientSoundInterval() {
        return 160;
    }

    public void playAmbientSound() {
        if (this.isAlive()) {
            this.advanced.playSound(this.getTarget() != null ? 1 : 0, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    protected void playHurtSound(DamageSource source) {
        this.advanced.playSound(2, this.getSoundVolume(), this.getVoicePitch());
    }

    public SoundEvent getDeathSound() {
        return null;
    }

    protected float getVoicePitch() {
        return this.advanced.disablePitch ? 1.0F : super.getVoicePitch();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (this.advanced.getSound(4) != null) {
            this.advanced.playSound(4, 0.15F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }

    }

    public ServerPlayerEntity getFakeChatPlayer() {
        if (this.level.isClientSide) {
            return null;
        } else {
            EntityUtil.Copy(this, ChatEventPlayer);
            ChatEventProfile.npc = this;
            ChatEventPlayer.setLevel(this.level);
            ChatEventPlayer.setPos(this.getX(), this.getY(), this.getZ());
            return ChatEventPlayer;
        }
    }

    public void saySurrounding(Line line) {
        if (line != null) {
            if (line.getShowText() && !line.getText().isEmpty()) {
                ServerChatEvent event = new ServerChatEvent(this.getFakeChatPlayer(), line.getText(), new TranslationTextComponent(line.getText().replace("%", "%%")));
                if (CustomNpcs.NpcSpeachTriggersChatEvent && (MinecraftForge.EVENT_BUS.post(event) || event.getComponent() == null)) {
                    return;
                }

                line.setText(event.getComponent().getString().replace("%%", "%"));
            }

            List<PlayerEntity> inRange = this.level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(20.0, 20.0, 20.0));
            Iterator var3 = inRange.iterator();

            while(var3.hasNext()) {
                PlayerEntity player = (PlayerEntity)var3.next();
                this.say(player, line);
            }

        }
    }

    public void say(PlayerEntity player, Line line) {
        if (line != null && this.canNpcSee(player)) {
            if (!line.getSound().isEmpty()) {
                BlockPos pos = this.blockPosition();
                Packets.send((ServerPlayerEntity)player, new PacketPlaySound(line.getSound(), pos, this.getSoundVolume(), this.getVoicePitch()));
            }

            if (!line.getText().isEmpty()) {
                Packets.send((ServerPlayerEntity)player, new PacketChatBubble(this.getId(), new TranslationTextComponent(line.getText()), line.getShowText()));
            }

        }
    }

    public boolean shouldShowName() {
        return true;
    }

    public void push(double d, double d1, double d2) {
        if (this.isWalking() && !this.isKilled()) {
            super.push(d, d1, d2);
        }

    }

    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.npcVersion = compound.getInt("ModRev");
        VersionCompatibility.CheckNpcCompatibility(this, compound);
        this.display.readToNBT(compound);
        this.stats.readToNBT(compound);
        this.ais.readToNBT(compound);
        this.script.load(compound);
        this.timers.load(compound);
        this.advanced.readToNBT(compound);
        this.role.load(compound);
        this.job.load(compound);
        this.inventory.load(compound);
        this.transform.readToNBT(compound);
        this.killedtime = compound.getLong("KilledTime");
        this.totalTicksAlive = compound.getLong("TotalTicksAlive");
        this.linkedName = compound.getString("LinkedNpcName");
        if (!this.isClientSide()) {
            LinkedNpcController.Instance.loadNpcData(this);
        }

        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue((double)CustomNpcs.NpcNavRange);
        this.updateAI = true;
    }

    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        this.display.save(compound);
        this.stats.save(compound);
        this.ais.save(compound);
        this.script.save(compound);
        this.timers.save(compound);
        this.advanced.save(compound);
        this.role.save(compound);
        this.job.save(compound);
        this.inventory.save(compound);
        this.transform.save(compound);
        compound.putLong("KilledTime", this.killedtime);
        compound.putLong("TotalTicksAlive", this.totalTicksAlive);
        compound.putInt("ModRev", this.npcVersion);
        compound.putString("LinkedNpcName", this.linkedName);
    }

    public EntitySize getDimensions(Pose poseIn) {
        EntitySize size = this.baseSize;
        if (this.currentAnimation != 2 && this.currentAnimation != 7 && this.deathTime <= 0) {
            if (this.isPassenger() || this.currentAnimation == 1) {
                size = this.baseSize.scale(1.0F, 0.77F);
            }
        } else {
            size = sizeSleep;
        }

        size = size.scale((float)this.display.getSize() * 0.2F);
        if (this.display.getHitboxState() == 1 || this.isKilled() && this.stats.hideKilledBody) {
            size = EntitySize.scalable(1.0E-5F, size.height);
        }

        if ((double)(size.width / 2.0F) > this.level.getMaxEntityRadius()) {
            this.level.increaseMaxEntityRadius((double)(size.width / 2.0F));
        }

        return size;
    }

    public void tickDeath() {
        if (this.stats.spawnCycle != 3 && this.stats.spawnCycle != 4) {
            ++this.deathTime;
            if (!this.level.isClientSide) {
                if (!this.hasDied) {
                    this.remove();
                }

                if (this.killedtime < System.currentTimeMillis() && (this.stats.spawnCycle == 0 || this.level.isDay() && this.stats.spawnCycle == 1 || !this.level.isDay() && this.stats.spawnCycle == 2)) {
                    this.reset();
                }

            }
        } else {
            super.tickDeath();
        }
    }

    public void reset() {
        this.hasDied = false;
        this.removed = false;
        this.dead = false;
        this.revive();
        this.wasKilled = false;
        this.setSprinting(false);
        this.setHealth(this.getMaxHealth());
        this.entityData.set(Animation, 0);
        this.entityData.set(Walking, false);
        this.entityData.set(IsDead, false);
        this.entityData.set(Interacting, false);
        this.interactingEntities.clear();
        this.combatHandler.reset();
        this.setTarget((LivingEntity)null);
        this.setLastHurtByMob((LivingEntity)null);
        this.deathTime = 0;
        if (this.ais.returnToStart && !this.hasOwner() && !this.isClientSide() && !this.isPassenger()) {
            this.moveTo((double)this.getStartXPos(), this.getStartYPos(), (double)this.getStartZPos(), this.yRot, this.xRot);
        }

        this.killedtime = 0L;
        this.clearFire();
        this.removeAllEffects();
        this.travel(Vector3d.ZERO);
        this.walkDistO = this.walkDist = 0.0F;
        this.getNavigation().stop();
        this.currentAnimation = 0;
        this.refreshDimensions();
        this.updateAI = true;
        this.ais.movingPos = 0;
        if (this.getOwner() != null) {
            this.getOwner().setLastHurtMob((Entity)null);
        }

        this.bossInfo.setVisible(this.display.getBossbar() == 1);
        this.job.reset();
        EventHooks.onNPCInit(this);
    }

    public void onCollide() {
        if (this.isAlive() && this.tickCount % 4 == 0 && !this.level.isClientSide) {
            AxisAlignedBB axisalignedbb = null;
            if (this.getVehicle() != null && this.getVehicle().isAlive()) {
                axisalignedbb = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
            } else {
                axisalignedbb = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
            }

            List list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            if (list != null) {
                for(int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity)list.get(i);
                    if (entity != this && entity.isAlive()) {
                        EventHooks.onNPCCollide(this, entity);
                    }
                }

            }
        }
    }

    public void handleInsidePortal(BlockPos pos) {
    }

    public void cloakUpdate() {
        this.prevChasingPosX = this.chasingPosX;
        this.prevChasingPosY = this.chasingPosY;
        this.prevChasingPosZ = this.chasingPosZ;
        double d0 = this.getX() - this.chasingPosX;
        double d1 = this.getY() - this.chasingPosY;
        double d2 = this.getZ() - this.chasingPosZ;
        double d3 = 10.0;
        if (d0 > 10.0) {
            this.chasingPosX = this.getX();
            this.prevChasingPosX = this.chasingPosX;
        }

        if (d2 > 10.0) {
            this.chasingPosZ = this.getZ();
            this.prevChasingPosZ = this.chasingPosZ;
        }

        if (d1 > 10.0) {
            this.chasingPosY = this.getY();
            this.prevChasingPosY = this.chasingPosY;
        }

        if (d0 < -10.0) {
            this.chasingPosX = this.getX();
            this.prevChasingPosX = this.chasingPosX;
        }

        if (d2 < -10.0) {
            this.chasingPosZ = this.getZ();
            this.prevChasingPosZ = this.chasingPosZ;
        }

        if (d1 < -10.0) {
            this.chasingPosY = this.getY();
            this.prevChasingPosY = this.chasingPosY;
        }

        this.chasingPosX += d0 * 0.25;
        this.chasingPosZ += d2 * 0.25;
        this.chasingPosY += d1 * 0.25;
    }

    public boolean removeWhenFarAway(double distanceToPlayer) {
        return this.stats != null && this.stats.spawnCycle == 4;
    }

    public ItemStack getMainHandItem() {
        IItemStack item = null;
        if (this.isAttacking()) {
            item = this.inventory.getRightHand();
        } else if (this.role.getType() == 6) {
            item = ((RoleCompanion)this.role).getItemInHand();
        } else if (this.job.overrideMainHand) {
            item = this.job.getMainhand();
        } else {
            item = this.inventory.getRightHand();
        }

        return ItemStackWrapper.MCItem(item);
    }

    public ItemStack getOffhandItem() {
        IItemStack item = null;
        if (this.isAttacking()) {
            item = this.inventory.getLeftHand();
        } else if (this.job.overrideOffHand) {
            item = this.job.getOffhand();
        } else {
            item = this.inventory.getLeftHand();
        }

        return ItemStackWrapper.MCItem(item);
    }

    public ItemStack getItemBySlot(EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.MAINHAND) {
            return this.getMainHandItem();
        } else {
            return slot == EquipmentSlotType.OFFHAND ? this.getOffhandItem() : ItemStackWrapper.MCItem(this.inventory.getArmor(3 - slot.getIndex()));
        }
    }

    public void setItemSlot(EquipmentSlotType slot, ItemStack item) {
        if (slot == EquipmentSlotType.MAINHAND) {
            this.inventory.weapons.put(0, NpcAPI.Instance().getIItemStack(item));
        } else if (slot == EquipmentSlotType.OFFHAND) {
            this.inventory.weapons.put(2, NpcAPI.Instance().getIItemStack(item));
        } else {
            this.inventory.armor.put(3 - slot.getIndex(), NpcAPI.Instance().getIItemStack(item));
        }

    }

    public Iterable<ItemStack> getArmorSlots() {
        ArrayList<ItemStack> list = new ArrayList();

        for(int i = 0; i < 4; ++i) {
            list.add(ItemStackWrapper.MCItem((IItemStack)this.inventory.armor.get(3 - i)));
        }

        return list;
    }

    public Iterable<ItemStack> getAllSlots() {
        ArrayList list = new ArrayList();
        list.add(ItemStackWrapper.MCItem((IItemStack)this.inventory.weapons.get(0)));
        list.add(ItemStackWrapper.MCItem((IItemStack)this.inventory.weapons.get(2)));
        return list;
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
    }

    protected void dropFromLootTable(DamageSource damageSourceIn, boolean attackedRecently) {
    }

    public void die(DamageSource damagesource) {
        this.setSprinting(false);
        this.getNavigation().stop();
        this.clearFire();
        this.removeAllEffects();
        if (!this.isClientSide()) {
            this.advanced.playSound(3, this.getSoundVolume(), this.getVoicePitch());
            Entity attackingEntity = NoppesUtilServer.GetDamageSourcee(damagesource);
            NpcEvent.DiedEvent event = new NpcEvent.DiedEvent(this.wrappedNPC, damagesource, attackingEntity);
            event.droppedItems = this.inventory.getItemsRNG();
            event.expDropped = this.inventory.getExpRNG();
            event.line = this.advanced.getKilledLine();
            EventHooks.onNPCDied(this, event);
            this.bossInfo.setVisible(false);
            this.inventory.dropStuff(event, attackingEntity, damagesource);
            if (event.line != null) {
                this.saySurrounding(Line.formatTarget((Line)event.line, attackingEntity instanceof LivingEntity ? (LivingEntity)attackingEntity : null));
            }
        }

        super.die(damagesource);
    }

    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    public void remove() {
        this.hasDied = true;
        this.ejectPassengers();
        this.stopRiding();
        if (!this.level.isClientSide && this.stats.spawnCycle != 3 && this.stats.spawnCycle != 4) {
            this.setHealth(-1.0F);
            this.setSprinting(false);
            this.getNavigation().stop();
            this.setCurrentAnimation(2);
            this.refreshDimensions();
            if (this.killedtime <= 0L) {
                this.killedtime = (long)(this.stats.respawnTime * 1000) + System.currentTimeMillis();
            }

            this.role.killed();
            this.job.killed();
        } else {
            this.delete();
        }

    }

    public void delete() {
        VisibilityController.instance.remove(this);
        this.role.delete();
        this.job.delete();
        super.remove();
    }

    public float getStartXPos() {
        return (float)this.ais.startPos().getX() + this.ais.bodyOffsetX / 10.0F;
    }

    public float getStartZPos() {
        return (float)this.ais.startPos().getZ() + this.ais.bodyOffsetZ / 10.0F;
    }

    public boolean isVeryNearAssignedPlace() {
        double xx = this.getX() - (double)this.getStartXPos();
        double zz = this.getZ() - (double)this.getStartZPos();
        if (!(xx < -0.2) && !(xx > 0.2)) {
            return !(zz < -0.2) && !(zz > 0.2);
        } else {
            return false;
        }
    }

    public double getStartYPos() {
        return this.startYPos < 0.0 ? this.calculateStartYPos(this.ais.startPos()) : this.startYPos;
    }

    private double calculateStartYPos(BlockPos pos) {
        BlockPos startPos = this.ais.startPos();

        while(true) {
            while(pos.getY() > 0) {
                BlockState state = this.level.getBlockState(pos);
                VoxelShape shape = state.getShape(this.level, pos);
                if (!shape.isEmpty()) {
                    AxisAlignedBB bb = shape.bounds().move(pos);
                    if (this.ais.movementType != 2 || startPos.getY() > pos.getY() || state.getMaterial() != Material.WATER) {
                        return bb.maxY;
                    }

                    pos = pos.below();
                } else {
                    pos = pos.below();
                }
            }

            return 0.0;
        }
    }

    private BlockPos calculateTopPos(BlockPos pos) {
        for(BlockPos check = pos; check.getY() > 0; check = check.below()) {
            BlockState state = this.level.getBlockState(pos);
            VoxelShape shape = state.getShape(this.level, pos);
            if (!shape.isEmpty()) {
                AxisAlignedBB bb = shape.bounds().move(pos);
                if (bb != null) {
                    return check;
                }
            }
        }

        return pos;
    }

    public boolean isInRange(Entity entity, double range) {
        return this.isInRange(entity.getX(), entity.getY(), entity.getZ(), range);
    }

    public boolean isInRange(double posX, double posY, double posZ, double range) {
        double y = Math.abs(this.getY() - posY);
        if (posY >= 0.0 && y > range) {
            return false;
        } else {
            double x = Math.abs(this.getX() - posX);
            double z = Math.abs(this.getZ() - posZ);
            return x <= range && z <= range;
        }
    }

    public void givePlayerItem(PlayerEntity player, ItemStack item) {
        if (!this.level.isClientSide) {
            item = item.copy();
            float f = 0.7F;
            double d = (double)(this.level.random.nextFloat() * f) + (double)(1.0F - f);
            double d1 = (double)(this.level.random.nextFloat() * f) + (double)(1.0F - f);
            double d2 = (double)(this.level.random.nextFloat() * f) + (double)(1.0F - f);
            ItemEntity entityitem = new ItemEntity(this.level, this.getX() + d, this.getY() + d1, this.getZ() + d2, item);
            entityitem.setPickUpDelay(2);
            this.level.addFreshEntity(entityitem);
            int i = item.getCount();
            if (player.inventory.add(item)) {
                this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.take(entityitem, i);
                if (item.getCount() <= 0) {
                    entityitem.remove();
                }
            }

        }
    }

    public boolean isSleeping() {
        return this.currentAnimation == 2 && !this.isAttacking();
    }

    public boolean isWalking() {
        return this.ais.getMovingType() != 0 || this.isAttacking() || this.isFollower() || (Boolean)this.entityData.get(Walking);
    }

    public boolean isCrouching() {
        return this.currentAnimation == 4;
    }

    public void knockback(float strength, double ratioX, double ratioZ) {
        super.knockback(strength * (2.0F - this.stats.resistances.knockback), ratioX, ratioZ);
    }

    public Faction getFaction() {
        Faction fac = FactionController.instance.getFaction((Integer)this.entityData.get(FactionData));
        return fac == null ? FactionController.instance.getFaction(FactionController.instance.getFirstFactionId()) : fac;
    }

    public boolean isClientSide() {
        return this.level == null || this.level.isClientSide;
    }

    public void setFaction(int id) {
        if (id >= 0 && !this.isClientSide()) {
            this.entityData.set(FactionData, id);
        }
    }

    public boolean canBeAffected(EffectInstance effect) {
        if (this.stats.potionImmune) {
            return false;
        } else {
            return this.getMobType() == CreatureAttribute.ARTHROPOD && effect.getEffect() == Effects.POISON ? false : super.canBeAffected(effect);
        }
    }

    public boolean isAttacking() {
        return (Boolean)this.entityData.get(Attacking);
    }

    public boolean isKilled() {
        return this.removed || (Boolean)this.entityData.get(IsDead);
    }

    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeNbt(this.writeSpawnData());
    }

    public CompoundNBT writeSpawnData() {
        CompoundNBT compound = new CompoundNBT();
        this.display.save(compound);
        compound.putInt("MaxHealth", this.stats.maxHealth);
        compound.put("Armor", NBTTags.nbtIItemStackMap(this.inventory.armor));
        compound.put("Weapons", NBTTags.nbtIItemStackMap(this.inventory.weapons));
        compound.putInt("Speed", this.ais.getWalkingSpeed());
        compound.putBoolean("MountControl", this.ais.mountControl);
        compound.putBoolean("TextoCentrado", this.ais.textoCentrado);
        compound.putBoolean("DeadBody", this.stats.hideKilledBody);
        compound.putInt("StandingState", this.ais.getStandingType());
        compound.putInt("MovingState", this.ais.getMovingType());
        compound.putInt("Orientation", this.ais.orientation);
        compound.putFloat("PositionXOffset", this.ais.bodyOffsetX);
        compound.putFloat("PositionYOffset", this.ais.bodyOffsetY);
        compound.putFloat("PositionZOffset", this.ais.bodyOffsetZ);
        compound.putInt("Role", this.role.getType());
        compound.putInt("Job", this.job.getType());
        CompoundNBT bard;
        if (this.job.getType() == 1) {
            bard = new CompoundNBT();
            this.job.save(bard);
            compound.put("Bard", bard);
        }

        if (this.job.getType() == 9) {
            bard = new CompoundNBT();
            this.job.save(bard);
            compound.put("Puppet", bard);
        }

        if (this.role.getType() == 6) {
            bard = new CompoundNBT();
            this.role.save(bard);
            compound.put("Companion", bard);
        }

        if (this instanceof EntityCustomNpc) {
            compound.put("ModelData", ((EntityCustomNpc)this).modelData.save());
        }

        return compound;
    }

    public void readSpawnData(PacketBuffer buf) {
        this.readSpawnData(buf.readNbt());
    }

    public void readSpawnData(CompoundNBT compound) {
        this.stats.setMaxHealth(compound.getInt("MaxHealth"));
        this.ais.setWalkingSpeed(compound.getInt("Speed"));
        this.stats.hideKilledBody = compound.getBoolean("DeadBody");
        this.ais.setStandingType(compound.getInt("StandingState"));
        this.ais.mountControl = compound.getBoolean("MountControl");
        this.ais.textoCentrado = compound.getBoolean("TextoCentrado");
        this.ais.setMovingType(compound.getInt("MovingState"));
        this.ais.orientation = compound.getInt("Orientation");
        this.ais.bodyOffsetX = compound.getFloat("PositionXOffset");
        this.ais.bodyOffsetY = compound.getFloat("PositionYOffset");
        this.ais.bodyOffsetZ = compound.getFloat("PositionZOffset");
        this.inventory.armor = NBTTags.getIItemStackMap(compound.getList("Armor", 10));
        this.inventory.weapons = NBTTags.getIItemStackMap(compound.getList("Weapons", 10));
        this.advanced.setRole(compound.getInt("Role"));
        this.advanced.setJob(compound.getInt("Job"));
        CompoundNBT puppet;
        if (this.job.getType() == 1) {
            puppet = compound.getCompound("Bard");
            this.job.load(puppet);
        }

        if (this.job.getType() == 9) {
            puppet = compound.getCompound("Puppet");
            this.job.load(puppet);
        }

        if (this.role.getType() == 6) {
            puppet = compound.getCompound("Companion");
            this.role.load(puppet);
        }

        if (this instanceof EntityCustomNpc) {
            ((EntityCustomNpc)this).modelData.load(compound.getCompound("ModelData"));
        }

        this.display.readToNBT(compound);
        this.refreshDimensions();
    }

    public CommandSource createCommandSourceStack() {
        if (this.level.isClientSide) {
            return super.createCommandSourceStack();
        } else {
            EntityUtil.Copy(this, CommandPlayer);
            CommandPlayer.setLevel(this.level);
            CommandPlayer.setPos(this.getX(), this.getY(), this.getZ());
            return new CommandSource(this, this.position(), this.getRotationVector(), this.level instanceof ServerWorld ? (ServerWorld)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
        }
    }

    public ITextComponent getName() {
        return new TranslationTextComponent(this.display.getName());
    }

    public void setImmuneToFire(boolean immuneToFire) {
        this.stats.immuneToFire = immuneToFire;
    }

    public boolean fireImmune() {
        return this.stats.immuneToFire;
    }

    public boolean causeFallDamage(float distance, float modifier) {
        return !this.stats.noFallDamage ? super.causeFallDamage(distance, modifier) : false;
    }

    public void makeStuckInBlock(BlockState state, Vector3d motionMultiplierIn) {
        if (state != null && !state.is(Blocks.COBWEB) || !this.stats.ignoreCobweb) {
            super.makeStuckInBlock(state, motionMultiplierIn);
        }

    }

    public boolean canBeCollidedWith() {
        return !this.isKilled() && this.display.getHitboxState() == 2;
    }

    protected void pushEntities() {
        if (this.display.getHitboxState() == 0) {
            super.pushEntities();
        }
    }

    public boolean isPushable() {
        return this.isWalking() && !this.isKilled();
    }

    public PushReaction getPistonPushReaction() {
        return this.display.getHitboxState() == 0 ? super.getPistonPushReaction() : PushReaction.IGNORE;
    }

    public EntityAIRangedAttack getRangedTask() {
        return this.aiRange;
    }

    public String getRoleData() {
        return (String)this.entityData.get(RoleData);
    }

    public void setRoleData(String s) {
        this.entityData.set(RoleData, s);
    }

    public String getJobData() {
        return (String)this.entityData.get(RoleData);
    }

    public void setJobData(String s) {
        this.entityData.set(RoleData, s);
    }

    public World getCommandSenderWorld() {
        return this.level;
    }

    public boolean isInvisibleTo(PlayerEntity player) {
        return this.display.getVisible() == 1 && player.getMainHandItem().getItem() != CustomItems.wand && !this.display.availability.hasOptions();
    }

    public boolean isInvisible() {
        return this.display.getVisible() != 0 && !this.display.availability.hasOptions();
    }

    public void setInvisible(ServerPlayerEntity playerMP) {
        if (this.tracking.contains(playerMP.getId())) {
            this.tracking.remove(playerMP.getId());
            Packets.send(playerMP, new PacketNpcVisibleFalse(this.getId()));
        }

    }

    public void setVisible(ServerPlayerEntity playerMP) {
        if (!this.tracking.contains(playerMP.getId())) {
            this.tracking.add(playerMP.getId());
            Packets.send(playerMP, new PacketNpcVisibleTrue(this));
            playerMP.connection.send(new SEntityMetadataPacket(this.getId(), this.getEntityData(), true));
        }

        Packets.send(playerMP, new PacketNpcUpdate(this.getId(), this.writeSpawnData()));
    }

    public void sendMessage(ITextComponent var1, UUID sender) {
    }

    public void setCurrentAnimation(int animation) {
        this.currentAnimation = animation;
        this.entityData.set(Animation, animation);
    }

    public boolean canNpcSee(Entity entity) {
        return this.getSensing().canSee(entity);
    }

    public boolean isFollower() {
        if (this.advanced.scenes.getOwner() != null) {
            return true;
        } else {
            return this.role.isFollowing() || this.job.isFollowing();
        }
    }

    public LivingEntity getOwner() {
        if (this.advanced.scenes.getOwner() != null) {
            return this.advanced.scenes.getOwner();
        } else if (this.role.getType() == 2 && this.role instanceof RoleFollower) {
            return ((RoleFollower)this.role).owner;
        } else if (this.role.getType() == 6 && this.role instanceof RoleCompanion) {
            return ((RoleCompanion)this.role).owner;
        } else {
            return this.job.getType() == 5 && this.job instanceof JobFollower ? ((JobFollower)this.job).following : null;
        }
    }

    public boolean hasOwner() {
        if (this.advanced.scenes.getOwner() != null) {
            return true;
        } else {
            return this.role.getType() == 2 && ((RoleFollower)this.role).hasOwner() || this.role.getType() == 6 && ((RoleCompanion)this.role).hasOwner() || this.job.getType() == 5 && ((JobFollower)this.job).hasOwner();
        }
    }

    public int followRange() {
        if (this.advanced.scenes.getOwner() != null) {
            return 4;
        } else if (this.role.getType() == 2 && this.role.isFollowing()) {
            return 6;
        } else if (this.role.getType() == 6 && this.role.isFollowing()) {
            return 4;
        } else {
            return this.job.getType() == 5 && this.job.isFollowing() ? 4 : 15;
        }
    }

    protected float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
        if (this.role.getType() == 6) {
            damage = ((RoleCompanion)this.role).getDamageAfterArmorAbsorb(source, damage);
        }

        return damage;
    }

    public boolean isAlliedTo(Entity entity) {
        if (!this.isClientSide()) {
            if (entity instanceof PlayerEntity && this.getFaction().isFriendlyToPlayer((PlayerEntity)entity)) {
                return true;
            }

            if (entity == this.getOwner()) {
                return true;
            }

            if (entity instanceof EntityNPCInterface && ((EntityNPCInterface)entity).faction.id == this.faction.id) {
                return true;
            }
        }

        return super.isAlliedTo(entity);
    }

    public void setDataWatcher(EntityDataManager entityData) {
        this.entityData.assignValues(entityData.getAll());
    }

    public void travel(Vector3d travelVector) {
        BlockPos pos = this.blockPosition();
        if (this.isAlive() && this.isVehicle() && this.ais.mountControl && this.getControllingPassenger() != null) {
            LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
            this.yRot = livingentity.yRot;
            this.yRotO = this.yRot;
            this.xRot = livingentity.xRot * 0.5F;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yBodyRot;
            float f = livingentity.xxa * 0.5F;
            float f1 = livingentity.zza;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }
            this.maxUpStep = 1.0F;
            super.travel(new Vector3d(f, travelVector.y, f1));
        } else {
            this.maxUpStep = 0.5F;
            this.flyingSpeed = 0.02F;
            super.travel(travelVector);
        }
        if (this.role.getType() == 6 && !this.isClientSide()) {
            BlockPos delta = this.blockPosition().subtract(pos);
            ((RoleCompanion) this.role).addMovementStat(delta.getX(), delta.getY(), delta.getZ());
        }
    }

    public boolean canBeLeashed(PlayerEntity player) {
        return false;
    }

    public boolean isLeashed() {
        return false;
    }

    public boolean nearPosition(BlockPos pos) {
        BlockPos npcpos = this.blockPosition();
        float x = (float)(npcpos.getX() - pos.getX());
        float z = (float)(npcpos.getZ() - pos.getZ());
        float y = (float)(npcpos.getY() - pos.getY());
        float height = (float)(MathHelper.ceil(this.getBbHeight() + 1.0F) * MathHelper.ceil(this.getBbHeight() + 1.0F));
        return (double)(x * x + z * z) < 2.5 && (double)(y * y) < (double)height + 2.5;
    }

    public void tpTo(LivingEntity owner) {
        if (owner != null) {
            Direction facing = owner.getDirection().getOpposite();
            BlockPos pos = new BlockPos(owner.getX(), owner.getBoundingBox().minY, owner.getZ());
            pos = pos.offset(facing.getStepX(), 0, facing.getStepZ());
            pos = this.calculateTopPos(pos);

            for(int i = -1; i < 2; ++i) {
                for(int j = 0; j < 3; ++j) {
                    BlockPos check;
                    if (facing.getStepX() == 0) {
                        check = pos.offset(i, 0, j * facing.getStepZ());
                    } else {
                        check = pos.offset(j * facing.getStepX(), 0, i);
                    }

                    check = this.calculateTopPos(check);
                    if (!this.level.getBlockState(check).isSolidRender(this.level, check) && !this.level.getBlockState(check.above()).isSolidRender(this.level, check.above())) {
                        this.moveTo((double)((float)check.getX() + 0.5F), (double)check.getY(), (double)((float)check.getZ() + 0.5F), this.yRot, this.xRot);
                        this.getNavigation().stop();
                        break;
                    }
                }
            }

        }
    }

    public boolean canBeRiddenInWater(Entity rider) {
        return false;
    }

    public void onSyncedDataUpdated(DataParameter<?> para) {
        super.onSyncedDataUpdated(para);
        if (Animation.equals(para)) {
            this.refreshDimensions();
        }

    }

    static {
        Attacking = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
        Animation = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.INT);
        RoleData = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.STRING);
        JobData = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.STRING);
        FactionData = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.INT);
        Walking = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
        Interacting = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
        IsDead = EntityDataManager.defineId(EntityNPCInterface.class, DataSerializers.BOOLEAN);
        CommandProfile = new GameProfileAlt();
        ChatEventProfile = new GameProfileAlt();
        GenericProfile = new GameProfileAlt();
        sizeSleep = new EntitySize(0.8F, 0.4F, false);
    }
}
