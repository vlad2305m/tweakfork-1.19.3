package fi.dy.masa.tweakaforknomore.mixin;

import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import fi.dy.masa.tweakaforknomore.config.Configs;
import fi.dy.masa.tweakaforknomore.tweaks.RenderTweaks;
import fi.dy.masa.tweakaforknomore.util.IDecorationEntity;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher
{
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entityIn, Frustum frustum, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir)
    {
        boolean isPlayer = (entityIn instanceof PlayerEntity);
        if (entityIn instanceof AbstractDecorationEntity) {
            if (!RenderTweaks.isPositionValidForRendering(((IDecorationEntity) entityIn).getAttatched()))
                cir.setReturnValue(false);
        }
        if (!isPlayer && Configs.Generic.SELECTIVE_BLOCKS_HIDE_ENTITIES.getBooleanValue()) {
            if (!RenderTweaks.isPositionValidForRendering(entityIn.getBlockPos()))
                cir.setReturnValue(false);
        }

        if (FeatureToggleI.TWEAK_RENDER_ALL_ENTITIES().getBooleanValue())
        {
            cir.setReturnValue(true);
        }
    }
}
