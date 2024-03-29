package io.github.Tors_0.raesbetterfarming.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
	@Shadow
	public abstract int getMaxAge();

    @Shadow
	public abstract void applyGrowth(World world, BlockPos pos, BlockState state);

    @Shadow
    protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        return 0;
    }

    @Inject(method = "randomTick", at = @At(value = "HEAD", target = "Lnet/minecraft/util/random/RandomGenerator;nextInt(I)I"), cancellable = true)
	public void raes_farming$randomCropTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random, CallbackInfo ci) {
        float f = getAvailableMoisture(state.getBlock(),world,pos);
		if (f < 2) {
			ci.cancel();
		}
	}
}
