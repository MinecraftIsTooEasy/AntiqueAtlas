package hunternif.mc.atlas.core;

import net.minecraft.Item;
import net.minecraft.RNG;

public class Tile {
    public final int biomeID;
    private transient short variationNumber;

    public Tile(int biomeID) {
        this(biomeID, (byte) 0);
        randomizeTexture();
    }

    public Tile(int biomeID, byte variationNumber) {
        this.biomeID = biomeID;
        this.variationNumber = variationNumber;
    }

    public void randomizeTexture() {
        this.variationNumber = (short) Item.itemRand.nextInt(RNG.MAX_INDEX);
    }

    public short getVariationNumber() {
        return this.variationNumber;
    }

    public String toString() {
        return "tile" + this.biomeID;
    }
}
