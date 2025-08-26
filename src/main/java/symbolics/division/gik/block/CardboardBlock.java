package symbolics.division.gik.block;

import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import symbolics.division.gik.GIK;
import symbolics.division.gik.compat.AntisoakingAreaComponent;

import java.util.List;

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
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved && soaked(state) && world.getBlockState(pos).isOf(Blocks.AIR)) {
            for (Direction dir : Direction.values()) {
                BlockPos p2 = pos.offset(dir, 1);
                GIK.schedule(() -> {
                    BlockState bs = world.getBlockState(p2);
                    boolean b = soaked(bs);
                    if (b) world.breakBlock(p2, false);
                }, 1);
            }
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return neighborState.getFluidState().isIn(FluidTags.WATER) ? GIK.SOAKED_CARDBOARD.getStateWithProperties(state) : state;
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && state.isOf(GIK.SOAKED_CARDBOARD) || state.isOf(GIK.SOAKED_VERTICAL_CARDBOARD)) {
            if (projectile instanceof SplashPotionEntity) return;
            world.breakBlock(hit.getBlockPos(), false, projectile.getOwner());
            return;
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    public static void soak(World world, BlockPos pos) {
        if (world.isClient || !soakAllowed((ServerWorld) world, pos)) return;
        BlockState state = world.getBlockState(pos);
        if (state.isOf(GIK.CARDBOARD)) {
            world.setBlockState(pos, GIK.SOAKED_CARDBOARD.getStateWithProperties(state));
        } else if (state.isOf(GIK.VERTICAL_CARDBOARD)) {
            world.setBlockState(pos, GIK.SOAKED_VERTICAL_CARDBOARD.getStateWithProperties(state));
        }
    }

    public static boolean soaked(BlockState state) {
        return state.isOf(GIK.SOAKED_CARDBOARD) || state.isOf(GIK.SOAKED_VERTICAL_CARDBOARD);
    }

    public static boolean soakAllowed(ServerWorld world, BlockPos pos) {
        List<Area> areas = AreaSavedData.getServerData(world.getServer()).findTrackedAreasContaining(world, pos.toCenterPos());
        return areas.stream().noneMatch(a -> a.has(AntisoakingAreaComponent.TYPE));
    }
}
