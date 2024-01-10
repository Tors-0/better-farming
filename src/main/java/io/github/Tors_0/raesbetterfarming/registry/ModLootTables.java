package io.github.Tors_0.raesbetterfarming.registry;

import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;

import static io.github.Tors_0.raesbetterfarming.RaesBetterFarming.LOGGER;

public class ModLootTables {
    public static void init() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && LootTables.VILLAGE_TOOLSMITH_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.HAMMER_));
                tableBuilder.pool(poolBuilder);
            }
        });
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && LootTables.VILLAGE_WEAPONSMITH_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.HAMMER_));
                tableBuilder.pool(poolBuilder);
            }
        });

        LOGGER.info("{} finished registering loot tables", RaesBetterFarming.ID);
    }
}
