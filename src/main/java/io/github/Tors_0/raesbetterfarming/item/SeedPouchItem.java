package io.github.Tors_0.raesbetterfarming.item;

import io.github.Tors_0.raesbetterfarming.registry.ModTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SeedPouchItem extends Item {
    public static final int MAX_STORAGE = 384;
    public SeedPouchItem(Settings settings) {
        super(settings);
    }

    public static Optional<ItemStack> getAmmoForPreview(ItemStack stack) {
        return Optional.ofNullable(ItemStack.fromNbt(stack.getOrCreateNbt().getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0)));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = this.place(new ItemPlacementContext(context));
        if (!actionResult.isAccepted()) {
            return this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
        } else {
            return actionResult;
        }
    }
    public ActionResult place(ItemPlacementContext context) {
        if (!context.canPlace()) {
            return ActionResult.FAIL;
        } else {
            ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
            if (itemPlacementContext == null) {
                return ActionResult.FAIL;
            } else {
                BlockState blockState = this.getPlacementState(itemPlacementContext);
                ItemStack itemStack = itemPlacementContext.getStack();
                if (blockState == null) {
                    return ActionResult.FAIL;
                } else if (!this.place(itemPlacementContext, blockState)) {
                    return ActionResult.FAIL;
                } else if (getSeedPouchOccupancy(itemStack) < 1) {
                    return ActionResult.FAIL;
                } else {
                    BlockPos blockPos = itemPlacementContext.getBlockPos();
                    World world = itemPlacementContext.getWorld();
                    PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                    BlockState blockState2 = world.getBlockState(blockPos);
                    if (blockState2.isOf(blockState.getBlock())) {
                        blockState2 = this.placeFromTag(blockPos, world, itemStack, blockState2);
                        this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
                        blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
                        if (playerEntity instanceof ServerPlayerEntity) {
                            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                        }
                    }

                    BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
                    world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.create(playerEntity, blockState2));

                    ItemStack consumed = removeFirstStack(itemStack).get();
                    consumed.decrement(1);
                    if (!consumed.isEmpty()) {
                        addToBundle(itemStack,consumed);
                    }

                    return ActionResult.success(world.isClient);
                }
            }
        }
    }
    public Block getBlock(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
        if (!nbtList.isEmpty()) {
            NbtCompound nbtCompound2 = nbtList.getCompound(0);
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
            if (itemStack.getItem() instanceof BlockItem blockItem) {
                return blockItem.getBlock();
            }
        }
        return Blocks.AIR;
    }
    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundGroup().getPlaceSound();
    }

    @Nullable
    public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
        return context;
    }

    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        return writeTagToBlockEntity(world, player, pos, stack);
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext context) {
        BlockState blockState = this.getBlock(context.getPlayer().getStackInHand(context.getHand())).getPlacementState(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }
    private BlockState placeFromTag(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
            StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();
            Iterator var9 = nbtCompound2.getKeys().iterator();

            while(var9.hasNext()) {
                String string = (String)var9.next();
                Property<?> property = stateManager.getProperty(string);
                if (property != null) {
                    String string2 = nbtCompound2.get(string).asString();
                    blockState = with(blockState, property, string2);
                }
            }
        }

        if (blockState != state) {
            world.setBlockState(pos, blockState, 2);
        }

        return blockState;
    }
    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return (BlockState)property.parse(name).map((value) -> {
            return (BlockState)state.with(property, value);
        }).orElse(state);
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PlayerEntity playerEntity = context.getPlayer();
        ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
        return (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(state, context.getBlockPos(), shapeContext);
    }

    protected boolean checkStatePlacement() {
        return true;
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
    }

    public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null) {
            return false;
        } else {
            NbtCompound nbtCompound = getBlockEntityNbtFromStack(stack);
            if (nbtCompound != null) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null) {
                    if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
                        return false;
                    }

                    NbtCompound nbtCompound2 = blockEntity.toNbt();
                    NbtCompound nbtCompound3 = nbtCompound2.copy();
                    nbtCompound2.copyFrom(nbtCompound);
                    if (!nbtCompound2.equals(nbtCompound3)) {
                        blockEntity.readNbt(nbtCompound2);
                        blockEntity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }
    @Nullable
    public static NbtCompound getBlockEntityNbtFromStack(ItemStack stack) {
        return stack.getSubNbt("BlockEntityTag");
    }
    @Override
    public boolean onClickedOnOther(ItemStack thisStack, Slot otherSlot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack itemStack = otherSlot.getStack();
            if (itemStack.isEmpty()) {
                if (!thisStack.getOrCreateNbt().getList("Items", NbtElement.COMPOUND_TYPE).isEmpty()) {
                    this.playRemoveOneSound(player);
                }
                removeFirstStack(thisStack).ifPresent(removedStack -> addToBundle(thisStack, otherSlot.insertStack(removedStack)));
            } else if (itemStack.getItem().canBeNested() && itemStack.isIn(ModTags.Items.SEEDS)) {
                int i = (MAX_STORAGE - getSeedPouchOccupancy(thisStack)) / getItemOccupancy(itemStack);
                int j = addToBundle(thisStack, otherSlot.takeStackRange(itemStack.getCount(), i, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }
            return true;
        }
    }

    @Override
    public boolean onClicked(
            ItemStack thisStack, ItemStack otherStack, Slot thisSlot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference
    ) {
        if (clickType == ClickType.RIGHT && thisSlot.canTakePartial(player) && otherStack.isIn(ModTags.Items.SEEDS)) {
            if (otherStack.isEmpty()) {
                removeFirstStack(thisStack).ifPresent(itemStack -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set(itemStack);
                });
            } else {
                int i = addToBundle(thisStack, otherStack);
                if (i > 0) {
                    this.playInsertSound(player);
                    otherStack.decrement(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }
    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * -0.4F);
    }
    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                if (itemStack.getCount() > 64) {
                    ItemStack outputStack = new ItemStack(itemStack.getItem(),64);
                    itemStack.decrement(64);
                    nbtList.remove(0);
                    addToBundle(stack,itemStack);
                    return Optional.of(outputStack);
                }
                nbtList.remove(0);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }
    private static Optional<ItemStack> removeLastStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(nbtList.size()-1);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                nbtList.remove(nbtList.size()-1);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }
    public static boolean removeOne(ItemStack bundle, ItemStack stack) {
        NbtCompound nbtCompound = bundle.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return false;
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            if (nbtList.isEmpty()) {
                return false;
            } else {
                Optional<NbtCompound> nbtCompound1 = nbtList.stream()
                        .filter(NbtCompound.class::isInstance)
                        .map(NbtCompound.class::cast)
                        .filter(nbt -> ItemStack.fromNbt(nbt).getItem().equals(stack.getItem()))
                        .findFirst();
                if (nbtCompound1.isPresent()) {
                    ItemStack stack1 = ItemStack.fromNbt(nbtCompound1.get());
                    stack1.decrement(1);
                    stack1.writeNbt(nbtCompound1.get());
                    nbtList.remove(nbtCompound1.get());
                    nbtList.add(0,nbtCompound1.get());
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
    public static int addStackToBundle(ItemStack bundle, ItemStack stack) {
        return addToBundle(bundle,stack);
    }
    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canBeNested() && stack.isIn(ModTags.Items.SEEDS)) {
            NbtCompound nbtCompound = bundle.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }

            int seedPouchOccupancy = getSeedPouchOccupancy(bundle);
            int slotsPerItem = getItemOccupancy(stack);
            int itemsToAdd = Math.min(stack.getCount(), (MAX_STORAGE - seedPouchOccupancy) / slotsPerItem);
            if (itemsToAdd == 0) {
                return 0;
            } else {
                NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
                Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
                if (optional.isPresent()) {
                    NbtCompound nbtCompound2 = optional.get();
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    if (itemStack.getCount() + stack.getCount() > 127) {
                        int totalItemCount = itemStack.getCount() + stack.getCount();

                        itemStack.setCount(127);
                        totalItemCount -= 127;
                        itemStack.writeNbt(nbtCompound2);
                        nbtList.remove(nbtCompound2);
                        nbtList.add(0, nbtCompound2);

                        ItemStack leftovers = new ItemStack(stack.getItem(),totalItemCount);
                        NbtCompound nbtCompound3 = new NbtCompound();
                        leftovers.writeNbt(nbtCompound3);
                        nbtList.add(0,nbtCompound3);
                    } else {
                        itemStack.increment(itemsToAdd);
                        itemStack.writeNbt(nbtCompound2);
                        nbtList.remove(nbtCompound2);
                        nbtList.add(0, nbtCompound2);
                    }
                } else {
                    ItemStack itemStack2 = stack.copy();
                    itemStack2.setCount(itemsToAdd);
                    NbtCompound nbtCompound3 = new NbtCompound();
                    itemStack2.writeNbt(nbtCompound3);
                    nbtList.add(0, nbtCompound3);
                }

                return itemsToAdd;
            }
        } else {
            return 0;
        }
    }
    public static float getAmountFilled(ItemStack stack) {
        return (float) getSeedPouchOccupancy(stack) / (float) MAX_STORAGE;
    }
    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        return items.stream()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .filter(nbt -> ItemStack.canCombine(ItemStack.fromNbt(nbt), stack)/* && ItemStack.fromNbt(nbt).getCount() + stack.getCount() <= 190*/)
                .findFirst();
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            Optional<ItemStack> lastStack = removeLastStack(itemStack);
            if (lastStack.isPresent()) {
                addToBundle(itemStack,lastStack.get());
                return TypedActionResult.success(itemStack,world.isClient());
            } else {
                return TypedActionResult.fail(itemStack);
            }
        } else if (dropAllBundledItems(itemStack, user)) {
            this.playDropContentsSound((Entity) user);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }
    private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);

                for(int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound nbtCompound2 = nbtList.getCompound(i);
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    player.dropItem(itemStack, true);
                }
            }
            stack.removeSubNbt("Items");
            return true;
        }
    }
    private static int getItemOccupancy(ItemStack stack) {
        return (int)(64f / (float)stack.getMaxCount());
    }
    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * getSeedPouchOccupancy(stack) / MAX_STORAGE, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.packRgb(0.4F, 0.4F, 1.0F);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getOrCreateNbt().contains("Items");
    }

    private static int getSeedPouchOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }
    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
        }
    }
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        Stream<ItemStack> stacks = getBundledStacks(stack);
        Objects.requireNonNull(defaultedList);
        stacks.forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, getSeedPouchOccupancy(stack)));
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.minecraft.bundle.fullness", new Object[]{getSeedPouchOccupancy(stack), MAX_STORAGE}).formatted(Formatting.GRAY));
    }
}
