package fi.dy.masa.tweakeroo.mixin;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkLightProvider.class)
public class MixinChunkLightProvider_client {
    
    @Inject(method = "getStateForLighting", at = @At("HEAD"), cancellable = true)
    private void getStateForLightingInject(long pos, @Nullable MutableInt mutableInt, CallbackInfoReturnable<BlockState> ci) {

        if (!RenderTweaks.isPositionValidForRendering(pos)) {
            if (mutableInt != null) {
                mutableInt.setValue(0);
            }
    
            ci.setReturnValue(Blocks.AIR.getDefaultState());
            ci.cancel();
        }
    }
}
