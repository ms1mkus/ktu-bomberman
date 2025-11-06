public interface HUDDisplayProvider {
    String getMainBoldText();
    String getLeftSmaller();
    String getRightSmaller();
    String getBarrelInfo();
    String getName();
    
    String getSection1Label();
    String getSection1DataLabel();
    String getSection2Label();
    String getSection2DataLabel();
    String getSection2StatusLabel();
    String getSection3Label();
    String getSection3DataLabel();
    
    void onHUDAction();
    String getHUDActionLabel();
}


