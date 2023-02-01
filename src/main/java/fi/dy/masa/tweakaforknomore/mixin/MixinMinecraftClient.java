package fi.dy.masa.tweakaforknomore.mixin;


import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.tweakaforknomore.tweaks.RenderTweaks;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
{
    @Shadow @Nullable
    public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onLeftClickMouse(CallbackInfoReturnable<Boolean> ci)
    {
        if (FeatureToggleI.TWEAK_AREA_SELECTOR().getBooleanValue()) {
            RenderTweaks.select(false);
            ci.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onRightClickMouse(CallbackInfo ci)
    {
        if (FeatureToggleI.TWEAK_AREA_SELECTOR().getBooleanValue()) {
            RenderTweaks.select(true);
            ci.cancel();
            return;
        }
    }

    @Inject(method = "render(Z)V", at= @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/util/Window;swapBuffers()V"
    ))
    private void afterRenderEnd(CallbackInfo ci) {
        if (FeatureToggleI.TWEAK_STANDARD_ASPECT_RATIO().getBooleanValue()) {
            RenderTweaks.renderCoverEnd();
        }
    }

}
