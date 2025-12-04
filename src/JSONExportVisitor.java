public class JSONExportVisitor implements GameVisitor {
    private StringBuilder json = new StringBuilder();
    private boolean firstItem = true;

    public String getJson() {
        return json.toString();
    }

    @Override
    public void visit(GameWorld world) {
        json.setLength(0);
        json.append("{\n  \"gameWorld\": {\n");
        
        new MapGroup().accept(this);
        json.append(",\n");
        new PlayerGroup().accept(this);
        
        json.append("\n  }\n}");
    }

    @Override
    public void visit(MapGroup mapGroup) {
        json.append("    \"map\": [\n");
        
        MapIterator it = new MapIterator();
        firstItem = true;
        while (it.hasNext()) {
            if (!firstItem) json.append(",\n");
            new TileNode(it.next()).accept(this);
            firstItem = false;
        }
        
        json.append("\n    ]");
    }

    @Override
    public void visit(PlayerGroup playerGroup) {
        json.append("    \"players\": [\n");
        
        ActivePlayerIterator it = new ActivePlayerIterator();
        firstItem = true;
        while (it.hasNext()) {
            if (!firstItem) json.append(",\n");
            new PlayerNode(it.next()).accept(this);
            firstItem = false;
        }
        
        json.append("\n    ]");
    }

    @Override
    public void visit(TileNode tile) {
        Coordinate c = tile.getCoordinate();
        json.append(String.format("      {\"x\": %d, \"y\": %d, \"img\": \"%s\"}", c.x, c.y, c.img));
    }

    @Override
    public void visit(PlayerNode player) {
        PlayerData p = player.getPlayerData();
        json.append(String.format("      {\"x\": %d, \"y\": %d, \"bombs\": %d, \"alive\": %b}", 
            p.x, p.y, p.numberOfBombs, p.alive));
    }
}
