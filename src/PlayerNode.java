public class PlayerNode implements GameVisitable {
    private final PlayerData playerData;

    public PlayerNode(PlayerData playerData) {
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public void accept(GameVisitor visitor) {
        visitor.visit(this);
    }
}
