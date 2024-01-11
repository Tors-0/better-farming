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
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
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

import java.awt.*;
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
    protected static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS;

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
// add a tooltip saying "innate sweeping damage" or smth
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = (Pair) TILLING_ACTIONS.get(world.getBlockState(blockPos).getBlock());
        if (pair == null) {
            ActionResult letsHarvest = blockHarvest(context, blockPos);
            blockHarvest(context, blockPos.north());
            blockHarvest(context, blockPos.east());
            blockHarvest(context, blockPos.south());
            blockHarvest(context, blockPos.west());
            if (letsHarvest != null) {
                return letsHarvest;
            }
            return ActionResult.PASS;
        } else {
            Predicate<ItemUsageContext> predicate = (Predicate) pair.getFirst();
            Consumer<ItemUsageContext> consumer = (Consumer) pair.getSecond();
            // tills blocks in a plus shape (same as harvesting)
            tillBlock(new ItemUsageContext(context.getPlayer(),context.getHand(),new BlockHitResult(context.getHitPos(),context.getSide(),blockPos.north(),context.hitsInsideBlock())));
            tillBlock(new ItemUsageContext(context.getPlayer(),context.getHand(),new BlockHitResult(context.getHitPos(),context.getSide(),blockPos.east(),context.hitsInsideBlock())));
            tillBlock(new ItemUsageContext(context.getPlayer(),context.getHand(),new BlockHitResult(context.getHitPos(),context.getSide(),blockPos.south(),context.hitsInsideBlock())));
            tillBlock(new ItemUsageContext(context.getPlayer(),context.getHand(),new BlockHitResult(context.getHitPos(),context.getSide(),blockPos.west(),context.hitsInsideBlock())));
            if (predicate.test(context)) {
                PlayerEntity playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClient) {
                    consumer.accept(context);
                    if (playerEntity != null) {
                        context.getStack().damage(1, playerEntity, (p) -> {
                            p.sendToolBreakStatus(context.getHand());
                        });
                    }
                }

                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.PASS;
            }
        }
    }
    public void tillBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = (Pair) TILLING_ACTIONS.get(world.getBlockState(blockPos).getBlock());
        if (pair == null) {
            return;
        } else {
            Predicate<ItemUsageContext> predicate = (Predicate) pair.getFirst();
            Consumer<ItemUsageContext> consumer = (Consumer) pair.getSecond();
            if (predicate.test(context)) {
                PlayerEntity playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClient) {
                    consumer.accept(context);
                    if (playerEntity != null) {
                        context.getStack().damage(1, playerEntity, (p) -> {
                            p.sendToolBreakStatus(context.getHand());
                        });
                    }
                }
            }
        }
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
                if (1 == playerEntity.getInventory().remove(itemStack -> itemStack.isOf((crop.getPickStack(world, finalPos1, world.getBlockState(finalPos1))).getItem()), 1, playerEntity.getInventory())) {
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

    public static Consumer<ItemUsageContext> createTillAction(BlockState result) {
        return (context) -> {
            context.getWorld().setBlockState(context.getBlockPos(), result, 11);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Context.create(context.getPlayer(), result));
        };
    }

    public static Consumer<ItemUsageContext> createTillAndDropAction(BlockState result, ItemConvertible droppedItem) {
        return (context) -> {
            context.getWorld().setBlockState(context.getBlockPos(), result, 11);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Context.create(context.getPlayer(), result));
            Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(droppedItem));
        };
    }

    static {
        TILLING_ACTIONS = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())), Blocks.DIRT_PATH, Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())), Blocks.DIRT, Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())), Blocks.COARSE_DIRT, Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.DIRT.getDefaultState())), Blocks.ROOTED_DIRT, Pair.of((itemUsageContext) -> {
            return true;
        }, createTillAndDropAction(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))));
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
