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

public interface ModItems {
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    // public static final Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));

    // sickle attack speed/damage = ( hoe attack speed + sword attack speed ) / 2
    public static final Item WOODEN_SICKLE = createItem("wooden_sickle",
            new SickleItem(2f,-3.1f,ToolMaterials.WOOD, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item STONE_SICKLE = createItem("stone_sickle",
            new SickleItem(1.5f,-2.6f,ToolMaterials.STONE, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item GOLDEN_SICKLE = createItem("golden_sickle",
            new SickleItem(2f,-3.1f,ToolMaterials.GOLD, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item IRON_SICKLE = createItem("iron_sickle",
            new SickleItem(1f,-2.1f,ToolMaterials.IRON, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item DIAMOND_SICKLE = createItem("diamond_sickle",
            new SickleItem(0.5f,-1.6f,ToolMaterials.DIAMOND, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    public static final Item NETHERITE_SICKLE = createItem("netherite_sickle",
            new SickleItem(0f,-1.6f,ToolMaterials.NETHERITE, new QuiltItemSettings().group(ItemGroup.TOOLS)));
    // add a hammer to use for making sickles (hammer and sickle farming mod lets gooo!)
    public static final Item HAMMER = createItem("hammer",
            new HammerItem(ToolMaterials.IRON, new QuiltItemSettings().group(ItemGroup.TOOLS).recipeDamageRemainder(10)));
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
