import java.util.Iterator;

class GameMap implements Iterable<Coordinate> {
    @Override
    public Iterator<Coordinate> iterator() {
        return new MapIterator();
    }
}
