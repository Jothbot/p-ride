package net.jothb.pride;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P_ride implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("p-ride");
    @Override
    public void onInitialize() {

        LOGGER.info("Ride your players with P-Ride!");

    }
}
