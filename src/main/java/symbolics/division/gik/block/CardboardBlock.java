package symbolics.division.gik.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import symbolics.division.gik.GIK;

public class CardboardBlock extends SlabBlock {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;

    public CardboardBlock(Settings settings) {
        super(settings.sounds(BlockSoundGroup.MOSS_BLOCK));
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Z));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AXIS);
    }

    protected Block getSoakedVersion() {
        return GIK.SOAKED_CARDBOARD;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        state = state.with(AXIS, ctx.getHorizontalPlayerFacing().getAxis());
        for (Direction dir : Direction.values()) {
            if (ctx.getWorld().getBlockState(ctx.getBlockPos().add(dir.getVector())).getFluidState().isIn(FluidTags.WATER)) {
                return getSoakedVersion().getStateWithProperties(state);
            }
        }
        return state;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return neighborState.getFluidState().isIn(FluidTags.WATER) ? GIK.SOAKED_CARDBOARD.getStateWithProperties(state) : state;
    }
}
