package noppes.npcs.api.wrapper;

import noppes.npcs.api.IWorld;
import noppes.npcs.api.block.*;
import net.minecraft.tileentity.*;
import noppes.npcs.blocks.tiles.*;
import noppes.npcs.api.entity.data.*;
import net.minecraft.nbt.*;
import net.minecraft.world.server.*;
import net.minecraft.state.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.inventory.*;
import net.minecraftforge.registries.*;
import noppes.npcs.blocks.*;
import net.minecraftforge.fluids.*;
import noppes.npcs.api.*;
import noppes.npcs.entity.*;
import net.minecraft.util.math.vector.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import noppes.npcs.shared.common.util.*;

public class BlockWrapper implements IBlock
{
    private static final Map<String, BlockWrapper> blockCache;
    protected final IWorld level;
    protected final Block block;
    protected final BlockPos pos;
    protected final BlockPosWrapper bPos;
    protected TileEntity tile;
    protected TileNpcEntity storage;
    private final IData tempdata;
    private final IData storeddata;

    protected BlockWrapper(final World level, final Block block, final BlockPos pos) {
        this.tempdata = new IData() {
            @Override
            public void remove(final String key) {
                if (BlockWrapper.this.storage == null) {
                    return;
                }
                BlockWrapper.this.storage.tempData.remove(key);
            }

            @Override
            public void put(final String key, final Object value) {
                if (BlockWrapper.this.storage == null) {
                    return;
                }
                BlockWrapper.this.storage.tempData.put(key, value);
            }

            @Override
            public boolean has(final String key) {
                return BlockWrapper.this.storage != null && BlockWrapper.this.storage.tempData.containsKey(key);
            }

            @Override
            public Object get(final String key) {
                if (BlockWrapper.this.storage == null) {
                    return null;
                }
                return BlockWrapper.this.storage.tempData.get(key);
            }

            @Override
            public void clear() {
                if (BlockWrapper.this.storage == null) {
                    return;
                }
                BlockWrapper.this.storage.tempData.clear();
            }

            @Override
            public String[] getKeys() {
                return BlockWrapper.this.storage.tempData.keySet().toArray(new String[BlockWrapper.this.storage.tempData.size()]);
            }
        };
        this.storeddata = new IData() {
            @Override
            public void put(final String key, final Object value) {
                final CompoundNBT compound = this.getNBT();
                if (compound == null) {
                    return;
                }
                if (value instanceof Number) {
                    compound.putDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    compound.putString(key, (String)value);
                }
            }

            @Override
            public Object get(final String key) {
                final CompoundNBT compound = this.getNBT();
                if (compound == null) {
                    return null;
                }
                if (!compound.contains(key)) {
                    return null;
                }
                final INBT base = compound.get(key);
                if (base instanceof NumberNBT) {
                    return ((NumberNBT)base).getAsDouble();
                }
                return (base).getAsString();
            }

            @Override
            public void remove(final String key) {
                final CompoundNBT compound = this.getNBT();
                if (compound == null) {
                    return;
                }
                compound.remove(key);
            }

            @Override
            public boolean has(final String key) {
                final CompoundNBT compound = this.getNBT();
                return compound != null && compound.contains(key);
            }

            @Override
            public void clear() {
                if (BlockWrapper.this.tile == null) {
                    return;
                }
                BlockWrapper.this.tile.getTileData().put("CustomNPCsData", new CompoundNBT());
            }

            private CompoundNBT getNBT() {
                if (BlockWrapper.this.tile == null) {
                    return null;
                }
                final CompoundNBT compound = BlockWrapper.this.tile.getTileData().getCompound("CustomNPCsData");
                if (compound.isEmpty() && !BlockWrapper.this.tile.getTileData().contains("CustomNPCsData")) {
                    BlockWrapper.this.tile.getTileData().put("CustomNPCsData", compound);
                }
                return compound;
            }

            @Override
            public String[] getKeys() {
                final CompoundNBT compound = this.getNBT();
                if (compound == null) {
                    return new String[0];
                }
                return compound.getAllKeys().toArray(new String[compound.getAllKeys().size()]);
            }
        };
        this.level = NpcAPI.Instance().getIWorld((ServerWorld)level);
        this.block = block;
        this.pos = pos;
        this.bPos = new BlockPosWrapper(pos);
        this.setTile(level.getBlockEntity(pos));
    }

    @Override
    public int getX() {
        return this.pos.getX();
    }

    @Override
    public int getY() {
        return this.pos.getY();
    }

    @Override
    public int getZ() {
        return this.pos.getZ();
    }

    @Override
    public IPos getPos() {
        return this.bPos;
    }

