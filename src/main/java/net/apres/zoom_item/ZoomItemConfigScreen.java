package net.apres.zoom_item;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ZoomItemConfigScreen extends Screen {
    private static final Identifier DIRT_BACKGROUND = new Identifier("textures/block/dirt.png");
    private final Screen parent;
    private ButtonWidget keyBindButton;
    private boolean waitingForKey = false;
    private String conflictMessage = null;

    public ZoomItemConfigScreen(Screen parent) {
        super(Text.translatable("zoomitem.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        CyclingButtonWidget<Boolean> toggleButton = CyclingButtonWidget.onOffBuilder(ZoomItem.isEnabled())
                .build(
                        this.width / 2 - 75, 60, 150, 20,
                        Text.translatable("zoomitem.screen.enabled"),
                        (button, value) -> {
                            ZoomItem.setEnabled(value);
                        }
                );

        keyBindButton = ButtonWidget.builder(
                        Text.literal(Text.translatable("zoomitem.screen.toggle_key").getString() + " " + getKeyName()),
                        button -> {
                            waitingForKey = true;
                            button.setMessage(Text.translatable("zoomitem.screen.press_key"));
                            conflictMessage = null;
                        }
                )
                .dimensions(this.width / 2 - 75, 90, 150, 20)
                .build();

        ButtonWidget doneButton = ButtonWidget.builder(Text.translatable("zoomitem.screen.done"), button -> close())
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20)
                .build();

        addDrawableChild(toggleButton);
        addDrawableChild(keyBindButton);
        addDrawableChild(doneButton);
    }

    private void updateKeyBindings() {
        try {
            Method method = KeyBinding.class.getDeclaredMethod("updateKeysByCode");
            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, KeyBinding> getAllKeyBindings() {
        try {
            Field field = KeyBinding.class.getDeclaredField("KEYS_BY_ID");
            field.setAccessible(true);
            return (Map<String, KeyBinding>) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private InputUtil.Key getBoundKey(KeyBinding kb) {
        try {
            Field field = KeyBinding.class.getDeclaredField("boundKey");
            field.setAccessible(true);
            return (InputUtil.Key) field.get(kb);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isKeyUsed(InputUtil.Key newKey) {
        Map<String, KeyBinding> allKeys = getAllKeyBindings();
        if (allKeys == null) return false;

        for (KeyBinding kb : allKeys.values()) {
            if (kb == ZoomItemKeyBindings.toggleKey) continue;

            InputUtil.Key boundKey = getBoundKey(kb);
            if (boundKey != null && boundKey.equals(newKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            InputUtil.Key newKey = InputUtil.Type.KEYSYM.createFromCode(keyCode);

            if (isKeyUsed(newKey)) {
                conflictMessage = Text.translatable("zoomitem.screen.key_used").getString();
                waitingForKey = false;
                keyBindButton.setMessage(Text.literal(Text.translatable("zoomitem.screen.toggle_key").getString() + " " + getKeyName()));
            } else {
                ZoomItemKeyBindings.toggleKey.setBoundKey(newKey);
                updateKeyBindings();
                waitingForKey = false;
                conflictMessage = null;
                keyBindButton.setMessage(Text.literal(Text.translatable("zoomitem.screen.toggle_key").getString() + " " + getKeyName()));
            }

            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private String getKeyName() {
        return ZoomItemKeyBindings.toggleKey.getBoundKeyLocalizedText().getString();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderDirtBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        if (waitingForKey) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("zoomitem.screen.press_key").getString(),
                    this.width / 2, 130, 0xFFFF00);
        }

        if (conflictMessage != null) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    conflictMessage,
                    this.width / 2, 160, 0xFF5555);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderDirtBackground(DrawContext context) {
        for (int x = 0; x < this.width; x += 16) {
            for (int y = 0; y < this.height; y += 16) {
                context.drawTexture(DIRT_BACKGROUND, x, y, 0, 0, 16, 16, 16, 16);
            }
        }
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}