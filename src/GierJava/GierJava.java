package GierJava;

import mindustry.mod.*;
import GierJava.content.*;
import GierJava.dialogs.*;
import GierJava.gen.*;
import mindustry.game.EventType;

public class GierJava extends Mod{
    @Override
    public void loadContent(){
        // Call this before loading any content!
        EntityRegistry.register();

        GierPlanets.load();
        GierUnitTypes.load();
        GierBlocks.load();
        GierTechTree.load();

    }
}
