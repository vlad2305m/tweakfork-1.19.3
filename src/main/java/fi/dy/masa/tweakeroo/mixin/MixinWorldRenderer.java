package fi.dy.masa.tweakeroo.mixin;

import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import fi.dy.masa.tweakeroo.util.CameraUtils;

@Mixin(value = WorldRenderer.class, priority = 1001)
public abstract class MixinWorldRenderer
{
    @Shadow private int cameraChunkX;
    @Shadow private int cameraChunkZ;

    private int lastUpdatePosX;
    private int lastUpdatePosZ;

    @Inject(method = "tickRainSplashing", at = @At("HEAD"), cancellable = true) // renderRain
    private void cancelRainRender(Camera camera, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void cancelRainRender(LightmapTextureManager lightmap, float partialTicks, double x, double y, double z, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_RAIN_EFFECTS.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=terrain_setup"))
    private void preSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            CameraUtils.setFreeCameraSpectator(true);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=compilechunks"))
    private void postSetupTerrain(net.minecraft.client.util.math.MatrixStack matrixStack, float partialTicks, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lightmap, Matrix4f matrix4f, CallbackInfo ci)
    {
        CameraUtils.setFreeCameraSpectator(false);
    }

    // Allow rendering the client player entity by spoofing one of the entity rendering conditions while in Free Camera mode
    @Redirect(method = "render", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
    private Entity allowRenderingClientPlayerInFreeCameraMode(Camera camera)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            return mc.player;
        }

        return camera.getFocusedEntity();
    }

    @Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    private void spawnParticleInject(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> ci) {
        if (Configs.Generic.SELECTIVE_BLOCKS_HIDE_PARTICLES.getBooleanValue()) {
            if (!RenderTweaks.isPositionValidForRendering(new BlockPos(x,y,z))) {
                ci.setReturnValue(null);
                ci.cancel();
            }
        }
    }

    // These injections will fail when Sodium is present, but the Free Camera
    // rendering seems to work fine with Sodium without these anyway
    @Inject(method = "setupTerrain", require = 0,
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
            target = "Lnet/minecraft/client/render/WorldRenderer;lastCameraChunkUpdateX:D"))
    private void rebuildChunksAroundCamera1(
            Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            // Hold on to the previous update position before it gets updated
            this.lastUpdatePosX = this.cameraChunkX;
            this.lastUpdatePosZ = this.cameraChunkZ;
        }
    }

    // These injections will fail when Sodium is present, but the Free Camera
    // rendering seems to work fine with Sodium without these anyway
    @Inject(method = "setupTerrain", require = 0,
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/render/BuiltChunkStorage;updateCameraPosition(DD)V"))
    private void rebuildChunksAroundCamera2(
            Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci)
    {
        // Mark the chunks at the edge of the free camera's render range for rebuilding
        // when the camera moves around.
        // Normally these rebuilds would happen when the server sends chunks to the client when the player moves around.
        // But in Free Camera mode moving the ViewFrustum/BuiltChunkStorage would cause the terrain
        // to disappear because of no dirty marking calls from chunk loading.
        if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
        {
            int x = MathHelper.floor(camera.getPos().x) >> 4;
            int z = MathHelper.floor(camera.getPos().z) >> 4;
            CameraUtils.markChunksForRebuild(x, z, this.lastUpdatePosX, this.lastUpdatePosZ);
        }
    }
}
