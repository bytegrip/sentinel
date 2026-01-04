package sh.myo.sentinel.item;

import net.minecraft.item.Item;
import sh.myo.sentinel.SentinelTab;
import sh.myo.sentinel.Tags;

public class ItemThuliumOre extends Item {

    public ItemThuliumOre() {
        setRegistryName(Tags.MOD_ID, "thulium_ore_item");
        setTranslationKey(Tags.MOD_ID + ".thulium_ore_item");
        setCreativeTab(SentinelTab.INSTANCE);
    }
}
