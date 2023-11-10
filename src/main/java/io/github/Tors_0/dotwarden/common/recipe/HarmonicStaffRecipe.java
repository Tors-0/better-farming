package io.github.Tors_0.dotwarden.common.recipe;

import io.github.Tors_0.dotwarden.common.DOTWarden;
import io.github.Tors_0.dotwarden.common.registry.ModItems;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HarmonicStaffRecipe extends SpecialCraftingRecipe {
    private static final Ingredient ECHO_CHAMBER = Ingredient.ofItems(ModItems.ECHO_CHAMBER);
    private static final Ingredient ECHO_SHARD = Ingredient.ofItems(Items.ECHO_SHARD);
    private static final Ingredient AMETHYST_CLUSTER = Ingredient.ofItems(Items.AMETHYST_CLUSTER);

    public HarmonicStaffRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        for (int i = 6; i < 9; ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (ECHO_SHARD.test(itemStack)) {
                    return ECHO_CHAMBER.test(inventory.getStack(i - 3)) && AMETHYST_CLUSTER.test(inventory.getStack(i - 6));
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        for (int i = 3; i < 6; ++i) {
            ItemStack echoStack = inventory.getStack(i);
            if (
                    !echoStack.isEmpty()
                    && echoStack.getNbt() != null
                    && echoStack.getOrCreateNbt().contains("Items")
                    && ItemStack.fromNbt(echoStack.getOrCreateNbt().getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0)).getCount() == 1
            ) {
                if (inventory.getStack(i-3).isOf(Items.AMETHYST_CLUSTER) && inventory.getStack(i+3).isOf(Items.ECHO_SHARD)) {
                    return new ItemStack(ModItems.HARMONIC_STAFF);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(ModItems.HARMONIC_STAFF);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DOTWarden.HARMONIC_STAFF_RECIPE;
    }
}
