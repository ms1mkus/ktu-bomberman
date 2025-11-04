public abstract class GameElement {
    protected SkinImpl skinImpl;
    public GameElement(SkinImpl skinImpl) {
        this.skinImpl = skinImpl;
    }
    
    public void setSkinImpl(SkinImpl skinImpl) {
        this.skinImpl = skinImpl;
    }
    
    public abstract String getSpriteKey();
    
    public SkinImpl getSkinImpl() {
        return skinImpl;
    }
}