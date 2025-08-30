package symbolics.division.gik.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import symbolics.division.gik.GIK;

public class VerticalCardboardBlock extends CardboardBlock {
    protected static final VoxelShape NORTH = VerticalCardboardBlock.createCuboidShape(0, 0, 0, 16, 16, 8);
    protected static final VoxelShape SOUTH = VerticalCardboardBlock.createCuboidShape(0, 0, 8, 16, 16, 16);
    protected static final VoxelShape EAST = VerticalCardboardBlock.createCuboidShape(8, 0, 0, 16, 16, 16);
    protected static final VoxelShape WEST = VerticalCardboardBlock.createCuboidShape(0, 0, 0, 8, 16, 16);

    public VerticalCardboardBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos p = BlockPos.ofFloored(ctx.getHitPos().add(ctx.getSide().getDoubleVector().multiply(0.01)));
        BlockState oldState = ctx.getWorld().getBlockState(p);

        if (oldState.isOf(this)) {
            return oldState.with(TYPE, SlabType.DOUBLE);
        }

        BlockState state = super.getPlacementState(ctx);
        Vec3d center = ctx.getBlockPos().toCenterPos();
        state = state.with(AXIS, ctx.getHorizontalPlayerFacing().rotateYClockwise().getAxis());

        if (state.get(AXIS) == Direction.Axis.X) {
            return ctx.getHitPos().getX() > center.x
                    ? state.with(TYPE, SlabType.TOP)
                    : state.with(TYPE, SlabType.BOTTOM);
        } else {
            return ctx.getHitPos().getZ() < center.z
                    ? state.with(TYPE, SlabType.TOP)
                    : state.with(TYPE, SlabType.BOTTOM);
        }
    }

    @Override
    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = state.get(TYPE);
        if (slabType != SlabType.DOUBLE && itemStack.isOf(this.asItem())) {
            return new Box(context.getBlockPos()).contains(context.getHitPos().subtract(context.getSide().getDoubleVector().multiply(-0.01)));
        } else {
            return false;
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(TYPE)) {
            case DOUBLE -> VoxelShapes.fullCube();
            case TOP -> switch (state.get(AXIS)) {
                case X -> EAST;
                default -> NORTH;
            };
            case BOTTOM -> switch (state.get(AXIS)) {
                case X -> WEST;
                default -> SOUTH;
            };
        };
    }


    @Override
    public BlockState getSoakedVersion(BlockState state) {
        return GIK.SOAKED_VERTICAL_CARDBOARD.getStateWithProperties(state);
    }
}
