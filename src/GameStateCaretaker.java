import java.util.ArrayList;
import java.util.List;

public class GameStateCaretaker {
    private static final List<GameStateMemento> mementos = new ArrayList<>();

    public static void saveState() {
        // We need to access Game.you, Game.enemy1 etc.
        // Since they are static in Game, we can access them.
        Player[] players = new Player[] { Game.you, Game.enemy1, Game.enemy2, Game.enemy3 };
        
        // We need to ensure players are initialized
        if (Game.you == null) return;

        mementos.add(new GameStateMemento(Client.map, players));
        // System.out.println("Saved state at " + System.currentTimeMillis());
    }

    public static List<GameStateMemento> getMementos() {
        return mementos;
    }
    
    public static GameStateMemento getMemento(int index) {
        if (index >= 0 && index < mementos.size()) {
            return mementos.get(index);
        }
        return null;
    }
}
