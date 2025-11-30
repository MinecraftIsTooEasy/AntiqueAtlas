package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Rect;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.Map;

public class DimensionData implements ITileStorage {
    public final int dimension;
    private final Map<ShortVec2, Tile> tiles = new HashMap<>();
    private final Rect scope = new Rect();
    private final ShortVec2 tmpKey = new ShortVec2(0, 0);

    public DimensionData(int dimension) {
        this.dimension = dimension;
    }

    public Map<ShortVec2, Tile> getSeenChunks() {
        return this.tiles;
    }

    @Override
    public void setTile(int x, int y, Tile tile) {
        this.tiles.put(new ShortVec2(x, y), tile);
        this.scope.extendTo(x, y);
    }

    @Override
    public Tile getTile(int x, int y) {
        return this.tiles.get(this.tmpKey.set(x, y));
    }

    @Override
    public boolean hasTileAt(int x, int y) {
        return this.tiles.containsKey(this.tmpKey.set(x, y));
    }

    @Override
    public Rect getScope() {
        return this.scope;
    }

    public DimensionData clone() {
        DimensionData data = new DimensionData(this.dimension);
        data.tiles.putAll(this.tiles);
        data.scope.set(this.scope);
        return data;
    }
}
