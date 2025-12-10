public class ExplosionChainBuilder {
    public static ExplosionEffectHandler buildExplosionChain() {
        ExplosionEffectHandler bombCoverHandler = new BombCoverHandler();
        ExplosionEffectHandler wallHandler = new WallProtectionHandler();
        ExplosionEffectHandler blockHandler = new BlockDestructionHandler();
        ExplosionEffectHandler bombHandler = new BombChainReactionHandler();
        ExplosionEffectHandler playerHandler = new PlayerDamageHandler();
        ExplosionEffectHandler visualHandler = new VisualEffectHandler();
        
        bombCoverHandler.setNext(wallHandler);
        wallHandler.setNext(blockHandler);
        blockHandler.setNext(bombHandler);
        bombHandler.setNext(playerHandler);
        playerHandler.setNext(visualHandler);
        
        return bombCoverHandler;
    }
}