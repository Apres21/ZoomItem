package net.apres.zoom_item.render;

import net.apres.zoom_item.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ZoomRenderer {
    private static Slot lastFocusedSlot = null;
    private static ZoomedItemRenderState cachedState = null;
    private static int lastScreenWidth = 0;
    private static int lastScreenHeight = 0;

    public static void render(DrawContext context, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            lastFocusedSlot = null;
            cachedState = null;
            return;
        }

        Slot focusedSlot = ((HandledScreenAccessor) screen).getFocusedSlot();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Кэширование
        if (focusedSlot != null && focusedSlot == lastFocusedSlot
                && cachedState != null && !cachedState.stack().isEmpty()
                && ItemStack.areEqual(cachedState.stack(), focusedSlot.getStack())
                && screenWidth == lastScreenWidth && screenHeight == lastScreenHeight) {
            renderZoomedItemDirectly(context, cachedState);
            return;
        }

        lastFocusedSlot = focusedSlot;
        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;

        if (focusedSlot != null && focusedSlot.hasStack()) {
            ItemStack stack = focusedSlot.getStack();
            int guiLeft = ((HandledScreenAccessor) screen).getX();

            int size = (int) (Math.min(screenWidth, screenHeight) * 0.45);
            int x;

            if (guiLeft >= size + 8) {
                x = (guiLeft - size) / 2;
            } else {
                x = guiLeft - size - 4;
            }
            int y = (screenHeight - size) / 2;

            cachedState = new ZoomedItemRenderState(stack.copy(), x, y, size);
            renderZoomedItemDirectly(context, cachedState);
        } else {
            cachedState = null;
        }
    }

    private static void renderZoomedItemDirectly(DrawContext context, ZoomedItemRenderState state) {
        if (state.stack().isEmpty()) return;

        context.getMatrices().push();

        context.getMatrices().translate(state.x(), state.y(), 0);
        float scale = state.size() / 16f;
        context.getMatrices().scale(scale, scale, 1.0f);

        context.drawItem(state.stack(), 0, 0);

        context.getMatrices().pop();
    }
}