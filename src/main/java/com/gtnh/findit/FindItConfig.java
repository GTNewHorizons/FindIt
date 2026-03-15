package com.gtnh.findit;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "findit")
public class FindItConfig {

    @Config.Comment("Radius to search within")
    @Config.DefaultInt(16)
    @Config.RangeInt(min = 1)
    @Config.Name("SearchRadius")
    public static int SEARCH_RADIUS;

    @Config.Comment("Search cooldown in ticks")
    @Config.DefaultInt(10)
    @Config.RangeInt(min = 0)
    @Config.Name("SearchCooldown")
    public static int SEARCH_COOLDOWN;

    @Config.Comment("Maximum number of positions that can be displayed by item/block search")
    @Config.DefaultInt(20)
    @Config.RangeInt(min = 0)
    @Config.Name("MaxResponseSize")
    public static int MAX_RESPONSE_SIZE;

    @Config.Comment("Rotate the player's view to face found items/blocks after searching")
    @Config.DefaultBoolean(false)
    @Config.Name("EnableRotateView")
    public static boolean ENABLE_ROTATE_VIEW;

    @Config.Comment("Use particles for the block highlighter instead of outlining found blocks")
    @Config.DefaultBoolean(false)
    @Config.Name("UseParticleHighlighter")
    public static boolean USE_PARTICLE_HIGHLIGHTER;

    @Config.Comment("Search items dropped on the ground")
    @Config.DefaultBoolean(true)
    @Config.Name("SearchItemsOnGround")
    public static boolean SEARCH_ITEMS_ON_GROUND;

    @Config.Comment("Search items and fluids in GT pipes")
    @Config.DefaultBoolean(false)
    @Config.Name("SearchInGregTechPipes")
    public static boolean SEARCH_IN_GT_PIPES;

    @Config.Comment("Search items and fluids in EnderIO conduits")
    @Config.DefaultBoolean(false)
    @Config.Name("SearchInEnderIOConduits")
    public static boolean SEARCH_IN_ENDERIO_CONDUITS;

    @Config.Comment("Item highlighting duration in seconds")
    @Config.DefaultInt(10)
    @Config.RangeInt(min = 1)
    @Config.Name("ItemHighlightingDuration")
    public static int ITEM_HIGHLIGHTING_DURATION;

    @Config.Comment("Block highlighting duration in seconds")
    @Config.DefaultInt(8)
    @Config.RangeInt(min = 1)
    @Config.Name("BlockHighlightingDuration")
    public static int BLOCK_HIGHLIGHTING_DURATION;

    @Config.Comment("Item highlighting color as an ARGB hexadecimal color code.\nDo not prefix with \"0x\"")
    @Config.DefaultString("FFFF8726")
    @Config.Name("ItemHighlightingColor")
    public static String ITEM_HIGHLIGHTING_COLOR;

    @Config.Comment("If true, the item stack size is ignored. If false, items are only highlighted if their stack size is greater than zero.\n"
            + "This is useful when working with barrels or storage drawers.")
    @Config.DefaultBoolean(true)
    @Config.Name("ItemHighlightingEmptyItemStacks")
    public static boolean ITEM_HIGHLIGHTING_EMPTY_ITEMSTACKS;
}
