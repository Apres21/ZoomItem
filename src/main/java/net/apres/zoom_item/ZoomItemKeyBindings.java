package net.apres.zoom_item;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ZoomItemKeyBindings {
    public static KeyBinding toggleKey;

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitem.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.zoomitem"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.wasPressed()) {
                boolean newState = !ZoomItem.isEnabled();
                ZoomItem.setEnabled(newState);
                if (client.player != null) {
                    Text message = newState ?
                            Text.translatable("zoomitem.message.enabled") :
                            Text.translatable("zoomitem.message.disabled");
                    client.player.sendMessage(message, true);
                }
            }
        });
    }
}