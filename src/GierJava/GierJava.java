package GierJava;

import GierJava.ui.dialogs.*;
import GierJava.graphics.GierEffects;
import GierJava.content.*;
import GierJava.gen.*;
import GierJava.graphics.*;

import mindustry.game.EventType;
import mindustry.mod.*;
import mindustry.ctype.UnlockableContent;
import mindustry.content.Blocks;
import mindustry.world.blocks.environment.*;
import mindustry.world.*;

import arc.func.Cons;
import arc.Events;
import arc.Core;

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
        GierWeather.load();
        GierSectorPreset.load();
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

            GierMenuRenderer.install();
            GierPlanets.gier.radius = 0.5f;
            GierPlanets.gier.generator = new GierPlanetGenerator();

            new ArchiveResearchDialog();
            
            /*
            GierPlanets.kela.sectors.each(s -> {
                if (s.preset != null) return;
                s.generateEnemyBase = true;
            });
            */
            GierPlanets.kela.reloadMeshAsync();
            GierPlanets.kela.techTree = GierPlanets.gier.techTree;

            GierPlanets.gier.sectors.each(s -> {
                s.setName(Core.bundle.get("gier.sectors-" + s.id + ".name"));
            });
            
            GierPlanets.gier.techTree.each(n -> {
                n.content.shownPlanets.add(GierPlanets.kela);
                if (n.content instanceof Block){
                    Block block = n.content;
                    block.envEnabled = -1;
                }
            });

        });
    }

}
