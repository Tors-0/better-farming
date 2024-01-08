package io.github.Tors_0.raesbetterfarming.registry;

import io.github.Tors_0.raesbetterfarming.RaesBetterFarming;
import io.github.Tors_0.raesbetterfarming.item.HammerItem;
import io.github.Tors_0.raesbetterfarming.item.SickleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.Tors_0.raesbetterfarming.RaesBetterFarming.LOGGER;

public interface ModItems {
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    // Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));

    // sickle attack damage = ( hoe attack dmg + sword attack dmg ) / 2
    // sickle attack speed = ( hoe attack speed + axe attack speed ) / 2
    Item WOODEN_SICKLE = createItem("wooden_sickle",
            new SickleItem(2f,-3.1f,ToolMaterials.WOOD, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    Item STONE_SICKLE = createItem("stone_sickle",
            new SickleItem(1.5f,-2.6f,ToolMaterials.STONE, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    Item GOLDEN_SICKLE = createItem("golden_sickle",
            new SickleItem(2f,-3f,ToolMaterials.GOLD, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    Item IRON_SICKLE = createItem("iron_sickle",
            new SickleItem(1f,-2.05f,ToolMaterials.IRON, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    Item DIAMOND_SICKLE = createItem("diamond_sickle",
            new SickleItem(0.5f,-1.5f,ToolMaterials.DIAMOND, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    Item NETHERITE_SICKLE = createItem("netherite_sickle",
            new SickleItem(0f,-1.5f,ToolMaterials.NETHERITE, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    // add a hammer to use for making sickles (hammer and sickle farming mod lets gooo!)
    Item HAMMER = createItem("hammer",
            new HammerItem(4f,-2.9f,ToolMaterials.DIAMOND, new QuiltItemSettings().group(ItemGroup.TOOLS).recipeDamageRemainder(20)));
    private static <T extends Item> T createItem(String name, T item) {
        ITEMS.put(item, new Identifier(RaesBetterFarming.ID, name));
        return item;
    }

    static void init() {
        ITEMS.keySet().forEach(item -> {
            Registry.register(Registry.ITEM, ITEMS.get(item), item);
        });

        LOGGER.info("{} finished registering items", RaesBetterFarming.ID);
    }
}
