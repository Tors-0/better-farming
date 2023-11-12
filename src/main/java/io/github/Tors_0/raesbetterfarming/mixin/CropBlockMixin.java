package io.github.Tors_0.raesbetterfarming.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static io.github.Tors_0.raesbetterfarming.RaesBetterFarming.LOGGER;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
	@Shadow
	public abstract int getMaxAge();

	@Shadow
	public abstract BlockState withAge(int age);

	@Shadow
	public abstract void applyGrowth(World world, BlockPos pos, BlockState state);

	@Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/random/RandomGenerator;nextInt(I)I"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void raes_farming$randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random, CallbackInfo ci, int i, float f) {
		if (f >= 3.5 && i < this.getMaxAge()-1 && Math.random()/3 > (1f/f)) {
			this.applyGrowth(world,pos,state);
		}
	}
}
