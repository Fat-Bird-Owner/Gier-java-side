package GierJava;

import GierJava.graphics.GierEffects;
import mindustry.mod.*;
import arc.Events;
import GierJava.content.*;
import GierJava.gen.*;
import mindustry.game.EventType;
import arc.Events;

import mindustry.ctype.UnlockableContent;
import mindustry.content.Blocks;
import mindustry.world.blocks.environment.*;
import arc.func.Cons;

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
        DerelictBlocks.load();
        GierTechTree.load();

    }

    public static <T extends UnlockableContent> void replace(UnlockableContent cont, Cons<T> replacer){
        replacer.get((T) cont);
    }

    @Override 
    public void init(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            replace(Blocks.basaltVent, (SteamVent s) -> s.effect = GierEffects.ventSmoke);
            replace(Blocks.stoneVent, (SteamVent s) -> s.effect = GierEffects.ventSmoke);
            GierPlanets.kela.techTree = GierPlanets.gier.techTree;
            GierPlanets.gier.techTree.each(n -> {
                n.content.shownPlanets.add(GierPlanets.kela);
            });

        });
    }

}
