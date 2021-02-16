package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {
    @Environment(EnvType.CLIENT)
    @Inject(method = "getOpacity", at = @At("HEAD"), cancellable = true)
    private void getOpacityInject(BlockView world, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
        if (!RenderTweaks.isPositionValidForRendering(pos)) {
            ci.setReturnValue(0);
            ci.cancel();
        }
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "isOpaqueFullCube", at = @At("HEAD"), cancellable = true)
    private void isOpaqueFullCubeInject(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        if (!RenderTweaks.isPositionValidForRendering(pos)) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }

    
}
