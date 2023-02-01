package fi.dy.masa.tweakaforknomore.mixin;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity
{
    private MixinClientPlayerEntity(ClientWorld world, GameProfile profile)
    {
        super(world, profile);
    }

    @Inject(method = "shouldSlowDown", at = @At(value = "HEAD"), cancellable = true)
    private void shouldSlowDown(CallbackInfoReturnable<Boolean> ci) {
        if (FeatureToggleI.TWEAK_NO_SNEAK_SLOWDOWN().getBooleanValue()) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
