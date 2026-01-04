package sh.myo.sentinel.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import sh.myo.sentinel.SentinelTab;
import sh.myo.sentinel.Tags;

public class BlockThuliumBlock extends Block {

    public BlockThuliumBlock() {
        super(Material.IRON);
        setRegistryName(Tags.MOD_ID, "thulium_block");
        setTranslationKey(Tags.MOD_ID + ".thulium_block");
        setCreativeTab(SentinelTab.INSTANCE);
        setHardness(5.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 2);
    }
}
