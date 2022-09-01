package com.gtnh.findit;

import com.gtnh.findit.service.blockfinder.BlockFindService;
import com.gtnh.findit.service.blockfinder.ClientBlockFindService;
import com.gtnh.findit.service.cooldown.SearchCooldownService;
import com.gtnh.findit.service.itemfinder.ClientItemFindService;
import com.gtnh.findit.service.itemfinder.ItemFindService;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(
        modid = FindIt.MOD_ID,
        name = FindIt.MOD_NAME,
        version = FindIt.VERSION,
        dependencies = "required-after:NotEnoughItems;required-after:gregtech",
        acceptableRemoteVersions = "*"
)
public class FindIt {

    // Mod info
    public static final String MOD_ID = "GRADLETOKEN_MODID";
    public static final String MOD_NAME = "GRADLETOKEN_MODNAME";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    @Mod.Instance(MOD_ID)
    public static FindIt INSTANCE;

    private boolean extraUtilitiesLoaded;

    private SearchCooldownService cooldownService;
    private BlockFindService blockFindService;
    private ItemFindService itemFindService;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        extraUtilitiesLoaded = Loader.isModLoaded("ExtraUtilities");
        FindItConfig.setup(event.getSuggestedConfigurationFile());
        boolean isClient = event.getSide() == Side.CLIENT;

        cooldownService = new SearchCooldownService();
        blockFindService = isClient ? new ClientBlockFindService() : new BlockFindService();
        itemFindService = isClient ? new ClientItemFindService() : new ItemFindService();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
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
}
