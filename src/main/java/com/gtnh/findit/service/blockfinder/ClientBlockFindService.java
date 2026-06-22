package com.gtnh.findit.service.blockfinder;

import static com.gtnh.findit.util.ClientFinderHelperUtils.lookAtTarget;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
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

import codechicken.nei.ItemList;
import codechicken.nei.LayoutManager;
import codechicken.nei.SearchField;
import codechicken.nei.api.API;
import codechicken.nei.api.ItemFilter;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientBlockFindService extends BlockFindService {

    private final BlockHighlighter blockHighlighter;

    public ClientBlockFindService() {
        this.blockHighlighter = new BlockHighlighter();

        API.addHashBind("gui.findit.find_block", Keyboard.KEY_Y);
        GuiContainerManager.addInputHandler(new BlockFindInputHandler());

        MinecraftForge.EVENT_BUS.register(new WorldRenderListener());
        FMLCommonHandler.instance().bus().register(new TickListener());
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

    public static class TickListener {

        private boolean keyWasDown = false;

        @SubscribeEvent
        public void onClientPostTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            if (Minecraft.getMinecraft().theWorld == null) {
                return;
            }

            final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (!(screen instanceof GuiContainer)) {
                keyWasDown = false;
                return;
            }

            int targetKey = codechicken.nei.NEIClientConfig.getKeyBinding("gui.findit.find_block");
            boolean keyIsDown = Keyboard.isKeyDown(targetKey);
            boolean altIsDown = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
            if (altIsDown && keyIsDown && !keyWasDown) {
                SearchField sf = LayoutManager.searchField;
                if (sf != null) {
                    if (sf.text() != null && !sf.text().isEmpty()) {
                        ItemFilter filter = sf.getFilter();
                        if (filter != null) {
                            Set<FindBlockRequest.BlockKey> matching = new HashSet<>();
                            for (ItemStack itemStack : ItemList.items) {
                                if (filter.matches(itemStack)) {
                                    Block block = Block.getBlockFromItem(itemStack.getItem());
                                    if (block != Blocks.air) {
                                        matching.add(
                                                new FindBlockRequest.BlockKey(
                                                        Block.getIdFromBlock(block),
                                                        itemStack.getItemDamage()));
                                        if (matching.size() > 1000) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!matching.isEmpty()) {
                                FindItNetwork.CHANNEL.sendToServer(new FindBlockRequest(matching));
                            }
                        }
                    }
                }
            }
            keyWasDown = keyIsDown;
        }
    }

    private static class BlockFindInputHandler extends AbstractStackFinder {

        @Override
        protected String getKeyBindId() {
            return "gui.findit.find_block";
        }

        @Override
        public boolean lastKeyTyped(GuiContainer guiContainer, char c, int i) {
            if (!codechicken.nei.NEIClientConfig.isKeyHashDown(getKeyBindId())) {
                return false;
            }

            ItemStack stack = GuiContainerManager.getStackMouseOver(guiContainer);
            if (stack == null || stack.getItem() == null) {
                return false;
            }

            return findStack(stack);
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
