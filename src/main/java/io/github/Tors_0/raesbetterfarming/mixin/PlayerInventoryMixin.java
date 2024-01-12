package io.github.Tors_0.raesbetterfarming.mixin;

import io.github.Tors_0.raesbetterfarming.extensions.PlayerInventoryExtension;
import io.github.Tors_0.raesbetterfarming.item.SeedPouchItem;
import io.github.Tors_0.raesbetterfarming.registry.ModTags;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements PlayerInventoryExtension {
    @Shadow
    public abstract ItemStack getStack(int slot);


    @Shadow
    public abstract boolean insertStack(int slot, ItemStack stack);

    @Shadow
    @Final
    public DefaultedList<ItemStack> main;

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
    public void raes_farming$hijackSeedItemsToPouch(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isIn(ModTags.Items.SEEDS)) {
            if (this.raes_farming$indexOf(true) != -1) {
                NbtCompound nbtCompound = this.getStack(this.raes_farming$indexOf(true)).getOrCreateNbt();
                if (nbtCompound.contains("Enabled")) {
                    if (!nbtCompound.getBoolean("Enabled")) {
                        return;
                    }
                }
                int itemsAdded = SeedPouchItem.addStackToBundle(
                        this.getStack(this.raes_farming$indexOf(true)),
                        stack
                );
                if (itemsAdded >= stack.getCount()) {
                    stack.setCount(0);
                    cir.setReturnValue(true);
                } else {
                    ItemStack overflow = new ItemStack(stack.getItem(),stack.getCount()-itemsAdded);
                    stack.setCount(0);
                    cir.setReturnValue(this.insertStack(-1,overflow));
                }
            }
        }
    }

    @Unique
    @Override
    public int raes_farming$indexOf(boolean withSpace) {
        for (int i = 0; i < this.main.size(); i++) {
            if (this.main.get(i).getItem() instanceof SeedPouchItem) {
                if (!withSpace) {
                    return i;
                } else {
                    if (SeedPouchItem.isEnabled(this.main.get(i)) && SeedPouchItem.getAmountFilled(this.main.get(i)) < 1f) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
