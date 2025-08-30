package symbolics.division.gik.compat;

import net.minecraft.block.BlockState;
import net.superkat.ziptoit.api.ZipcasterEvents;
import symbolics.division.gik.block.Soakable;

public class ZipCasterCompat {
    public static void init() {
        ZipcasterEvents.WALL_STICK_START.register((serverPlayerEntity, teleportPos, blockPos) -> {
            BlockState state = serverPlayerEntity.getWorld().getBlockState(blockPos);
            if (Soakable.soaked(state)) {
                serverPlayerEntity.getWorld().breakBlock(blockPos, false);
            }
        });
    }
}
