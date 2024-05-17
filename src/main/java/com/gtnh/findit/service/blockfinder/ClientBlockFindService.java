package com.gtnh.findit.service.blockfinder;

import static com.gtnh.findit.util.ClientFinderHelperUtils.lookAtTarget;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.gtnh.findit.FindIt;
import com.gtnh.findit.FindItConfig;
import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.fx.BlockHighlighter;
import com.gtnh.findit.fx.ParticlePosition;
import com.gtnh.findit.util.AbstractStackFinder;

import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientBlockFindService extends BlockFindService {

    private final BlockHighlighter blockHighlighter;

    public ClientBlockFindService() {
        this.blockHighlighter = new BlockHighlighter();

        API.addHashBind("gui.findit.find_block", Keyboard.KEY_Y);
        GuiContainerManager.addInputHandler(new BlockFindInputHandler());

        MinecraftForge.EVENT_BUS.register(new WorldRenderListener());
    }

    public void handleResponse(EntityClientPlayerMP player, BlockFoundResponse response) {

        if (!response.getPositions().isEmpty()) {
            player.closeScreen();

            if (FindItConfig.ENABLE_ROTATE_VIEW) {
                lookAtTarget(player, response);
            }
        }

        if (FindItConfig.USE_PARTICLE_HIGHLIGHTER) {
            ParticlePosition.highlightBlocks(player.worldObj, response.getPositions());
        } else {
            this.blockHighlighter.highlightBlocks(
                    response.getPositions(),
                    System.currentTimeMillis() + FindItConfig.BLOCK_HIGHLIGHTING_DURATION * 1000L);
        }
    }

    public class WorldRenderListener {

        @SubscribeEvent
        public void renderWorldLastEvent(RenderWorldLastEvent event) {
            blockHighlighter.renderHighlightedBlock(event);
        }
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
