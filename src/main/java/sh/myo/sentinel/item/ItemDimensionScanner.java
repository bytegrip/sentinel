package sh.myo.sentinel.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sh.myo.sentinel.Sentinel;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDimensionScanner extends Item {

    private static final String NBT_FUEL = "ScannerFuel";
    private static final int MAX_FUEL = 256;
    private static final int FUEL_PER_USE = 8;

    public ItemDimensionScanner() {
        setMaxStackSize(1);
        setRegistryName("dimension_scanner");
        setTranslationKey("sentinel.dimension_scanner");
        setCreativeTab(sh.myo.sentinel.SentinelTab.INSTANCE);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int fuel = getFuel(stack);
        tooltip.add(TextFormatting.DARK_GRAY + "Cross-Dimensional Detection System");
        tooltip.add(TextFormatting.GRAY + "Scans: " + TextFormatting.AQUA + "All Dimensions");
        tooltip.add(TextFormatting.GRAY + "Accuracy: " + TextFormatting.AQUA + "80% " + TextFormatting.DARK_GRAY + "(20% error rate)");
        tooltip.add(TextFormatting.GRAY + "Fuel: " + TextFormatting.AQUA + fuel + TextFormatting.DARK_GRAY + "/" + FUEL_PER_USE + " for 1 scan");
        if (fuel >= FUEL_PER_USE) {
            tooltip.add(TextFormatting.DARK_GRAY + "Right-click to scan dimensions");
        } else {
            tooltip.add(TextFormatting.RED + "No fuel - refill with Thulium Blocks");
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (worldIn.isRemote) {
            int guiId = handIn == EnumHand.MAIN_HAND ? 2 : 3;
            playerIn.openGui(Sentinel.instance, guiId, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
        }
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static int getFuel(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return 0;
        }
        return nbt.getInteger(NBT_FUEL);
    }
    
    public static void setFuel(ItemStack stack, int fuel) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(NBT_FUEL, Math.min(fuel, MAX_FUEL));
    }
    
    public static void consumeFuel(ItemStack stack) {
        int current = getFuel(stack);
        if (current >= FUEL_PER_USE) {
            setFuel(stack, current - FUEL_PER_USE);
        }
    }

    public int getMaxFuel() {
        return MAX_FUEL;
    }

    public int getFuelPerUse() {
        return FUEL_PER_USE;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - ((double) getFuel(stack) / (double) MAX_FUEL);
    }
}
