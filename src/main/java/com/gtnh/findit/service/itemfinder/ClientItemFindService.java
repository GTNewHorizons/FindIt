package com.gtnh.findit.service.itemfinder;

import codechicken.nei.api.API;
import codechicken.nei.event.NEIConfigsLoadedEvent;
import codechicken.nei.guihook.GuiContainerManager;
import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.fx.ParticlePosition;
import com.gtnh.findit.fx.SlotHighlighter;
import com.gtnh.findit.util.AbstractStackFinder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ClientItemFindService extends ItemFindService {

    private ContainerHighlightData highlightData = null;

    public ClientItemFindService() {
        if (!FindIt.isExtraUtilitiesLoaded()) {
            API.addHashBind("gui.findit.find_item", Keyboard.KEY_T);
        }
        GuiContainerManager.addInputHandler(new ItemFindInputHandler());

        MinecraftForge.EVENT_BUS.register(new GuiListener());
        MinecraftForge.EVENT_BUS.register(new NEIEventListener());
        FMLCommonHandler.instance().bus().register(new TickListener());
    }

    public void handleResponse(EntityClientPlayerMP player, ItemFoundResponse response) {
        World world = player.worldObj;

        player.closeScreen();
        ParticlePosition.highlightBlocks(world, response.getPositions());
    }

    public void handleSlotHighlight(HighlightSlotsPacket packet) {
        highlightData = new ContainerHighlightData(packet.getWindowId(), packet.getTargetStack(), packet.getTargetSlots());
    }

    public class TickListener {

        @SubscribeEvent
        public void onClientPostTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            WorldClient world = Minecraft.getMinecraft().theWorld;
            if (world == null) {
                return;
            }
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (!(screen instanceof GuiContainer)) {
                return;
            }
            GuiContainer gui = (GuiContainer) screen;

            if (highlightData != null) {
                if (highlightData.windowId == gui.inventorySlots.windowId) {
                    highlightData.updateHighlightedSlots(gui);
                } else {
                    highlightData = null;
                }
            }
        }
    }

    public class GuiListener {

        @SubscribeEvent
        public void onGuiPostRender(GuiScreenEvent.DrawScreenEvent.Pre event) {
            if (!(event.gui instanceof GuiContainer)) {
                return;
            }
            GuiContainer gui = (GuiContainer) event.gui;

            if (highlightData != null && highlightData.windowId == gui.inventorySlots.windowId) {
                SlotHighlighter.highlightSlots(gui, highlightData.highlightedSlots, 0xFFFF8726);
            }
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

    private static class ContainerHighlightData {
        private final int windowId;
        private final ItemStack targetStack;
        private final List<Integer> targetSlots;
        private final List<Integer> highlightedSlots = new ArrayList<>();

        public ContainerHighlightData(int windowId, ItemStack targetStack, List<Integer> targetSlots) {
            this.windowId = windowId;
            this.targetStack = targetStack;
            this.targetSlots = targetSlots;
        }

        public void updateHighlightedSlots(GuiContainer gui) {
            highlightedSlots.clear();

            List<Slot> slots = (List<Slot>) gui.inventorySlots.inventorySlots;
            Item targetItem = targetStack.getItem();
            int targetMeta = targetStack.getItemDamage();

            for (int slotId : targetSlots) {
                if (slotId >= slots.size()) {
                    continue;
                }

                Slot slot = slots.get(slotId);
                ItemStack stack = slot.getStack();
                if (stack != null && stack.getItem() == targetItem && stack.getItemDamage() == targetMeta) {
                    highlightedSlots.add(slotId);
                }
            }
        }
    }

    public static class NEIEventListener {
        @SubscribeEvent
        public void onNEIConfigsLoaded(NEIConfigsLoadedEvent event) {
            if (FindIt.isExtraUtilitiesLoaded()) {
                GuiContainerManager.inputHandlers.removeIf((inputHandler) ->
                        inputHandler.getClass().getName().equals("com.rwtema.extrautils.nei.ping.NEIPing")
                );
            }
        }
    }

    public static ClientItemFindService getInstance() {
        return (ClientItemFindService) FindIt.getItemFindService();
    }
}