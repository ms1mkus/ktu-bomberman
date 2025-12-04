class VisitorTest {
    public static void main(String[] args) {
        setupMockData();

        System.out.println("=== Testing GameStatusVisitor ===");
        GameWorld world = new GameWorld();
        GameStatusVisitor statusVisitor = new GameStatusVisitor();
        world.accept(statusVisitor);

        System.out.println("\n=== Testing XMLExportVisitor ===");
        XMLExportVisitor xmlVisitor = new XMLExportVisitor();
        world.accept(xmlVisitor);
        System.out.println(xmlVisitor.getXml());

        System.out.println("\n=== Testing JSONExportVisitor ===");
        JSONExportVisitor jsonVisitor = new JSONExportVisitor();
        world.accept(jsonVisitor);
        System.out.println(jsonVisitor.getJson());

        System.out.println("\n=== Testing CheatDetectionVisitor ===");
        // Inject a cheater for testing
        Server.player[2].numberOfBombs = 99; 
        Server.player[2].x = -500;
        
        CheatDetectionVisitor cheatVisitor = new CheatDetectionVisitor();
        world.accept(cheatVisitor);
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

        // Initialize Server.map (Small map for testing)
        Server.map = new Coordinate[Const.LIN][Const.COL];
        for (int i = 0; i < Const.LIN; i++) {
            for (int j = 0; j < Const.COL; j++) {
                Server.map[i][j] = new Coordinate(i * Const.SIZE_SPRITE_MAP, j * Const.SIZE_SPRITE_MAP);
                Server.map[i][j].img = "img_" + i + "_" + j;
            }
        }
    }
}
