import java.util.HashMap;
import java.util.Map;

public class BlockHealthManager {
    private static Map<String, Integer> blockHealth = new HashMap<>();
    
    public static void initializeBlock(String key) {
        blockHealth.put(key, 1000);
    }
    
    public static int getHealth(String key) {
        return blockHealth.getOrDefault(key, 1000);
    }
    
    public static void setHealth(String key, int health) {
        if (health <= 0) {
            blockHealth.remove(key);
        } else {
            blockHealth.put(key, health);
        }
    }
    
    public static void initializeAllBlocks() {
        for (int i = 0; i < Const.LIN; i++) {
            for (int j = 0; j < Const.COL; j++) {
                if (Server.map[i][j].img.equals("block")) {
                    String key = j + "," + i;
                    initializeBlock(key);
                }
            }
        }
    }
}