import java.awt.Color;

public class Potion {
    public enum Type {
        HEALING,
        POISON
    }

    private final Type type;
    private final int radius;         // pixels
    private final int durationMs;     // visual effect duration
    private final Color color;        // client-side visual only
    private final String name;        // debug/telemetry

    Potion(Type type, int radius, int durationMs, Color color, String name) {
        this.type = type;
        this.radius = radius;
        this.durationMs = durationMs;
        this.color = color;
        this.name = name;
    }

    public Type getType() { return type; }
    public int getRadius() { return radius; }
    public int getDurationMs() { return durationMs; }
    public Color getColor() { return color; }
    public String getName() { return name; }
}
