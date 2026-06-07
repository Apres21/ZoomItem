package net.apres.zoom_item;

import net.fabricmc.api.ClientModInitializer;

public class ZoomItemClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ZoomItem.LOGGER.info("ZoomItem Client initialized!");
    }
}