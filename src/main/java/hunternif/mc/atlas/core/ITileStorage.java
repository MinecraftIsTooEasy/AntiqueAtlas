package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Rect;

public interface ITileStorage {
    void setTile(int i, int i2, Tile tile);

    Tile getTile(int i, int i2);

    boolean hasTileAt(int i, int i2);

    Rect getScope();
}
