package fi.dy.masa.tweakaforknomore.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum WeatherOverrideMode implements IConfigOptionListEntry {

    CLEAR("CLEAR", "tweakaforknomore.label.weather_override_mode.clear", false, false),
    THUNDER("THUNDER", "tweakaforknomore.label.weather_override_mode.thunder", false, true),
    RAIN("RAIN", "tweakaforknomore.label.weather_override_mode.rain", true, false),
    RAINING_THUNDER("RAINING_THUNDER", "tweakaforknomore.label.weather_override_mode.raining_thunder", true, true);

    private final String configName;
    private final String unlocName;
    private final boolean isRaining;
    private final boolean isThundering;

    WeatherOverrideMode(String configName, String unlocName, boolean isRaining, boolean isThundering) {
        this.configName = configName;
        this.unlocName = unlocName;
        this.isRaining = isRaining;
        this.isThundering = isThundering;
    }

    public boolean isRaining() {
        return isRaining;
    }

    public boolean isThundering() {
        return isThundering;
    }

    @Override
    public String getStringValue() {
        return this.configName;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate(this.unlocName);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();

        if (forward) {
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public IConfigOptionListEntry fromString(String value) {
        for (WeatherOverrideMode mode : WeatherOverrideMode.values()) {
            if (mode.configName.equalsIgnoreCase(value)) {
                return mode;
            }
        }

        return WeatherOverrideMode.CLEAR;
    }
}
