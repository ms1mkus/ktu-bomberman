public class VisualEffectHandler implements ExplosionEffectHandler {
    private ExplosionEffectHandler next;
    
    @Override
    public boolean canHandle(int line, int col) {
        return Server.map[line][col].img.equals("floor-1");
    }
    
    @Override
    public ExplosionResult handleExplosion(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range) {
        String explosionSprite = getExplosionSprite(bombL, bombC, line, col, dirL, dirC, distance, range);
        
        new Thrower(explosionSprite, Const.indexExplosion, Const.RATE_FIRE_UPDATE, line, col).start();
        checkIfExplosionKilledSomeone(line, col);
        
        return ExplosionResult.CONTINUE;
    }
    
    private String getExplosionSprite(int bombL, int bombC, int currentL, int currentC, int dirL, int dirC, int distance, int range) {
        if (currentL == bombL && currentC == bombC) {
            return "center-explosion";
        }
        
        boolean isVertical = dirL != 0;
        boolean isHorizontal = dirC != 0;
        
        if (isVertical) {
            if (distance == range) {
                return dirL > 0 ? "down-explosion" : "up-explosion";
            } else if (distance == 1) {
                return dirL > 0 ? "down-explosion" : "up-explosion";
            } else {
                return "mid-vert-explosion";
            }
        } else if (isHorizontal) {
            if (distance == range) {
                return dirC > 0 ? "right-explosion" : "left-explosion";
            } else if (distance == 1) {
                return dirC > 0 ? "right-explosion" : "left-explosion";
            } else {
                return "mid-hori-explosion";
            }
        }
        
        return "center-explosion";
    }
    
    private void checkIfExplosionKilledSomeone(int linSprite, int colSprite) {
        int linPlayer, colPlayer, x, y;

        for (int id = 0; id < Const.QTY_PLAYERS; id++)
            if (Server.player[id].alive) {
                x = Server.player[id].x + Const.WIDTH_SPRITE_PLAYER / 2;
                y = Server.player[id].y + 2 * Const.HEIGHT_SPRITE_PLAYER / 3;
    
                colPlayer = x / Const.SIZE_SPRITE_MAP;
                linPlayer = y / Const.SIZE_SPRITE_MAP;
    
                if (linSprite == linPlayer && colSprite == colPlayer) {
                    Server.player[id].alive = false;
                    ClientManager.sendToAllClients(id + " newStatus dead");
                }
            }
    }
    
    @Override
    public void setNext(ExplosionEffectHandler next) {
        this.next = next;
    }
}