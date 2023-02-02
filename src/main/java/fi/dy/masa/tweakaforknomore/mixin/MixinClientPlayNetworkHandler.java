package fi.dy.masa.tweakaforknomore.mixin;

import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.CombinedDynamicRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import fi.dy.masa.tweakaforknomore.config.Configs;
import fi.dy.masa.tweakaforknomore.tweaks.MiscTweaks;
import fi.dy.masa.tweakaforknomore.tweaks.RenderTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{

    @Shadow
    private ClientWorld world;

    @Shadow
    private int chunkLoadDistance;

    @Shadow
    private CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries;
    
    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreenListener(OpenScreenS2CPacket packet, CallbackInfo ci) {
        if (!RenderTweaks.onOpenScreen(packet.getName(),packet.getScreenHandlerType(),packet.getSyncId())) {
            ci.cancel();
        }
    }

    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventoryListener(InventoryS2CPacket packet, CallbackInfo ci) {
        if (!RenderTweaks.onInventory(packet.getSyncId(),packet.getContents())) {
            ci.cancel();
        }
    }

    @Inject(method = "onBlockEvent", at = @At("HEAD"), cancellable = true)
    private void overrideBlockEvent(BlockEventS2CPacket packet, CallbackInfo ci) {
        if (Configs.Disable.DISABLE_CLIENT_BLOCK_EVENTS.getBooleanValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "onLightUpdate", at = @At("HEAD"), cancellable = true)
    private void onLightUpdateEvent(LightUpdateS2CPacket packet, CallbackInfo ci) {
        int i = packet.getChunkX();
        int j = packet.getChunkZ();
        RenderTweaks.onLightUpdateEvent(i,j, ci);
    }

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (!((packet instanceof PlayerMoveC2SPacket) || (packet instanceof PlayPongC2SPacket) || (packet instanceof KeepAliveC2SPacket) || (packet instanceof CustomPayloadC2SPacket))) {
            MiscTweaks.resetAfkTimer();
        }
    }


    @Inject(method = "onPlayerRespawn", at=@At(value = "NEW",
    target="net/minecraft/client/world/ClientWorld"))
    private void onPlayerRespawnInject(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        RenderTweaks.resetWorld(combinedDynamicRegistries.getCombinedRegistryManager(), chunkLoadDistance);
    }

    
    @Inject(method = "onGameJoin", at=@At(value = "NEW",
    target="net/minecraft/client/world/ClientWorld"))
    private void onGameJoinInject(GameJoinS2CPacket packet, CallbackInfo ci) {
        RenderTweaks.resetWorld(combinedDynamicRegistries.getCombinedRegistryManager(), chunkLoadDistance);
    }

    @Inject(method = "onChunkData", at=@At("RETURN"))
    private void onChunkDataInject(ChunkDataS2CPacket packet, CallbackInfo ci) {
        int cx = packet.getX();
		int cz = packet.getZ();
        RenderTweaks.loadFakeChunk(cx, cz);

        if (!FeatureToggleI.TWEAK_SELECTIVE_BLOCKS_RENDERING().getBooleanValue()) {
            return;
        }
		WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(cx, cz);
	
		if (worldChunk != null) {
            BlockPos.Mutable pos = new BlockPos.Mutable();
			ChunkSection[] sections = worldChunk.getSectionArray();
            for (int i = 0; i < sections.length; i++) {
                ChunkSection section = sections[i];
                if (section != null && !section.isEmpty()) {
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                pos.set(x+worldChunk.getPos().getStartX(),y+section.getYOffset(),z+worldChunk.getPos().getStartZ());

                                if (!RenderTweaks.isPositionValidForRendering(pos)) {
                                    BlockEntity be = worldChunk.getBlockEntity(pos);
                                    BlockState state = section.getBlockState(x, y, z);
                                    worldChunk.setBlockState(pos, Blocks.AIR.getDefaultState(), false);
                                    RenderTweaks.setFakeBlockState(this.world, pos, state, be);
                                }
                            }
                        }
                    }
                  
                }
            }
		}
    }

    @Inject(method = "onUnloadChunk",at=@At("RETURN"))
    private void onUnloadChunkInject(UnloadChunkS2CPacket packet, CallbackInfo ci) {
        int i = packet.getX();
		int j = packet.getZ();
        RenderTweaks.unloadFakeChunk(i,j);
    }
    

    @Inject(method = "onChunkLoadDistance",at=@At("RETURN"))
    private void onChunkLoadDistanceInject(ChunkLoadDistanceS2CPacket packet, CallbackInfo ci) {
        RenderTweaks.getFakeWorld().getChunkManager().updateLoadDistance(packet.getDistance());
    }

    @Inject(method = "onChunkRenderDistanceCenter",at=@At("RETURN"))
    private void onChunkRenderDistanceCenterInject(ChunkRenderDistanceCenterS2CPacket packet, CallbackInfo ci) {
        RenderTweaks.getFakeWorld().getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
    }
    
}
