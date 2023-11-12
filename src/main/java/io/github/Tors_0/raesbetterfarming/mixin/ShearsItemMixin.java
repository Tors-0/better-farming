package io.github.Tors_0.raesbetterfarming.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
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

import static io.github.Tors_0.raesbetterfarming.networking.RBFNetworking.HARVEST_PACKET_ID;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
    public void raes_farming$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        if (!world.isClient()) {
            PlayerEntity playerEntity = context.getPlayer();
            BlockPos pos = context.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();

            if ((block instanceof VineBlock vines ^ block instanceof WeepingVinesPlantBlock) && (playerEntity != null)) {
                if (!(world.getBlockState(pos.up()).getBlock() instanceof VineBlock || world.getBlockState(pos.up()).getBlock() instanceof WeepingVinesPlantBlock)) {
                    pos = pos.down();
                    if (!(world.getBlockState(pos.down()).getBlock() instanceof VineBlock || world.getBlockState(pos.down()).getBlock() instanceof WeepingVinesPlantBlock)) {
                        cir.setReturnValue(ActionResult.FAIL);
                    }
                }
                short countVines = 0;
                while (world.getBlockState(pos.down(countVines)).getBlock() instanceof VineBlock || world.getBlockState(pos.down(countVines)).getBlock() instanceof WeepingVinesPlantBlock) {
                    world.breakBlock(pos.down(countVines),false,playerEntity,1);
                    countVines++;
                }
                world.breakBlock(pos.down(countVines),false,playerEntity,1);
                countVines++;

                ItemStack i = new ItemStack(block instanceof VineBlock ? Items.VINE : Items.WEEPING_VINES, countVines);
                if (!playerEntity.giveItemStack(i)) {
                    playerEntity.dropStack(i);
                }
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            } else if (block instanceof TwistingVinesPlantBlock vine && playerEntity != null) {
                if (!(world.getBlockState(pos.down()).getBlock() instanceof TwistingVinesPlantBlock)) {
                    pos = pos.up();
                    if (!(world.getBlockState(pos.up()).getBlock() instanceof TwistingVinesPlantBlock)) {
                        cir.setReturnValue(ActionResult.FAIL);
                    }
                }
                short countVines = 0;
                while (world.getBlockState(pos.up(countVines)).getBlock() instanceof TwistingVinesPlantBlock) {
                    world.breakBlock(pos.up(countVines),false,playerEntity,1);
                    countVines++;
                }
                world.breakBlock(pos.up(countVines),false,playerEntity,1);
                countVines++;
                ItemStack i = new ItemStack(Items.TWISTING_VINES,countVines);
                if (!playerEntity.giveItemStack(i)) {
                    playerEntity.dropStack(i);
                }
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
