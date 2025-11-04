import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Image;


public class ProtanopiaSkinImpl extends SkinImpl {
    private boolean mapProtanopia;
    private boolean playerProtanopia;
    
    // Classic player colors
    private final String[] classicColors = {"white", "black", "red", "yellow"};
    // Protanopia-friendly player colors  
    private final String[] protanopiaColors = {"cyan", "grey", "pink", "cream"};
    
    public ProtanopiaSkinImpl(boolean mapProtanopia, boolean playerProtanopia) {
        this.mapProtanopia = mapProtanopia;
        this.playerProtanopia = playerProtanopia;
    }
    
    @Override
    public String getPlayerColor(int playerId) {
        if (playerProtanopia) {
            return protanopiaColors[playerId % protanopiaColors.length];
        } else {
            return classicColors[playerId % classicColors.length];
        }
    }
    
    @Override
    public String getPlayerSprite(int playerId, String status) {
        if (!playerProtanopia) {
            String[] classicColors = {"white", "black", "red", "yellow"};
            String color = classicColors[playerId % classicColors.length];
            return color + "/" + status;
        }
        String color = getPlayerColor(playerId);
        String protanKey = color + "-proton/" + status;
        String baseKey = "white/" + status;
        Image baseImage = Sprite.ht.get(baseKey);
        
        if (baseImage == null) {
            String[] classicColors = {"white", "black", "red", "yellow"};
            String fallbackColor = classicColors[playerId % classicColors.length];
            return fallbackColor + "/" + status;
        }
        
        try {
            BufferedImage baseBuffered = toBufferedImage(baseImage);
            BufferedImage coloredImage = recolorPlayerSprite(baseBuffered, color);
            Sprite.ht.put(protanKey, coloredImage);
            
            return protanKey;
        } catch (Exception e) {
            String[] classicColors = {"white", "black", "red", "yellow"};
            String fallbackColor = classicColors[playerId % classicColors.length];
            return fallbackColor + "/" + status;
        }
    }
    private BufferedImage recolorPlayerSprite(BufferedImage image, String colorName) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Color targetColor = getTargetColor(colorName);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                
                if (alpha > 0) { // Process non-transparent pixels
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;
                    
                    // Only recolor non-white/non-gray areas (the actual player character)
                    if (isPlayerBodyPixel(red, green, blue)) {
                        int newPixel = applyPlayerColor(red, green, blue, targetColor);
                        result.setRGB(x, y, newPixel);
                    } else {
                        // Keep original pixel (background, outlines, etc.)
                        result.setRGB(x, y, pixel);
                    }
                } else {
                    result.setRGB(x, y, pixel); // Keep transparent pixels
                }
            }
        }
        
        return result;
    }
    
    private boolean isPlayerBodyPixel(int red, int green, int blue) {
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        int difference = max - min;
        return difference > 20 && max > 50 && min < 200;
    }
    
    private int applyPlayerColor(int origRed, int origGreen, int origBlue, Color targetColor) {
        float brightness = (origRed * 0.299f + origGreen * 0.587f + origBlue * 0.114f) / 255f;
        
        int newRed = (int)(targetColor.getRed() * brightness);
        int newGreen = (int)(targetColor.getGreen() * brightness);
        int newBlue = (int)(targetColor.getBlue() * brightness);
        
        newRed = Math.min(255, Math.max(0, newRed));
        newGreen = Math.min(255, Math.max(0, newGreen));
        newBlue = Math.min(255, Math.max(0, newBlue));
        
        int alpha = (origRed == 0 && origGreen == 0 && origBlue == 0) ? 0 : 255;
        
        return (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
    }
    
    private Color getTargetColor(String colorName) {
        switch (colorName) {
            case "cyan": return new Color(141, 220, 220);
            case "grey": return new Color(160, 160, 160);
            case "pink": return new Color(237, 128, 233);
            case "cream": return new Color(253, 251, 212);
            default: return new Color(255, 255, 255);
        }
    }
    
    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        
        BufferedImage buffered = new BufferedImage(
            image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        
        java.awt.Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        return buffered;
    }
    
    @Override
    public String getBlockSprite() { 
        if (mapProtanopia) {
            return "block-protan";
        } else {
            return "block";
        }
    }
    
    @Override
    public String getWallSprite() { 
        if (mapProtanopia) {
            return "wall-down-left";
        } else {
            return "wall-center";
        }
    }
    
    @Override
    public String getFloorSprite() { 
        if (mapProtanopia) {
            return "floor-1-protan";
        } else {
            return "floor-1";
        }
    }
    
    @Override
    public String getBombSprite(String state) { 
        if (mapProtanopia) {
            return "bomb-planted-protan-" + state;
        } else {
            return "bomb-planted-" + state;
        }
    }
    
    @Override
    public String getExplosionSprite(String type, int frame) { 
        if (mapProtanopia) {
            return type + "-explosion-protan-" + frame;
        }
        return type + "-explosion-" + frame;
    }
    
    // Getters to check current mode
    public boolean isMapProtanopia() {
        return mapProtanopia;
    }
    
    public boolean isPlayerProtanopia() {
        return playerProtanopia;
    }
}
//     @Override
//     public String getExplosionSprite(String type, int frame) { 
//         if (type.equals("center") || type.equals("up") || type.equals("down") || 
//             type.equals("left") || type.equals("right")) {
//             return type + "-explosion-protan-" + frame;
//         }
//         return type + "-explosion-protan-" + frame; 
//     }
// }