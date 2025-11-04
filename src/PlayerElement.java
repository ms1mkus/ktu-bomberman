public class PlayerElement extends GameElement {
    private int playerId;
    private String status;
    
    public PlayerElement(SkinImpl skinImpl, int playerId, String status) {
        super(skinImpl);
        this.playerId = playerId;
        this.status = status;
    }
    
    @Override
    public String getSpriteKey() {
        return skinImpl.getPlayerSprite(playerId, status);
    }
    
    public void setStatus(String newStatus) {
        this.status = newStatus;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public String getStatus() {
        return status;
    }

    public String getPlayerColor() {
        return skinImpl.getPlayerColor(playerId);
    }
}