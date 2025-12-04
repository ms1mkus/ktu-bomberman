public class XMLExportVisitor implements GameVisitor {
    private StringBuilder xml = new StringBuilder();
    private int indentLevel = 0;

    private void indent() {
        for (int i = 0; i < indentLevel; i++) xml.append("  ");
    }

    public String getXml() {
        return xml.toString();
    }

    @Override
    public void visit(GameWorld world) {
        xml.setLength(0);
        indent(); xml.append("<GameWorld>\n");
        indentLevel++;
        
        new MapGroup().accept(this);
        new PlayerGroup().accept(this);
        
        indentLevel--;
        indent(); xml.append("</GameWorld>\n");
    }

    @Override
    public void visit(MapGroup mapGroup) {
        indent(); xml.append("<Map>\n");
        indentLevel++;
        
        MapIterator it = new MapIterator();
        while (it.hasNext()) {
            new TileNode(it.next()).accept(this);
        }
        
        indentLevel--;
        indent(); xml.append("</Map>\n");
    }

    @Override
    public void visit(PlayerGroup playerGroup) {
        indent(); xml.append("<Players>\n");
        indentLevel++;
        
        ActivePlayerIterator it = new ActivePlayerIterator();
        while (it.hasNext()) {
            new PlayerNode(it.next()).accept(this);
        }
        
        indentLevel--;
        indent(); xml.append("</Players>\n");
    }

    @Override
    public void visit(TileNode tile) {
        Coordinate c = tile.getCoordinate();
        indent(); 
        xml.append(String.format("<Tile x=\"%d\" y=\"%d\" img=\"%s\" />\n", c.x, c.y, c.img));
    }

    @Override
    public void visit(PlayerNode player) {
        PlayerData p = player.getPlayerData();
        indent();
        xml.append(String.format("<Player x=\"%d\" y=\"%d\" bombs=\"%d\" />\n", p.x, p.y, p.numberOfBombs));
    }
}
