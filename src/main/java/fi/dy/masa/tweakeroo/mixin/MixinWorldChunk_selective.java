package fi.dy.masa.tweakeroo.mixin;

import java.util.BitSet;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(WorldChunk.class)
public class MixinWorldChunk_selective {

    @Shadow
    @Final
    World world;

    @Shadow @Final
    private ChunkPos pos;

    @Shadow
    @Final
    private ChunkSection[] sections;

    @Inject(method = "setBlockState", at = @At("HEAD"), cancellable = true)
    private void setBlockStateInject(BlockPos pos, BlockState state, boolean moved,
            CallbackInfoReturnable<BlockState> ci) {
        int y = pos.getY();
        int i = y >> 4 - (world.getBottomY() >> 4);
        ChunkSection chunkSection = this.sections[i];
        if (chunkSection == null) {
            if (state.isAir()) {
                return;
            }

            chunkSection = new ChunkSection(ChunkSectionPos.getSectionCoord(i));
            this.sections[i] = chunkSection;
        }
    }

    @Inject(method = "loadFromPacket", at = @At("RETURN"))
    private void loadFromPacketInject(@Nullable BiomeArray biomes, PacketByteBuf buf, NbtCompound nbt, BitSet bitSet,
            CallbackInfo ci) {
        for (int i = 0; i < this.sections.length; i++) {
            if (sections[i] != null) {
            
            }
        }
    }

}
