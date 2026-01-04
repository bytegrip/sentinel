package sh.myo.sentinel.item;

import net.minecraft.item.Item;
import sh.myo.sentinel.SentinelTab;
import sh.myo.sentinel.Tags;

public class ItemThuliumIngot extends Item {

    public ItemThuliumIngot() {
        setRegistryName(Tags.MOD_ID, "thulium_ingot");
        setTranslationKey(Tags.MOD_ID + ".thulium_ingot");
        setCreativeTab(SentinelTab.INSTANCE);
    }
}
