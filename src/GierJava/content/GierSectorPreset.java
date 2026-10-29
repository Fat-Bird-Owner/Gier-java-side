package GierJava.content;

import mindustry.type.SectorPreset;
import static GierJava.content.GierPlanets.*;

public class GierSectorPreset {
    public static SectorPreset  //kela
    Chasm;

    public static void load(){

        Chasm = new SectorPreset("Chasm", kela, 24){{
            addStartingItems = true;
            captureWave = 10;
            showSectorLandInfo = false;
            allDatabaseTabs = true;
            alwaysUnlocked = true;
            overrideLaunchDefaults = true;
            difficulty = 1;
            outline = false;
        }};
    }
}
