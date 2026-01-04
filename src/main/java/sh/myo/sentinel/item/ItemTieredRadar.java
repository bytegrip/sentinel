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

public abstract class ItemTieredRadar extends Item {

    private static final String NBT_FUEL = "RadarFuel";
    
    protected final String tierName;
    protected final int maxFuel;
    protected final double range;
    protected final double anglePrecision;
    protected final String fuelType;
    protected final int fuelPerUse;

    public ItemTieredRadar(String tierName, int maxFuel, double range, double anglePrecision, String fuelType, int fuelPerUse) {
        this.tierName = tierName;
        this.maxFuel = maxFuel;
        this.range = range;
        this.anglePrecision = anglePrecision;
        this.fuelType = fuelType;
        this.fuelPerUse = fuelPerUse;
        setMaxStackSize(1);
        setCreativeTab(sh.myo.sentinel.SentinelTab.INSTANCE);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (worldIn.isRemote) {
            int guiId = handIn == EnumHand.MAIN_HAND ? 0 : 1;
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
    
    public static void setFuel(ItemStack stack, int fuel, int maxFuel) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(NBT_FUEL, Math.min(fuel, maxFuel));
    }
    
    public static void consumeFuel(ItemStack stack, int maxFuel) {
        int current = getFuel(stack);
        if (current > 0) {
            setFuel(stack, current - 1, maxFuel);
        }
    }
    
    public int getMaxFuel() {
        return maxFuel;
    }
    
    public double getRange() {
        return range;
    }
    
    public double getAnglePrecision() {
        return anglePrecision;
    }
    
    public int getFuelPerUse() {
        return fuelPerUse;
    }
    
    public ItemStack getFuelDisplayItem() {
        switch (fuelType) {
            case "Logs":
                return new ItemStack(net.minecraft.init.Blocks.LOG);
            case "Cobblestone":
                return new ItemStack(net.minecraft.init.Blocks.COBBLESTONE);
            case "Coal Blocks":
                return new ItemStack(net.minecraft.init.Blocks.COAL_BLOCK);
            case "Iron Blocks":
                return new ItemStack(net.minecraft.init.Blocks.IRON_BLOCK);
            case "Gold Blocks":
                return new ItemStack(net.minecraft.init.Blocks.GOLD_BLOCK);
            case "Diamond Blocks":
                return new ItemStack(net.minecraft.init.Blocks.DIAMOND_BLOCK);
            case "Obsidian":
                return new ItemStack(net.minecraft.init.Blocks.OBSIDIAN);
            case "Emerald Blocks":
                return new ItemStack(net.minecraft.init.Blocks.EMERALD_BLOCK);
            case "Thulium Blocks":
                return new ItemStack(Sentinel.THULIUM_BLOCK);
            default:
                return new ItemStack(Sentinel.THULIUM_INGOT);
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int fuel = getFuel(stack);
        tooltip.add(TextFormatting.DARK_GRAY + tierName + " Detection System");
        tooltip.add(TextFormatting.GRAY + "Range: " + TextFormatting.AQUA + formatRange(range));
        tooltip.add(TextFormatting.GRAY + "Precision: " + TextFormatting.AQUA + String.format("%.1f°", anglePrecision));
        tooltip.add(TextFormatting.GRAY + "Fuel: " + TextFormatting.AQUA + fuel + TextFormatting.DARK_GRAY + "/" + maxFuel);
        tooltip.add(TextFormatting.GRAY + "Consumes: " + TextFormatting.AQUA + fuelPerUse + " " + fuelType);
        if (fuel > 0) {
            tooltip.add(TextFormatting.DARK_GRAY + "Right-click to scan area");
        } else {
            tooltip.add(TextFormatting.RED + "No fuel - refill with " + fuelType);
        }
    }
    
    private String formatRange(double range) {
        if (range >= 1000000) {
            return String.format("%.1fM blocks", range / 1000000.0);
        } else if (range >= 1000) {
            return String.format("%.0fK blocks", range / 1000.0);
        } else {
            return String.format("%.0f blocks", range);
        }
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - ((double) getFuel(stack) / (double) maxFuel);
    }
}
