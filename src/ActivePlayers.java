import java.util.Iterator;

class ActivePlayers implements Iterable<PlayerData> {
    @Override
    public Iterator<PlayerData> iterator() {
        return new ActivePlayerIterator();
    }
}
