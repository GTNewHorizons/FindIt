package com.gtnh.findit.proxy;

import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import com.gtnh.findit.FindIt;
import com.gtnh.findit.network.PlzFindItMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;


public class FindItClient extends FindItCommon implements IContainerInputHandler{

    @Override
    public void preinit(FMLPreInitializationEvent event) {
        super.preinit(event);
        API.addKeyBind("gui.findit", Keyboard.KEY_Y);
        GuiContainerManager.addInputHandler((IContainerInputHandler)new FindItClient());
    }

    public boolean keyTyped(final GuiContainer window, final char c, final int k) {
        final int keyBinding = NEIClientConfig.getKeyBinding("gui.findit");
        if (k != keyBinding) {
            return false;
        }

        final LayoutManager layout = LayoutManager.instance();
        if (layout == null || LayoutManager.itemPanel == null || NEIClientConfig.isHidden()) {
            return false;
        }

        final ItemStack stack = GuiContainerManager.getStackMouseOver(window);
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        FindIt.NETWORK.sendToServer(new PlzFindItMessage(stack));
        return true;
    }

    public void onKeyTyped(final GuiContainer guiContainer, final char c, final int i) {
    }

    public boolean lastKeyTyped(final GuiContainer guiContainer, final char c, final int i) {
        return false;
    }

    public boolean mouseClicked(final GuiContainer guiContainer, final int i, final int i1, final int i2) {
        return false;
    }

    public void onMouseClicked(final GuiContainer guiContainer, final int i, final int i1, final int i2) {
    }

    public void onMouseUp(final GuiContainer guiContainer, final int i, final int i1, final int i2) {
    }

    public boolean mouseScrolled(final GuiContainer guiContainer, final int i, final int i1, final int i2) {
        return false;
    }

    public void onMouseScrolled(final GuiContainer guiContainer, final int i, final int i1, final int i2) {
    }

    public void onMouseDragged(final GuiContainer guiContainer, final int i, final int i1, final int i2, final long l) {
    }

}
