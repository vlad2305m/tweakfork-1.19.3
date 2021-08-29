package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.WeatherOverrideMode;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelProperties.class)
public class MixinLevelProperties {

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void isRainingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggle.TWEAK_WEATHER_OVERRIDE.getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isRaining());
        }
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    private void isThunderingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggle.TWEAK_WEATHER_OVERRIDE.getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isThundering());
        }
    }

}
