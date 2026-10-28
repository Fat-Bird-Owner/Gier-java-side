package GierJava.content;

import arc.struct.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static GierJava.content.GierBlocks.*;
import static GierJava.content.GierPlanets.*;
import static GierJava.content.GierSectorPreset.*;
import static mindustry.content.TechTree.*;

public class GierTechTree {

    public static void load(){
        GierPlanets.gier.techTree = nodeRoot("Gier", coreSatellite, () -> {

            node(hydroDrill, () -> {
                node(rail, () -> {
                    node(overheadBelt, () -> {});
                    node(railOverflow, () -> {});
                    node(droneFacility, () -> {
                        node(facilityDropzone, () -> {});
                    });
                });

                node(sunderFurnace, () -> {
                    node(industrialTrimmer, () -> {});
                });

                node(crate, () -> {});
            });

            node(coreFacility, () -> {});

            node(transitNode, () -> {
                node(powerGrid, () -> {});

                node(photonPanel, () -> {
                    node(electricCapacitor, () -> {});
                    node(ignitionGenerator, () -> {});
                });
                node(tension, () -> {});
                node(copperFort, () -> {});
            });

            node(restorator, () -> {
                node(mendPylon, () -> {});
            });

            node(Chasm, () -> {});

        });
    }
}
