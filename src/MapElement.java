// MapElement.java
public class MapElement extends GameElement {
    private String tileType;
    
    public MapElement(SkinImpl skinImpl, String tileType) {
        super(skinImpl);
        this.tileType = tileType;
    }
    
    public String getSpriteKey() {
        if (tileType.equals("block")) {
            return skinImpl.getBlockSprite();
        } else if (tileType.startsWith("wall")) {
            return skinImpl.getWallSprite();
        } else if (tileType.equals("floor-1") || tileType.equals("floor-2")) {
            return skinImpl.getFloorSprite();
        } else if (tileType.startsWith("bomb-planted")) {
            String state = tileType.substring(tileType.lastIndexOf("-") + 1);
            return skinImpl.getBombSprite(state);
        } else if (tileType.contains("explosion")) {
            if (tileType.startsWith("mid-hori-explosion")) {
                String frame = tileType.substring(tileType.lastIndexOf("-") + 1);
                return skinImpl.getExplosionSprite("mid-hori", Integer.parseInt(frame));
            } else if (tileType.startsWith("mid-vert-explosion")) {
                String frame = tileType.substring(tileType.lastIndexOf("-") + 1);
                return skinImpl.getExplosionSprite("mid-vert", Integer.parseInt(frame));
            } else {
                String[] parts = tileType.split("-");
                if (parts.length >= 3) {
                    String type = parts[0];
                    String frame = parts[parts.length - 1];
                    return skinImpl.getExplosionSprite(type, Integer.parseInt(frame));
                }
            }
        }
        return tileType;
    }
    public void setTileType(String newTileType) {
        this.tileType = newTileType;
    }
    
    public String getTileType() {
        return tileType;
    }
}