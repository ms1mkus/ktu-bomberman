public class GhostDecorator extends PlayerDecorator {
    private boolean used = false;
    
    public GhostDecorator(PlayerAbilities wrappee) {
        super(wrappee);
    }
    
    @Override
    public boolean isGhost() {
        return !used;
    }
    
    public void useGhost() {
        used = true;
    }
    
    public boolean isUsed() {
        return used;
    }
}