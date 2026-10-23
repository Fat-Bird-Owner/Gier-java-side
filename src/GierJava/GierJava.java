package GierJava;

import mindustry.mod.*;
import arc.Events;
import GierJava.content.*;
import GierJava.gen.*;

public class GierJava extends Mod{
    @Override
    public void loadContent(){
        // Call this before loading any content!
        EntityRegistry.register();

        GierPlanets.load();
        GierItems.load();
        GierUnitTypes.load();
        GierBlocks.load();
        GierTechTree.load();

    }
}
