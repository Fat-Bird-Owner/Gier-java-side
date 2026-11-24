package GierJava;

import GierJava.ui.dialogs.*;
import GierJava.graphics.GierEffects;
import GierJava.content.*;
import GierJava.gen.*;
import GierJava.graphics.*;
import GierJava.world.meta.*;

import mindustry.game.EventType;
import mindustry.mod.*;
import mindustry.ctype.UnlockableContent;
import mindustry.content.Blocks;
import mindustry.world.blocks.environment.*;
import mindustry.content.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.type.*;

import arc.func.Cons;
import arc.Events;
import arc.Core;

import static GierJava.content.GierItems.*;
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

        Events.run(EventType.Trigger.update, AsteroidSectorDialog::update);
        
        Events.on(EventType.ClientLoadEvent.class, e -> {

            new AsteroidSectorDialog();
            
            replace(Blocks.basaltVent, (SteamVent s) -> s.effect = GierEffects.ventSmoke);
            replace(Blocks.stoneVent, (SteamVent s) -> s.effect = GierEffects.ventSmoke);
            
        /*
            replace(Blocks.sand, (Floor s) -> s.itemDrop = GierItems.sand);
            replace(Blocks.oreCopper, (OreBlock s) -> s.itemDrop = GierItems.copper);
            replace(Blocks.oreLead, (OreBlock s) -> s.itemDrop = GierItems.lead);
            replace(Blocks.oreScrap, (OreBlock s) -> s.itemDrop = GierItems.scrap);
            replace(Blocks.oreTitanium, (OreBlock s) -> s.itemDrop = GierItems.titanium);
            replace(Blocks.oreThorium, (OreBlock s) -> s.itemDrop = GierItems.thorium);
            replace(Blocks.oreBeryllium, (OreBlock s) -> s.itemDrop = GierItems.beryllium);
            replace(Blocks.oreTungsten, (OreBlock s) -> s.itemDrop = GierItems.tungsten);
            replace(Blocks.oreCrystalThorium, (OreBlock s) -> s.itemDrop = GierItems.thorium);
         */

            replace(Blocks.carbonStone, (Floor s) -> s.attributes.set(GierAttribute.carbon, 1f)); 
            
            GierMenuRenderer.install();
            GierPlanets.gier.radius = 0.5f;
            GierPlanets.gier.generator = new GierPlanetGenerator();

            GierPlanets.notva.radius = 0.5f;
            GierPlanets.notva.generator = new NotvaPlanetGenerator();

            GierPlanets.verilus.radius = 0.5f;
            GierPlanets.verilus.generator = new VerilusPlanetGenerator();
    
            //new ArchiveResearchDialog();
            
            /*
            GierPlanets.kela.sectors.each(s -> {
                if (s.preset != null) return;
                s.generateEnemyBase = true;
            });
            */
            GierPlanets.kela.reloadMeshAsync();
            GierPlanets.kela.techTree = GierPlanets.verilus.techTree = GierPlanets.notva.techTree =  GierPlanets.gier.techTree;
            
            GierPlanets.gier.sectors.each(s -> {
                s.setName(Core.bundle.get("gier.sectors-" + s.id + ".name"));
                GierPlanets.gier.generator.generateSector(s);
            });

            GierPlanets.notva.sectors.each(s -> {
                GierPlanets.notva.generator.generateSector(s);
            });
            
            GierPlanets.gier.techTree.each(n -> {
                n.content.shownPlanets.add(GierPlanets.kela);
                n.content.shownPlanets.add(GierPlanets.notva);
                n.content.shownPlanets.add(GierPlanets.verilus);
                if (n.content instanceof Block){
                    Block block = (Block)n.content;
                    block.envEnabled = -1;
                } else if (n.content instanceof Item item){
                    item.localizedName = "[#" + item.color.toString() + "]" + item.localizedName + "[]";
                }
            });

        });
    }

}
