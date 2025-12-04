public class PlayerGroup implements GameVisitable {
    @Override
    public void accept(GameVisitor visitor) {
        visitor.visit(this);
    }
}
