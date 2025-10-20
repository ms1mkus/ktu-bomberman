import java.awt.Color;

interface PotionBuilder {
    void reset();
    void setType(Potion.Type type);
    void setRadius(int radius);
    void setDuration(int durationMs);
    void setColor(Color color);
    void setName(String name);
    Potion build();
}

class HealingPotionBuilder implements PotionBuilder {
    private Potion.Type type;
    private int radius;
    private int durationMs;
    private Color color;
    private String name;

    public HealingPotionBuilder() { reset(); }

    @Override
    public void reset() {
        type = Potion.Type.HEALING;
        radius = 64; // 2 tiles
        durationMs = 800; // short visual sparkles
        color = new Color(50, 205, 50); // lime green
        name = "Healing Potion";
    }

    @Override
    public void setType(Potion.Type type) { this.type = type; }
    @Override
    public void setRadius(int radius) { this.radius = radius; }
    @Override
    public void setDuration(int durationMs) { this.durationMs = durationMs; }
    @Override
    public void setColor(Color color) { this.color = color; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public Potion build() {
        return new Potion(type, radius, durationMs, color, name);
    }
}

class PoisonPotionBuilder implements PotionBuilder {
    private Potion.Type type;
    private int radius;
    private int durationMs;
    private Color color;
    private String name;

    public PoisonPotionBuilder() { reset(); }

    @Override
    public void reset() {
        type = Potion.Type.POISON;
        radius = 64; // 2 tiles
        durationMs = 1200; // lingers a bit longer
        color = new Color(148, 0, 211); // dark violet
        name = "Poison Potion";
    }

    @Override
    public void setType(Potion.Type type) { this.type = type; }
    @Override
    public void setRadius(int radius) { this.radius = radius; }
    @Override
    public void setDuration(int durationMs) { this.durationMs = durationMs; }
    @Override
    public void setColor(Color color) { this.color = color; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public Potion build() {
        return new Potion(type, radius, durationMs, color, name);
    }
}

class PotionDirector {
    public Potion construct(PotionBuilder builder) {
        builder.reset();
        return builder.build();
    }
}
