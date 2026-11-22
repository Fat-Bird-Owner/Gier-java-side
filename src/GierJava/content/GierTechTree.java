package GierJava.content;

import arc.struct.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static GierJava.content.GierBlocks.*;
import static GierJava.content.GierItems.*;
import static GierJava.content.GierPlanets.*;
import static GierJava.content.GierSectorPreset.*;
import static GierJava.content.GierPlanets.*;
import static mindustry.content.TechTree.*;

public class GierTechTree {

    public static void load(){
        GierPlanets.gier.techTree = nodeRoot("Gier", coreSatellite, () -> {
/*
            node(sun, () -> {
                node(gier, () -> {});
                node(notva, () -> {});
                node(verilus, () -> {});
                node(serpulo, () -> {
                    node(erekir, () -> {});
                });
            });
 */           
            nodeProduce(lead, () -> {
                
                nodeProduce(scrap, () -> {});
                
                nodeProduce(copper, () -> {
                    nodeProduce(beryllium, () -> {
                        nodeProduce(oxide, () -> {});
                    });
                    
                    nodeProduce(titanium, () -> {
                        nodeProduce(surgeAlloy, () -> {});
                        nodeProduce(plastanium, () -> {});
                        nodeProduce(depletedThorium, () -> {
                            nodeProduce(thorium, () -> {
                                nodeProduce(phaseFabric, () -> {});
                            });
                        });
                        
                        nodeProduce(tungsten, () -> {
                            nodeProduce(carbide, () -> {});
                        });
                        
                    });
                    
                });
                nodeProduce(graphite, () -> {
                    
                    nodeProduce(sand, () -> {

                       nodeProduce(blastCompound, () -> {});
                       nodeProduce(silicon, () -> {});
                       nodeProduce(metaglassChunk, () -> {
                        nodeProduce(metaglass, () -> {});
                       }); 
                        
                    });
                    
                });
            });

            node(hydroDrill, () -> {
                node(precipiceGrinder, () -> {});
                node(tonCrusher, () -> {});
                node(heavyExcavator, () -> {});

                node(mechanicalSiphon, () -> {
                    node(electricSiphon, () -> {});
                });
                
                node(dihydrogenShaft, () -> {
                    node(industrialBorehole, () -> {});
                    node(fluidSilo, () -> {
                        node(fluidTank, () -> {});
                        node(fluidJunction, () -> {});
                    });
                    
                });
                
                node(rail, () -> {
                    node(tungstenRail, () -> {});
                    node(overheadBelt, () -> {});
                    node(railOverflow, () -> {});
                    node(droneFacility, () -> {
                        node(facilityDropzone, () -> {});
                    });
                });

                node(sunderFurnace, () -> {
                    node(industrialTrimmer, () -> {
                        node(thermalForge, () -> {});
                        node(fabricatorMill, () -> {});
                    });
                    node(plastaniumCrusher, () -> {});
                    node(surgeFurnace, () -> {});
                    node(oxidizationReactor, () -> {});
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
                node(outpost, () -> {});
                
                node(photonPanel, () -> {
                    node(photonCell, () -> {});
                    node(electricCapacitor, () -> {});
                    node(ignitionGenerator, () -> {
                        node(wattReactor, () -> {});
                    });
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
