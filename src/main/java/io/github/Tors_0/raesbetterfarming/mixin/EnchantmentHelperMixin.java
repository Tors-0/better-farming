package io.github.Tors_0.raesbetterfarming.mixin;

import io.github.Tors_0.raesbetterfarming.item.SickleItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getSweepingMultiplier", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void raes_farming$doSweepingForSickle(LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        if (entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SickleItem) {
            int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, entity) + 1;
            cir.setReturnValue(i > 0 ? SweepingEnchantment.getMultiplier(i) + 0.4f : 0.0F);
        }
    }
    @Inject(method = "getLooting", at = @At(value = "HEAD"), cancellable = true)
    private static void raes_farming$doLootingForSickle(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SickleItem) {
            cir.setReturnValue(EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, entity) + 1);
        }
    }
}
