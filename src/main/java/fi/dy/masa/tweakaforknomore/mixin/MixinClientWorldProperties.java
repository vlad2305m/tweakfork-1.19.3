package fi.dy.masa.tweakaforknomore.mixin;

import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.tweakaforknomore.config.Configs;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.Properties.class)
public class MixinClientWorldProperties {
    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void timeOfDayOverride(CallbackInfoReturnable<Long> ci) {
        if (FeatureToggleI.TWEAK_DAY_CYCLE_OVERRIDE().getBooleanValue()) {
            ci.setReturnValue((long) Configs.Generic.DAY_CYCLE_OVERRIDE_TIME.getIntegerValue());
        }
    }
}
