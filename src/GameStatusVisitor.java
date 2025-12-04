public class GameStatusVisitor implements GameVisitor {
    private int playerCount = 0;
    private int tileCount = 0;

    @Override
    public void visit(GameWorld world) {
        System.out.println("--- Game Status Report ---");
        // Define traversal: Visit Map then Players
        new MapGroup().accept(this);
        new PlayerGroup().accept(this);
        System.out.println("--------------------------");
        System.out.println("Total Tiles: " + tileCount);
        System.out.println("Total Active Players: " + playerCount);
    }

    @Override
    public void visit(MapGroup mapGroup) {
        System.out.println("Scanning Map...");
        // Use Iterator to traverse children
        MapIterator it = new MapIterator();
        while (it.hasNext()) {
            new TileNode(it.next()).accept(this);
        }
    }

    @Override
    public void visit(PlayerGroup playerGroup) {
        System.out.println("Scanning Players...");
        // Use Iterator to traverse children
        ActivePlayerIterator it = new ActivePlayerIterator();
        while (it.hasNext()) {
            new PlayerNode(it.next()).accept(this);
        }
    }

    @Override
    public void visit(TileNode tile) {
        tileCount++;
        // Example operation: print special tiles
        Coordinate c = tile.getCoordinate();
        // Assuming "1", "2", "3" are destructible walls or similar in this game context
        // or just print every 10th tile to avoid spam
        if (tileCount % 20 == 0) {
            // System.out.println("Tile at " + c.x + "," + c.y + ": " + c.img);
        }
    }

    @Override
    public void visit(PlayerNode player) {
        playerCount++;
        PlayerData p = player.getPlayerData();
        System.out.println("Player at " + p.x + "," + p.y + " (Bombs: " + p.numberOfBombs + ")");
    }
}
