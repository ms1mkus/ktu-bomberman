import java.awt.Color;
import java.awt.Graphics;

public class PlayerHat implements Cloneable {
    private boolean visible;
    private Color color;
    
    public PlayerHat() {
        this.visible = false;
        this.color = new Color(255, 0, 0);
    }
    
    public void toggle() {
        this.visible = !this.visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void draw(Graphics g, int playerX, int playerY, int playerWidth, int playerHeight) {
        if (visible) {
            int hatWidth = playerWidth - 20;
            int hatHeight = playerHeight / 3; 
            int startX = playerX + (playerWidth - hatWidth) / 2;
            int startY = playerY + 4;

            g.setColor(new Color(200, 30, 30));
            g.fillPolygon(
                new int[]{startX, startX + hatWidth / 2, startX + hatWidth},
                new int[]{startY + hatHeight, startY, startY + hatHeight},
                3
            );

            g.setColor(new Color(150, 20, 20));
            g.fillPolygon(
                new int[]{
                    startX + hatWidth / 2, startX + hatWidth - 1, startX + hatWidth - 1,
                    startX + hatWidth / 2 + 1
                },
                new int[]{
                    startY + 1, startY + hatHeight - 1, startY + hatHeight, startY + 2
                },
                4
            );

            g.setColor(new Color(240, 60, 60));
            g.drawLine(startX + 2, startY + hatHeight - 1, startX + hatWidth / 2 - 2, startY + 2);

            int trimY = startY + hatHeight;
            g.setColor(new Color(240, 240, 240));
            g.fillRect(startX - 1, trimY, hatWidth + 2, 4);

            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < hatWidth / 4; i++) {
                int x = startX - 1 + i * 4 + (i % 2);
                g.fillRect(x, trimY + (i % 2), 1, 1);
            }

            int pomX = startX + hatWidth / 2;
            int pomY = startY - 3;
            g.setColor(new Color(255, 255, 255));
            g.fillOval(pomX - 3, pomY - 3, 6, 6);

            g.setColor(new Color(220, 220, 220));
            g.fillRect(pomX - 1, pomY - 1, 2, 2);
            g.fillRect(pomX + 1, pomY + 1, 1, 1);
            g.fillRect(pomX - 2, pomY + 1, 1, 1);
        }
    }

    @Override
    public PlayerHat clone() {
        try {
            return (PlayerHat) super.clone();
        } catch (CloneNotSupportedException e) {
            PlayerHat copy = new PlayerHat();
            copy.visible = this.visible;
            copy.color = this.color;
            return copy;
        }
    }
}
