public class PlayerDamageHandler implements ExplosionEffectHandler {
    private ExplosionEffectHandler next;
    
    @Override
    public boolean canHandle(int line, int col) {
        return isPlayerAtPosition(line, col);
    }
    
    @Override
    public ExplosionResult handleExplosion(int line, int col, int bombOwnerId,
        int bombL, int bombC, int dirL, int dirC, int distance, int range){
        //System.out.println("Checking player damage at (" + line + "," + col + ")");
        
        for (int playerId = 0; playerId < Const.QTY_PLAYERS; playerId++) {
            if (Server.player[playerId].alive && isPlayerAtPosition(playerId, line, col)) {
                damagePlayer(playerId, bombOwnerId);
            }
        }
        
        // Players don't block explosions - continue propagation
        if (next != null) {
            return next.handleExplosion(line, col, bombOwnerId, bombL, bombC, dirL, dirC, distance, range);
        }
        return ExplosionResult.CONTINUE;
    }
    
    private boolean isPlayerAtPosition(int line, int col) {
        for (int playerId = 0; playerId < Const.QTY_PLAYERS; playerId++) {
            if (Server.player[playerId].alive && isPlayerAtPosition(playerId, line, col)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isPlayerAtPosition(int playerId, int line, int col) {
        int playerCenterX = Server.player[playerId].x + Const.WIDTH_SPRITE_PLAYER / 2;
        int playerCenterY = Server.player[playerId].y + 2 * Const.HEIGHT_SPRITE_PLAYER / 3;
        
        int playerCol = playerCenterX / Const.SIZE_SPRITE_MAP;
        int playerLine = playerCenterY / Const.SIZE_SPRITE_MAP;
        
        return playerLine == line && playerCol == col;
    }
    
    private void damagePlayer(int playerId, int bombOwnerId) {
        Server.player[playerId].alive = false;
        ClientManager.sendToAllClients(playerId + " newStatus dead");
        System.out.println("Player " + playerId + " killed by explosion from player " + bombOwnerId);
        
    }
    
    @Override
    public void setNext(ExplosionEffectHandler next) {
        this.next = next;
    }
}