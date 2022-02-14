package com.gtnh.findit.service.itemfinder;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.util.WorldUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import java.util.*;

public class ItemFindService {

    private final Map<EntityPlayerMP, ItemFoundResult> lastResults = new HashMap<>();
    private final Map<EntityPlayerMP, Integer> lastProcessedContainers = new HashMap<>(); // (player) -> (windowId)

    public ItemFindService() {
        FindItNetwork.registerMessage(FindItemRequest.Handler.class, FindItemRequest.class, Side.SERVER);
        FindItNetwork.registerMessage(ItemFoundResponse.Handler.class, ItemFoundResponse.class, Side.CLIENT);
        FindItNetwork.registerMessage(HighlightSlotsPacket.Handler.class, HighlightSlotsPacket.class, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new ContainerListener());
    }

    private ItemFoundResult getLastFoundResult(EntityPlayerMP player) {
        ItemFoundResult lastResult = lastResults.get(player);
        if (lastResult != null && !lastResult.isValid(player.getEntityWorld())) {
            lastResults.remove(player);
            return null;
        }
        return lastResult;
    }

    public void handleRequest(EntityPlayerMP player, FindItemRequest request) {
        if (FindIt.getCooldownService().checkSearchCooldown(player)) {
            return;
        }

        Item itemToFind = request.getStackToFind().getItem();
        int metaToFind = request.getStackToFind().getItemDamage();

        List<ChunkPosition> positions = new ArrayList<>();

        tileIter:
        for (TileEntity tileEntity : WorldUtils.getTileEntitiesAround(player, FindItConfig.SEARCH_RADIUS)) {
            try {
                if (!(tileEntity instanceof IInventory)) {
                    continue;
                }
                IInventory inventory = (IInventory) tileEntity;

                for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                    ItemStack stackInSlot = inventory.getStackInSlot(slot);
                    if (stackInSlot == null || stackInSlot.getItem() != itemToFind) {
                        continue;
                    }

                    if (!itemToFind.getHasSubtypes() || stackInSlot.getItemDamage() == metaToFind) {
                        positions.add(new ChunkPosition(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
                        if (positions.size() == FindItConfig.MAX_RESPONSE_SIZE) {
                            break tileIter;
                        }
                        break;
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (!positions.isEmpty()) {
            ItemFoundResult foundResult = new ItemFindService.ItemFoundResult(
                    request.getStackToFind(), positions,
                    player.getEntityWorld().getTotalWorldTime()
            );

            lastResults.put(player, foundResult);
            FindItNetwork.CHANNEL.sendTo(new ItemFoundResponse(request.getStackToFind(), positions), player);
        }
    }

    private void processOpenContainer(EntityPlayerMP player) {
        lastProcessedContainers.put(player, player.openContainer.windowId);
        Container container = player.openContainer;

        ItemFoundResult lastFoundResult = getLastFoundResult(player);
        if (lastFoundResult == null) {
            return;
        }

        List<Slot> slots = (List<Slot>) container.inventorySlots;
        List<Integer> targetSlots = new ArrayList<>();
        Set<ChunkPosition> tilePositions = new HashSet<>();

        for (int slotId = 0; slotId < slots.size(); slotId++) {
            Slot slot = slots.get(slotId);
            IInventory inventory = getSlotTrueInventory(slot);
            if (!(inventory instanceof TileEntity)) {
                continue;
            }

            TileEntity tile = (TileEntity) inventory;
            ChunkPosition tilePos = new ChunkPosition(tile.xCoord, tile.yCoord, tile.zCoord);

            if (lastFoundResult.containsPosition(tilePos)) {
                targetSlots.add(slotId);
                tilePositions.add(tilePos);

                if (targetSlots.size() >= 256) {
                    break;
                }
            }
        }

        if (!targetSlots.isEmpty()) {
            tilePositions.forEach(lastFoundResult::pollPosition);

            FindItNetwork.CHANNEL.sendTo(
                    new HighlightSlotsPacket(container.windowId, lastFoundResult.foundStack, targetSlots),
                    player
            );
        }
    }

    private IInventory getSlotTrueInventory(Slot slot) {
        if (slot.inventory instanceof InventoryLargeChest) {
            InventoryLargeChest largeChest = (InventoryLargeChest) slot.inventory;
            return slot.slotNumber < largeChest.upperChest.getSizeInventory() ? largeChest.upperChest : largeChest.lowerChest;
        }
        return slot.inventory;
    }

    public class ContainerListener {

        // called every tick to check if player can interact with container
        @SubscribeEvent
        public void onContainerOpen(PlayerOpenContainerEvent event) {
            if (event.entityPlayer.worldObj.isRemote) {
                return;
            }
            EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;

            // may not spend time to process player inventory
            if (player.openContainer == player.inventoryContainer) {
                return;
            }

            int lastProcessedContainer = lastProcessedContainers.getOrDefault(player, -1);
            if (player.openContainer.windowId != lastProcessedContainer) {
                processOpenContainer(player);
            }
        }
    }

    public static class ItemFoundResult {
        private final ItemStack foundStack;
        private final Set<ChunkPosition> positions = new HashSet<>();
        private final long responseTime;

        public ItemFoundResult(ItemStack foundStack, List<ChunkPosition> positions, long responseTime) {
            this.foundStack = foundStack;
            this.positions.addAll(positions);
            this.responseTime = responseTime;
        }

        public ItemStack getFoundStack() {
            return foundStack;
        }

        public boolean isValid(World world) {
            return !positions.isEmpty() && world.getTotalWorldTime() - responseTime < 20 * 60; // 60 sec
        }

        public boolean containsPosition(ChunkPosition position) {
            return positions.contains(position);
        }

        public void pollPosition(ChunkPosition position) {
            positions.remove(position);
        }
    }
}
