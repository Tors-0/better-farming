package io.github.Tors_0.dotwarden.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulItem extends Item {
    public SoulItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.dotwarden.captured_soul.tooltip")
                .setStyle(Style.EMPTY.withColor(Formatting.ITALIC).withColor(Formatting.DARK_GRAY)));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
