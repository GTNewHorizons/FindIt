package com.gtnh.findit.fx;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.gtnh.findit.util.ClientFinderHelperUtils;

public class BlockHighlighter {

    private List<ChunkPosition> positions = new ArrayList<>();
    private long expireHighlight;

    public void highlightBlocks(List<ChunkPosition> positions, long expireHighlight) {
        this.positions = positions;
        this.expireHighlight = expireHighlight;
    }

    public void renderHighlightedBlock(RenderWorldLastEvent event) {
        if (this.positions.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        long time = System.currentTimeMillis();

        if (((time / 500) & 1) == 0) {
            return;
        }

        if (time > expireHighlight) {
            positions.clear();
        }

        EntityPlayerSP p = mc.thePlayer;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glLineWidth(3);
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

        for (ChunkPosition pos : this.positions) {
            drawBlockHighlightBox(mc.theWorld, pos);
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void drawBlockHighlightBox(WorldClient world, ChunkPosition pos) {
        Block block = world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);

        AxisAlignedBB box;
        if (block instanceof BlockChest) {
            box = getChestHighlightBox(world, (BlockChest) block, pos);
        } else {
            box = getBlockHighlightBox(world, block, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
        }

        FxHelper.renderBoxOutline(box);
    }

    private static AxisAlignedBB getChestHighlightBox(WorldClient world, BlockChest block, ChunkPosition pos) {
        AxisAlignedBB chestBox = getBlockHighlightBox(world, block, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);

        TileEntity tileEntity = world.getTileEntity(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
        if (!(tileEntity instanceof TileEntityChest)) {
            return chestBox;
        }
        TileEntityChest chest = (TileEntityChest) tileEntity;

        TileEntityChest nearChest = null;
        if (chest.adjacentChestXNeg != null) {
            nearChest = chest.adjacentChestXNeg;
        } else if (chest.adjacentChestXPos != null) {
            nearChest = chest.adjacentChestXPos;
        } else if (chest.adjacentChestZNeg != null) {
            nearChest = chest.adjacentChestZNeg;
        } else if (chest.adjacentChestZPos != null) {
            nearChest = chest.adjacentChestZPos;
        }
        if (nearChest == null) {
            return chestBox;
        }

        AxisAlignedBB nearChestBox = getBlockHighlightBox(
                world,
                nearChest.getBlockType(),
                nearChest.xCoord,
                nearChest.yCoord,
                nearChest.zCoord);
        return ClientFinderHelperUtils.mergeAABB(chestBox, nearChestBox);
    }

    private static AxisAlignedBB getBlockHighlightBox(WorldClient world, Block block, int x, int y, int z) {
        block.setBlockBoundsBasedOnState(world, x, y, z);
        AxisAlignedBB box = block.getSelectedBoundingBoxFromPool(world, x, y, z);
        if (box.maxX - box.minX < 0.25f || box.maxY - box.minY < 0.25f || box.maxZ - box.minZ < 0.25f) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        }
        return box;
    }
}
