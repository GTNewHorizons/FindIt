package com.gtnh.findit.util.mods;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.storage.inventory.ItemInventoryBackpack;
import forestry.storage.items.ItemBackpack;

public class ForestryUtils {

    private ForestryUtils() {}

    public static Optional<IInventory> getInventoryOfPotentialStorageItem(ItemStack potentialBackpackItemStack) {
        // Checks for Forestry backpacks.
        Item item = potentialBackpackItemStack.getItem();
        if (item instanceof ItemBackpack) {
            ItemBackpack backpack = (ItemBackpack) item;

            // We're running the GUI code client-side only, thus the only player interacting with the GUI is the
            // player that the backpack's inventory is checked against.
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            ItemInventoryBackpack inventory = new ItemInventoryBackpack(
                    player,
                    backpack.getBackpackSize(),
                    potentialBackpackItemStack);
            return Optional.of(inventory);
        }

        return Optional.empty();
    }
}
