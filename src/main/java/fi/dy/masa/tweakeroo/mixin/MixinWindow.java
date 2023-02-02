package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import fi.dy.masa.tweakeroo.util.IMixinWindow;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.util.Window;

import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;

@Mixin(Window.class)
public abstract class MixinWindow implements IMixinWindow {

    double targetAspectRatio = 16.0 / 9.0;

    @Shadow public abstract int getWidth();
    @Shadow public abstract int getHeight();

    @Shadow @Final
	private WindowEventHandler eventHandler;

    @Shadow @Final
	private long handle;

    @Shadow
	private int framebufferWidth;

    @Shadow
	private int framebufferHeight;

    @Shadow
    private double scaleFactor;

    private int yOffset = 0;
    private int originalFramebufferHeight = 1;

    // TODO: use invoke
    @Overwrite
	private void onFramebufferSizeChanged(long window, int width, int height) {
		if (window == this.handle) {
			int i = this.framebufferWidth;
			int j = this.framebufferHeight;
			if (width != 0 && height != 0) {
				this.framebufferWidth = width;
                this.originalFramebufferHeight = height;
                this.yOffset = RenderTweaks.getHeightOffsetWithAspectRatio(this.targetAspectRatio, this.framebufferWidth, height);
				this.framebufferHeight = height - yOffset;
				if (this.framebufferWidth != i || this.framebufferHeight != j) {
					this.eventHandler.onResolutionChanged();
				}
			}
		}
	}

    @Inject(method = "updateFramebufferSize", at = @At("RETURN"))
	private void updateFramebufferSizeInject(CallbackInfo ci) {
        this.yOffset = RenderTweaks.getHeightOffsetWithAspectRatio(this.targetAspectRatio, this.framebufferWidth, this.framebufferHeight);
        this.originalFramebufferHeight = this.framebufferHeight;
        this.framebufferHeight = this.framebufferHeight - this.yOffset;
	}

    @Override
    public int getYOffset() {
        return this.yOffset;
    }

    @Override 
    public int getOriginalScaledHeight() {
        int j = (int)((double)this.originalFramebufferHeight / scaleFactor);
        return (double)this.originalFramebufferHeight / scaleFactor > (double)j ? j + 1 : j;
    }

    @Override
    public int getOriginalFramebufferHeight() {
        return this.originalFramebufferHeight;
    }

    @Inject(method = "getScaleFactor", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_customGuiScaleGetScale(CallbackInfoReturnable<Double> cir)
    {
        if (FeatureToggle.TWEAK_CUSTOM_INVENTORY_GUI_SCALE.getBooleanValue() &&
                MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>)
        {
            int scale = Configs.Generic.CUSTOM_INVENTORY_GUI_SCALE.getIntegerValue();

            if (scale > 0)
            {
                cir.setReturnValue((double) scale);
            }
        }
    }

    @Inject(method = "getScaledWidth", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_customGuiScaleGetWidth(CallbackInfoReturnable<Integer> cir)
    {
        if (FeatureToggle.TWEAK_CUSTOM_INVENTORY_GUI_SCALE.getBooleanValue() &&
                MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>)
        {
            int scale = Configs.Generic.CUSTOM_INVENTORY_GUI_SCALE.getIntegerValue();

            if (scale > 0)
            {
                cir.setReturnValue((int) Math.ceil((double) this.getWidth() / scale));
            }
        }
    }

    @Inject(method = "getScaledHeight", at = @At("HEAD"), cancellable = true)
    private void tweakeroo_customGuiScaleGetHeight(CallbackInfoReturnable<Integer> cir)
    {
        if (FeatureToggle.TWEAK_CUSTOM_INVENTORY_GUI_SCALE.getBooleanValue() &&
                MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?>)
        {
            int scale = Configs.Generic.CUSTOM_INVENTORY_GUI_SCALE.getIntegerValue();

            if (scale > 0)
            {
                cir.setReturnValue((int) Math.ceil((double) this.getHeight() / scale));
            }
        }
    }
}
