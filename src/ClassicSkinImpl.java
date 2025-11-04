public class ClassicSkinImpl extends SkinImpl {
    private final String[] playerColors = {"white", "black", "red", "yellow"};
    
    @Override
    public String getPlayerColor(int playerId) {
        return playerColors[playerId % playerColors.length];
    }
    
    @Override
    public String getPlayerSprite(int playerId, String status) {
        String color = getPlayerColor(playerId);
        return color + "/" + status;
    }
    
    @Override
    public String getBlockSprite() { 
        return "block";
    }
    
    @Override
    public String getWallSprite() { 
        return "wall-center";
    }
    
    @Override
    public String getFloorSprite() { 
        return "floor-1";
    }
    
    @Override
    public String getBombSprite(String state) { 
        return "bomb-planted-" + state;
    }
    
    @Override
    public String getExplosionSprite(String type, int frame) { 
        return type + "-explosion-" + frame;
    }
}