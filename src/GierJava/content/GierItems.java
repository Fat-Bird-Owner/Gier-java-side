package GierJava.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

public class GierItems{
    public static Item metaglassChunk,

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
            
            flammability = 0f
            explosiveness = 0.8f
            charge = 0.25f

            hardness = 4;
            cost = 3f;
            
        }};

    }
}
