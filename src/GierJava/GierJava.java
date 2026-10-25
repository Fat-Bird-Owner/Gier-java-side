package GierJava;

import GierJava.graphics.GierEffects;
import mindustry.mod.*;
import arc.Events;
import GierJava.content.*;
import GierJava.gen.*;
import mindustry.game.EventType;
import arc.Events;
import mindustry.content.Blocks.*;

import static mindustry.Vars.*;

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

    @Override 
    public void init(){
        Events.on(EventType.ClientLoadEvent.class, () -> {
            Blocks.basaltVent.effect = GierEffects.ventSmoke;
        });
    }

}
