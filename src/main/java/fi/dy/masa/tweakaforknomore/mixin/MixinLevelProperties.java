package fi.dy.masa.tweakaforknomore.mixin;

import fi.dy.masa.tweakaforknomore.config.Configs;
import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import fi.dy.masa.tweakaforknomore.util.WeatherOverrideMode;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelProperties.class)
public class MixinLevelProperties {

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void isRainingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggleI.TWEAK_WEATHER_OVERRIDE().getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isRaining());
        }
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    private void isThunderingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggleI.TWEAK_WEATHER_OVERRIDE().getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isThundering());
        }
    }

}
