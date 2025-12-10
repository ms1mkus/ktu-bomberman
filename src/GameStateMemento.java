import java.util.ArrayList;
import java.util.List;

public class GameStateMemento {
    private final Coordinate[][] mapState;
    private final PlayerState[] playerStates;
    private final long timestamp;

    public GameStateMemento(Coordinate[][] map, Player[] players) {
        this.timestamp = System.currentTimeMillis();
        
        // Deep copy map
        this.mapState = new Coordinate[Const.LIN][Const.COL];
        for (int i = 0; i < Const.LIN; i++) {
            for (int j = 0; j < Const.COL; j++) {
                // Coordinate(x, y, img)
                if (map[i][j] != null) {
                    this.mapState[i][j] = new Coordinate(map[i][j].x, map[i][j].y, map[i][j].img);
                }
            }
        }

        // Deep copy players
        this.playerStates = new PlayerState[players.length];
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                this.playerStates[i] = new PlayerState(players[i].x, players[i].y, players[i].status, players[i].alive);
            }
        }
    }

    public Coordinate[][] getMapState() { return mapState; }
    public PlayerState[] getPlayerStates() { return playerStates; }
    public long getTimestamp() { return timestamp; }

    public static class PlayerState {
        int x, y;
        String status;
        boolean alive;

        PlayerState(int x, int y, String status, boolean alive) {
            this.x = x;
            this.y = y;
            this.status = status;
            this.alive = alive;
        }
    }
}
