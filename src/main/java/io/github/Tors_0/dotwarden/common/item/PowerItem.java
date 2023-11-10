package io.github.Tors_0.dotwarden.common.item;

import io.github.Tors_0.dotwarden.common.DOTWarden;
import io.github.Tors_0.dotwarden.common.extensions.PlayerExtensions;
import io.github.Tors_0.dotwarden.common.networking.DOTWNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.text.Style.EMPTY;

public class PowerItem extends Item {
	private static final Predicate<ItemStack> SCULK_ITEM = itemStack -> (itemStack.isOf(Items.SCULK));

	public PowerItem(Settings settings) {
		super(settings);
	}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            user.getItemCooldownManager().set(this,20);
            ItemStack stack = user.getStackInHand(hand);
            if (user.getInventory().contains(new ItemStack(Items.SCULK))) {
                ((PlayerExtensions)user).dotwarden$addPower(
                        user.getInventory().remove(SCULK_ITEM, ptsUntilNextLevel(stack), user.getInventory())
                );

                while (((PlayerExtensions)user).dotwarden$getPower() >= ptsUntilNextLevel(stack)) {
                    ((PlayerExtensions) user).dotwarden$addPower( - ptsUntilNextLevel(stack));
                    ((PlayerExtensions) user).dotwarden$addPowerLevel(1);
                    stack.getOrCreateSubNbt(DOTWarden.ID).putInt("powerlevels", ((PlayerExtensions) user).dotwarden$getPowerLevel());
                    stack.getOrCreateSubNbt(DOTWarden.ID).putInt("power", ((PlayerExtensions) user).dotwarden$getPower());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(((PlayerExtensions)user).dotwarden$getPowerLevel());
                buf.writeInt(((PlayerExtensions)user).dotwarden$getPower());
                ServerPlayNetworking.send((ServerPlayerEntity)user, DOTWNetworking.POWERLEVEL_PACKET_ID, buf);
                return TypedActionResult.success(stack,true);
            } else {
                while (((PlayerExtensions)user).dotwarden$getPower() >= ptsUntilNextLevel(stack)) {
                    ((PlayerExtensions) user).dotwarden$addPower( - ptsUntilNextLevel(stack));
                    ((PlayerExtensions) user).dotwarden$addPowerLevel(1);
                    stack.getOrCreateSubNbt(DOTWarden.ID).putInt("powerlevels", ((PlayerExtensions) user).dotwarden$getPowerLevel());
                    stack.getOrCreateSubNbt(DOTWarden.ID).putInt("power", ((PlayerExtensions) user).dotwarden$getPower());
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(((PlayerExtensions)user).dotwarden$getPowerLevel());
                buf.writeInt(((PlayerExtensions)user).dotwarden$getPower());
                ServerPlayNetworking.send((ServerPlayerEntity)user, DOTWNetworking.POWERLEVEL_PACKET_ID, buf);
                return TypedActionResult.pass(user.getStackInHand(hand));
            }
        }
        return super.use(world, user, hand);
    }
	public static int ptsUntilNextLevel(ItemStack stack) {
		int level = stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevels");
		if (level >= 30) {
			return (112 + (level - 30) * 9);
		} else {
			return level >= 15 ? (37 + (level - 15) * 5) :
                    (7 + level * 2);
		}
	}
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerExtensions player) {
            if (!world.isClient()) {
                stack.getOrCreateSubNbt(DOTWarden.ID).putInt("powerlevels", player.dotwarden$getPowerLevel());
                stack.getOrCreateSubNbt(DOTWarden.ID).putInt("power", player.dotwarden$getPower());
            }
        }
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack itm = new ItemStack(this);
        itm.getOrCreateSubNbt(DOTWarden.ID).putString("owner","Nobody");
        itm.getOrCreateSubNbt(DOTWarden.ID).putInt("powerlevels", 0);
        itm.getOrCreateSubNbt(DOTWarden.ID).putInt("power", 0);
        return itm;
    }
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }
    @Override
    public int getItemBarStep(ItemStack stack) {
        return (stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevels") % 13) + 1;
    }
    @Override
    public int getItemBarColor(ItemStack stack) {
        return switch ((int) (stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevels") / 13f)) {
            default -> 0x8d6acc;
            case 0 -> 0xff1212;
            case 1 -> 0xf5a031;
            case 2 -> 0xf2f74d;
            case 3 -> 0x8fed66;
            case 4 -> 0x7af5ab;
            case 5 -> 0x6af3f7;
            case 6 -> 0x428aed;
            case 7 -> 0x4e35f0;
        };
    }
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Style style = EMPTY.withColor(Formatting.DARK_AQUA);
		Text tip1 = Text.translatable("item.dotwarden.power_of_the_disciple.tooltip");
		Text tip2 = Text.translatable("item.dotwarden.power_of_the_disciple.tooltip1");
        tooltip.add(Text.literal(!stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner").isEmpty() ?
                stack.getOrCreateSubNbt(DOTWarden.ID).getString("owner"): "Nobody")
                .append(Text.translatable("item.dotwarden.power_of_the_disciple.tooltip2")));
		tooltip.add(Text.literal("").append(tip1).append(": " + stack.getOrCreateSubNbt(DOTWarden.ID).getInt("powerlevels")).setStyle(style));
		tooltip.add(Text.literal("").append(tip2).append(": " + stack.getOrCreateSubNbt(DOTWarden.ID).getInt("power")  + " / " +
                ptsUntilNextLevel(stack)).setStyle(EMPTY.withColor(Formatting.GRAY)));
		super.appendTooltip(stack, world, tooltip, context);
	}
}
