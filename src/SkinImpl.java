public abstract class SkinImpl {
    // Map methods
    public abstract String getBlockSprite();
    public abstract String getWallSprite();
    public abstract String getFloorSprite();
    public abstract String getBombSprite(String state);
    public abstract String getExplosionSprite(String type, int frame);
    
    // Player methods
    public abstract String getPlayerColor(int playerId);
    public abstract String getPlayerSprite(int playerId, String status);
}