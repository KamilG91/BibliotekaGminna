package Interface.Config;

import Service.DAO.LibraryDataHandler;

/**
 * Class that keeps any config needed. It is synchronized with config.xml. Also based on a singleton pattern.
 */
public class Config {
    private String iconPath;
    private static Config uniqueInstance = LibraryDataHandler.readConfigDataFromXml();

    /**
     * Construtor only neeeded to instantiate Loading from XML
     */
    private Config() { }

    /**
     * Return an instance of config.
     * @return singleton instance of config
     */
    public static Config getInstance() {
        return uniqueInstance;
    }

    /**
     * Return Icon Path
     * @return icon Path
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Sets Icon Path and synchropnizes with XML
     * @param iconPath path of new Icon
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
        LibraryDataHandler.writeConfigDataToXml(uniqueInstance);
    }
}
