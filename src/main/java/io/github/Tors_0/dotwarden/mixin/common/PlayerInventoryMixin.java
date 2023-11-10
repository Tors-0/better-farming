package io.github.Tors_0.dotwarden.mixin.common;

import io.github.Tors_0.dotwarden.common.registry.ModItems;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    // code credit to https://github.com/Grohiik/sticky-elytra for this mixin code
    @Shadow
    public abstract boolean isEmpty();

    @Redirect(method = "dropAll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean
    onDropAll(ItemStack stack) {
        return isEmpty() || stack.getItem() == ModItems.CORRUPTED_HEART;
    }
}
