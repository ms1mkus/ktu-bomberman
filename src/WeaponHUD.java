import java.awt.Graphics;
import java.awt.Font;

class WeaponHUD {
    private static HUDDisplayProvider currentProvider = null;
    private static int buttonX, buttonY, buttonWidth, buttonHeight;
    private static int hatButtonX, hatButtonY, hatButtonWidth, hatButtonHeight;
    
    public static void setDisplayProvider(HUDDisplayProvider provider) {
        currentProvider = provider;
    }
    
    public static boolean isButtonClicked(int mouseX, int mouseY) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }
    
    public static boolean isHatButtonClicked(int mouseX, int mouseY) {
        return mouseX >= hatButtonX && mouseX <= hatButtonX + hatButtonWidth &&
               mouseY >= hatButtonY && mouseY <= hatButtonY + hatButtonHeight;
    }
    
    public static void triggerHatAction() {
        if (currentProvider != null) {
            currentProvider.onHUDAction();
        }
    }
    
    public static void draw(Graphics g, int width, int height) {
        if (currentProvider == null) {
            return;
        }
        
        int x = width - 250;
        int y = 10;
        int padding = 10;
        int boxWidth = 230;
        int boxHeight = 165;
        
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        java.awt.Color bgColor = new java.awt.Color(0, 0, 0, 200);
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x - padding, y - padding, boxWidth, boxHeight, 10, 10);
        
        g2d.setColor(java.awt.Color.CYAN);
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawRoundRect(x - padding, y - padding, boxWidth, boxHeight, 10, 10);
        
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(java.awt.Color.CYAN);
        String title = currentProvider.getName();
        g.drawString(title, x, y + 15);
        
        g2d.setColor(java.awt.Color.GRAY);
        g2d.drawLine(x - 5, y + 20, x + boxWidth - 20, y + 20);
        
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(java.awt.Color.ORANGE);
        g.drawString(currentProvider.getSection1Label(), x, y + 38);
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(java.awt.Color.WHITE);
        g.drawString(currentProvider.getSection1DataLabel() + ": " + currentProvider.getMainBoldText(), x + 10, y + 52);
        
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(java.awt.Color.YELLOW);
        g.drawString(currentProvider.getSection2Label(), x, y + 72);
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(java.awt.Color.WHITE);
        g.drawString(currentProvider.getSection2DataLabel() + ": " + currentProvider.getLeftSmaller(), x + 10, y + 86);
        
        String rightText = currentProvider.getRightSmaller();
        if (!rightText.isEmpty()) {
            g.setColor(java.awt.Color.RED);
            g.drawString(currentProvider.getSection2StatusLabel() + ": " + rightText, x + 10, y + 100);
        } else {
            g.setColor(java.awt.Color.GREEN);
            g.drawString(currentProvider.getSection2StatusLabel() + ": Ready", x + 10, y + 100);
        }
        
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(java.awt.Color.LIGHT_GRAY);
        g.drawString(currentProvider.getSection3Label(), x, y + 120);
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(java.awt.Color.WHITE);
        g.drawString(currentProvider.getSection3DataLabel() + ": " + currentProvider.getBarrelInfo(), x + 10, y + 134);
        
        buttonWidth = 80;
        buttonHeight = 20;
        buttonX = x + boxWidth - buttonWidth - 15;
        buttonY = y + boxHeight - buttonHeight - 5;
        
        g2d.setColor(new java.awt.Color(50, 150, 50, 200));
        g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 5, 5);
        
        g2d.setColor(java.awt.Color.GREEN);
        g2d.setStroke(new java.awt.BasicStroke(1));
        g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 5, 5);
        
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.setColor(java.awt.Color.WHITE);
        g.drawString("SWITCH HUD", buttonX + 8, buttonY + 14);
    }
    
    public static void drawHatButton(Graphics g, int width, int height) {
        if (currentProvider == null) {
            return;
        }
        
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        String label = currentProvider.getHUDActionLabel();
        boolean hatVisible = label.contains("ON");
        
        hatButtonWidth = 100;
        hatButtonHeight = 30;
        hatButtonX = 10;
        hatButtonY = 30;
        
        if (hatVisible) {
            g2d.setColor(new java.awt.Color(220, 20, 20, 200));
        } else {
            g2d.setColor(new java.awt.Color(80, 80, 80, 200));
        }
        g2d.fillRoundRect(hatButtonX, hatButtonY, hatButtonWidth, hatButtonHeight, 8, 8);
        
        if (hatVisible) {
            g2d.setColor(java.awt.Color.RED);
        } else {
            g2d.setColor(java.awt.Color.GRAY);
        }
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawRoundRect(hatButtonX, hatButtonY, hatButtonWidth, hatButtonHeight, 8, 8);
        
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(java.awt.Color.WHITE);
        g.drawString(label, hatButtonX + 12, hatButtonY + 20);
    }
}


