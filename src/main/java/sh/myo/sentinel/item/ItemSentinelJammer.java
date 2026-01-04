package sh.myo.sentinel.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sh.myo.sentinel.SentinelTab;

import java.util.List;

public class ItemSentinelJammer extends Item {
    
    private static final String NBT_ACTIVE = "JammerActive";
    private static final String NBT_FUEL = "JammerFuel";
    private static final int MAX_FUEL = 256;
    
    public ItemSentinelJammer() {
        this.setMaxStackSize(1);
        this.setCreativeTab(SentinelTab.INSTANCE);
        this.setTranslationKey("sentinel.radar_jammer");
        this.setRegistryName("radar_jammer");
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String baseName = super.getItemStackDisplayName(stack);
        if (isActive(stack)) {
            return baseName + " " + TextFormatting.GREEN + "[ACTIVE]";
        } else {
            return baseName + " " + TextFormatting.GRAY + "[INACTIVE]";
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int fuel = getFuel(stack);
        boolean active = isActive(stack);
        
        tooltip.add(TextFormatting.DARK_GRAY + "Radar Countermeasure Device");
        tooltip.add(TextFormatting.GRAY + "Fuel Cells: " + TextFormatting.AQUA + fuel + TextFormatting.DARK_GRAY + "/" + MAX_FUEL);
        
        if (active) {
            tooltip.add(TextFormatting.GREEN + "[ACTIVE]");
            if (fuel == 0) {
                tooltip.add(TextFormatting.RED + "No fuel remaining");
            }
        } else {
            tooltip.add(TextFormatting.GRAY + "[INACTIVE]");
        }
        
        tooltip.add(TextFormatting.DARK_GRAY + "Right-click to toggle power");
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (!worldIn.isRemote) {
            boolean active = isActive(stack);
            setActive(stack, !active);
            
            if (!active && getFuel(stack) == 0) {
                setActive(stack, false);
            }
        }
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    public void onUpdate(ItemStack stack, World worldIn, net.minecraft.entity.Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && isActive(stack) && entityIn instanceof EntityPlayer) {
            if (worldIn.getTotalWorldTime() % 400 == 0) {
                if (getFuel(stack) > 0) {
                    consumeFuel(stack);
                } else {
                    setActive(stack, false);
                }
            }
        }
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int fuel = getFuel(stack);
        return 1.0 - (fuel / (double) MAX_FUEL);
    }
    
    public static boolean isActive(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound().getBoolean(NBT_ACTIVE);
        }
        return false;
    }
    
    public static void setActive(ItemStack stack, boolean active) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean(NBT_ACTIVE, active);
    }
    
    public static int getFuel(ItemStack stack) {
        if (stack.hasTagCompound()) {
            return stack.getTagCompound().getInteger(NBT_FUEL);
        }
        return 0;
    }
    
    public static void setFuel(ItemStack stack, int fuel) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(NBT_FUEL, Math.min(fuel, MAX_FUEL));
    }
    
    public static void consumeFuel(ItemStack stack) {
        int current = getFuel(stack);
        if (current > 0) {
            setFuel(stack, current - 1);
        }
    }
    
    public static int getMaxFuel() {
        return MAX_FUEL;
    }
    
    public static boolean hasJammer(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemSentinelJammer && isActive(stack)) {
                return true;
            }
        }
        return false;
    }
}
