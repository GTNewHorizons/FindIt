package com.gtnh.findit.util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;

public abstract class AbstractStackFinder implements IContainerInputHandler {

    protected abstract String getKeyBindId();

    protected abstract boolean findStack(ItemStack stack);

    @Override
    public boolean keyTyped(GuiContainer window, char c, int key) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer guiContainer, char c, int i) {}

    @Override
    public boolean lastKeyTyped(GuiContainer guiContainer, char c, int i) {
        if (!NEIClientConfig.isKeyHashDown(getKeyBindId())) {
            return false;
        }

        ItemStack stack = GuiContainerManager.getStackMouseOver(guiContainer);
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        return findStack(stack);
    }

    @Override
    public boolean mouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {
        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {}

    @Override
    public void onMouseUp(GuiContainer guiContainer, int i, int i1, int i2) {}

    @Override
    public boolean mouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {}

    @Override
    public void onMouseDragged(GuiContainer guiContainer, int i, int i1, int i2, long l) {}
}
