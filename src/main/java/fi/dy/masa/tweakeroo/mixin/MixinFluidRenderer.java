package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {
    
    @Inject(method = "method_29710", at = @At("HEAD"), cancellable = true)
    private static void fluidRenderOverride(BlockView arg, Direction arg1, float f, BlockPos arg2, BlockState arg3, CallbackInfoReturnable<Boolean> ci) {
        if (!RenderTweaks.isPositionValidForRendering(arg2)) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
