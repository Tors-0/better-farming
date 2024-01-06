package io.github.Tors_0.raesbetterfarming.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import java.util.function.Predicate;

import static io.github.Tors_0.raesbetterfarming.networking.RBFNetworking.HARVEST_PACKET_ID;

@Mixin(HoeItem.class)
public class HoeItemMixin {
	@Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
	public void raes_farming$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		World world = context.getWorld();
		if (!world.isClient()) {
			ServerPlayerEntity playerEntity = (ServerPlayerEntity) context.getPlayer();
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
                BlockPos finalPos1 = pos;
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf((crop.getPickStack(world, finalPos1, world.getBlockState(finalPos1))).getItem()), 1, playerEntity.getInventory())) {
                    world.setBlockState(pos, crop.getDefaultState());
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send(playerEntity,HARVEST_PACKET_ID,buf);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
				cir.setReturnValue(ActionResult.SUCCESS);
			} else if (block instanceof SugarCaneBlock && playerEntity != null) {
                if (!(world.getBlockState(pos.down()).getBlock() instanceof SugarCaneBlock)) {
                    pos = pos.up();
                    if (!(world.getBlockState(pos.up()).getBlock() instanceof SugarCaneBlock)) {
                        cir.setReturnValue(ActionResult.FAIL);
                    }
                }
                short countCanes = 0;
                while (world.getBlockState(pos.up(countCanes)).getBlock() instanceof SugarCaneBlock) {
                    world.breakBlock(pos.up(countCanes),false);
                    countCanes++;
                }
                ItemStack i = new ItemStack(Items.SUGAR_CANE,countCanes);
                if (!playerEntity.giveItemStack(i)) {
                    playerEntity.dropStack(i);
                }
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (block instanceof CocoaBlock cocoa && playerEntity != null && world.getBlockState(pos).get(CocoaBlock.AGE) == 2) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> cocoaDrops = world.getBlockState(pos).getDroppedStacks(builder);
                for (ItemStack i : cocoaDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf(Items.COCOA_BEANS), 1, playerEntity.getInventory())) {
                    world.setBlockState(pos, cocoa.getDefaultState());
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send(playerEntity,HARVEST_PACKET_ID,buf);
                world.setBlockState(pos, world.getBlockState(pos).with(CocoaBlock.AGE,0),2);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (block instanceof NetherWartBlock netherWart && playerEntity != null && world.getBlockState(pos).get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> wartDrops = world.getBlockState(pos).getDroppedStacks(builder);
                for (ItemStack i : wartDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf(Items.NETHER_WART), 1, playerEntity.getInventory())) {
                    world.setBlockState(pos, netherWart.getDefaultState());
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send(playerEntity,HARVEST_PACKET_ID,buf);
                world.setBlockState(pos, world.getBlockState(pos).with(NetherWartBlock.AGE,0),2);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
	}
}
