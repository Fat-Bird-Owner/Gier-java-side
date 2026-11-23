package GierJava.content;

import mindustry.type.weather.*;
import mindustry.graphics.*;
import mindustry.gen.*;
import mindustry.type.Weather;
import GierJava.world.weather.*;
import arc.graphics.*;

import mindustry.entities.part.*;
import mindustry.entities.bullet.*;
import mindustry.content.Fx;

public class GierWeather {
    public static Weather 
    fragStorm,
    debrisShower;

    public static void load(){
        fragStorm = new ParticleWeather("frag-storm"){{
            color = Color.valueOf("a7c1fa");
            noiseColor = Color.valueOf("3c3837");
            drawNoise = true;
            particleRegion = "particle";
            drawParticles = true;
            opacityMultiplier = 1f;
            useWindVector = true;
            sizeMax = 5f;
            sizeMin = 1.4f;
            minAlpha = 0.5f;
            maxAlpha = 1f;
            density = 15000f;
            baseSpeed = 1f;
            force = 0f;
            sound = Sounds.loopSmelter;
            soundVol = 0.65f;
        }};

        debrisShower = new RockWeather("debris-shower"){{
            spawnChance = 0.05f;

            addBullets(
                new BasicBulletType(0f, 0f){{
                    
                  parts.add(new ShapePart(){{
                        moveX = -400f;
                        moveY = -400f;
                        progress = PartProgress.life.inv();
                        layer = 130f;
                        color = Color.valueOf("3c3837");
                 }});

                width = 0f;
                height = 0f;
                    
                ignoreSpawnAngle = true;
                keepVelocity = false;
                speed = 0f;
                reflectable = false;
                absorbable = false;
                collides = false;

                    fragBullet = new BombBulletType(0f, 0f){{
                        lifetime = 6f;
                        splashDamage = 25f;
                        splashDamageRadius = 20f;
                        despawnHit = false;
                        hitEffect = Fx.absorb;
                        despawnEffect = Fx.none;
                    }};

                    fragBullets = 1;
                    despawnEffect = Fx.none;                  
                }}, 0.2f
            );

            drawParticles = true;
            useWindVector = true;
        }};
    }

}
