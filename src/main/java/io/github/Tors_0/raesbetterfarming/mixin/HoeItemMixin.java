package io.github.Tors_0.raesbetterfarming.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static io.github.Tors_0.raesbetterfarming.networking.RBFNetworking.HARVEST_PACKET_ID;

@Mixin(HoeItem.class)
public class HoeItemMixin {
	@Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
	public void raesfarming$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		World world = context.getWorld();
		if (!world.isClient()) {
			PlayerEntity playerEntity = context.getPlayer();
			BlockPos pos = context.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof CropBlock crop && crop.isMature(world.getBlockState(pos)) && playerEntity != null) {
				LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
					.random(world.random)
					.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
					.parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
					.parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
				List<ItemStack> cropDrops = world.getBlockState(pos).getDroppedStacks(builder);
                for (ItemStack i : cropDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                playerEntity.getInventory().removeOne(crop.getPickStack(world,pos,world.getBlockState(pos)));
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity,HARVEST_PACKET_ID,buf);
                world.playSound(playerEntity,pos,SoundEvents.BLOCK_CROP_BREAK,SoundCategory.BLOCKS,1f,1f);
				world.setBlockState(pos, crop.getDefaultState());
				cir.setReturnValue(ActionResult.SUCCESS);
			}
		}
	}
}
