package io.github.Tors_0.raesbetterfarming.registry;

import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, new Identifier(RaesBetterFarming.ID, name));
		return item;
	}

	static void init() {
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registry.ITEM, ITEMS.get(item), item);
		});
	}
}
