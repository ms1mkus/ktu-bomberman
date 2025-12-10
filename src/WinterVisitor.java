import java.util.Calendar;

public class WinterVisitor implements GameVisitor {

    @Override
    public void visit(GameWorld world) {
        // Check if it is winter (December, January, February)
        if (isWinter()) {
            toggleWinterTheme();
        } else {
            System.out.println("It's not winter! No snow for you.");
        }
        
        // Continue traversal (optional for this specific feature, but good practice)
        // We need to handle the fact that we might be on Client where Server.map is null
        // So we rely on the passed structure if possible, or just skip if we can't traverse.
        try {
            new MapGroup().accept(this);
            new PlayerGroup().accept(this);
        } catch (Exception e) {
            // Ignore traversal errors on Client side due to Server static data dependency
        }
    }

    @Override
    public void visit(MapGroup mapGroup) {
        // Could modify tiles here if we wanted specific tile changes
    }

    @Override
    public void visit(PlayerGroup playerGroup) {
        // Could modify players here
    }

    @Override
    public void visit(TileNode tile) {
        // No-op
    }

    @Override
    public void visit(PlayerNode player) {
        // No-op
    }

    private boolean isWinter() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH); // 0-indexed (0 = Jan, 11 = Dec)
        return month == Calendar.DECEMBER || month == Calendar.JANUARY || month == Calendar.FEBRUARY;
    }

    private void toggleWinterTheme() {
        if (Game.getDrawSpriteStrategy() instanceof WinterDrawStrategy) {
            Game.setDrawSpriteStrategy(new DrawSpriteStrategyDefault());
            System.out.println("Winter theme disabled.");
        } else {
            Game.setDrawSpriteStrategy(new WinterDrawStrategy());
            System.out.println("Winter theme enabled!");
        }
    }
}
