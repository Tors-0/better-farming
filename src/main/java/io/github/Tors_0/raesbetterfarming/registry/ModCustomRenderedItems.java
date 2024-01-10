package io.github.Tors_0.raesbetterfarming.registry;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.item.Item;

import java.util.Set;
import java.util.function.Consumer;

public class ModCustomRenderedItems {
    private static final Set<Item> ITEMS = new ReferenceOpenHashSet<>();

    public static void register(Item item) {
        ITEMS.add(item);
    }

    public static void forEach(Consumer<Item> consumer) {
        ITEMS.forEach(consumer);
    }
}
