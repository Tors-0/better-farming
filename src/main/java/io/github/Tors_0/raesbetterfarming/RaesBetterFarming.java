package io.github.Tors_0.raesbetterfarming;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.piece.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.villager.api.TradeOfferHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.Tors_0.raesbetterfarming.registry.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class RaesBetterFarming implements ModInitializer {
	public static final String ID = "raesbetterfarming";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Now initializing {} version {}", mod.metadata().name(), mod.metadata().version());

		ModItems.init();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && LootTables.VILLAGE_TOOLSMITH_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.HAMMER));
                tableBuilder.pool(poolBuilder);
            }
        });
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && LootTables.VILLAGE_WEAPONSMITH_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.HAMMER));
                tableBuilder.pool(poolBuilder);
            }
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.TOOLSMITH, 1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 10),
                            new ItemStack(ModItems.HAMMER, 1),
                            1, 5, 0.02f
                    ));
                });
	}
}
