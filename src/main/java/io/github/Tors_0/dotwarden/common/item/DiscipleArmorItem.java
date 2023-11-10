package io.github.Tors_0.dotwarden.common.item;

import io.github.Tors_0.dotwarden.common.registry.ModArmorMaterials;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class DiscipleArmorItem extends ArmorItem {
    public DiscipleArmorItem(EquipmentSlot slot, Settings settings) {
        super(ModArmorMaterials.DISCIPLE, slot, settings);
    }

}
