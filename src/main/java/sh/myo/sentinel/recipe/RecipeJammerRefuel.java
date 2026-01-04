package sh.myo.sentinel.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import sh.myo.sentinel.Sentinel;
import sh.myo.sentinel.item.ItemSentinelJammer;

public class RecipeJammerRefuel extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean hasJammer = false;
        int thuliumCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Sentinel.SENTINEL_JAMMER) {
                    if (hasJammer) return false;
                    hasJammer = true;
                } else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(Sentinel.THULIUM_BLOCK)) {
                    thuliumCount++;
                } else {
                    return false;
                }
            }
        }

        return hasJammer && thuliumCount > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack jammerStack = ItemStack.EMPTY;
        int thuliumCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Sentinel.SENTINEL_JAMMER) {
                    jammerStack = stack.copy();
                } else if (stack.getItem() == net.minecraft.item.Item.getItemFromBlock(Sentinel.THULIUM_BLOCK)) {
                    thuliumCount++;
                }
            }
        }

        if (!jammerStack.isEmpty()) {
            int currentFuel = ItemSentinelJammer.getFuel(jammerStack);
            int newFuel = Math.min(currentFuel + thuliumCount, ItemSentinelJammer.getMaxFuel());
            ItemSentinelJammer.setFuel(jammerStack, newFuel);
        }

        return jammerStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
