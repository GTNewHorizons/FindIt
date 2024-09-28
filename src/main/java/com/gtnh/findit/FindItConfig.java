package com.gtnh.findit;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class FindItConfig {

    public static int SEARCH_RADIUS = 16;
    public static int SEARCH_COOLDOWN = 10;
    public static int MAX_RESPONSE_SIZE = 20;
    public static boolean ENABLE_ROTATE_VIEW = false;
    public static boolean USE_PARTICLE_HIGHLIGHTER = false;
    public static boolean SEARCH_ITEMS_ON_GROUND = true;
    public static boolean SEARCH_IN_GT_PIPES = false;
    public static boolean SEARCH_IN_ENDERIO_CONDUITS = false;
    public static int ITEM_HIGHLIGHTING_DURATION = 10;
    public static int BLOCK_HIGHLIGHTING_DURATION = 8;
    public static int ITEM_HIGHLIGHTING_COLOR = 0xFFFF8726;
    public static boolean ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS = true;

    public static void setup(final File file) {
        final Configuration config = new Configuration(file);
        try {
            config.load();

            SEARCH_RADIUS = config.get(Configuration.CATEGORY_GENERAL, "SearchRadius", "16", "Radius to search within")
                    .getInt();

            SEARCH_COOLDOWN = config
                    .get(Configuration.CATEGORY_GENERAL, "SearchCooldown", "10", "Search cooldown in ticks").getInt();

            MAX_RESPONSE_SIZE = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "MaxResponseSize",
                    "20",
                    "Maximum number of positions that can be displayed by item/block search").getInt();

            USE_PARTICLE_HIGHLIGHTER = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "UseParticleHighlighter",
                    "false",
                    "Use Particle for Block Highlighter").getBoolean();

            SEARCH_IN_GT_PIPES = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "SearchInGregTechPipes",
                    "false",
                    "Search items & fluids in GT pipes").getBoolean();

            SEARCH_IN_ENDERIO_CONDUITS = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "SearchInEnderIOConduits",
                    "false",
                    "Search items & fluids in EnderIO conduits").getBoolean();

            SEARCH_ITEMS_ON_GROUND = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "SearchItemsOnGround",
                    "true",
                    "Search items dropped on ground").getBoolean();

            ITEM_HIGHLIGHTING_DURATION = config
                    .get(Configuration.CATEGORY_GENERAL, "ItemHighlightingDuration", "10", "Item highlighting duration")
                    .getInt();

            BLOCK_HIGHLIGHTING_DURATION = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "BlockHighlightingDuration",
                    "8",
                    "Block highlighting duration in seconds").getInt();

            ENABLE_ROTATE_VIEW = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "EnableRotateView",
                    "false",
                    "Rotate player's view when searched").getBoolean();

            ITEM_HIGHLIGHTING_COLOR = Integer.parseUnsignedInt(
                    config.get(
                            Configuration.CATEGORY_GENERAL,
                            "ItemHighlightingColor",
                            "FFFF8726",
                            "Item highlighting color as a hexadecimal color code. For example 0xFFFF8726").getString(),
                    16);

            ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "ItemHighlightingEmptyItemStacks",
                    "true",
                    "If true, the item stack size is ignored. If false, items are only highlighted if their stack size is greater than zero.\n"
                            + "This is useful when working with barrels or storage drawers.")
                    .getBoolean();
        } catch (Exception ignore) {} finally {
            if (config.hasChanged()) config.save();
        }
    }
}
