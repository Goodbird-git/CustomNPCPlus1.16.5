package noppes.npcs.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.common.ToolType;
import noppes.npcs.constants.*;
import noppes.npcs.*;
import net.minecraft.world.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.pathfinding.*;
import net.minecraft.block.*;
import net.minecraft.state.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.tileentity.*;
import noppes.npcs.blocks.tiles.*;
import net.minecraft.network.*;

public class BlockCarpentryBench extends BlockInterface
{
    public static final IntegerProperty ROTATION;

    public BlockCarpentryBench() {
        super(AbstractBlock.Properties.of(Material.WOOD).strength(0.5F).sound(SoundType.WOOD));
    }

    public ActionResultType use(final BlockState state, final World level, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockRayTraceResult ray) {
        if (!level.isClientSide) {
            NoppesUtilServer.openContainerGui((ServerPlayerEntity)player, EnumGuiType.PlayerAnvil, buffer -> buffer.writeBlockPos(pos));
        }
        return ActionResultType.SUCCESS;
    }

    public VoxelShape getOcclusionShape(final BlockState p_196247_1_, final IBlockReader p_196247_2_, final BlockPos p_196247_3_) {
        return VoxelShapes.empty();
    }

    public boolean isPathfindable(final BlockState p_196266_1_, final IBlockReader p_196266_2_, final BlockPos p_196266_3_, final PathType p_196266_4_) {
        return false;
    }

    protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { (Property)BlockCarpentryBench.ROTATION });
    }

    public BlockState getStateForPlacement(final BlockItemUseContext context) {
        final int var6 = MathHelper.floor(context.getPlayer().yRot / 90.0f + 0.5) & 0x3;
        return (BlockState)this.defaultBlockState().setValue((Property)BlockCarpentryBench.ROTATION, (Comparable)var6);
    }

    public TileEntity newBlockEntity(final IBlockReader worldIn) {
        return new TileBlockAnvil();
    }

    static {
        ROTATION = IntegerProperty.create("rotation", 0, 3);
    }
}
