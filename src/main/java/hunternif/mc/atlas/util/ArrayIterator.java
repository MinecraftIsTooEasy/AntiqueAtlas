package hunternif.mc.atlas.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
    private final T[] array;
    private int currentIndex = 0;

    public ArrayIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return this.currentIndex < this.array.length;
    }

    @Override
    public T next() {
        int i = this.currentIndex;
        this.currentIndex = i + 1;
        return this.array[i];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove items from an array");
    }
}
