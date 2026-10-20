package GierJava.content;

import arc.struct.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static GierJava.content.GierBlocks.*;
import static GierJava.content.GierPlanets.*;
import static mindustry.content.TechTree.*;

public class GierTechTree {

    public static void load(){
        Planets.gier.techTree = nodeRoot("Gier", coreSatellite, () -> {

            node(hydroDrill, () -> {
                node(rail, () -> {});
                node(sunderFurnace, () -> {});
                node(crate, () -> {});
            });

            node(transitNode, () -> {
                node(photonPanel, () -> {});
                node(copperFort, () -> {});
            });

            node(restorator, () -> {
                node(mendPylon, () -> {});
            });

        });
    }
}