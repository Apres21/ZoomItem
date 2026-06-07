package net.apres.zoom_item.render;

import net.minecraft.item.ItemStack;

public record ZoomedItemRenderState(ItemStack stack, int x, int y, int size) {
    public int x1() { return x; }
    public int y1() { return y; }
    public int x2() { return x + size; }
    public int y2() { return y + size; }
}