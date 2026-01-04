package sh.myo.sentinel.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import sh.myo.sentinel.Sentinel;

import java.util.Random;

public class WorldGenThulium implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            generateOverworld(random, chunkX, chunkZ, world);
        }
    }

    private void generateOverworld(Random random, int chunkX, int chunkZ, World world) {
        generateOre(Sentinel.THULIUM_ORE_BLOCK.getDefaultState(), world, random, chunkX * 16, chunkZ * 16, 3, 1, 0, 16);
    }

    private void generateOre(IBlockState ore, World world, Random random, int x, int z, int veinSize, int chances, int minHeight, int maxHeight) {
        int heightRange = maxHeight - minHeight;
        WorldGenMinable generator = new WorldGenMinable(ore, veinSize);

        for (int i = 0; i < chances; i++) {
            int xPos = x + random.nextInt(16);
            int yPos = minHeight + random.nextInt(heightRange);
            int zPos = z + random.nextInt(16);

            generator.generate(world, random, new BlockPos(xPos, yPos, zPos));
        }
    }
}
