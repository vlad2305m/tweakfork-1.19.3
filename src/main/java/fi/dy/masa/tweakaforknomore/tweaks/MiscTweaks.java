package fi.dy.masa.tweakaforknomore.tweaks;

import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.tweakaforknomore.config.FeatureToggleI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import fi.dy.masa.tweakaforknomore.config.Configs;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;

public class MiscTweaks
{
    public static int ticksSinceAfk = 0;
    public static boolean performedAfkAction = false;

    public static void resetAfkTimer() {
        if (!performedAfkAction || ticksSinceAfk > Configs.Generic.AFK_TIMEOUT.getIntegerValue() + 20*5) {
            ticksSinceAfk = 0;
            performedAfkAction = false;
        }
    }

    public static void disconnectGracefully(MinecraftClient mc) {
        boolean flag = mc.isInSingleplayer();
        boolean flag1 = mc.isConnectedToRealms();
        
        mc.world.disconnect();
        if (flag) {
            mc.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        } else {
            mc.disconnect();
        }

        TitleScreen lv = new TitleScreen();
        if (flag) {
            mc.setScreen(lv);
        } else if (flag1) {
            mc.setScreen(new RealmsMainScreen(lv));
        } else {
            mc.setScreen(new MultiplayerScreen(lv));
        }
    }

    private static void performAfkAction(MinecraftClient mc) {
        String action = Configs.Generic.AFK_ACTION.getStringValue();

        if (action.equals("/disconnect")) {
            disconnectGracefully(mc);
            return;
        }

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(action));
    }

    public static void checkAfk(MinecraftClient mc) {
        ticksSinceAfk++;
        if (!performedAfkAction && FeatureToggleI.TWEAK_AFK_TIMEOUT().getBooleanValue()) {
            int afkTimeout = Configs.Generic.AFK_TIMEOUT.getIntegerValue();
            if (ticksSinceAfk > afkTimeout) {
                performAfkAction(mc);
                performedAfkAction = true;
            } else if (ticksSinceAfk > afkTimeout - 20*30) {
                InfoUtils.printActionbarMessage("tweakaforknomore.message.afk_detected", Math.ceil((double)(afkTimeout - ticksSinceAfk) / 2.0) / 10.0);
            }
        }
    }

}
