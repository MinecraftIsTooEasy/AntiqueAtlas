package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.Tile;

public class SubTile {
    public Tile tile;
    public int x;
    public int y;
    public Shape shape;
    public Part part;

    public enum Shape {
        CONVEX,
        CONCAVE,
        HORIZONTAL,
        VERTICAL,
        FULL,
        SINGLE_OBJECT
    }

    public enum Part {
        TOP_LEFT(0, 0),
        TOP_RIGHT(1, 0),
        BOTTOM_LEFT(0, 1),
        BOTTOM_RIGHT(1, 1);

        int u;
        int v;

        Part(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public SubTile(Part part) {
        this.part = part;
    }

    public int getTextureU() {
        return switch (this.shape) {
            case SINGLE_OBJECT -> this.part.u;
            case CONCAVE -> 2 + this.part.u;
            case VERTICAL, CONVEX -> this.part.u * 3;
            case HORIZONTAL, FULL -> 2 - this.part.u;
        };
    }

    public int getTextureV() {
        return switch (this.shape) {
            case SINGLE_OBJECT, CONCAVE -> this.part.v;
            case VERTICAL, FULL -> 4 - this.part.v;
            case CONVEX, HORIZONTAL -> 2 + (this.part.v * 3);
        };
    }
}
