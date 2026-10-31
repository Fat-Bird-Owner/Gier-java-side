package GierJava.content;

import ent.anno.Annotations.*;
import mindustry.gen.*;
import mindustry.type.*;
import GierJava.gen.*;
import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import arc.struct.Seq;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.ai.types.*;
import mindustry.gen.Sounds;
import mindustry.entities.abilities.*;
import mindustry.entities.pattern.*;

public class GierUnitTypes {
    // `genIO` is only used here to avoid creating a `revision` directory, which is used to track IO.
    public static @EntityDef(value = {Unitc.class, MDTXFixc.class}, genIO = false) UnitType 
    
    // Core units
    restoration,
    
    // Exogen units
    spark,
    drive;
    
    public static @EntityDef(value = {Unitc.class, MDTXFixc.class}, genIO = false) UnitType ash;
    
    // Internals
    public static @EntityDef(value = {Unitc.class, BuildingTetherc.class, Payloadc.class, MDTXFixc.class}, genIO = false) UnitType carrier;
    
    public static void load(){
        restoration = new UnitType("restoration"){{
            speed = 3.5f;
            flying = true;
            outlineColor = Color.valueOf("2f2f3f");
            accel = 0.116667f;
            drag = 0.035f;
            flyingLayer = 123f;
            targetable = false;
            faceTarget = false;
            drawSoftShadow = false;

            isEnemy = false;
            mineTier = 2;
            mineSpeed = 10f;
            mineBeamOffset = 0f;
            buildSpeed = 1.5f;

            itemCapacity = 40;

            engineSize = 0f;
            hitSize = 14f;
            envEnabled = -1;

            engines.addAll(Seq.with(
            new UnitEngine(-4f, 0f, 2f, 0f),
            new UnitEngine(4f, 0f, 2f, 180f),
            new UnitEngine(0f, 4f, 2f, -90f),
            new UnitEngine(0f, -4f, 2f, 90f)
            ));
        }};

        spark = new UnitType("spark"){{

        flying = true;
        outlineColor = Color.valueOf("2f2f3f");
        speed = 2f;
        accel = 0.04f;
        drag = 0.02f;

        envEnabled = -1;

        hitSize = 7f;
        omniMovement = false;
        rotateSpeed = 3.5f;
        health = 250f;
        engineOffset = 6f;
        engineSize = 0f;
        circleTarget = true;
        range = 300f;
        drawSoftShadow = false;

        targetFlags = new BlockFlag[]{
            BlockFlag.reactor,
            BlockFlag.core,
            BlockFlag.generator,
            BlockFlag.factory,
            BlockFlag.storage,
            BlockFlag.turret
        };

    parts.add(new HoverPart(){{
        radius = 6f;
        color = Color.valueOf("ffd37fff");
        layerOffset = -0.1f;
        y = -6f;
        x = 0f;
    }});

    weapons.add(
    new Weapon("spark-weapon") {{
        shootCone = 180f;
        x = 3.15f;
        y = 0.5f;
        shootSound = Sounds.blockExplode1;
        reload = 25f;
        shoot.shots = 2;
        shoot.shotDelay = 0f;
        inaccuracy = 15f;

        bullet = new MissileBulletType() {{
            homingPower = 0.075f;
            speed = 1.5f;
            trailColor = Color.valueOf("44d2f0");
            weaveScale = 2f;
            weaveMag = 1f;
            keepVelocity = false;
            accel = 0.03334f;
            layer = 69f;

            backColor = Color.valueOf("ffd37fff");
            frontColor = Color.valueOf("ffd37fff");
            trailInterval = 6f;
            trailRotation = true;

            trailEffect = new ParticleEffect() {{
                baseLength = 0f;
                length = 0.1f;
                particles = 2;
                cone = 20f;
                lenFrom = 4f;
                lenTo = 6f;
                strokeFrom = 2f;
                strokeTo = 0f;
                line = true;
                colorFrom = Color.valueOf("ffd37fff");
                colorTo = Color.valueOf("ffd37fff");
                offset = 180f;
            }};

            lifetime = 120f;
            scaledSplashDamage = true;
            splashDamage = 15f;
            splashDamageRadius = 20f;
            damage = 10f;
            shieldDamageMultiplier = 0.65f;
            hitEffect = Fx.explosion;
            hitSound = Sounds.explosionCleroi;
            despawnHit = false;
        }};
    }},
    new Weapon() {{
        alwaysShooting = true;
        alwaysContinuous = true;
        alternate = false;
        shootY = 0f;
        recoil = 0f;
        y = -1.5f;
        x = -3.125f;
        shootSound = Sounds.none;

        bullet = new BulletType() {{
                keepVelocity = false;
                speed = 0f;

                collides = false;
                hittable = false;
                absorbable = false;

                shootEffect = Fx.none;
                smokeEffect = Fx.none;
                hitEffect = Fx.none;
                despawnEffect = Fx.none;

                trailLength = 20;
                trailWidth = 0.5f;
                trailColor = Color.valueOf("FFD280FF");
                layer = 92f;
                lightOpacity = 0f;
            }};
        }});

            }};

        drive = new UnitType("drive"){{
            flying = true;
            speed = 2.5f;
            accel = 0.025f;
            health = 500f;
            drag = 0.015f;
            hitSize = 12f;
            createWreck = true;

            targetAir = false;
            omniMovement = true;
            circleTarget = true;
            outlineColor = Color.valueOf("2f2f3f");
            drawSoftShadow = false;

            engineSize = 0f;
            parts.add(new HoverPart(){{
                color = Color.valueOf("FFD280FF");
                radius = 8f;
                layerOffset = -1f;
            }});

            weapons.add(new Weapon(){{
                    x = 0f;
                    y = 0f;
                    rotate = true;
                    shootCone = 15f;
                    reload = 10f;
                    inaccuracy = 20f;
                    minShootVelocity = 0.5f;

                    bullet = new ArtilleryBulletType(2f, 0f){{
                            spin = 12f;
                            lifetime = 90f;
                            shrinkY = 0.5f;
                            shrinkX = 0.5f;
                            hitShake = 1.75f;
                            width = 7.5f;
                            height = 7.5f;
                            layer = 65f;
                            keepVelocity = false;

                            trailEffect = Fx.none;
                            splashDamage = 34f;
                            splashDamageRadius = 12f;
                            hitEffect = Fx.flakExplosion;
                    }};
                }});
            }};

            ash = new UnitType("ash"){{
            flying = true;
            flowfieldPathType = 3;

            targetFlags = new BlockFlag[]{BlockFlag.factory, BlockFlag.battery, BlockFlag.core};

            targetPriority = 3.5f;
            outlineColor = Color.valueOf("2f2f3f");
            speed = 2.5f;
            accel = 0.15f;
            drag = 0.05f;
            armor = 5f;
            range = 200f;

            crushDamage = 12f;

            hitSize = 12.5f;
            omniMovement = false;
            rotateSpeed = 3.5f;
            health = 1250f;
            engineOffset = 6f;
            engineSize = 0f;
            drawSoftShadow = false;
            faceTarget = true;
            circleTarget = true;
            circleTargetRadius = 100f;

            parts.add(new HoverPart(){{
                    radius = 6f;
                    color = Color.valueOf("ffd37fff");
                    layerOffset = -0.1f;
                    y = -8f;
                    x = 0f;
            }});

            parts.add(new HoverPart(){{
                    radius = 4f;
                    color = Color.valueOf("ffd37fff");
                    layerOffset = -0.1f;
                    y = 2.5f;
                    x = 4f;
            }});

            parts.add(new HoverPart(){{
                    radius = 4f;
                    color = Color.valueOf("ffd37fff");
                    layerOffset = -0.1f;
                    y = 2.5f;
                    x = -4f;
            }});

            /*
            abilities.add(new MoveLightningAbility(){{
                    heatRegion = "gier-java-ash-full";
                    chance = 0f;
                    color = Color.valueOf("ffffff77");
            }});
            */

            abilities.add(new ShieldRegenFieldAbility(30f, 300f, 180f, 0f));

            weapons.add(new Weapon(){{
                    reload = 300f;
                    shootStatus = StatusEffects.slow;
                    shootStatusDuration = 70f;
                    chargeSound = Sounds.chargeVela;
                    //shootSound = Sounds.ashArtilleryShoot;
                    shoot.firstShotDelay = 90f;

                    parts.add(new ShapePart(){{
                            hollow = true;
                            circle = true;
                            radius = 80f;
                            radiusTo = 0f;
                            progress = PartProgress.charge;
                            color = Color.valueOf("ffaa5f00");
                            colorTo = Color.valueOf("ffaa5fff");
                    }});

                    parts.add(new ShapePart(){{
                            hollow = true;
                            circle = true;
                            radius = 80f;
                            radiusTo = 0f;
                            progress = PartProgress.warmup;
                            color = Color.valueOf("ffaa5f00");
                            colorTo = Color.valueOf("ffaa5fff");
                    }});

                    bullet = new BasicBulletType(6.5f, 1f){{
                            width = 15f;
                            height = 21f;
                            lifetime = 90f;
                            homingPower = 0.04f;

                            splashDamage = 250f;
                            splashDamageRadius = 40f;
                            scaledSplashDamage = true;

                            hitShake = 25f;
                            despawnHit = true;

                            recoil = 2.5f;
                            trailRotation = true;
                            trailInterval = 1f;
                            trailEffect = Fx.randLifeSpark;
                            trailColor = Color.valueOf("ffaa5f");

                            shieldDamageMultiplier = 250f;

                    hitEffect = new MultiEffect(
                        Fx.launchPod,
                        new WrapEffect(Fx.scatheExplosionSmall, Color.valueOf("ffaa5f")),
                        new WrapEffect(Fx.titanExplosion, Color.valueOf("ffaa5f77"))
                    );
                        //hitSound = Sounds.ashArtilleryHit;
                    }};
            }});

            weapons.add(new Weapon("cr-weapon"){{
                    reload = 25f;
                    x = 0f;
                    y = -2f;
                    mirror = false;

                    rotate = true;
                    rotateSpeed = 3.25f;
                    shake = 2f;

                    shootSound = Sounds.shootFuse;
                    shoot = new ShootSpread(3, 15f);

                    bullet = new ShrapnelBulletType(){{
                            serrations = 0;
                            fromColor = Color.valueOf("f3e979");
                            toColor = Color.valueOf("f3e979");
                            length = 37.5f;
                            width = 6f;

                            damage = 17.5f;
                            shootEffect = Fx.none;
                            smokeEffect = new ParticleEffect(){{
                                    particles = 4;
                                    cone = 45f;
                                    colorFrom = Color.valueOf("f3e979");
                                    colorTo = Color.valueOf("f3e979");
                                    lenFrom = 4f;
                                    lenTo = 6f;
                                    strokeFrom = 3f;
                                    strokeTo = 0f;
                                    length = 40f;
                                    line = true;
                                    lifetime = 12f;
                            }};
                            hitColor = Color.valueOf("f3e979");
                            hitEffect = Fx.hitBeam;
                    }};
            }});
            }};

            carrier = new UnitType("carrier"){{
                speed = 3.5f;
                health = 110f;

                envEnabled = -1;

                flying = true;
                outlineRadius = 0;
                accel = 0.13f;
                drag = 0.02f;
                faceTarget = false;
                drawSoftShadow = false;
                isEnemy = false;

                itemCapacity = 250;
                targetPriority = 2f;
                rotateSpeed = 4.5f;

                engines.addAll(Seq.with(
                    new UnitEngine(-6.5f, 0f, 2f, 0f),
                    new UnitEngine(6.5f, 0f, 2f, 180f),
                    new UnitEngine(0f, 6.5f, 2f, -90f),
                    new UnitEngine(0f, -6.5f, 2f, 90f)
                ));

                controller = u -> new CargoAI();
                playerControllable = false;
                logicControllable = false;
            }};

        EntityRegistry.registerUnits();
    }
}
