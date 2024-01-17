package noppes.npcs.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityCustomModel extends CreatureEntity implements IAnimatable, IAnimationTickable {
    private AnimationFactory factory = new AnimationFactory(this);
    public ResourceLocation modelResLoc=new ResourceLocation("geckolib3", "geo/bike.geo.json");
    public ResourceLocation animResLoc=new ResourceLocation("geckolib3", "bike.animation.json");
    public ResourceLocation textureResLoc = new ResourceLocation("geckolib3", "textures/model/entity/bike.png");
    public String idleAnim = "";
    public String walkAnim = "";
    public String hurtAnim = "";
    public String attackAnim = "";
    public AnimationBuilder dialogAnim = null;
    public AnimationBuilder manualAnim = null;
    public ItemStack leftHeldItem;
    private <E extends IAnimatable> PlayState predicateMovement(AnimationEvent<E> event) {
        if (manualAnim != null) {
            if (event.getController().getAnimationState() == AnimationState.Stopped) {
                manualAnim = null;
            } else {
                if (event.getController().currentAnimationBuilder != manualAnim) {
                    event.getController().markNeedsReload();
                }
                event.getController().setAnimation(manualAnim);
                return PlayState.CONTINUE;
            }
        }
        if (dialogAnim != null) {
            if (event.getController().getAnimationState() == AnimationState.Stopped) {
                dialogAnim = null;
            } else {
                if (event.getController().currentAnimationBuilder != dialogAnim) {
                    event.getController().markNeedsReload();
                }
                event.getController().setAnimation(dialogAnim);
                return PlayState.CONTINUE;
            }
        }
        if (!event.isMoving() || walkAnim.isEmpty()) {
            if (!idleAnim.isEmpty()) {
                event.getController().setAnimation(new AnimationBuilder().loop(idleAnim));
            } else {
                return PlayState.STOP;
            }
        } else {
            event.getController().setAnimation(new AnimationBuilder().loop(walkAnim));
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicateAttack(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    public EntityCustomModel(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        this.noCulling = true;
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "movement", 10, this::predicateMovement));
        data.addAnimationController(new AnimationController<>(this, "attack", 10, this::predicateAttack));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public int tickTimer() {
        return tickCount;
    }

    @Override
    public void tick() {
        super.tick();
    }
}