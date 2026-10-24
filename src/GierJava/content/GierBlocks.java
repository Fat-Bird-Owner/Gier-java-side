package GierJava.content;

import GierJava.world.blocks.*;
import GierJava.content.GierItems;
import GierJava.content.GierUnitTypes;

import mindustry.content.Fx;
import mindustry.gen.Sounds;
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

    // Environement
    gierCopperOre,
    
    // Turret
    tension,

    // Distribution
    rail,
    overheadBelt,
    railOverflow,
    droneFacility,
    facilityDropzone,

    // Production
    hydroDrill,

    // Crafting
    sunderFurnace,
    industrialTrimmer,

    // Defense
    copperFort,

    // Power
    transitNode,
    powerGrid,
    photonPanel,
    ignitionGenerator,
    electricCapacitor,
    
    // Effect
    coreSatellite,
    coreFacility,
    crate,
    restorator,
    mendPylon;

    public static void load(){

        // Environment

        gierCopperOre = new OreBlock("gier-copper-ore"){{
            variants = 4;
            itemDrop = Items.copper;
        }};
        
        // Turret

        tension = new BoostPowerTurret("tension"){{

            size = 4;
            outlineRadius = 0;
            range = 320f;
            health = 2500;
            envEnabled = -1;
            shootSound = Sounds.shootScathe;

            coolant = consumeCoolant(0.33334f);
            shootWarmupSpeed = 0.05f;
            minWarmup = 0.9f;
            consumePower(1f);

            databaseTag = "gier";
            researchCostMultiplier = 0.05f;
            rotateSpeed = 0f;
            recoil = 0f;
            shootY = 0f;
            hasShadow = false;
            customShadow = true;
            category = Category.turret;
            reload = 95f;
            shake = 0.65f;
            shootEffect = Fx.none;

            boostItem = Items.graphite;
            consumeItem(boostItem, boostItemAmount).optional(true, true);

            requirements(Category.turret, with(
                Items.lead, 250,
                Items.copper, 250
            ));

            buildTime = 300f;
            rotate = false;
            shootCone = 180f;
            inaccuracy = 0f;
            shootType = new BulletType(){{
                speed = 30f;
                lifetime = 10f;
                damage = 0f;
                collides = false;
                instantDisappear = true;
                despawnHit = true;
                fragBullets = 4;
                fragSpread = 90f;
                fragRandomSpread = 0f;
                fragVelocityMin = 1f;
                fragVelocityMax = 1f;
                hitEffect = new MultiEffect(
                    new ParticleEffect(){{
                        lifetime = 32f;
                        colorFrom = Color.valueOf("ff8545c2");
                        colorTo = Color.valueOf("7c3f3f00");
                        region = "circle-shadow";
                        particles = 132;
                        heightTo = 0.5f;
                        cone = 360f;
                        sizeFrom = 3f;
                        sizeTo = 7f;
                        cap = false;
                        layer = 109f;
                        length = 44f;
                    }},
                    new ParticleEffect(){{
                        colorFrom = Color.valueOf("ff8545c2");
                        colorTo = Color.valueOf("e23000ff");
                        lifetime = 66f;
                        cone = 360f;
                        sizeFrom = 2.4f;
                        sizeTo = 0f;
                        cap = false;
                        layer = 64f;
                        length = 64f;
                        particles = 35;
                        interp = arc.math.Interp.sineOut;
                    }}
                );

                fragBullet = new MissileBulletType(1f, 65f){{
                    trailEffect = Fx.none;
                    trailChance = 0.25f;
                    trailWidth = 2f;
                    width = 10f;
                    height = 17f;
                    lifetime = 120f;
                    homingPower = 0.1f;
                    homingDelay = 20f;
                    homingRange = 300f;
                    impact = true;
                    knockback = 15f;
                    trailLength = 12;
                    trailChance = 0f;
                    trailInterval = 0.2f;
                    trailRotation = true;

                    trailEffect = new MultiEffect(
                        new ParticleEffect(){{
                            colorFrom = Color.valueOf("ff8545c2");
                            colorTo = Color.valueOf("7c3f3f33");
                            region = "circle-shadow";
                            particles = 2;
                            heightTo = 0.5f;
                            lifetime = 22f;
                            cone = 45f;
                            sizeFrom = 3f;
                            sizeTo = 7f;
                            cap = false;

                            layer = 109f;

                        }},

                        new ParticleEffect(){{
                            colorFrom = Color.valueOf("ff8545c2");
                            colorTo = Color.valueOf("e23100ff");
                            particles = 2;
                            heightTo = 0.5f;
                            lifetime = 33f;
                            cone = 22.5f;
                            sizeFrom = 1.7f;
                            sizeTo = 0f;
                            cap = false;
                            layer = 64f;
                            length = 44f;
                        }}
                    );

                    damage = 65f;
                    splashDamage = 20f;
                    splashDamageRadius = 40f;
                    scaledSplashDamage = true;
                    frontColor = Color.valueOf("7d4c27");
                    accel = 0.045f;
                    hitEffect = new MultiEffect(

                        new ParticleEffect(){{
                            lifetime = 44f;
                            colorFrom = Color.valueOf("ff854588");
                            colorTo = Color.valueOf("ff854500");
                            region = "circle-shadow";
                            particles = 1;
                            sizeFrom = 13f;
                            sizeTo = 65f;
                            cap = false;
                            length = 0f;
                            interp = arc.math.Interp.sineOut;
                        }},
                        new WrapEffect(Fx.titanExplosionFrag, Color.valueOf("ff8545ff"))
                    );

                    hitSound = Sounds.unitExplode3;
                    despawnHit = true;
                    hitShake = 6.5f;

                }};

            }};

            elevation = 0f;
            drawer = new DrawTurret(){{
                basePrefix = "-lid";
                parts.add(new RegionPart("-lid"){{
                    growX = -0.25f;
                    growY = -0.25f;
                    color = Color.white;
                    colorTo = Color.valueOf("8f8f8f7f");
                }});
                }};
            }};

        // Distribution

        rail = new StackConveyor("rail"){{
            requirements(Category.distribution, with(Items.copper, 1));
            researchCostMultiplier = 0.05f;

            envEnabled = -1;
            speed = 0.01667f;
            baseEfficiency = 1f;

            databaseTag = "gier";
            alwaysUnlocked = true;

        }};

        railOverflow = new ConfigGate("rail-overflow"){{
            requirements(Category.distribution, with(Items.lead, 20, Items.copper, 10));
            databaseTag = "gier";
            researchCostMultiplier = 0.15f;
            baseExplosiveness = 10f;
        }};

        overheadBelt = new OmniDirectionalBridge("overhead-belt"){{
            requirements(Category.distribution, with(Items.lead, 20, Items.silicon, 20));
            researchCostMultiplier = 0.15f;
            range = 5;

            customShadow = true;
            baseExplosiveness = 40f;

            envEnabled = -1;
            databaseTag = "gier";
            alwaysUnlocked = true;
        }};

        droneFacility = new UnitCargoLoader("drone-facility"){{
            requirements(Category.distribution, with(Items.lead, 150, Items.copper, 100));
            size = 2;
            baseExplosiveness = 20f;

            unitType = GierUnitTypes.carrier;
            customShadow = true;
            databaseTag = "gier";

            itemCapacity = 300;
            envEnabled = -1;
            polyRadius = 5.5f;

            consumePower(0.5f);
        }};

        facilityDropzone = new UnitCargoUnloadPoint("facility-dropzone"){{
            requirements(Category.distribution, with(Items.lead, 200, Items.copper, 150));
            size = 2;
            baseExplosiveness = 15f;

            customShadow = true;
            dumpTime = 1;
            staleTimeDuration = 1f;

            databaseTag = "gier";

            itemCapacity = 200;
            envEnabled = -1;
        }};

        // Production

        hydroDrill = new Drill("hydro-drill"){{
            requirements(Category.production, with(Items.lead, 25, Items.copper, 25));
            consumePower(0.08334f);
            consumeLiquid(Liquids.water, 0.08334f).boost();
            researchCostMultiplier = 0.05f;

            envEnabled = -1;
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
            outputItems = with(Items.silicon, 25, GierItems.metaglassChunk, 25);

            baseExplosiveness = 250f;
            
            envEnabled = -1;
            consumePower(5);
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawArcSmelt(),
            new DrawDefault()
            );

        }};
        
        industrialTrimmer = new GenericCrafter("industrial-trimmer"){{
            requirements(Category.crafting, with(Items.lead, 250, Items.copper, 250));
            size = 3;
            researchCostMultiplier = 0.1f;

            databaseTag = "gier";
            baseExplosiveness = 150f;
            craftTime = 100f;

            itemCapacity = 40;
            consumeItems(with(GierItems.metaglassChunk, 20));
            outputItems = with(Items.metaglass, 20);

            envEnabled = -1;
            consumePower(5);
            drawer = new DrawMulti(
            new DrawRegion("-bottom"),
            new DrawPistons() {{
                suffix = "-piston-0";
                sides = 1;
                sinMag = 20f;
                sinScl = 6;
                lenOffset = -18f;
            }},
            new DrawPistons() {{
                suffix = "-piston-0";
                angleOffset = 90f;
                sinOffset = 60f;
                sides = 1;
                sinMag = 20f;
                sinScl = 6;
                lenOffset = -18f;
            }},
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

            baseExplosiveness = 50f;
            shieldRotation = 45f;
            cooldownNormal = 0.1f;
            shieldHealth = 500f;
        }};

        // Power

        transitNode = new TransitNode("transit-node"){{
            requirements(Category.power, with(Items.copper, 20));
            range = 6;
            baseColor = Pal.accent;

            envEnabled = -1;
            researchCostMultiplier = 0.05f;
            databaseTag = "gier";
        }};

        powerGrid = new TransitNode("power-grid"){{
            requirements(Category.power, with(Items.silicon, 10, Items.surgeAlloy, 5));
            range = 11;
            baseColor = Pal.accent;

            baseExplosiveness = 25f;
            consumePowerBuffered(100f);
            
            envEnabled = -1;
            researchCostMultiplier = 0.05f;
            databaseTag = "gier";
        }};
        
        photonPanel = new SolarGenerator("photon-panel"){{
            requirements(Category.power, with(Items.lead, 50, Items.copper, 100));
            customShadow = true;
            size = 2;

            envEnabled = -1;
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

        ignitionGenerator = new ConsumeGenerator("ignition-generator"){{
            requirements(Category.power, with(Items.lead, 250, Items.copper, 200));
            size = 2;

            envEnabled = -1;
            databaseTag = "gier";
            researchCostMultiplier = 0.05f;

            consumeItem(Items.graphite);
            powerProduction = 1.5f;

            drawer = new DrawMulti(
            new DrawDefault(),
            new DrawGlowRegion("-glow"){{
                color = Color.valueOf("ff8545ff");
            }});
            
        }};

        electricCapacitor = new Battery("electric-capacitor"){{
            requirements(Category.power, with(Items.lead, 50, Items.copper, 75));
            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawPower("-power")
            );

            consumePowerBuffered(500f);
            envEnabled = -1;
            baseExplosiveness = 25f;
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

            alwaysUnlocked = true;
            
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
