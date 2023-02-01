package fi.dy.masa.tweakaforknomore.mixin;

import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true) // MCP: onPlayerDamageBlock
    private void handleBreakingRestriction2(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (FeatureToggleI.TWEAK_AREA_SELECTOR().getBooleanValue()) {
            cir.setReturnValue(true);
        }
    }
}
