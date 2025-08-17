package symbolics.division.gik.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.gik.block.CardboardBlock;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
    @Inject(
            method = "explodeWaterPotion",
            at = @At("TAIL")
    )
    public void wetten(ServerWorld world, CallbackInfo ci, @Local(ordinal = 0) Box box) {
        for (BlockPos pos : BlockPos.iterate(box)) {
            CardboardBlock.soak(world, pos);
        }
    }
}
