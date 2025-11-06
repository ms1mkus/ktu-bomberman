class WeaponStatsProvider implements HUDDisplayProvider {
    private WeaponComponent bullet;
    private WeaponComponent magazine;
    private WeaponComponent barrel;
    private boolean isReloading;
    private long reloadTimeRemaining;
    
    public WeaponStatsProvider(WeaponComponent bullet, WeaponComponent magazine, WeaponComponent barrel, boolean isReloading, long reloadTimeRemaining) {
        this.bullet = bullet;
        this.magazine = magazine;
        this.barrel = barrel;
        this.isReloading = isReloading;
        this.reloadTimeRemaining = reloadTimeRemaining;
    }
    
    @Override
    public String getMainBoldText() {
        int accuracy = bullet.getPrimaryStat() + barrel.getSecondaryStat();
        return accuracy + "% (" + bullet.getName() + ")";
    }
    
    @Override
    public String getLeftSmaller() {
        return magazine.getCurrentCapacity() + "/" + magazine.getMaxCapacity() + " (" + magazine.getName() + ")";
    }
    
    @Override
    public String getRightSmaller() {
        if (isReloading) {
            double seconds = reloadTimeRemaining / 1000.0;
            return String.format("%.1fs", seconds);
        }
        return "";
    }
    
    @Override
    public String getBarrelInfo() {
        int fireRate = barrel.getPrimaryStat();
        return fireRate + "ms (" + barrel.getName() + ")";
    }
    
    @Override
    public String getName() {
        String bulletName = bullet.getName();
        if (bulletName.contains("Fast")) return "Fast";
        if (bulletName.contains("Heavy")) return "Heavy";
        if (bulletName.contains("Tornado")) return "Tornado";
        return "Weapon";
    }
    
    @Override
    public String getSection1Label() {
        return "[BULLET]";
    }
    
    @Override
    public String getSection1DataLabel() {
        return "Accuracy";
    }
    
    @Override
    public String getSection2Label() {
        return "[MAGAZINE]";
    }
    
    @Override
    public String getSection2DataLabel() {
        return "Ammo";
    }
    
    @Override
    public String getSection2StatusLabel() {
        return isReloading ? "Reloading" : "Status";
    }
    
    @Override
    public String getSection3Label() {
        return "[BARREL]";
    }
    
    @Override
    public String getSection3DataLabel() {
        return "Fire Rate";
    }
    
    @Override
    public void onHUDAction() {
        if (Game.you != null && Game.you.hat != null) {
            Game.you.triggerHat();
            boolean visible = Game.you.hat.isVisible();
            
            System.out.println("\nHat toggled: " + (visible ? "ON" : "OFF"));
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


