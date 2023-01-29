package com.gtnh.findit;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class FindItConfig {

    public static int SEARCH_RADIUS = 16;
    public static int SEARCH_COOLDOWN = 10;
    public static int MAX_RESPONSE_SIZE = 20;

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
        } catch (Exception ignore) {} finally {
            if (config.hasChanged()) config.save();
        }
    }
}
