package GierJava.content;

import mindustry.type.weather.*;
import mindustry.graphics.*;
import mindustry.gen.*;
import mindustry.type.Weather;
import arc.graphics.*;

public class GierWeather {
    public static Weather 
    fragStorm;

    public static void load(){
        new ParticleWeather("frag-storm"){{
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
    }

}