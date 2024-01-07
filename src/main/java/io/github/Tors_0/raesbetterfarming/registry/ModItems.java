package io.github.Tors_0.raesbetterfarming.registry;

import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;
import io.github.Tors_0.raesbetterfarming.item.ScytheItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// public static final Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));

    // scythe attack speed/damage = ( hoe attack speed + sword attack speed ) / 2
    public static final Item WOODEN_SCYTHE = createItem("wooden_scythe",
            new ScytheItem(1.5f,-2.7f,ToolMaterials.WOOD, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item STONE_SCYTHE = createItem("stone_scythe",
            new ScytheItem(1,-2.2f,ToolMaterials.STONE, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item GOLDEN_SCYTHE = createItem("golden_scythe",
            new ScytheItem(1.5f,-2.7f,ToolMaterials.GOLD, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item IRON_SCYTHE = createItem("iron_scythe",
            new ScytheItem(0.5f,-1.7f,ToolMaterials.IRON, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item DIAMOND_SCYTHE = createItem("diamond_scythe",
            new ScytheItem(0,-1.2f,ToolMaterials.DIAMOND, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item NETHERITE_SCYTHE = createItem("netherite_scythe",
            new ScytheItem(-0.5f,-1.2f,ToolMaterials.NETHERITE, new QuiltItemSettings().group(ItemGroup.TOOLS)));

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
