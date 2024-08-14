package com.gtnh.findit;

import com.gtnh.findit.service.blockfinder.BlockFindService;
import com.gtnh.findit.service.blockfinder.ClientBlockFindService;
import com.gtnh.findit.service.cooldown.SearchCooldownService;
import com.gtnh.findit.service.itemfinder.ClientItemFindService;
import com.gtnh.findit.service.itemfinder.ItemFindService;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(
        modid = FindIt.MOD_ID,
        name = FindIt.MOD_NAME,
        version = FindIt.VERSION,
        dependencies = "required-after:NotEnoughItems;after:gregtech",
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.7.10]")
public class FindIt {

    // Mod info
    public static final String MOD_ID = "findit";
    public static final String MOD_NAME = "FindIt";
    public static final String VERSION = Tags.VERSION;

    @Mod.Instance(MOD_ID)
    public static FindIt INSTANCE;

    private boolean extraUtilitiesLoaded;
    private boolean gregTechloaded;
    private boolean enderIOloaded;

    private boolean draconicEvolutionLoaded;

    private SearchCooldownService cooldownService;
    private BlockFindService blockFindService;
    private ItemFindService itemFindService;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.extraUtilitiesLoaded = Loader.isModLoaded("ExtraUtilities");
        this.gregTechloaded = Loader.isModLoaded("gregtech");
        this.enderIOloaded = Loader.isModLoaded("EnderIO");
        this.draconicEvolutionLoaded = Loader.isModLoaded("DraconicEvolution");

        FindItConfig.setup(event.getSuggestedConfigurationFile());
        boolean isClient = event.getSide() == Side.CLIENT;

        this.cooldownService = new SearchCooldownService();
        this.blockFindService = isClient ? new ClientBlockFindService() : new BlockFindService();
        this.itemFindService = isClient ? new ClientItemFindService() : new ItemFindService();
    }

    public static SearchCooldownService getCooldownService() {
        return INSTANCE.cooldownService;
    }

    public static BlockFindService getBlockFindService() {
        return INSTANCE.blockFindService;
    }

    public static ItemFindService getItemFindService() {
        return INSTANCE.itemFindService;
    }

    public static boolean isExtraUtilitiesLoaded() {
        return INSTANCE.extraUtilitiesLoaded;
    }

    public static boolean isGregTechLoaded() {
        return INSTANCE.gregTechloaded;
    }

    public static boolean isEnderIOLoaded() {
        return INSTANCE.enderIOloaded;
    }

    public static boolean isDraconicEvolutionLoaded() {
        return INSTANCE.draconicEvolutionLoaded;
    }
}
