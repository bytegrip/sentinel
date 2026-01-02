package sh.myo.sentinel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SentinelTab extends CreativeTabs {

    public static final SentinelTab INSTANCE = new SentinelTab();

    private SentinelTab() {
        super(Tags.MOD_ID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(Sentinel.SENTINEL_RADAR);
    }
}
