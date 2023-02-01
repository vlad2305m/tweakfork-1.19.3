package fi.dy.masa.tweakaforknomore.mixin;

import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.tweakaforknomore.config.Configs;
import fi.dy.masa.tweakaforknomore.tweaks.RenderTweaks;
import fi.dy.masa.tweakaforknomore.util.WeatherOverrideMode;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Shadow @Final
    public boolean isClient;

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void isRainingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggleI.TWEAK_WEATHER_OVERRIDE().getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isRaining());
        }
    }

    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
    private void getRainGradientOverride(float delta, CallbackInfoReturnable<Float> ci) {
        if (FeatureToggleI.TWEAK_WEATHER_OVERRIDE().getBooleanValue()) {
            final boolean isRaining = ((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isRaining();
            ci.setReturnValue(isRaining ? 1F : 0F);
        }
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    private void isThunderingOverride(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggleI.TWEAK_WEATHER_OVERRIDE().getBooleanValue()) {
            ci.setReturnValue(((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isThundering());
        }
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
    private void getThunderGradientOverride(float delta, CallbackInfoReturnable<Float> ci) {
        if (FeatureToggleI.TWEAK_WEATHER_OVERRIDE().getBooleanValue()) {
            final boolean isThundering = ((WeatherOverrideMode) Configs.Generic.WEATHER_OVERRIDE_OPTION.getOptionListValue()).isThundering();
            ci.setReturnValue(isThundering ? 1F : 0F);
        }
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    private void setBlockStateInject(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> ci) {
        if (!isClient) {
            return;
        }
      
        if (!RenderTweaks.isPositionValidForRendering(pos)) {
            if ((flags & RenderTweaks.PASSTHROUGH) != 0) {
                return;
            }
            MinecraftClient mc = MinecraftClient.getInstance();
            RenderTweaks.setFakeBlockState(mc.world, pos, state, null);
            ci.setReturnValue(false);
        }
    }
}
