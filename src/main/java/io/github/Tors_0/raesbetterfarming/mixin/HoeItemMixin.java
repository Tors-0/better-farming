package io.github.Tors_0.raesbetterfarming.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
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

import static io.github.Tors_0.raesbetterfarming.networking.RBFNetworking.HARVEST_PACKET_ID;

@Mixin(HoeItem.class)
public class HoeItemMixin {
	@Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
	public void raes_farming$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		World world = context.getWorld();
		if (!world.isClient()) {
			PlayerEntity playerEntity = context.getPlayer();
			BlockPos pos = context.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof CropBlock crop && crop.isMature(world.getBlockState(pos)) && playerEntity != null) {
                LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) world)
                        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                        .add(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .add(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
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
				world.setBlockState(pos, crop.getDefaultState());
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
            } else if (block instanceof CocoaBlock && playerEntity != null && world.getBlockState(pos).get(CocoaBlock.AGE) == 2) {
                LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) world)
                        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                        .add(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .add(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> cocoaDrops = world.getBlockState(pos).getDroppedStacks(builder);
                for (ItemStack i : cocoaDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                playerEntity.getInventory().removeOne(new ItemStack(Items.COCOA_BEANS));
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity,HARVEST_PACKET_ID,buf);
                world.setBlockState(pos, world.getBlockState(pos).with(CocoaBlock.AGE,0),2);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (block instanceof NetherWartBlock && playerEntity != null && world.getBlockState(pos).get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE) {
                LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) world)
                        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                        .add(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .add(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> wartDrops = world.getBlockState(pos).getDroppedStacks(builder);
                for (ItemStack i : wartDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                playerEntity.getInventory().removeOne(new ItemStack(Items.NETHER_WART));
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity,HARVEST_PACKET_ID,buf);
                world.setBlockState(pos, world.getBlockState(pos).with(NetherWartBlock.AGE,0),2);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
	}
}
