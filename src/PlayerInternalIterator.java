import java.util.function.Consumer;

// Internal Iterator: Manages the iterations in the background.
// Dynamic Iterator: Uses actual data collection (Server.player)
class PlayerInternalIterator {
    
    // The client provides the action, but the iterator controls the loop
    public void forEach(Consumer<PlayerData> action) {
        if (Server.player == null) return;
        
        for (int i = 0; i < Const.QTY_PLAYERS; i++) {
            PlayerData p = Server.player[i];
            // Dynamic check: checks the current state of the array during iteration
            if (p != null && p.logged) {
                action.accept(p);
            }
        }
    }
}
