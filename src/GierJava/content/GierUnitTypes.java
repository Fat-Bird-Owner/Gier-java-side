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
import mindustry.content.UnitTypes;

public class GierUnitTypes {
    // `genIO` is only used here to avoid creating a `revision` directory, which is used to track IO.
    public static @EntityDef(value = {Unitc.class}, genIO = false) UnitType 
    
    // Core units
    restoration,
    
    // Exogen units
    spark;
    
    // Internals
    public static @EntityDef(value = {Unitc.class, BuildingTetherc.class, Payloadc.class}, genIO = false) UnitType carrier;

    // Test
    public static @EntityDef(value = {Unitc.class, Segmentc.class}, genIO = false) UnitType worship;
    
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
                trailColor = Color.valueOf("2f2f3f");
                layer = 92f;
                lightOpacity = 0f;
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

            worship = new UnitType("worship"){{
                speed = 4f;
                hitSize = 5f;
                rotateSpeed = 3f;
                legStraightness = 0.4f;
                legCount = 6;
                legLength = 8f;
                lockLegBase = true;
                segmentUnits = 10;
                segmentUnit = UnitTypes.mace;
            }};

        EntityRegistry.registerUnits();
    }
}
