package fi.dy.masa.tweakeroo.mixin;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World
{
    protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l)
    {
        super(properties, registryKey, dimensionType, supplier, bl, bl2, l);
    }

    @Override
    @Nullable
    public
	BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state) {
        if (!RenderTweaks.isPositionValidForRendering(pos)) return null;
		return super.raycastBlock(start, end, pos, shape, state);
	}
    
    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private void disableClientEntityTicking(Entity entity, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_CLIENT_ENTITY_UPDATES.getBooleanValue() &&
            (entity instanceof PlayerEntity) == false)
        {
            ci.cancel();
        }
    }

    /* TODO 1.17 is this still needed?
    @Inject(method = "addEntitiesToChunk", at = @At("HEAD"), cancellable = true)
    private void fixChunkEntityLeak(WorldChunk chunk, CallbackInfo ci)
    {
        if (Configs.Fixes.CLIENT_CHUNK_ENTITY_DUPE.getBooleanValue())
        {
            for (int y = 0; y < 16; ++y)
            {
                // The chunk already has entities, which means it's a re-used existing chunk,
                // in such a case we don't want to add the from the world entities again, otherwise
                // they are basically duped within the Chunk.
                if (chunk.getEntitySectionArray()[y].size() > 0)
                {
                    ci.cancel();
                    return;
                }
            }
        }
    }
    
    */

    @Inject(method = "scheduleBlockRerenderIfNeeded", at = @At("HEAD"), cancellable = true)
    private void disableChunkReRenders(BlockPos pos, BlockState old, BlockState updated, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_CHUNK_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "scheduleBlockRenders", at = @At("HEAD"), cancellable = true)
    private void disableChunkReRenders(int x, int y, int z, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_CHUNK_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }

    @Inject(method = "updateListeners", at = @At("HEAD"), cancellable = true)
    private void disableChunkReRenders(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci)
    {
        if (Configs.Disable.DISABLE_CHUNK_RENDERING.getBooleanValue())
        {
            ci.cancel();
        }
    }
}
