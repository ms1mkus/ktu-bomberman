class PlayerAbilitiesInfoProviderHUDAdapter implements HUDDisplayProvider {
    private PlayerAbilitiesInfoProvider infoProvider;
    
    public PlayerAbilitiesInfoProviderHUDAdapter(PlayerAbilitiesInfoProvider infoProvider) {
        this.infoProvider = infoProvider;
    }
    
    
    @Override
    public String getMainBoldText() {
        return infoProvider.fetchMovementVelocity();
    }
    
    @Override
    public String getLeftSmaller() {
        return infoProvider.fetchExplosionRadius();
    }
    
    @Override
    public String getRightSmaller() {
        return infoProvider.checkGhostModeStatus();
    }
    
    @Override
    public String getBarrelInfo() {
        return infoProvider.getBombTypeDescription();
    }
    
    @Override
    public String getName() {
        return infoProvider.retrievePlayerIdentifier();
    }
    
    @Override
    public String getSection1Label() {
        return infoProvider.getPrimaryStatLabel();
    }
    
    @Override
    public String getSection1DataLabel() {
        return infoProvider.getPrimaryDataName();
    }
    
    @Override
    public String getSection2Label() {
        return infoProvider.getSecondaryStatLabel();
    }
    
    @Override
    public String getSection2DataLabel() {
        return infoProvider.getSecondaryDataName();
    }
    
    @Override
    public String getSection2StatusLabel() {
        return infoProvider.getStatusFieldName();
    }
    
    @Override
    public String getSection3Label() {
        return infoProvider.getTertiaryStatLabel();
    }
    
    @Override
    public String getSection3DataLabel() {
        return infoProvider.getTertiaryDataName();
    }
    
    @Override
    public void onHUDAction() {
        if (Game.you != null && Game.you.hat != null) {
            Game.you.triggerHat();
        }
    }
    
    @Override
    public String getHUDActionLabel() {
        if (Game.you != null && Game.you.hat != null && Game.you.hat.isVisible()) {
            return "🎅 HAT ON";
        }
        return "HAT OFF";
    }
}
