import java.util.Iterator;
import java.util.NoSuchElementException;

class MapIterator implements Iterator<Coordinate> {
    private int row = 0;
    private int col = 0;

    @Override
    public boolean hasNext() {
        return Server.map != null && row < Const.LIN;
    }

    @Override
    public Coordinate next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Coordinate c = Server.map[row][col];
        col++;
        if (col >= Const.COL) {
            col = 0;
            row++;
        }
        return c;
    }
}
