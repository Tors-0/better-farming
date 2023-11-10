package io.github.Tors_0.dotwarden.common.item;

import io.github.Tors_0.dotwarden.common.DOTWarden;
import io.github.Tors_0.dotwarden.common.registry.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CorruptedHeartItem extends Item {
    public CorruptedHeartItem(Settings settings) {
        super(settings);
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack itm = new ItemStack(ModItems.CORRUPTED_HEART);
        itm.getOrCreateSubNbt(DOTWarden.ID).putString("owner","Nobody");
        return itm;
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Owner: ").append(!stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner").isEmpty() ?
                stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner"): "Nobody"));
        super.appendTooltip(stack, world, tooltip, context);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return !stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner").isEmpty();
    }
}
