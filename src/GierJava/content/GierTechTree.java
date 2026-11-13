package GierJava.content;

import arc.struct.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static mindustry.content.Items.*;
import static GierJava.content.GierBlocks.*;
import static GierJava.content.GierItems.*;
import static GierJava.content.GierPlanets.*;
import static GierJava.content.GierSectorPreset.*;
import static GierJava.content.GierPlanets.*;
import static mindustry.content.TechTree.*;

public class GierTechTree {

    public static void load(){
        GierPlanets.gier.techTree = nodeRoot("Gier", coreSatellite, () -> {

            node(sun, () -> {
                node(gier, () -> {});
                node(notva, () -> {});
                node(verilus, () -> {});
                node(serpulo, () -> {
                    node(erekir, () -> {});
                });
            });
            
            node(lead, () -> {
                node(copper, () -> {
                    node(beryllium, () -> {
                        node(oxide, () -> {});
                    });
                    node(titanium, () -> {
                        node(surgeAlloy, () -> {});
                        node(plastanium, () -> {});
                        
                        node(tungsten, () -> {
                            node(carbide, () -> {});
                        });
                        
                    });
                });
                node(graphite, () -> {
                    
                    node(sand, () -> {

                       node(blastCompound, () -> {});
                       node(silicon, () -> {});
                       node(metaglassChunk, () -> {
                        node(metaglass, () -> {});
                       }); 
                        
                    });
                    
                });
            });

            node(hydroDrill, () -> {
                node(precipiceGrinder, () -> {});
                node(tonCrusher, () -> {});
                node(dihydrogenShaft, () -> {
                    node(fluidSilo, () -> {
                        node(fluidTank, () -> {});
                    });
                });
                node(rail, () -> {
                    node(overheadBelt, () -> {});
                    node(railOverflow, () -> {});
                    node(droneFacility, () -> {
                        node(facilityDropzone, () -> {});
                    });
                });

                node(sunderFurnace, () -> {
                    node(industrialTrimmer, () -> {});
                    node(plastaniumCrusher, () -> {});
                });

                node(crate, () -> {});
            });

            node(coreFacility, () -> {});
            node(assemblyBay, () -> {
                node(assemblyReconstructor, () -> {});
            });

            node(exportPlatform, () -> {
                node(launchPlatform, () -> {});
            });
            
            node(transitNode, () -> {
                node(powerGrid, () -> {});

                node(photonPanel, () -> {
                    node(electricCapacitor, () -> {});
                    node(ignitionGenerator, () -> {});
                });
                node(tension, () -> {
                    node(momentum, () -> {});
                    node(kilton, () -> {});
                });
                node(copperFort, () -> {
                    node(scrapMound, () -> {});
                    node(fusionDome, () -> {});   
                });
            });

            node(restorator, () -> {
                node(mendPylon, () -> {});
            });

            node(Chasm, () -> {});

        });
    }
}
