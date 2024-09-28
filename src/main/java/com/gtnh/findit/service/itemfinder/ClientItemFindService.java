package com.gtnh.findit.service.itemfinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.fx.EntityHighlighter;
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
    private final EntityHighlighter itemEntitiesHighlighter;

    private FindItemRequest foundItem = null;
    private long expirationTime = 0;

    public ClientItemFindService() {
        if (!FindIt.isExtraUtilitiesLoaded()) {
            API.addHashBind("gui.findit.find_item", Keyboard.KEY_T);
        }

        this.slotHighlighter = new SlotHighlighter();
        this.itemEntitiesHighlighter = new EntityHighlighter();

        GuiContainerManager.addDrawHandler(this.slotHighlighter);
        GuiContainerManager.addInputHandler(new ItemFindInputHandler());

        MinecraftForge.EVENT_BUS.register(new NEIEventListener());
        MinecraftForge.EVENT_BUS.register(new WorldRenderListener());
        FMLCommonHandler.instance().bus().register(new TickListener());
    }

    public void handleResponse(EntityClientPlayerMP player, ItemFoundResponse response) {
        this.slotHighlighter.highlightSlots(null, new HashSet<>(), 0xFFFF8726);
        this.foundItem = response.getFoundStack() != null ? new FindItemRequest(response.getFoundStack()) : null;
        this.expirationTime = System.currentTimeMillis() + FindItConfig.ITEM_HIGHLIGHTING_DURATION * 1000L;

        if (foundItem == null || !FindItConfig.SEARCH_ITEMS_ON_GROUND) {
            return;
        }

        int searchRadius = FindItConfig.SEARCH_RADIUS;
        AxisAlignedBB searchBox = AxisAlignedBB
                .getBoundingBox(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ)
                .expand(searchRadius, searchRadius, searchRadius);

        @SuppressWarnings("unchecked")
        List<EntityItem> itemEntities = player.worldObj.getEntitiesWithinAABB(EntityItem.class, searchBox);
        List<Integer> foundItemEntities = new ArrayList<>();

        for (EntityItem itemEntity : itemEntities) {
            if (foundItem.isStackSatisfies(player, itemEntity.getEntityItem())) {
                foundItemEntities.add(itemEntity.getEntityId());
                if (foundItemEntities.size() >= FindItConfig.MAX_RESPONSE_SIZE) {
                    break;
                }
            }
        }

        this.itemEntitiesHighlighter.highlightEntities(foundItemEntities, expirationTime);
    }

    public class TickListener {

        @SubscribeEvent
        public void onClientPostTick(TickEvent.ClientTickEvent event) {

            if (foundItem == null || event.phase != TickEvent.Phase.END) {
                return;
            }

            // `WorldClient` is only available on the client-side, thus effectively checking if the game is running on
            // the client. We are only interested in highlighting slots when the player is in a GUI; the operation is
            // bound client-side.
            if (Minecraft.getMinecraft().theWorld == null) {
                return;
            }

            // We are only interested in GUIs that contain some kind of inventory.
            final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (!(screen instanceof GuiContainer)) {
                return;
            }

            final GuiContainer gui = (GuiContainer) screen;
            final HashSet<Slot> highlightedSlots = new HashSet<>();

            // If the expiration time has passed, we reset the found item. This is done to prevent the item from being
            // highlighted indefinitely.
            if (System.currentTimeMillis() > expirationTime) {
                foundItem = null;
            }
            if (foundItem == null) {
                return;
            }

            // We continue to iterate over each slot of the given GUI's inventory and checking if the slot contains the
            // item that we are looking for.
            @SuppressWarnings("unchecked")
            List<Slot> slots = gui.inventorySlots.inventorySlots;
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            for (Slot slot : slots) {
                if (!(slot.inventory instanceof InventoryPlayer)
                        && foundItem.isStackSatisfies(player, slot.getStack())) {
                    highlightedSlots.add(slot);

                    if (highlightedSlots.size() > 256) {
                        break;
                    }
                }
            }

            slotHighlighter.highlightSlots(gui, highlightedSlots, FindItConfig.ITEM_HIGHLIGHTING_COLOR);
        }
    }

    public class WorldRenderListener {

        @SubscribeEvent
        public void renderWorldLastEvent(RenderWorldLastEvent event) {
            itemEntitiesHighlighter.renderHighlightedEntities(event);
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

    /**
     * This class is responsible for removing the NEI ping handler from the list of input handlers. This is done to
     * prevent the NEI ping handler from interfering with the FindIt keybind.
     */
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

    /**
     * Returns the instance of the client item find service.
     */
    public static ClientItemFindService getInstance() {
        return (ClientItemFindService) FindIt.getItemFindService();
    }
}
