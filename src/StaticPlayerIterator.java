import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// Static Iterator: Iterates through a data copy (snapshot)
// External Iterator: Client controls the iteration via hasNext() and next()
class StaticPlayerIterator implements Iterator<PlayerData> {
    private final List<PlayerData> snapshot;
    private int currentIndex = 0;

    public StaticPlayerIterator() {
        snapshot = new ArrayList<>();
        // Create a snapshot of the currently active players
        if (Server.player != null) {
            for (PlayerData p : Server.player) {
                if (p != null && p.logged) {
                    snapshot.add(p);
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return currentIndex < snapshot.size();
    }

    @Override
    public PlayerData next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return snapshot.get(currentIndex++);
    }
}
