package GierJava.content;

import mindustry.content.*;
import mindustry.entities.effect.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.draw.*;
import arc.graphics.Color;

import static mindustry.type.ItemStack.with;

public class GierBlocks {
    public static Block 

    // production
    hydroDrill,
    
    // Effect
    coreFacility;

    public static void load(){


        // Production

        hydroDrill = new Drill("hydro-drill"){{
            requirements(Category.production, with(Items.lead, 25, Items.copper, 25));
            consumePower(0.08334f);

            customShadow = true;
            baseExplosiveness = 3f;
            drillTime = 200f;
            tier = 2;
        }};

        // Effect

        coreFacility = new CoreBlock("core-outpost"){{
            requirements(Category.effect, with(Items.lead, 1500, Items.copper, 1250, Items.graphite, 500));
            size = 4;
            health = 4500f;
            itemCapacity = 5000;
            customShadow = true;
        }};



    }

}