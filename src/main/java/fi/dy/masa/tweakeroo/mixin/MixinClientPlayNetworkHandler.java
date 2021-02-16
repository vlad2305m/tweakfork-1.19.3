package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.tweakeroo.Tweakeroo;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.tweaks.PlacementTweaks;
import fi.dy.masa.tweakeroo.tweaks.RenderTweaks;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Inject(method = "onScreenHandlerSlotUpdate", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    private void onHandleSetSlot(net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci)
    {
        if (PlacementTweaks.shouldSkipSlotSync(packet.getSlot(), packet.getItemStack()))
        {
            ci.cancel();
        }
    }

    @Inject(method = "onCombatEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void onPlayerDeath(net.minecraft.network.packet.s2c.play.CombatEventS2CPacket packetIn, CallbackInfo ci)
    {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();

        if (FeatureToggle.TWEAK_PRINT_DEATH_COORDINATES.getBooleanValue() && mc.player != null)
        {
            net.minecraft.util.math.BlockPos pos = fi.dy.masa.malilib.util.PositionUtils.getEntityBlockPos(mc.player);
            String str = String.format("You died @ %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            net.minecraft.text.LiteralText message = new net.minecraft.text.LiteralText(str);
            message.getStyle().withClickEvent(new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.SUGGEST_COMMAND, pos.getX() + " " + pos.getY() + " " + pos.getZ()));
            message.formatted(net.minecraft.util.Formatting.UNDERLINE);
            net.minecraft.client.MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
            Tweakeroo.logger.info(str);
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
}
