package GierJava.world.weather;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.weather.*;

public class RockWeather extends ParticleWeather{

    public ObjectFloatMap<BulletType> bullets = new ObjectFloatMap<>();

    public float spawnChance = 0.05f;
    public boolean windDrag = true;
    public float windDragScaleMin = 1f, windDragScaleMax = 1f;

    private float minIntensity = Float.POSITIVE_INFINITY;

    public RockWeather(String name){
        super(name);
    }

    public void addBullet(BulletType bullet, float intensity){
        bullets.put(bullet, intensity);
        minIntensity = Math.min(minIntensity, intensity);
    }

    public void addBullets(Object... entries){
        for(int i = 0; i < entries.length - 1; i += 2){
            addBullet(
                (BulletType)entries[i],
                1f - ((Number)entries[i + 1]).floatValue()
            );
        }
    }

    @Override
    public void update(WeatherState state){
        super.update(state);

        if(Vars.net.client() || bullets.isEmpty()) return;
        if(!Mathf.chanceDelta(spawnChance * state.intensity)) return;

        float intensity = Mathf.lerp(
            minIntensity,
            Math.max(minIntensity, Mathf.random(1f)),
            state.intensity
        );

        BulletType selected = null;
        float threshold = Float.NEGATIVE_INFINITY;

        for(ObjectFloatMap.Entry<BulletType> entry : bullets.entries()){
            if(entry.value <= intensity && entry.value >= threshold){
                selected = entry.key;
                threshold = entry.value;
            }
        }

        if(selected == null) return;

        float speedScale = Mathf.random(windDragScaleMin, windDragScaleMax);
        float distance = speedScale * selected.lifetime * selected.speed;
        float angle = windDrag ? state.windVector.angle() : 0f;

        float x = Mathf.random(Vars.world.unitWidth());
        float y = Mathf.random(Vars.world.unitHeight());

        selected.createNet(
            Team.derelict,
            x - Angles.trnsx(angle, distance),
            y - Angles.trnsy(angle, distance),
            angle,
            selected.damage,
            speedScale,
            1
        );
    }
}
