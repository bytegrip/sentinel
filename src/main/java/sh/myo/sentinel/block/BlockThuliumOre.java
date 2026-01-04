package sh.myo.sentinel.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import sh.myo.sentinel.Sentinel;
import sh.myo.sentinel.SentinelTab;
import sh.myo.sentinel.Tags;

import java.util.Random;

public class BlockThuliumOre extends Block {

    public BlockThuliumOre() {
        super(Material.ROCK);
        setRegistryName(Tags.MOD_ID, "thulium_ore");
        setTranslationKey(Tags.MOD_ID + ".thulium_ore");
        setCreativeTab(SentinelTab.INSTANCE);
        setHardness(3.0F);
        setResistance(5.0F);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Sentinel.THULIUM_ORE_ITEM;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    
    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            return random.nextInt(2) == 0 ? 2 : 1;
        }
        return 1;
    }
}
