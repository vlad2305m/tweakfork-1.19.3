package fi.dy.masa.tweakaforknomore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakaforknomore.config.Configs;

@Mixin(net.minecraft.entity.Entity.class)
public abstract class MixinEntity
{
    @Shadow public net.minecraft.world.World world;

    @Inject(method = "setOnFireFromLava", at = @At("HEAD"), cancellable = true)
    private void injectLavaDestroyFix(CallbackInfo ci) {
        if (Configs.Fixes.LAVA_DESTROY_FIX.getBooleanValue()) {
            if (this.world.isClient) {
                ci.cancel();
            }
        }
    }
}
