package sh.myo.sentinel.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import sh.myo.sentinel.item.ItemTieredRadar;

public class RecipeTieredRadarRefuel extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final Item radarItem;
    private final Item fuelItem;
    private final int maxFuel;

    public RecipeTieredRadarRefuel(Item radarItem, Item fuelItem, int maxFuel) {
        this.radarItem = radarItem;
        this.fuelItem = fuelItem;
        this.maxFuel = maxFuel;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean hasRadar = false;
        int fuelCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == radarItem) {
                    if (hasRadar) return false;
                    hasRadar = true;
                } else if (stack.getItem() == fuelItem) {
                    fuelCount++;
                } else {
                    return false;
                }
            }
        }

        return hasRadar && fuelCount > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack radar = ItemStack.EMPTY;
        int fuelCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == radarItem) {
                    radar = stack.copy();
                } else if (stack.getItem() == fuelItem) {
                    fuelCount++;
                }
            }
        }

        if (!radar.isEmpty() && fuelCount > 0) {
            int currentFuel = ItemTieredRadar.getFuel(radar);
            int newFuel = Math.min(currentFuel + fuelCount, maxFuel);
            
            ItemStack result = radar.copy();
            ItemTieredRadar.setFuel(result, newFuel, maxFuel);
            return result;
        }

        return ItemStack.EMPTY;
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

    @Override
    public boolean isDynamic() {
        return true;
    }
}
