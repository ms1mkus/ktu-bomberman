class IteratorTest {
    public static void main(String[] args) {
        setupMockData();

        testActivePlayerIterator();
        testStaticPlayerIterator();
        testInternalIterator();
        testMapIterator();
    }

    static void setupMockData() {
        // Initialize Server.player
        Server.player = new PlayerData[Const.QTY_PLAYERS];
        
        // Player 0: Logged in
        Server.player[0] = new PlayerData(10, 10);
        Server.player[0].logged = true;
        
        // Player 1: Not logged in
        Server.player[1] = new PlayerData(20, 20);
        Server.player[1].logged = false;

        // Player 2: Logged in
        Server.player[2] = new PlayerData(30, 30);
        Server.player[2].logged = true;

        // Player 3: Not logged in
        Server.player[3] = new PlayerData(40, 40);
        Server.player[3].logged = false;

        // Initialize Server.map
        Server.map = new Coordinate[Const.LIN][Const.COL];
        for (int i = 0; i < Const.LIN; i++) {
            for (int j = 0; j < Const.COL; j++) {
                Server.map[i][j] = new Coordinate(i * Const.SIZE_SPRITE_MAP, j * Const.SIZE_SPRITE_MAP);
                Server.map[i][j].img = "img_" + i + "_" + j;
            }
        }
        
        System.out.println("--- Mock Data Setup Complete ---");
    }

    static void testActivePlayerIterator() {
        System.out.println("\n--- Testing ActivePlayerIterator (Dynamic/External) ---");
        System.out.println("Expected: Player 0 and Player 2");
        
        ActivePlayers activePlayers = new ActivePlayers();
        for (PlayerData p : activePlayers) {
            System.out.println("Found active player at: " + p.x + "," + p.y);
        }
    }

    static void testStaticPlayerIterator() {
        System.out.println("\n--- Testing StaticPlayerIterator (Static/External) ---");
        
        StaticPlayerIterator staticIter = new StaticPlayerIterator();
        
        System.out.println("Modifying data: Logging out Player 0...");
        Server.player[0].logged = false; // Change state after iterator creation
        
        System.out.println("Iterating through snapshot:");
        while (staticIter.hasNext()) {
            PlayerData p = staticIter.next();
            System.out.println("Snapshot player at: " + p.x + "," + p.y + " (Current logged status: " + p.logged + ")");
        }
        
        // Restore state
        Server.player[0].logged = true;
    }

    static void testInternalIterator() {
        System.out.println("\n--- Testing PlayerInternalIterator (Dynamic/Internal) ---");
        
        PlayerInternalIterator internalIter = new PlayerInternalIterator();
        internalIter.forEach(p -> {
            System.out.println("Internal iterator processing player at: " + p.x + "," + p.y);
        });
    }

    static void testMapIterator() {
        System.out.println("\n--- Testing MapIterator (Dynamic/External) ---");
        System.out.println("Iterating first 5 cells:");
        
        GameMap gameMap = new GameMap();
        int count = 0;
        for (Coordinate c : gameMap) {
            System.out.println("Cell: " + c.img + " at " + c.x + "," + c.y);
            count++;
            if (count >= 5) break;
        }
    }
}
