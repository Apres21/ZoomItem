package net.apres.zoom_item;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomItem implements ModInitializer {
    public static final String MOD_ID = "zoomitem";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static boolean enabled = true;

    @Override
    public void onInitialize() {
        LOGGER.info("ZoomItem mod initialized for 1.20.1!");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        LOGGER.info("ZoomItem enabled: " + value);
    }
}