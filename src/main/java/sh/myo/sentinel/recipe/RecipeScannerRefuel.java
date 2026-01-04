package sh.myo.sentinel.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import sh.myo.sentinel.Sentinel;
import sh.myo.sentinel.item.ItemDimensionScanner;

public class RecipeScannerRefuel extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean hasScanner = false;
        int thuliumBlockCount = 0;
        
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Sentinel.DIMENSION_SCANNER) {
                    if (hasScanner) return false;
                    hasScanner = true;
                } else if (stack.getItem() == Item.getItemFromBlock(Sentinel.THULIUM_BLOCK)) {
                    thuliumBlockCount++;
                } else {
                    return false;
                }
            }
        }
        
        return hasScanner && thuliumBlockCount > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack scannerStack = ItemStack.EMPTY;
        int thuliumBlockCount = 0;
        
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Sentinel.DIMENSION_SCANNER) {
                    scannerStack = stack.copy();
                } else if (stack.getItem() == Item.getItemFromBlock(Sentinel.THULIUM_BLOCK)) {
                    thuliumBlockCount++;
                }
            }
        }
        
        if (!scannerStack.isEmpty() && thuliumBlockCount > 0) {
            int currentFuel = ItemDimensionScanner.getFuel(scannerStack);
            ItemDimensionScanner.setFuel(scannerStack, currentFuel + thuliumBlockCount);
        }
        
        return scannerStack;
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
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }
}
