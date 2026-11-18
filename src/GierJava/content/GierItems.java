package GierJava.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

public class GierItems{
    public static Item metaglassChunk,

    // Vanilla items 
    scrap, copper, lead, graphite, titanium, thorium, silicon, plastanium,
    phaseFabric, surgeAlloy, sand, blastCompound, metaglass,
    beryllium, tungsten, oxide, carbide, fissileMatter,
    
    // Endgame
    denseAlloy,
    cualbult;

    public static void load(){

        metaglassChunk = new Item("metaglass-chunk", Color.valueOf("ebeff5")){{
            cost = 1.5f;
        }};

        denseAlloy = new Item("dense-alloy", Color.valueOf("BB966B")){{
            flammability = 0.5f;
            explosiveness = 0.2f;
            radioactivity = 0.1f;
            charge = 0.25f;

            hardness = 5;
            cost = 2f;
        }};
        
        cualbult = new Item("cualbult", Color.valueOf("8A73C6")){{
            
            flammability = 0f;
            explosiveness = 0.8f;
            charge = 0.25f;

            hardness = 4;
            cost = 3f;
            
        }};

        copper = new Item("copper", Color.valueOf("d99d73")){{
            hardness = 1;
            cost = 0.5f;
            alwaysUnlocked = true;
        }};

        lead = new Item("lead", Color.valueOf("8c7fa9")){{
            hardness = 1;
            cost = 0.7f;
        }};

        metaglass = new Item("metaglass", Color.valueOf("ebeef5")){{
            cost = 1.5f;
        }};

        graphite = new Item("graphite", Color.valueOf("b2c6d2")){{
            cost = 1f;
        }};

        sand = new Item("sand", Color.valueOf("f7cba4")){{
            lowPriority = true;
            buildable = false;
            alwaysUnlocked = true;
        }};

        titanium = new Item("titanium", Color.valueOf("8da1e3")){{
            hardness = 3;
            cost = 1f;
        }};

        thorium = new Item("thorium", Color.valueOf("f9a3c7")){{
            explosiveness = 0.2f;
            hardness = 4;
            radioactivity = 1f;
            cost = 1.1f;
            healthScaling = 0.2f;
        }};

        scrap = new Item("scrap", Color.valueOf("777777")){{
            cost = 0.5f;
        }};

        silicon = new Item("silicon", Color.valueOf("53565c")){{
            cost = 0.8f;
        }};

        plastanium = new Item("plastanium", Color.valueOf("cbd97f")){{
            flammability = 0.1f;
            explosiveness = 0.2f;
            cost = 1.3f;
            healthScaling = 0.1f;
        }};

        phaseFabric = new Item("phase-fabric", Color.valueOf("f4ba6e")){{
            cost = 1.3f;
            radioactivity = 0.6f;
            healthScaling = 0.25f;
        }};

        surgeAlloy = new Item("surge-alloy", Color.valueOf("f3e979")){{
            cost = 1.2f;
            charge = 0.75f;
            healthScaling = 0.25f;
        }};

        blastCompound = new Item("blast-compound", Color.valueOf("ff795e")){{
            flammability = 0.4f;
            explosiveness = 1.2f;
        }};

        beryllium = new Item("beryllium", Color.valueOf("3a8f64")){{
            hardness = 3;
            cost = 1.2f;
        }};

        tungsten = new Item("tungsten", Color.valueOf("768a9a")){{
            hardness = 5;
            cost = 1.5f;
        }};

        oxide = new Item("oxide", Color.valueOf("e4ffd6")){{
            cost = 1.2f;
        }};

        carbide = new Item("carbide", Color.valueOf("89769a")){{
            cost = 1.4f;
        }};

        fissileMatter = new Item("fissile-matter", Color.valueOf("5e988d")){{
            radioactivity = 1.5f;
        }};
        
    }
}
