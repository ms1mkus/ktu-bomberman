public class MapGroup implements GameVisitable {
    @Override
    public void accept(GameVisitor visitor) {
        visitor.visit(this);
    }
}
