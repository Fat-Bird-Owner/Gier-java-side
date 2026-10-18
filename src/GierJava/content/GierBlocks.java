package GierJava.content;

import GierJava.world.blocks.*;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.content.Items;
import mindustry.content.Liquids;

import static mindustry.type.ItemStack.with;

public class GierBlocks {
    public static Block 

    // production
    hydroDrill,

    // power
    transitNode,
    photonPanel,
    
    // Effect
    coreFacility;

    public static void load(){


        // Production

        hydroDrill = new Drill("hydro-drill"){{
            requirements(Category.production, with(Items.lead, 25, Items.copper, 25));
            consumePower(0.08334f);
            consumeLiquid(Liquids.water, 0.08334f).boost();

            databaseTag = "gier";

            liquidBoostIntensity = 2f;
            conductivePower = true;
            outputsPower = true;

            size = 2;
            customShadow = true;
            baseExplosiveness = 3f;
            drillTime = 200f;
            tier = 2;
        }};

        // Power

        transitNode = new TransitNode("transit-node"){{
            requirements(Category.power, with(Items.copper, 20));
            range = 11;
            baseColor = Pal.accent;

            databaseTag = "gier";
        }};

        photonPanel = new SolarGenerator("photon-panel"){{
            requirements(Category.power, with(Items.lead, 50, Items.copper, 100));
            customShadow = true;
            size = 2;

            databaseTag = "gier";

            powerProduction = 0.166667f;
            alwaysUnlocked = true;
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawGlowRegion()
            );
        }};

        // Effect

        coreFacility = new PowerCore("core-facility"){{
            requirements(Category.effect, with(Items.lead, 1500, Items.copper, 1250, Items.graphite, 500));

            databaseTag = "gier";

            size = 4;
            health = 4500;
            powerProduction = 1f;
            itemCapacity = 5000;
            customShadow = true;
            unitType = GierUnitTypes.restoration;
        }};



    }
}