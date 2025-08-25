package symbolics.division.gik.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.gik.compat.ZipCasterCompat;

@Pseudo
@Mixin(targets = "net.superkat.ziptoit.ZipToIt")
public class ZipToItCompat {
    @Inject(
            method = "onInitialize",
            at = @At("TAIL")
    )
    public void compat(CallbackInfo ci) {
        ZipCasterCompat.init();
    }
}
