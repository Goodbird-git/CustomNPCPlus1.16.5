package noppes.npcs;

import net.minecraftforge.fml.common.*;
import net.minecraft.entity.projectile.*;
import net.minecraftforge.event.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.event.entity.*;
import noppes.npcs.entity.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraftforge.registries.*;
import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "customnpcs")
@ObjectHolder("customnpcs")
public class CustomEntities
{
    @ObjectHolder("npcpony")
    public static EntityType<? extends CreatureEntity> entityNpcPony;
    @ObjectHolder("npccrystal")
    public static EntityType<? extends CreatureEntity> entityNpcCrystal;
    @ObjectHolder("npcslime")
    public static EntityType<? extends CreatureEntity> entityNpcSlime;
    @ObjectHolder("npcdragon")
    public static EntityType<? extends CreatureEntity> entityNpcDragon;
    @ObjectHolder("npcgolem")
    public static EntityType<? extends CreatureEntity> entityNPCGolem;
    @ObjectHolder("customnpc")
    public static EntityType<? extends CreatureEntity> entityCustomNpc;
    @ObjectHolder("customnpc64x32")
    public static EntityType<? extends CreatureEntity> entityNPC64x32;
    @ObjectHolder("customnpcalex")
    public static EntityType<? extends CreatureEntity> entityNpcAlex;
    @ObjectHolder("customnpcclassic")
    public static EntityType<? extends CreatureEntity> entityNpcClassicPlayer;
    @ObjectHolder("customnpcchairmount")
    public static EntityType<?> entityChairMount;
    @ObjectHolder("customnpcprojectile")
    public static EntityType<? extends ThrowableEntity> entityProjectile;
    private static List<EntityType> types;

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        CustomEntities.types.clear();
        registerNpc(event.getRegistry(), EntityNpcPony.class, "npcpony", EntityNpcPony::new);
        registerNpc(event.getRegistry(), EntityNpcCrystal.class, "npccrystal", EntityNpcCrystal::new);
        registerNpc(event.getRegistry(), EntityNpcSlime.class, "npcslime",EntityNpcSlime::new);
        registerNpc(event.getRegistry(), EntityNpcDragon.class, "npcdragon", EntityNpcDragon::new);
        registerNpc(event.getRegistry(), EntityNPCGolem.class, "npcgolem", EntityNPCGolem::new);
        registerNpc(event.getRegistry(), EntityCustomNpc.class, "customnpc", EntityCustomNpc::new);
        registerNpc(event.getRegistry(), EntityNPC64x32.class, "customnpc64x32", EntityNPC64x32::new);
        registerNpc(event.getRegistry(), EntityNpcAlex.class, "customnpcalex", EntityNpcAlex::new);
        registerNpc(event.getRegistry(), EntityNpcClassicPlayer.class, "customnpcclassic", EntityNpcClassicPlayer::new);
        registerNewentity(event.getRegistry(), EntityChairMount.class, "customnpcchairmount", EntityChairMount::new, 64, 10, false, 0.001f, 0.001f);
        registerNewentity(event.getRegistry(), EntityProjectile.class, "customnpcprojectile", EntityProjectile::new, 64, 20, true, 0.5f, 0.5f);
    }

    @SubscribeEvent
    public static void attribute(final EntityAttributeCreationEvent event) {
        for (final EntityType type : CustomEntities.types) {
            event.put(type, EntityNPCInterface.createMobAttributes().build());
        }
    }

    private static <T extends Entity> void registerNpc(final IForgeRegistry<EntityType<?>> registry, final Class<? extends Entity> c, final String name, final EntityType.IFactory<T> factoryIn) {
        final EntityType.Builder<?> builder = (EntityType.Builder<?>)EntityType.Builder.of((EntityType.IFactory)factoryIn, EntityClassification.CREATURE);
        builder.setTrackingRange(10);
        builder.setUpdateInterval(3);
        builder.setShouldReceiveVelocityUpdates(false);
        builder.clientTrackingRange(10);
        builder.sized(1.0f, 1.0f);
        ResourceLocation registryName = new ResourceLocation("customnpcs", name);
        final EntityType type = builder.build(registryName.toString()).setRegistryName(registryName);
        CustomEntities.types.add(type);
        registry.register(type);
        if (CustomNpcs.FixUpdateFromPre_1_12) {
            registryName = new ResourceLocation("customnpcs." + name);
            registry.register(builder.build(registryName.toString()).setRegistryName(registryName));
        }
    }

    private static <T extends Entity> void registerNewentity(final IForgeRegistry<EntityType<?>> registry, final Class<? extends Entity> c, final String name, final EntityType.IFactory<T> factoryIn, final int range, final int update, final boolean velocity, final float width, final float height) {
        final EntityType.Builder<?> builder = (EntityType.Builder<?>)EntityType.Builder.of((EntityType.IFactory)factoryIn, EntityClassification.MISC);
        builder.setTrackingRange(range);
        builder.setUpdateInterval(update);
        builder.setShouldReceiveVelocityUpdates(velocity);
        builder.sized(width, height);
        builder.clientTrackingRange(4);
        final ResourceLocation registryName = new ResourceLocation("customnpcs", name);
        registry.register(builder.build(registryName.toString()).setRegistryName(registryName));
    }

    static {
        CustomEntities.types = new ArrayList<>();
    }
}
