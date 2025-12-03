import java.util.Iterator;
import java.util.NoSuchElementException;

class ActivePlayerIterator implements Iterator<PlayerData> {
    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
        while (currentIndex < Const.QTY_PLAYERS) {
            if (Server.player != null && Server.player[currentIndex] != null && Server.player[currentIndex].logged) {
                return true;
            }
            currentIndex++;
        }
        return false;
    }

    @Override
    public PlayerData next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return Server.player[currentIndex++];
    }
}
