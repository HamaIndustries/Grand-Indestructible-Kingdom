package symbolics.division.gik.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class CardboardBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    public static final BooleanProperty SINISTER = Properties.OMINOUS;


    public CardboardBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS, SINISTER);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction.Axis axis = ctx.getSide().getAxis();
        Vec3d delta = ctx.getHitPos().subtract(ctx.getBlockPos().toCenterPos());
        boolean swap = switch (axis) {
            case X -> Math.abs(delta.y) > Math.abs(delta.z);
            case Z -> Math.abs(delta.y) > Math.abs(delta.x);
            case Y -> Math.abs(delta.z) > Math.abs(delta.x);
        };
        return getDefaultState().with(AXIS, axis).with(SINISTER, swap);
    }
}
