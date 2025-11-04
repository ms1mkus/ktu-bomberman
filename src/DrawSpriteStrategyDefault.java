import java.awt.*;
public class DrawSpriteStrategyDefault extends DrawSpriteStrategyBase
{
    public void drawImage(java.awt.Image img, int x, int y, int width, int height)
    {
        graphics.drawImage(img, x, y, width, height, null);
    }

    public void drawOval(Color color, int x, int y, int width, int height)
    {
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
        graphics.fillOval(x, y, width, height);
    }

    public void drawRect(Color color, int x, int y, int width, int height)
    {
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
        graphics.fillRect(x, y, width, height);
    }
}
