import java.util.*;

public class SkinManager {
    private SkinImpl currentSkin;
    private List<GameElement> gameElements;
    private boolean isMapProtanopia = false;
    private boolean isPlayerProtanopia = false;
    
    public SkinManager() {
        this.currentSkin = new ClassicSkinImpl();
        this.gameElements = new ArrayList<>();
    }
    
    public void toggleMapProtanopia() {
        isMapProtanopia = !isMapProtanopia;
        updateSkin();
    }
    
    public void togglePlayerProtanopia() {
        isPlayerProtanopia = !isPlayerProtanopia;
        updateSkin();
    }
    
    private void updateSkin() {
        if (isMapProtanopia || isPlayerProtanopia) {
            currentSkin = new ProtanopiaSkinImpl(isMapProtanopia, isPlayerProtanopia);
        } else {
            currentSkin = new ClassicSkinImpl();
        }        
        refreshAllElements();
    }
    
    public PlayerElement createPlayer(int playerId, String status) {
        PlayerElement player = new PlayerElement(currentSkin, playerId, status);
        gameElements.add(player);
        return player;
    }
    
    public MapElement createMapTile(String tileType) {
        MapElement mapTile = new MapElement(currentSkin, tileType);
        gameElements.add(mapTile);
        return mapTile;
    }
    
    private void refreshAllElements() {
        for (GameElement element : gameElements) {
            element.setSkinImpl(currentSkin);
        }
    }
    
    public SkinImpl getCurrentSkin() {
        return currentSkin;
    }
    
    public boolean isMapProtanopia() {
        return isMapProtanopia;
    }
    
    public boolean isPlayerProtanopia() {
        return isPlayerProtanopia;
    }
    
    public boolean isFullProtanopia() {
        return isMapProtanopia && isPlayerProtanopia;
    }
}