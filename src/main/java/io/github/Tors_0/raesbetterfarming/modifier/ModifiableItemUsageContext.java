package io.github.Tors_0.raesbetterfarming.modifier;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class ModifiableItemUsageContext extends ItemUsageContext {
    public ModifiableItemUsageContext(ItemUsageContext context, BlockPos pos) {
        super(context.getPlayer(),context.getHand(),new BlockHitResult(context.getHitPos(),context.getSide(),pos, context.hitsInsideBlock()));
    }
}
