import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WinterDrawStrategy extends DrawSpriteStrategyBase {

    private static class Snowflake {
        int x, y;
        int speed;
        int size;

        Snowflake(int x, int y, int speed, int size) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.size = size;
        }
    }

    private List<Snowflake> snowflakes = new ArrayList<>();
    private Random random = new Random();
    private boolean initialized = false;

    @Override
    public void drawImage(Image img, int x, int y, int width, int height) {
        // Draw the original image
        if (graphics != null) {
            graphics.drawImage(img, x, y, width, height, null);
            // Removed per-sprite overlay to reduce clutter and improve performance
        }
    }

    @Override
    public void drawOval(Color color, int x, int y, int width, int height) {
        if (graphics != null) {
            graphics.setColor(color);
            graphics.fillOval(x, y, width, height);
        }
    }

    @Override
    public void drawRect(Color color, int x, int y, int width, int height) {
        if (graphics != null) {
            graphics.setColor(color);
            graphics.fillRect(x, y, width, height);
        }
    }

    @Override
    public void drawOverlay(int width, int height) {
        if (graphics == null) return;

        if (!initialized) {
            for (int i = 0; i < 100; i++) {
                snowflakes.add(new Snowflake(
                    random.nextInt(width),
                    random.nextInt(height),
                    random.nextInt(3) + 1, // speed 1-3
                    random.nextInt(3) + 2  // size 2-4
                ));
            }
            initialized = true;
        }

        graphics.setColor(Color.WHITE);
        for (Snowflake s : snowflakes) {
            // Draw pixelated snowflake (rect)
            graphics.fillRect(s.x, s.y, s.size, s.size);

            // Update position
            s.y += s.speed;
            
            // Reset if out of bounds
            if (s.y > height) {
                s.y = -s.size;
                s.x = random.nextInt(width);
            }
        }
    }
}
