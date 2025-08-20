package symbolics.division.gik.mixin.compat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.gik.block.CardboardBlock;

@Pseudo
@Mixin(targets = "net.superkat.ziptoit.zipcast.ZipcastManager", remap = false)
public class ZipToItCompat {
    @Inject(
            method = "startWallStick",
            at = @At(value = "INVOKE", target = "Lnet/superkat/ziptoit/duck/ZipcasterPlayer;ziptoit$stickToWall(Lnet/minecraft/util/math/Vec3d;)V")
    )
    private static void breakShit(LivingEntity player, Vec3d pos, boolean sendPackets, CallbackInfo ci) {
        if (player.getWorld().isClient) return;
        for (BlockPos p : BlockPos.iterate(player.getBoundingBox().expand(1))) {
            if (CardboardBlock.soaked(player.getWorld().getBlockState(p))) {
                player.getWorld().breakBlock(p, false, player);
            }
        }
    }
}
