//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.Tors_0.raesbetterfarming.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import io.github.Tors_0.raesbetterfarming.extensions.PlayerInventoryExtension;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.Tors_0.raesbetterfarming.networking.RBFNetworking.HARVEST_PACKET_ID;

public class SickleItem extends SwordItem implements Vanishable {
    private final TagKey<Block> effectiveBlocks;
    protected final float miningSpeed;
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public SickleItem(float toolBaseDamage, float attackSpeed, ToolMaterial material, Item.Settings settings) {
        super(material, (int) toolBaseDamage, attackSpeed, settings);
        this.effectiveBlocks = BlockTags.HOE_MINEABLE;
        this.miningSpeed = material.getMiningSpeedMultiplier();
        this.attackDamage = toolBaseDamage + material.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", (double)this.attackDamage, Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", (double)attackSpeed, Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();

        ActionResult[] letsHarvest = new ActionResult[]{blockHarvest(context, blockPos),
                blockHarvest(context, blockPos.north()),
                blockHarvest(context, blockPos.east()),
                blockHarvest(context, blockPos.south()),
                blockHarvest(context, blockPos.west())};
        for (ActionResult aR : letsHarvest) {
            if (aR == ActionResult.SUCCESS) {
                return aR;
            }
        }
        return ActionResult.PASS;
    }
    public ActionResult blockHarvest(ItemUsageContext context, BlockPos blockPos) {
        World world = context.getWorld();
        if (!world.isClient()) {
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) context.getPlayer();
            Block block = world.getBlockState(blockPos).getBlock();
            if (block instanceof CropBlock crop && crop.isMature(world.getBlockState(blockPos)) && playerEntity != null) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> cropDrops = world.getBlockState(blockPos).getDroppedStacks(builder);
                for (ItemStack i : cropDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                BlockPos finalPos1 = blockPos;
                ItemStack pickStack = (crop.getPickStack(world, finalPos1, world.getBlockState(finalPos1)));
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf(pickStack.getItem()), 1, playerEntity.getInventory())
                        || SeedPouchItem.removeOne(playerEntity.getInventory().getStack(((PlayerInventoryExtension)playerEntity.getInventory()).raes_farming$indexOf(false)),pickStack)
                ) {
                    world.setBlockState(blockPos, crop.getDefaultState());
                } else {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockPos);
                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world,blockPos)) {
                    ServerPlayNetworking.send(player, HARVEST_PACKET_ID, buf);
                }
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                return ActionResult.SUCCESS;
            } else if (block instanceof SugarCaneBlock && playerEntity != null) {
                if (!(world.getBlockState(blockPos.down()).getBlock() instanceof SugarCaneBlock)) {
                    if (!(world.getBlockState(blockPos.up()).getBlock() instanceof SugarCaneBlock)) {
                        return ActionResult.FAIL;
                    }
                    blockPos = blockPos.up();
                }
                short countCanes = 0;
                while (world.getBlockState(blockPos.up(countCanes)).getBlock() instanceof SugarCaneBlock) {
                    world.breakBlock(blockPos.up(countCanes),false);
                    countCanes++;
                }
                ItemStack i = new ItemStack(Items.SUGAR_CANE,countCanes);
                if (!playerEntity.giveItemStack(i)) {
                    playerEntity.dropStack(i);
                }
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                return ActionResult.SUCCESS;
            } else if (block instanceof CocoaBlock cocoa && playerEntity != null && world.getBlockState(blockPos).get(CocoaBlock.AGE) == 2) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> cocoaDrops = world.getBlockState(blockPos).getDroppedStacks(builder);
                for (ItemStack i : cocoaDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf(Items.COCOA_BEANS), 1, playerEntity.getInventory())) {
                    world.setBlockState(blockPos, cocoa.getDefaultState());
                } else {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockPos);
                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world,blockPos)) {
                    ServerPlayNetworking.send(player, HARVEST_PACKET_ID, buf);
                }
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(CocoaBlock.AGE,0),2);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                return ActionResult.SUCCESS;
            } else if (block instanceof NetherWartBlock netherWart && playerEntity != null && world.getBlockState(blockPos).get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE) {
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.MAIN_HAND))
                        .parameter(LootContextParameters.TOOL, playerEntity.getStackInHand(Hand.OFF_HAND));
                List<ItemStack> wartDrops = world.getBlockState(blockPos).getDroppedStacks(builder);
                for (ItemStack i : wartDrops) {
                    if (!playerEntity.giveItemStack(i)) {
                        playerEntity.dropStack(i);
                    }
                }
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf(Items.NETHER_WART), 1, playerEntity.getInventory())) {
                    world.setBlockState(blockPos, netherWart.getDefaultState());
                } else {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockPos);
                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world,blockPos)) {
                    ServerPlayNetworking.send(player, HARVEST_PACKET_ID, buf);
                }
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(NetherWartBlock.AGE,0),2);
                context.getStack().damage(1, playerEntity, (p) -> {
                    p.sendToolBreakStatus(context.getHand());
                });
                return ActionResult.SUCCESS;
            }
        }
        return null;
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(this.effectiveBlocks) ? this.miningSpeed : 1.0F;
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, (e) -> { // unlike a regular hoe, this only takes 1 durability for attacks as it can be a weapon also
            e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        world.playSound(player,player.getBlockPos(),SoundEvents.BLOCK_SMITHING_TABLE_USE,SoundCategory.BLOCKS,1f,1f);
    }

    public boolean isSuitableFor(BlockState state) {
        int i = this.getMaterial().getMiningLevel();
        if (i < 3 && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        } else if (i < 2 && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        } else {
            return i < 1 && state.isIn(BlockTags.NEEDS_STONE_TOOL) ? false : state.isIn(this.effectiveBlocks) || state.isOf(Blocks.COBWEB);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.raesbetterfarming.sickles").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true)));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
