package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.optifine.render.RenderEnv;

@Pseudo
@Mixin(targets = "net.optifine.util.BlockUtils")
public class MixinOptifineBlockUtils {
    @Inject(method = "shouldSideBeRendered(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/optifine/render/RenderEnv;)Z",
    at = @At("HEAD"), cancellable = true)
    private static void shouldSideBeRenderedInject(BlockState blockStateIn, BlockView blockReaderIn, BlockPos blockPosIn, Direction facingIn, RenderEnv renderEnv, CallbackInfoReturnable<Boolean> ci) {
        if (!RenderTweaks.isPositionValidForRendering(blockPosIn.offset(facingIn))) {
            ci.setReturnValue(true);
            ci.cancel();
       }

    }
}
