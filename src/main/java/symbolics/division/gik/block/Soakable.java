package symbolics.division.gik.block;

import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.data.AreaClientData;
import dev.doublekekse.area_lib.data.AreaSavedData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import symbolics.division.gik.GIK;
import symbolics.division.gik.compat.AntisoakingAreaComponent;

import java.util.List;

public interface Soakable {
    boolean isSoaked(BlockState state);

    BlockState getSoakedVersion(BlockState state);

    static void soak(World world, BlockPos pos) {
        if (world.isClient) return;
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof Soakable soakable && soakAllowed(world, pos)) {
            world.setBlockState(pos, soakable.getSoakedVersion(state));
        }
    }

    static boolean soaked(BlockState state) {
        return state.isIn(GIK.SOAKED);
    }

    static void breakNearby(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved && Soakable.soaked(state) && world.getBlockState(pos).isOf(Blocks.AIR)) {
            for (Direction dir : Direction.values()) {
                BlockPos p2 = pos.offset(dir, 1);
                GIK.schedule(() -> {
                    if (Soakable.soaked(world.getBlockState(p2))) world.breakBlock(p2, false);
                }, 1);
            }
        }
    }

    static boolean soakAllowed(WorldView worldView, BlockPos pos) {
        if (!(worldView instanceof World world)) return false;
        AreaSavedData area;
        if (world.isClient()) {
            area = AreaClientData.getClientLevelData();
        } else {
            area = AreaSavedData.getServerData(((ServerWorld) world).getServer());
        }
        List<Area> areas = area.findTrackedAreasContaining(world, pos.toCenterPos());
        return areas.stream().anyMatch(a -> a.has(AntisoakingAreaComponent.TYPE));
    }
}
