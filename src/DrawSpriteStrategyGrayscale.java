import javax.swing.*;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;

public class DrawSpriteStrategyGrayscale extends DrawSpriteStrategyBase
{
    public void drawImage(java.awt.Image img, int x, int y, int width, int height)
    {

        ImageFilter filter = new GrayFilter(false, 0);
        ImageProducer producer = new FilteredImageSource(img.getSource(), filter);
        Image mage = Toolkit.getDefaultToolkit().createImage(producer);

        graphics.drawImage(mage, x, y, width, height, null); // fallback
    }

    public void drawOval(Color color, int x, int y, int width, int height)
    {

        Color grayColor = new Color(
                (color.getRed() + color.getGreen() + color.getBlue()) / 3,
                (color.getRed() + color.getGreen() + color.getBlue()) / 3,
                (color.getRed() + color.getGreen() + color.getBlue()) / 3,
                color.getAlpha()
        );

        graphics.setColor(grayColor);
        graphics.fillOval(x, y, width, height);
    }

    public void drawRect(Color color, int x, int y, int width, int height)
    {
        Color grayColor = new Color(
                (color.getRed() + color.getGreen() + color.getBlue()) / 3,
                (color.getRed() + color.getGreen() + color.getBlue()) / 3,
                (color.getRed() + color.getGreen() + color.getBlue()) / 3,
                color.getAlpha()
        );

        graphics.setColor(grayColor);
        graphics.fillRect(x, y, width, height);
    }

}
