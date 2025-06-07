package py660.rickcraft;

import net.fabricmc.api.ModInitializer;

import net.minecraft.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rickcraft implements ModInitializer {
    public static final String MOD_ID = "rickcraft";
    //public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        //LOGGER.info("Yo Yo Yo!!!");
        ModItems.initialize();
    }
}