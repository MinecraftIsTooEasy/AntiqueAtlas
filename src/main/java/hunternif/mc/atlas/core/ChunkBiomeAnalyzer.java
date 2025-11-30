package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.BiomeGenBase;
import net.minecraft.Block;
import net.minecraft.Chunk;

public class ChunkBiomeAnalyzer {
    public static final int NOT_FOUND = -1;
    public static final ChunkBiomeAnalyzer instance = new ChunkBiomeAnalyzer();
    private static final int waterPoolBiomeID = BiomeGenBase.river.biomeID;
    private static final int waterPoolMultiplier = 2;
    private static final int waterMultiplier = 4;
    private static final int beachMultiplier = 3;

    public int getMeanBiomeID(Chunk chunk) {
        BiomeGenBase[] biomes = BiomeGenBase.biomeList;
        int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
        int[] biomeOccurences = new int[biomes.length];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int biomeID = chunkBiomes[(x << 4) | z];
                int y = chunk.getHeightValue(x, z);
                Block topBlock = Block.getBlock(chunk.getBlockID(x, y - 1, z));
                if (topBlock != null && topBlock == Block.waterStill && biomeID != BiomeGenBase.swampland.biomeID && biomeID != BiomeGenBase.swampland.biomeID + 128) {
                    int i = waterPoolBiomeID;
                    biomeOccurences[i] = biomeOccurences[i] + 2;
                }
                if (biomeID >= 0 && biomeID < biomes.length && biomes[biomeID] != null) {
                    BiomeGenBase biome = biomes[biomeID];
                    if (biome.equals(BiomeGenBase.river) || biome.equals(BiomeGenBase.ocean)) {
                        biomeOccurences[biomeID] = biomeOccurences[biomeID] + waterMultiplier;
                    } else if (biome.equals(BiomeGenBase.beach)) {
                        biomeOccurences[biomeID] = biomeOccurences[biomeID] + beachMultiplier;
                    } else {
                        biomeOccurences[biomeID] = biomeOccurences[biomeID] + 1;
                    }
                }
            }
        }
        int meanBiomeId = -1;
        int meanBiomeOccurences = 0;
        for (int i2 = 0; i2 < biomeOccurences.length; i2++) {
            if (biomeOccurences[i2] > meanBiomeOccurences) {
                meanBiomeId = i2;
                meanBiomeOccurences = biomeOccurences[i2];
            }
        }
        return meanBiomeId;
    }
}
