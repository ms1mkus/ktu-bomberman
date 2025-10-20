import java.util.HashMap;
import java.util.Map;

class PotionManager {
    private static final Map<String, Potion.Type> groundPotions = new HashMap<>(); // key: "c,l" tile key
    private static final Map<Integer, Potion.Type> playerInventory = new HashMap<>(); // one slot per player
    private static final PotionDirector director = new PotionDirector();

    static boolean hasPotionOnGround(int l, int c) {
        String key = c + "," + l;
        return groundPotions.containsKey(key);
    }

    static void spawnPotionOnGround(int l, int c, Potion.Type type) {
        String key = c + "," + l;
        groundPotions.put(key, type);
        String tileName = type == Potion.Type.HEALING ? "potion-healing" : "potion-poison";
        MapUpdatesThrowerHandler.changeMap(tileName, l, c);
        ClientManager.sendToAllClients("-1 mapUpdate " + tileName + " " + l + " " + c);
    }

    static boolean pickUpIfPresent(int playerId, int l, int c) {
        String key = c + "," + l;
        Potion.Type type = groundPotions.get(key);
        if (type == null) return false;
        // assign one-slot inventory; overwrite old
        playerInventory.put(playerId, type);
        groundPotions.remove(key);
        MapUpdatesThrowerHandler.changeMap("floor-1", l, c);
        ClientManager.sendToAllClients(playerId + " potionPicked " + type.name());
        return true;
    }

    static boolean playerHasPotion(int playerId) {
        return playerInventory.containsKey(playerId);
    }

    static Potion usePlayerPotion(int playerId) {
        Potion.Type type = playerInventory.remove(playerId);
        if (type == null) return null;
        PotionBuilder builder = switch (type) {
            case HEALING -> new HealingPotionBuilder();
            case POISON -> new PoisonPotionBuilder();
        };
        return director.construct(builder);
    }

    static void applyPotionEffect(int throwerId, Potion potion, int targetX, int targetY) {
        if (potion == null) return;

        // broadcast a simple visual cue to clients; they will draw a colored circle that fades
        ClientManager.sendToAllClients("-1 potionEffect " + potion.getType().name() + " " + targetX + " " + targetY + " " + potion.getRadius() + " " + potion.getDurationMs());

        int radius = potion.getRadius();
        int r2 = radius * radius;

        for (int pid = 0; pid < Const.QTY_PLAYERS; pid++) {
            if (!Server.player[pid].alive) continue;
            int px = Server.player[pid].x + Const.WIDTH_SPRITE_PLAYER / 2;
            int py = Server.player[pid].y + Const.HEIGHT_SPRITE_PLAYER / 2;
            int dx = px - targetX;
            int dy = py - targetY;
            if (dx * dx + dy * dy <= r2) {
                switch (potion.getType()) {
                    case HEALING -> {
                        // revive if dead not allowed; instead grant brief ghost as protection
                        Server.player[pid].addGhost();
                        ClientManager.sendToAllClients(pid + " powerUp ghost");
                    }
                    case POISON -> {
                        // simple effect: mark as dead
                        Server.player[pid].alive = false;
                        ClientManager.sendToAllClients(pid + " newStatus dead");
                    }
                }
            }
        }
    }
}
