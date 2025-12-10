import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class WinterDrawStrategy extends DrawSpriteStrategyBase {

    @Override
    public void drawImage(Image img, int x, int y, int width, int height) {
        // Draw the original image
        if (graphics != null) {
            graphics.drawImage(img, x, y, width, height, null);
            
            // Draw a semi-transparent white overlay to simulate snow/cold
            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, width, height);
            g2d.dispose();
        }
    }

    @Override
    public void drawOval(Color color, int x, int y, int width, int height) {
        if (graphics != null) {
            graphics.setColor(color);
            graphics.fillOval(x, y, width, height);
            
            // Snow overlay on ovals too
            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, width, height);
            g2d.dispose();
        }
    }

    @Override
    public void drawRect(Color color, int x, int y, int width, int height) {
        if (graphics != null) {
            graphics.setColor(color);
            graphics.fillRect(x, y, width, height);

             // Snow overlay on rects too
            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, width, height);
            g2d.dispose();
        }
    }
}
