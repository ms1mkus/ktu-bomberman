
import java.awt.*;

public abstract class DrawSpriteStrategyBase
{
    protected Graphics graphics;
    public void setGraphics(Graphics g) { graphics = g;}

    public abstract void drawImage(java.awt.Image img, int x, int y, int width, int height);
    public abstract void drawOval(Color color, int x, int y, int width, int height);
    public abstract void drawRect(Color color, int x, int y, int width, int height);
    
    public void drawOverlay(int width, int height) {}
}
