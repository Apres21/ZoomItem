package net.apres.zoom_item;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindScreen extends Screen {
    private static final Identifier DIRT_BACKGROUND = new Identifier("textures/block/dirt.png");
    private final Screen parent;
    private final KeyBinding keyBinding;
    private boolean waitingForKey = false;

    public KeyBindScreen(Screen parent, KeyBinding keyBinding) {
        super(Text.translatable("zoomitem.screen.press_key"));
        this.parent = parent;
        this.keyBinding = keyBinding;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.translatable("zoomitem.screen.reset"), button -> {
                    keyBinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_Z));
                    waitingForKey = false;
                    close();
                })
                .dimensions(this.width / 2 - 75, this.height - 60, 150, 20)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("zoomitem.screen.cancel"), button -> close())
                .dimensions(this.width / 2 - 75, this.height - 30, 150, 20)
                .build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            keyBinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
            waitingForKey = false;
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderDirtBackground(context);

        String message = waitingForKey ?
                Text.translatable("zoomitem.screen.press_key").getString() :
                String.format(Text.translatable("zoomitem.screen.current_key").getString(),
                        keyBinding.getBoundKeyLocalizedText().getString());

        context.drawCenteredTextWithShadow(this.textRenderer, message, this.width / 2, 60, 0xFFFFFF);

        if (!waitingForKey) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("zoomitem.screen.click_to_change").getString(),
                    this.width / 2, 100, 0xAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!waitingForKey && mouseY > 80 && mouseY < 120) {
            waitingForKey = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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