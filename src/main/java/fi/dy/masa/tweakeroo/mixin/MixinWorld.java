package fi.dy.masa.tweakeroo.mixin;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.WeatherOverrideMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void disableBlockEntityTicking(CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_TILE_ENTITY_TICKING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "tickEntity(Ljava/util/function/Consumer;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void preventEntityTicking(Consumer<T> consumer, T entityIn, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_ENTITY_TICKING.getBooleanValue() && (entityIn instanceof PlayerEntity) == false)
        {
            ci.cancel();
        }
    }

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void isRainingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggle.TWEAK_WEATHER_OVERRIDE.getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isRaining());
        }
    }

    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
    private void getRainGradientOverride(float delta, CallbackInfoReturnable<Float> ci) {
        if (FeatureToggle.TWEAK_WEATHER_OVERRIDE.getBooleanValue()) {
            final boolean isRaining = ((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isRaining();
            ci.setReturnValue(isRaining ? 1F : 0F);
        }
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    private void isThunderingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggle.TWEAK_WEATHER_OVERRIDE.getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isThundering());
        }
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
    private void getThunderGradientOverride(float delta, CallbackInfoReturnable<Float> ci) {
        if (FeatureToggle.TWEAK_WEATHER_OVERRIDE.getBooleanValue()) {
            final boolean isThundering = ((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isThundering();
            ci.setReturnValue(isThundering ? 1F : 0F);
        }
    }
}
