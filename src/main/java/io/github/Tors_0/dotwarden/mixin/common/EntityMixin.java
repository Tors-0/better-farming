package io.github.Tors_0.dotwarden.mixin.common;

import io.github.Tors_0.dotwarden.common.item.DiscipleArmorItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.IntStream;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isPlayer();

    @Shadow
    public abstract void emitGameEvent(GameEvent event);

    @Shadow
    public abstract boolean saveNbt(NbtCompound nbt);

    @Inject(method = "dampensVibrations", at = @At(value = "HEAD"), cancellable = true)
    public void dotwarden$dampen(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof PlayerEntity player) {
            boolean sneaky = IntStream.rangeClosed(0, 3).mapToObj(player.getInventory()::getArmorStack)
                    .allMatch(itemStack -> itemStack.getItem() instanceof DiscipleArmorItem);
            if (sneaky) cir.setReturnValue(true);
        }
    }
}
