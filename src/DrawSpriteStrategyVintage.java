import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawSpriteStrategyVintage extends DrawSpriteStrategyBase
{

    public void drawImage(java.awt.Image img, int x, int y, int width, int height)
    {

        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buf.createGraphics();
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();

        for (int i = 0; i < buf.getWidth(); i++)
        {
            for (int j = 0; j < buf.getHeight(); j++)
            {
                Color c = new Color(buf.getRGB(i, j), true);
                Color sat = saturateColor(c, 0.5f);
                buf.setRGB(i, j, sat.getRGB());
            }
        }

        graphics.drawImage(buf, x, y, width, height, null);
    }

    public void drawOval(Color color, int x, int y, int width, int height)
    {
        Color sat = saturateColor(color, 0.5f); // 1.5x saturation

        graphics.setColor(sat);
        graphics.fillOval(x, y, width, height);
    }

    public void drawRect(Color color, int x, int y, int width, int height)
    {
        Color sat = saturateColor(color, 0.5f); // 1.5x saturation

        graphics.setColor(sat);
        graphics.fillRect(x, y, width, height);
    }

    public Color saturateColor(Color color, float factor)
    {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        hsb[1] = Math.min(1.0f, hsb[1] * factor);

        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return new Color((rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF,
                color.getAlpha());
    }

}
