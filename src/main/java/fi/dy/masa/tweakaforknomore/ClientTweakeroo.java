package fi.dy.masa.tweakaforknomore;

import net.fabricmc.api.ClientModInitializer;

public class ClientTweakeroo implements ClientModInitializer {
   
    public static final ThreadLocal<Boolean> isClientTracker = new ThreadLocal<>() {
        @Override protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public void onInitializeClient() {
        isClientTracker.set(true);
    }

    public static boolean isClient() {
        return isClientTracker.get();
    }
}


