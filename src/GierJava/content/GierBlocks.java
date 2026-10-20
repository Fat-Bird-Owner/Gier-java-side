package GierJava.content;

import GierJava.world.blocks.*;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import GierJava.graphics.GierPal;
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
import mindustry.world.consumers.*;
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

    // Distribution
    rail,

    // Production
    hydroDrill,

    // Crafting
    sunderFurnace,

    // Defense
    copperFort,

    // Power
    transitNode,
    photonPanel,
    
    // Effect
    coreSatellite,
    coreFacility,
    crate,
    restorator,
    mendPylon;

    public static void load(){

        // Distribution

        rail = new StackConveyor("rail"){{
            requirements(Category.distribution, with(Items.copper, 1));
            researchCostMultiplier = 0.05f;
            
            speed = 0.01667f;
            baseEfficiency = 1f;

            databaseTag = "gier";
            alwaysUnlocked = true;

        }};

        // Production

        hydroDrill = new Drill("hydro-drill"){{
            requirements(Category.production, with(Items.lead, 25, Items.copper, 25));
            consumePower(0.08334f);
            consumeLiquid(Liquids.water, 0.08334f).boost();
            researchCostMultiplier = 0.05f;

            databaseTag = "gier";
            squareSprite = false;

            liquidBoostIntensity = 2f;
            conductivePower = true;
            outputsPower = true;

            size = 2;
            customShadow = true;
            baseExplosiveness = 3f;
            drillTime = 200f;
            tier = 2;
        }};

        // Crafting

        sunderFurnace = new GenericCrafter("sunder-furnace"){{
            requirements(Category.crafting, with(Items.lead, 1000, Items.copper, 1000));
            size = 4;
            researchCostMultiplier = 0.1f;

            databaseTag = "gier";
            baseExplosiveness = 200f;
            craftTime = 5f * 60f;

            itemCapacity = 100;
            consumeItems(with(Items.lead, 25, Items.graphite, 25, Items.sand, 50));
            outputItems = with(Items.silicon, 25, Items.metaglass, 25);

            consumePower(5);
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawArcSmelt(),
            new DrawDefault()
            );

        }};

        // Defense

        copperFort = new IntegrityShield("copper-fort"){{
            requirements(Category.defense, with(Items.copper, 50, Items.lead, 25));
            health = 500;
            sides = 4;
            researchCostMultiplier = 0.05f;

            consumePower(0.05f);
            databaseTag = "gier";
            customShadow = true;
            radius = 56f;

            shieldRotation = 45f;
            cooldownNormal = 0.1f;
            shieldHealth = 500f;
        }};

        // Power

        transitNode = new TransitNode("transit-node"){{
            requirements(Category.power, with(Items.copper, 20));
            range = 11;
            baseColor = Pal.accent;

            researchCostMultiplier = 0.05f;
            databaseTag = "gier";
        }};

        photonPanel = new SolarGenerator("photon-panel"){{
            requirements(Category.power, with(Items.lead, 50, Items.copper, 100));
            customShadow = true;
            size = 2;

            databaseTag = "gier";
            researchCostMultiplier = 0.05f;

            powerProduction = 0.166667f;
            alwaysUnlocked = true;
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawGlowRegion(){{
                color = Color.valueOf("6f6f6f");
            }});
        }};

        // Effect

        coreSatellite = new PowerCore("core-satellite"){{
            requirements(Category.effect, with(Items.lead, 1000, Items.copper, 500));

            databaseTag = "gier";
            squareSprite = false;
            researchCostMultiplier = 0.05f;

            size = 3;
            health = 3000;
            powerProduction = 1f;
            itemCapacity = 2500;
            customShadow = true;
            unitType = GierUnitTypes.restoration;
            
        }};

        coreFacility = new PowerCore("core-facility"){{
            requirements(Category.effect, with(Items.lead, 1500, Items.copper, 1250, Items.graphite, 500));

            databaseTag = "gier";
            squareSprite = false;
            researchCostMultiplier = 0.01f;

            size = 4;
            health = 4500;
            powerProduction = 4f;
            itemCapacity = 5000;
            customShadow = true;
            unitType = GierUnitTypes.restoration;

        }};

        crate = new StorageBlock("crate"){{
            requirements(Category.effect, with(Items.lead, 200, Items.graphite, 50));
            size = 2;

            databaseTag = "gier";
            squareSprite = false;
            researchCostMultiplier = 0.05f;

            baseExplosiveness = 2.5f;
            explosivenessScale = 2.5f;
            flammabilityScale = 1.5f;
        }};

        restorator = new SquareMendProjector("restorator"){{
            requirements(Category.effect, with(Items.lead, 200, Items.copper, 100));
            customShadow = true;
            baseExplosiveness = 25f;
            researchCostMultiplier = 0.05f;

            databaseTag = "gier";
            range = 84f;
            reload = 60f;

            consumeItem(Items.graphite).boost();
            consumePower(0.5f);
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawPulseShape(true){{
                color = GierPal.metaglassLight;
            }});

            extendedColor = Pal.gray;
            healPercent = 2.5f;
            phaseBoost = 2.5f;
            baseColor = phaseColor = GierPal.metaglassLight;
            phaseRangeBoost = 20f;
        }};

        mendPylon = new SquareMendProjector("mend-pylon"){{
            requirements(Category.effect, with(Items.lead, 200, Items.copper, 200, Items.silicon, 100));
            size = 2;

            customShadow = true;
            baseExplosiveness = 50f;
            researchCostMultiplier = 0.25f;

            databaseTag = "gier";
            range = 128f;
            reload = 60f;

            consumeItem(Items.silicon).boost();
            consumePower(1.5f);
            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawBlurSpin("-rotator", 4){{
                blurThresh = 1f;
            }},
            new DrawPulseShape(true){{
                color = GierPal.metaglassLight;
            }});

            extendedColor = Pal.gray;
            healPercent = 6f;
            phaseBoost = 4f;
            baseColor = phaseColor = GierPal.metaglassLight;
            phaseRangeBoost = 80f;
        }};

    }
}