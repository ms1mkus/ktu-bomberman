public class CheatDetectionVisitor implements GameVisitor {
    private int suspiciousPlayers = 0;

    @Override
    public void visit(GameWorld world) {
        System.out.println("--- Cheat Detection Report ---");
        suspiciousPlayers = 0;
        new PlayerGroup().accept(this);
        if (suspiciousPlayers == 0) {
            System.out.println("No suspicious activity detected.");
        } else {
            System.out.println("WARNING: " + suspiciousPlayers + " suspicious players found!");
        }
        System.out.println("------------------------------");
    }

    @Override
    public void visit(MapGroup mapGroup) {
        // Not checking map for cheats in this version
    }

    @Override
    public void visit(PlayerGroup playerGroup) {
        ActivePlayerIterator it = new ActivePlayerIterator();
        while (it.hasNext()) {
            new PlayerNode(it.next()).accept(this);
        }
    }

    @Override
    public void visit(TileNode tile) {
        // Not checking tiles
    }

    @Override
    public void visit(PlayerNode player) {
        PlayerData p = player.getPlayerData();
        boolean suspicious = false;
        StringBuilder reasons = new StringBuilder();

        // Check 1: Out of bounds
        int maxX = Const.COL * Const.SIZE_SPRITE_MAP;
        int maxY = Const.LIN * Const.SIZE_SPRITE_MAP;
        if (p.x < 0 || p.x > maxX || p.y < 0 || p.y > maxY) {
            suspicious = true;
            reasons.append("[Out of Bounds] ");
        }

        // Check 2: Too many bombs (assuming max is 5 for normal play)
        if (p.numberOfBombs > 5) {
            suspicious = true;
            reasons.append("[Too Many Bombs: " + p.numberOfBombs + "] ");
        }

        if (suspicious) {
            suspiciousPlayers++;
            System.out.println("SUSPICIOUS PLAYER at " + p.x + "," + p.y + ": " + reasons.toString());
        }
    }
}
