package net.jothb.pride;

import net.fabricmc.api.ModInitializer;
import net.jothb.pride.networking.ModMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P_ride implements ModInitializer {

    public static final String MOD_ID = "p-ride";

    public static final Logger LOGGER = LoggerFactory.getLogger("modid");

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ModMessages.registerS2CPackets();

        LOGGER.info("Hi");

    }
}
