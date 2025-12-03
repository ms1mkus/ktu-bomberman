public class TileNode implements GameVisitable {
    private final Coordinate coordinate;

    public TileNode(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public void accept(GameVisitor visitor) {
        visitor.visit(this);
    }
}
