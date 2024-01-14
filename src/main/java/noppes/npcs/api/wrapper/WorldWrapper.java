package noppes.npcs.api.wrapper;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.EventHooks;
import noppes.npcs.api.*;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPlaySound;

import java.util.*;
import java.util.function.Predicate;

public class WorldWrapper implements IWorld
{
    public static Map<String, Object> tempData;
    public ServerWorld level;
    public IDimension dimension;
    private IData tempdata;
    private IData storeddata;

    private WorldWrapper(final World level) {
        this.tempdata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                WorldWrapper.tempData.put(key, value);
            }

            @Override
            public Object get(final String key) {
                return WorldWrapper.tempData.get(key);
            }

            @Override
            public void remove(final String key) {
                WorldWrapper.tempData.remove(key);
            }

            @Override
            public boolean has(final String key) {
                return WorldWrapper.tempData.containsKey(key);
            }

            @Override
            public void clear() {
                WorldWrapper.tempData.clear();
            }

            @Override
            public String[] getKeys() {
                return WorldWrapper.tempData.keySet().toArray(new String[WorldWrapper.tempData.size()]);
            }
        };
        this.storeddata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                final CompoundNBT compound = ScriptController.Instance.compound;
                if (value instanceof Number) {
                    compound.putDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    compound.putString(key, (String)value);
                }
                ScriptController.Instance.shouldSave = true;
            }

            @Override
            public Object get(final String key) {
                final CompoundNBT compound = ScriptController.Instance.compound;
                if (!compound.contains(key)) {
                    return null;
                }
                final INBT base = compound.get(key);
                if (base instanceof NumberNBT) {
                    return ((NumberNBT)base).getAsDouble();
                }
                return base.getAsString();
            }

            @Override
            public void remove(final String key) {
                ScriptController.Instance.compound.remove(key);
                ScriptController.Instance.shouldSave = true;
            }

            @Override
            public boolean has(final String key) {
                return ScriptController.Instance.compound.contains(key);
            }

            @Override
            public void clear() {
                ScriptController.Instance.compound = new CompoundNBT();
                ScriptController.Instance.shouldSave = true;
            }

            @Override
            public String[] getKeys() {
                return ScriptController.Instance.compound.getAllKeys().toArray(new String[ScriptController.Instance.compound.getAllKeys().size()]);
            }
        };
        this.level = (ServerWorld)level;
        this.dimension = new DimensionWrapper(level.dimension().location(), level.dimensionType());
    }

    @Override
    public ServerWorld getMCWorld() {
        return this.level;
    }

    @Override
    public IEntity[] getNearbyEntities(final int x, final int y, final int z, final int range, final int type) {
        return this.getNearbyEntities(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
    }

    @Override
    public IEntity[] getNearbyEntities(final IPos pos, final int range, final int type) {
        final AxisAlignedBB bb = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).move(pos.getMCBlockPos()).inflate((double)range, (double)range, (double)range);
        final List<Entity> entities = (List<Entity>)this.level.getEntitiesOfClass(this.getClassForType(type), bb);
        final List<IEntity> list = new ArrayList<>();
        for (final Entity living : entities) {
            list.add(NpcAPI.Instance().getIEntity(living));
        }
        return list.toArray(new IEntity[list.size()]);
    }

    @Override
    public IEntity[] getAllEntities(final int type) {
        final List<Entity> entities = this.getEntities(this.getClassForType(type), EntityPredicates.NO_SPECTATORS);
        final List<IEntity> list = new ArrayList<>();
        for (final Entity living : entities) {
            list.add(NpcAPI.Instance().getIEntity(living));
        }
        return list.toArray(new IEntity[list.size()]);
    }

    public List<Entity> getEntities(final Class<?> entityTypeIn, final Predicate<? super Entity> predicateIn) {
        final List<Entity> list = Lists.newArrayList();
        final ServerChunkProvider serverchunkprovider = this.level.getChunkSource();
        for (final Entity entity : this.level.getAllEntities()) {
            if (entityTypeIn.isAssignableFrom(entity.getClass()) && serverchunkprovider.hasChunk(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4) && predicateIn.test(entity)) {
                list.add(entity);
            }
        }
        return list;
    }

    @Override
    public IEntity getClosestEntity(final int x, final int y, final int z, final int range, final int type) {
        return this.getClosestEntity(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
    }

    @Override
    public IEntity getClosestEntity(final IPos pos, final int range, final int type) {
        final AxisAlignedBB bb = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).move(pos.getMCBlockPos()).inflate((double)range, (double)range, (double)range);
        final List<Entity> entities = (List<Entity>)this.level.getEntitiesOfClass(this.getClassForType(type), bb);
        double distance = range * range * range;
        Entity entity = null;
        for (final Entity e : entities) {
            final double r = pos.getMCBlockPos().distSqr(e.blockPosition());
            if (entity == null) {
                distance = r;
                entity = e;
            }
            else {
                if (r >= distance) {
                    continue;
                }
                distance = r;
                entity = e;
            }
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity getEntity(final String uuid) {
        try {
            final UUID id = UUID.fromString(uuid);
            Entity e = this.level.getEntity(id);
            if (e == null) {
                e = this.level.getPlayerByUUID(id);
            }
            if (e == null) {
                return null;
            }
            return NpcAPI.Instance().getIEntity(e);
        }
        catch (Exception e2) {
            throw new CustomNPCsException("Given uuid was invalid " + uuid);
        }
    }

    @Override
    public IEntity createEntityFromNBT(final INbt nbt) {
        final Entity entity = EntityType.create(nbt.getMCNBT(), this.level).orElse(null);
        if (entity == null) {
            throw new CustomNPCsException("Failed to create an entity from given NBT");
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity createEntity(final String id) {
        final ResourceLocation resource = new ResourceLocation(id);
        final EntityType type = ForgeRegistries.ENTITIES.getValue(resource);
        final Entity entity = type.create(this.level);
        if (entity == null) {
            throw new CustomNPCsException("Failed to create an entity from given id: " + id);
        }
        entity.setPos(0.0, 1.0, 0.0);
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IPlayer getPlayer(final String name) {
        for (final PlayerEntity entityplayer : this.level.players()) {
            if (name.equals(entityplayer.getName().getString())) {
                return (IPlayer)NpcAPI.Instance().getIEntity(entityplayer);
            }
        }
        return null;
    }

    private Class getClassForType(final int type) {
        if (type == -1) {
            return Entity.class;
        }
        if (type == 5) {
            return LivingEntity.class;
        }
        if (type == 1) {
            return PlayerEntity.class;
        }
        if (type == 4) {
            return AnimalEntity.class;
        }
        if (type == 3) {
            return MonsterEntity.class;
        }
        if (type == 2) {
            return EntityNPCInterface.class;
        }
        if (type == 6) {
            return ItemEntity.class;
        }
        if (type == 7) {
            return EntityProjectile.class;
        }
        if (type == 11) {
            return ThrowableEntity.class;
        }
        if (type == 10) {
            return AbstractArrowEntity.class;
        }
        if (type == 8) {
            return PixelmonHelper.getPixelmonClass();
        }
        if (type == 9) {
            return VillagerEntity.class;
        }
        return Entity.class;
    }

    @Override
    public long getTime() {
        return this.level.getDayTime();
    }

    @Override
    public void setTime(final long time) {
        this.level.setDayTime(time);
    }

    @Override
    public long getTotalTime() {
        return this.level.getGameTime();
    }

    @Override
    public IBlock getBlock(final int x, final int y, final int z) {
        return NpcAPI.Instance().getIBlock(this.level, new BlockPos(x, y, z));
    }

    @Override
    public IBlock getBlock(final IPos pos) {
        return NpcAPI.Instance().getIBlock(this.level, pos.getMCBlockPos());
    }

    public boolean isChunkLoaded(final int x, final int z) {
        return this.level.getChunkSource().hasChunk(x >> 4, z >> 4);
    }

    @Override
    public void setBlock(final int x, final int y, final int z, final String name, final int meta) {
        this.setBlock(NpcAPI.Instance().getIPos(x, y, z), name);
    }

    @Override
    public IBlock setBlock(final IPos pos, final String name) {
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        if (block == null) {
            throw new CustomNPCsException("There is no such block: %s", name);
        }
        this.level.setBlock(pos.getMCBlockPos(), block.defaultBlockState(), 2);
        return NpcAPI.Instance().getIBlock(this.level, pos.getMCBlockPos());
    }

    @Override
    public void removeBlock(final int x, final int y, final int z) {
        this.level.removeBlock(new BlockPos(x, y, z), false);
    }

    @Override
    public void removeBlock(final IPos pos) {
        this.level.removeBlock(pos.getMCBlockPos(), false);
    }

    @Override
    public float getLightValue(final int x, final int y, final int z) {
        return this.level.getLightEmission(new BlockPos(x, y, z)) / 16.0f;
    }

    @Override
    public IBlock getSpawnPoint() {
        BlockPos pos = this.level.getSharedSpawnPos();
        return NpcAPI.Instance().getIBlock(this.level, pos);
    }

    @Override
    public void setSpawnPoint(final IBlock block) {
        final ISpawnWorldInfo info = (ISpawnWorldInfo)this.level.getLevelData();
        info.setSpawn(new BlockPos(block.getX(), block.getY(), block.getZ()), 0.0f);
    }

    @Override
    public boolean isDay() {
        return this.level.getDayTime() % 24000L < 12000L;
    }

    @Override
    public boolean isRaining() {
        return this.level.getLevelData().isRaining();
    }

    @Override
    public void setRaining(final boolean bo) {
        final IServerWorldInfo data = (IServerWorldInfo)this.level.getLevelData();
        if (bo) {
            data.setRaining(true);
            data.setRainTime(120000000);
        }
        else {
            data.setRaining(false);
            data.setRainTime(0);
        }
    }

    @Override
    public void thunderStrike(final double x, final double y, final double z) {
        final LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(this.level);
        bolt.moveTo(x, y, z);
        bolt.setVisualOnly(false);
        this.level.addFreshEntity(bolt);
    }

    @Override
    public void spawnParticle(final String particle, final double x, final double y, final double z, final double dx, final double dy, final double dz, final double speed, final int count) {
        final ParticleType type = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particle));
        if (type == null) {
            throw new CustomNPCsException("Unknown particle type: " + particle);
        }
        this.level.sendParticles((IParticleData)type, x, y, z, count, dx, dy, dz, speed);
    }

    @Override
    public IData getTempdata() {
        return this.tempdata;
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public IItemStack createItem(final String name, final int size) {
        final Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
        if (item == null) {
            throw new CustomNPCsException("Unknown item id: " + name);
        }
        return NpcAPI.Instance().getIItemStack(new ItemStack(item, size));
    }

    @Override
    public IItemStack createItemFromNbt(final INbt nbt) {
        final ItemStack item = ItemStack.of(nbt.getMCNBT());
        if (item.isEmpty()) {
            throw new CustomNPCsException("Failed to create an item from given NBT");
        }
        return NpcAPI.Instance().getIItemStack(item);
    }

    @Override
    public void explode(final double x, final double y, final double z, final float range, final boolean fire, final boolean grief) {
        this.level.explode(null, x, y, z, range, fire, grief ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
    }

    @Override
    public IPlayer[] getAllPlayers() {
        final List<ServerPlayerEntity> list = this.level.getServer().getPlayerList().getPlayers();
        final IPlayer[] arr = new IPlayer[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            arr[i] = (IPlayer)NpcAPI.Instance().getIEntity(list.get(i));
        }
        return arr;
    }

    @Override
    public String getBiomeName(final int x, final int z) {
        return this.level.getBiome(new BlockPos(x, 0, z)).getRegistryName().toString();
    }

    @Override
    public IEntity spawnClone(final double x, final double y, final double z, final int tab, final String name) {
        return NpcAPI.Instance().getClones().spawn(x, y, z, tab, name, this);
    }

    @Override
    public void spawnEntity(final IEntity entity) {
        if (entity == null) {
            throw new CustomNPCsException("Entity given was null");
        }
        final Entity e = entity.getMCEntity();
        if (this.level.getEntity(e.getUUID()) != null) {
            throw new CustomNPCsException("Entity with this UUID already exists");
        }
        e.setPos(e.getX(), e.getY(), e.getZ());
        this.level.addFreshEntity(e);
    }

    @Override
    public IEntity getClone(final int tab, final String name) {
        return NpcAPI.Instance().getClones().get(tab, name, this);
    }

    @Override
    public IScoreboard getScoreboard() {
        return new ScoreboardWrapper(this.level.getServer());
    }

    @Override
    public void broadcast(final String message) {
        final StringTextComponent text = new StringTextComponent(message);
        for (final PlayerEntity p : this.level.getPlayers(e -> true)) {
            p.sendMessage(text, Util.NIL_UUID);
        }
    }

    @Override
    public int getRedstonePower(final int x, final int y, final int z) {
        return this.level.getDirectSignalTo(new BlockPos(x, y, z));
    }

    @Deprecated
    public static WorldWrapper createNew(final ServerWorld level) {
        return new WorldWrapper(level);
    }

    @Override
    public IDimension getDimension() {
        return this.dimension;
    }

    @Override
    public String getName() {
        return ((IServerWorldInfo)this.level.getLevelData()).getLevelName();
    }

    @Override
    public BlockPos getMCBlockPos(final int x, final int y, final int z) {
        return new BlockPos(x, y, z);
    }

    @Override
    public void playSoundAt(final IPos pos, final String sound, final float volume, final float pitch) {
        final BlockPos bp = pos.getMCBlockPos();
        Packets.sendNearby(this.level, bp, 16, new PacketPlaySound(sound, bp, volume, pitch));
    }

    @Override
    public void trigger(final int id, final Object... arguments) {
        EventHooks.onScriptTriggerEvent(ScriptController.Instance.forgeScripts, id, this, BlockPosWrapper.ZERO, null, arguments);
    }

    static {
        WorldWrapper.tempData = new HashMap<>();
    }
}
