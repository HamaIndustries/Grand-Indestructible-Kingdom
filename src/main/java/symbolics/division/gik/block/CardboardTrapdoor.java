package symbolics.division.gik.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import symbolics.division.gik.GIK;

public class CardboardTrapdoor extends TrapdoorBlock implements Soakable {
    public static final BooleanProperty SOAKED = Properties.BERRIES;

    public CardboardTrapdoor(Settings settings, boolean soaked) {
        super(BlockSetType.OAK, settings);
        this.setDefaultState(this.getDefaultState().with(SOAKED, soaked));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SOAKED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (!Soakable.soakAllowed(ctx.getWorld(), ctx.getBlockPos())) return state;
        for (Direction dir : Direction.values()) {
            if (ctx.getWorld().getBlockState(ctx.getBlockPos().add(dir.getVector())).getFluidState().isIn(FluidTags.WATER)) {
                return state.with(SOAKED, true);
            }
        }
        return state;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        Soakable.breakNearby(state, world, pos, moved);
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!Soakable.soakAllowed(world, pos)) return state;
        return neighborState.getFluidState().isIn(FluidTags.WATER) ? getSoakedVersion(state) : state;
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && Soakable.soaked(state)) {
            if (projectile instanceof SplashPotionEntity) return;
            world.breakBlock(hit.getBlockPos(), false, projectile.getOwner());
            return;
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    public boolean isSoaked(BlockState state) {
        return state.get(SOAKED, true);
    }

    @Override
    public BlockState getSoakedVersion(BlockState state) {
        return GIK.SOAKED_CARDBOARD_TRAPDOOR.getStateWithProperties(state);
    }
}
