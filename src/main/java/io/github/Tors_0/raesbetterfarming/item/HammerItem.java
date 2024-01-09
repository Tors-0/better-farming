package io.github.Tors_0.raesbetterfarming.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.BlockTags;

public class HammerItem extends MiningToolItem implements Vanishable {
    public HammerItem(float toolBaseDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(toolBaseDamage,attackSpeed,material,BlockTags.PICKAXE_MINEABLE, settings);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return Ingredient.ofItems(Items.IRON_INGOT).test(ingredient);
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> { // unlike a regular pickaxe, this only takes 1 durability for attacks as it can be a weapon also
            e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
        });
        return true;
    }
}
