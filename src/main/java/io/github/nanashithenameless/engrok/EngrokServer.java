package io.github.nanashithenameless.engrok;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static io.github.nanashithenameless.engrok.Engrok.LOGGER;

@Environment(EnvType.SERVER)
public class EngrokServer implements DedicatedServerModInitializer {
    public static int port;
    @Override
    public void onInitializeServer() {
        LOGGER.info("Dedicated server detected!");
    }
}
