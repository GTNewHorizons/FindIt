package com.gtnh.findit.service.itemfinder;

import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.fx.HighlighterHandler;
import com.gtnh.findit.fx.SlotHighlighter;
import com.gtnh.findit.util.AbstractStackFinder;

import codechicken.nei.api.API;
import codechicken.nei.event.NEIConfigsLoadedEvent;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientItemFindService extends ItemFindService {

    private final SlotHighlighter slotHighlighter;
    private FindItemRequest request = null;
    private long expirationTime = 0;

    public ClientItemFindService() {
        if (!FindIt.isExtraUtilitiesLoaded()) {
            API.addHashBind("gui.findit.find_item", Keyboard.KEY_T);
        }

        this.slotHighlighter = new SlotHighlighter();

        GuiContainerManager.addDrawHandler(this.slotHighlighter);
        GuiContainerManager.addInputHandler(new ItemFindInputHandler());

        MinecraftForge.EVENT_BUS.register(new GuiListener());
        MinecraftForge.EVENT_BUS.register(new NEIEventListener());
        FMLCommonHandler.instance().bus().register(new TickListener());
    }

    public void handleResponse(EntityClientPlayerMP player, ItemFoundResponse response) {
        this.slotHighlighter.highlightSlots(null, new HashSet<>(), 0xFFFF8726);
        this.request = response.getFoundStack() != null ? new FindItemRequest(response.getFoundStack()) : null;
        this.expirationTime = System.currentTimeMillis() + FindItConfig.ITEM_HIGHLIGHTING_DURATION * 1000;
    }

    public class TickListener {

        @SubscribeEvent
        public void onClientPostTick(TickEvent.ClientTickEvent event) {

            if (request == null || event.phase != TickEvent.Phase.END) {
                return;
            }

            if (Minecraft.getMinecraft().theWorld == null) {
                return;
            }

            final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (!(screen instanceof GuiContainer)) {
                return;
            }

            final GuiContainer gui = (GuiContainer) screen;
            final HashSet<Slot> highlightedSlots = new HashSet<>();

            if (System.currentTimeMillis() > expirationTime) {
                request = null;
            }

            if (request != null) {
                @SuppressWarnings("unchecked")
                List<Slot> slots = gui.inventorySlots.inventorySlots;

                for (Slot slot : slots) {
                    if (!(slot.inventory instanceof InventoryPlayer) && request.equals(slot.getStack())) {
                        highlightedSlots.add(slot);

                        if (highlightedSlots.size() > 256) {
                            break;
                        }
                    }
                }
            }

            slotHighlighter.highlightSlots(gui, highlightedSlots, 0xFFFF8726);
        }
    }

    public class GuiListener {

        @SubscribeEvent
        public void renderWorldLastEvent(RenderWorldLastEvent event) {
            HighlighterHandler.renderHilightedBlock(event);
        }
    }

    private static class ItemFindInputHandler extends AbstractStackFinder {

        @Override
        protected String getKeyBindId() {
            return FindIt.isExtraUtilitiesLoaded() ? "gui.xu_ping" : "gui.findit.find_item";
        }

        @Override
        protected boolean findStack(ItemStack stack) {
            FindItNetwork.CHANNEL.sendToServer(new FindItemRequest(stack));
            return true;
        }
    }

    public static class NEIEventListener {

        @SubscribeEvent
        public void onNEIConfigsLoaded(NEIConfigsLoadedEvent event) {
            if (FindIt.isExtraUtilitiesLoaded()) {
                GuiContainerManager.inputHandlers.removeIf(
                        (inputHandler) -> inputHandler.getClass().getName()
                                .equals("com.rwtema.extrautils.nei.ping.NEIPing"));
            }
        }
    }

    public static ClientItemFindService getInstance() {
        return (ClientItemFindService) FindIt.getItemFindService();
    }
}
