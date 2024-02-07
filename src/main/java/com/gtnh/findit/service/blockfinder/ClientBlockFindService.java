package com.gtnh.findit.service.blockfinder;

import com.gtnh.findit.service.itemfinder.ItemFoundResponse;
import com.gtnh.findit.util.AxisUtils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import org.lwjgl.input.Keyboard;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.fx.ParticlePosition;
import com.gtnh.findit.util.AbstractStackFinder;

import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;

public class ClientBlockFindService extends BlockFindService {

    public ClientBlockFindService() {
        API.addHashBind("gui.findit.find_block", Keyboard.KEY_Y);
        GuiContainerManager.addInputHandler(new BlockFindInputHandler());
    }

    public void handleResponse(EntityClientPlayerMP player, BlockFoundResponse response) {
        player.closeScreen();
        ParticlePosition.highlightBlocks(player.worldObj, response.getPositions());
        lookAtTarget(player, response);
    }

    public void lookAtTarget(EntityClientPlayerMP player, BlockFoundResponse response){
        Vec3 playerVec = player.getPosition(1.0F);
        ChunkPosition first = response.getPositions().remove(0);

        AxisUtils.AxisPair pair = AxisUtils.calculateAxisPair(playerVec, first);

        player.rotationYaw = pair.yaw();
        player.rotationPitch = pair.pitch();
    }

    private static class BlockFindInputHandler extends AbstractStackFinder {

        @Override
        protected String getKeyBindId() {
            return "gui.findit.find_block";
        }

        @Override
        protected boolean findStack(ItemStack stack) {
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block == Blocks.air) {
                return false;
            }

            FindItNetwork.CHANNEL.sendToServer(new FindBlockRequest(block, stack.getItemDamage()));
            return true;
        }
    }

    public static ClientBlockFindService getInstance() {
        return (ClientBlockFindService) FindIt.getBlockFindService();
    }
}
