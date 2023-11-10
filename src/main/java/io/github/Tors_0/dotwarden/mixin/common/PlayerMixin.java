package io.github.Tors_0.dotwarden.mixin.common;

import io.github.Tors_0.dotwarden.common.extensions.PlayerExtensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin implements PlayerExtensions {
    @Shadow
    protected abstract void vanishCursedItems();

    @Unique
    private int dotwarden$powerLevel = 0;
    @Unique
    private int dotwarden$power = 0;
    @Unique
    private boolean dotwarden$hasSacrificed = false;
    @Override
    public int dotwarden$getPowerLevel() {
        return dotwarden$powerLevel;
    }
    @Override
    public void dotwarden$setPowerLevel(int value) {
        this.dotwarden$powerLevel = value;
    }

    @Override
    public void dotwarden$addPowerLevel(int value) {
        this.dotwarden$powerLevel += value;
    }

    @Override
    public int dotwarden$getPower() {
        return this.dotwarden$power;
    }

    @Override
    public void dotwarden$setPower(int value) {
        this.dotwarden$power = value;
    }

    @Override
    public void dotwarden$addPower(int value) {
        this.dotwarden$power += value;
    }

    @Override
    public boolean dotwarden$hasSacrificed() {
        return this.dotwarden$hasSacrificed;
    }

    @Override
    public void dotwarden$setSacrifice(boolean state) {
        this.dotwarden$hasSacrificed = state;
    }

    @Inject(method="writeCustomDataToNbt", at = @At("TAIL"))
    public void dotwarden$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("dotwarden$powerlevel",this.dotwarden$powerLevel);
        nbt.putInt("dotwarden$power",this.dotwarden$power);
        nbt.putBoolean("dotwarden$hasSacrificed",this.dotwarden$hasSacrificed);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void dotwarden$readCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.dotwarden$powerLevel = nbt.getInt("dotwarden$powerlevel");
        this.dotwarden$power = nbt.getInt("dotwarden$power");
        this.dotwarden$hasSacrificed = nbt.getBoolean("dotwarden$hasSacrificed");
    }
}
