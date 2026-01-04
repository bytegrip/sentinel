package sh.myo.sentinel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sh.myo.sentinel.Sentinel;

public class SentinelTab extends CreativeTabs {

    public static final SentinelTab INSTANCE = new SentinelTab();

    private SentinelTab() {
        super(Tags.MOD_ID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(Sentinel.THULIUM_BLOCK);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> items) {
        items.add(new ItemStack(Sentinel.THULIUM_ORE_BLOCK));
        items.add(new ItemStack(Sentinel.THULIUM_ORE_ITEM));
        items.add(new ItemStack(Sentinel.THULIUM_INGOT));
        items.add(new ItemStack(Sentinel.THULIUM_BLOCK));
        
        items.add(new ItemStack(Sentinel.WOODEN_RADAR));
        items.add(new ItemStack(Sentinel.STONE_RADAR));
        items.add(new ItemStack(Sentinel.COAL_RADAR));
        items.add(new ItemStack(Sentinel.IRON_RADAR));
        items.add(new ItemStack(Sentinel.GOLD_RADAR));
        items.add(new ItemStack(Sentinel.DIAMOND_RADAR));
        items.add(new ItemStack(Sentinel.OBSIDIAN_RADAR));
        items.add(new ItemStack(Sentinel.EMERALD_RADAR));
        items.add(new ItemStack(Sentinel.THULIUM_RADAR));
        
        items.add(new ItemStack(Sentinel.SENTINEL_JAMMER));
    }
}
