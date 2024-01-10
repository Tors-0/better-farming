package io.github.Tors_0.raesbetterfarming;

import com.tterrag.registrate.Registrate;
import io.github.Tors_0.raesbetterfarming.registry.ModItems;
import io.github.Tors_0.raesbetterfarming.registry.ModLootTables;
import io.github.Tors_0.raesbetterfarming.registry.ModTradeOffers;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaesBetterFarming implements ModInitializer {
	public static final String ID = "raesbetterfarming";
	 /*
	 This logger is used to write text to the console and the log file.
	 It is considered best practice to use your mod name as the logger's name.
	 That way, it's clear which mod wrote info, warnings, and errors
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static final Registrate REGISTRATE = Registrate.create(ID);

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Now initializing {} version {}", mod.metadata().name(), mod.metadata().version());

        // register all mod items
		ModItems.init();
        // register all loot table modifications
        ModLootTables.init();
        // register all trade modifications
        ModTradeOffers.init();

        LOGGER.info("{} finished mod initialization", mod.metadata().name());
	}
}