    @Override
    public Object getProperty(final String name) {
        final BlockState state = this.getMCBlockState();
        for (final Property p : state.getProperties()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return state.getValue(p);
            }
        }
        throw new CustomNPCsException("Unknown property: " + name);
    }

    @Override
    public void setProperty(final String name, final Object val) {
        if (!(val instanceof Comparable)) {
            throw new CustomNPCsException("Not a valid property value: " + val);
        }
        final BlockState state = this.getMCBlockState();
        for (final Property<?> p : state.getProperties()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return;
            }
        }
        throw new CustomNPCsException("Unknown property: " + name);
    }

    @Override
    public String[] getProperties() {
        final Collection<Property<?>> props = this.getMCBlockState().getProperties();
        final List<String> list = new ArrayList<>();
        for (final Property prop : props) {
            list.add(prop.getName());
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void remove() {
        this.level.getMCWorld().removeBlock(this.pos, false);
    }

    @Override
    public boolean isRemoved() {
        final BlockState state = this.level.getMCWorld().getBlockState(this.pos);
        return state.getBlock() != this.block;
    }

    @Override
    public boolean isAir() {
        return this.block.isAir(this.level.getMCWorld().getBlockState(this.pos), this.level.getMCWorld(), this.pos);
    }

    @Override
    public BlockWrapper setBlock(final String name) {
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        if (block == null) {
            return this;
        }
        this.level.getMCWorld().setBlock(this.pos, block.defaultBlockState(), 2);
        return new BlockWrapper(this.level.getMCWorld(), block, this.pos);
    }

    @Override
    public BlockWrapper setBlock(final IBlock block) {
        this.level.getMCWorld().setBlock(this.pos, block.getMCBlock().defaultBlockState(), 2);
        return new BlockWrapper(this.level.getMCWorld(), block.getMCBlock(), this.pos);
    }

    @Override
    public boolean isContainer() {
        return this.tile != null && this.tile instanceof IInventory && ((IInventory)this.tile).getContainerSize() > 0;
    }

    @Override
    public IContainer getContainer() {
        if (!this.isContainer()) {
            throw new CustomNPCsException("This block is not a container");
        }
        return NpcAPI.Instance().getIContainer((IInventory)this.tile);
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
    public String getName() {
        return ForgeRegistries.BLOCKS.getKey(this.block).toString();
    }

    @Override
    public String getDisplayName() {
        if (this.tile == null || !(this.tile instanceof INameable)) {
            return this.getName();
        }
        return ((INameable)this.tile).getDisplayName().getString();
    }

    @Override
    public IWorld getWorld() {
        return this.level;
    }

    @Override
    public Block getMCBlock() {
        return this.block;
    }

    @Deprecated
    public static IBlock createNew(final World level, final BlockPos pos, final BlockState state) {
        final Block block = state.getBlock();
        final String key = state.toString() + pos.toString();
        BlockWrapper b = BlockWrapper.blockCache.get(key);
        if (b != null) {
            b.setTile(level.getBlockEntity(pos));
            return b;
        }
        if (block instanceof BlockScripted) {
            b = new BlockScriptedWrapper(level, block, pos);
        }
        else if (block instanceof BlockScriptedDoor) {
            b = new BlockScriptedDoorWrapper(level, block, pos);
        }
        else if (block instanceof IFluidBlock) {
            b = new BlockFluidContainerWrapper(level, block, pos);
        }
        else {
            b = new BlockWrapper(level, block, pos);
        }
        BlockWrapper.blockCache.put(key, b);
        return b;
    }

    public static void clearCache() {
        BlockWrapper.blockCache.clear();
    }

    @Override
    public boolean hasTileEntity() {
        return this.tile != null;
    }

    protected void setTile(final TileEntity tile) {
        this.tile = tile;
        if (tile instanceof TileNpcEntity) {
            this.storage = (TileNpcEntity)tile;
        }
    }

    @Override
    public INbt getBlockEntityNBT() {
        final CompoundNBT compound = new CompoundNBT();
        this.tile.save(compound);
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public void setTileEntityNBT(final INbt nbt) {
        this.tile.load(this.getMCBlockState(), nbt.getMCNBT());
        this.tile.setChanged();
        final BlockState state = this.level.getMCWorld().getBlockState(this.pos);
        this.level.getMCWorld().sendBlockUpdated(this.pos, state, state, 3);
    }

    @Override
    public TileEntity getMCTileEntity() {
        return this.tile;
    }

    @Override
    public BlockState getMCBlockState() {
        return this.level.getMCWorld().getBlockState(this.pos);
    }

    @Override
    public void blockEvent(final int type, final int data) {
        this.level.getMCWorld().blockEvent(this.pos, this.getMCBlock(), type, data);
    }

    @Override
    public void interact(final int side) {
        final PlayerEntity player = EntityNPCInterface.GenericPlayer;
        final World w = this.level.getMCWorld();
        player.setLevel(w);
        player.setPos(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        this.getMCBlockState().use(w, EntityNPCInterface.CommandPlayer, Hand.MAIN_HAND, new BlockRayTraceResult(Vector3d.ZERO, Direction.from3DDataValue(side), this.pos, true));
    }

    static {
        blockCache = new LRUHashMap<>(400);
    }
}
