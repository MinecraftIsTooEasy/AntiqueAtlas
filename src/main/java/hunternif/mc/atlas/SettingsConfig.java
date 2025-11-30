package hunternif.mc.atlas;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

/** Config for various performance and interface settings. */
public class SettingsConfig {
    private static final int VERSION = 3;

    private static final String GAMEPLAY = "Gameplay";
    private static final String INTERFACE = "Interface";
    private static final String PERFORMANCE = "Performance";

    private File configFile;
    private Configuration config;

    //TODO make these options configurable via an in-game GUI menu

    //============= Gameplay settings =============
    public boolean doSaveBrowsingPos = true;
    public boolean autoDeathMarker = true;
    public boolean autoVillageMarkers = true;

    //============ Interface settings =============
    public boolean doScaleMarkers = false;
    public double defaultScale = 0.5f;
    public double minScale = 1.0 / 32.0;
    public double maxScale = 4;
    public boolean doReverseWheelZoom = false;

    //=========== Performance settings ============
    public int scanRadius = 11;
    public boolean forceChunkLoading = false;
    public float newScanInterval = 1f;
    public boolean doRescan = true;
    public int rescanRate = 4;
    public boolean doScanPonds = true;

    public void load(File file) {
        Configuration config = new Configuration(file);
        config.load();

        config.addCustomCategoryComment(GAMEPLAY,
                "These settings will affect how the mod behaves in certain situations and the players' overall gameplay,\n"
                        + "but generally won't affect performance.");
        config.addCustomCategoryComment(INTERFACE,
                "These setting will affect the look and feel of the Atlas' interface.");
        config.addCustomCategoryComment(PERFORMANCE,
                "These settings affect the algorithms for scanning the world, drawing the map etc. Changing them may\n"
                        + "improve the game's overall stability and performance at the cost of Atlas' functionality.");

        doSaveBrowsingPos = config.get(GAMEPLAY, "do_save_browsing_pos", doSaveBrowsingPos,
                "Whether to remember last open browsing position and zoom level for each dimension in every atlas.\n"
                        + "If disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and\n"
                        + "zoom level, and map will \"follow\" player by default.").getBoolean(doSaveBrowsingPos);

        autoDeathMarker = config.get(GAMEPLAY, "auto_death_marker", autoDeathMarker,
                "Whether to add local marker for the spot where the player died.").getBoolean(autoDeathMarker);

        autoVillageMarkers = config.get(GAMEPLAY, "auto_village_markers", autoVillageMarkers,
                "Whether to add global markers for NPC villages.").getBoolean(autoVillageMarkers);

        defaultScale = config.get(INTERFACE, "default_scale", (float)defaultScale,
                "Default zoom level. The number corresponds to the size of a block on the map relative to the size of\n"
                        + "a GUI pixel. Preferrably a power of 2.").getDouble(defaultScale);

        minScale = config.get(INTERFACE, "min_scale", (float)minScale,
                "Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of\n"
                        + "a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!").getDouble(minScale);

        maxScale = config.get(INTERFACE, "max_scale", (float)maxScale,
                "Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of\n"
                        + "a GUI pixel. Preferrably a power of 2.").getDouble(maxScale);

        doScaleMarkers = config.get(INTERFACE, "do_scale_markers", doScaleMarkers,
                "Whether to change markers size depending on zoom level.").getBoolean(doScaleMarkers);

        doReverseWheelZoom = config.get(INTERFACE, "do_reverse_wheel_zoom", doReverseWheelZoom,
                "If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.\nIf true, then the direction is reversed.").getBoolean(doReverseWheelZoom);

        scanRadius = config.get(PERFORMANCE, "area_scan_radius", scanRadius,
                "The radius of the area around the player which is scanned by the Atlas at regular intervals.\n"
                        + "Note that this will not force faraway chunks to load, unless force_chunk_loading is enabled.\n"
                        + "Lower value gives better performance.").getInt(scanRadius);

        forceChunkLoading = config.get(PERFORMANCE, "force_chunk_loading", forceChunkLoading,
                "Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.\n"
                        + "Enabling this may SEVERELY decrease performance!").getBoolean(forceChunkLoading);

        newScanInterval = (float)config.get(PERFORMANCE, "area_scan_interval", newScanInterval,
                "Time in seconds between two scans of the area.\nHigher value gives better performance.").getDouble(newScanInterval);

        doRescan = config.get(PERFORMANCE, "do_rescan", doRescan,
                "Whether to rescan chunks in the area that have been previously mapped. This is useful in case of\n"
                        + "changes in coastline (including small ponds of water and lava), or if land disappears completely\n"
                        + "(for sky worlds).\nDisable for better performance.").getBoolean(doRescan);

        rescanRate = config.get(PERFORMANCE, "area_rescan_rate", rescanRate,
                "The number of area scans between full rescans.\nHigher value gives better performance.").getInt(rescanRate);

        doScanPonds = config.get(PERFORMANCE, "do_scan_ponds", doScanPonds,
                "Whether to perform additional scanning to locate small ponds of water or lava.\nDisable for better performance.").getBoolean(doScanPonds);

        config.save();
    }

    public void save(File file) {
        if (config == null || configFile == null || !configFile.equals(file)) {
            load(file);
        } else {
            config.save();
        }
    }
}