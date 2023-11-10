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

public class SeismicHornRecipe extends SpecialCraftingRecipe {
    private static final Ingredient ECHO_CHAMBER = Ingredient.ofItems(ModItems.ECHO_CHAMBER);
    private static final Ingredient GOAT_HORN = Ingredient.ofItems(Items.GOAT_HORN);
    private static final Ingredient SCULK_SHRIEKER = Ingredient.ofItems(Items.SCULK_SHRIEKER);

    public SeismicHornRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        for (int i = 3; i < 6; ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (GOAT_HORN.test(itemStack)) {
                    return ECHO_CHAMBER.test(inventory.getStack(i - 3)) && SCULK_SHRIEKER.test(inventory.getStack(i + 3));
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        for (int i = 0; i < 3; ++i) {
            ItemStack echoStack = inventory.getStack(i);
            if (
                    !echoStack.isEmpty()
                    && echoStack.getNbt() != null
                    && echoStack.getOrCreateNbt().contains("Items")
                    && ItemStack.fromNbt(echoStack.getOrCreateNbt().getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0)).getCount() == 2
            ) {
                if (inventory.getStack(i+3).isOf(Items.GOAT_HORN) && inventory.getStack(i+6).isOf(Items.SCULK_SHRIEKER)) {
                    return new ItemStack(ModItems.SEISMIC_HORN);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(ModItems.SEISMIC_HORN);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DOTWarden.SEISMIC_HORN_RECIPE;
    }
}
