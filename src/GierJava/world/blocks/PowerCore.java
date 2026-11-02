package GierJava.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.math.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class PowerCore extends CoreBlock{

    public float powerProduction = 2f;

    public PowerCore(String name){
        super(name);
        outputsPower = true;
        consumesPower = false;
        hasPower = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower){
            addBar("power", (PowerCoreBuild entity) -> new Bar(
                () -> Core.bundle.format(
                    "bar.poweroutput",
                    Strings.fixed(entity.getPowerProduction() * 60f * entity.timeScale(), 1)
                ),
                () -> Pal.powerBar,
                () -> entity.getPowerProduction() / powerProduction * 60f
            ));
        }
    }

    public class PowerCoreBuild extends CoreBuild{

        private Planet landingPlanet;
        private final PlanetParams landingParams = new PlanetParams();
        private final Vec3 landingDirection = new Vec3();

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction : 0f;
        }

        @Override
        public void beginLaunch(boolean launching){
            super.beginLaunch(launching);

            thrusterTime = 0f;

            if(headless || launching) return;

            Sector sector = state.rules.sector;

            if(sector != null && sector.planet != null){
                landingPlanet = sector.planet;

                if(sector.tile != null){
                    landingDirection.set(sector.tile.v).nor();
                }else{
                    landingDirection.set(
                        Mathf.random(-1f, 1f),
                        Mathf.random(-1f, 1f),
                        Mathf.random(-1f, 1f)
                    ).nor();
                }
            }else{
                landingPlanet = state.rules.planetBackground != null
                    ? state.rules.planetBackground.planet
                    : state.rules.planet;

                landingDirection.set(
                    Mathf.random(-1f, 1f),
                    Mathf.random(-1f, 1f),
                    Mathf.random(-1f, 1f)
                ).nor();
            }

            if(landingPlanet == null) return;

            landingParams.planet = landingPlanet;
            landingParams.drawSkybox = true;
            landingParams.drawUi = false;
            landingParams.alwaysDrawAtmosphere = true;
            landingParams.uiAlpha = 0f;
        }

        @Override
        public void drawLaunch(){
            if(landingPlanet == null) return;

            float fin = Mathf.clamp(renderer.getLandTimeIn());
            float zoom = Interp.pow3In.apply(fin);

            landingParams.camPos.set(landingDirection);
            landingParams.zoom = Mathf.lerp(1f, -0.45f, zoom);

            landingParams.drawSkybox = true;
            landingParams.drawUi = false;
            landingParams.alwaysDrawAtmosphere = true;
            landingParams.uiAlpha = 0f;

            renderer.planets.render(landingParams);

            Draw.reset();
        }

        @Override
        public void draw(){
            thrusterTime = 0f;

            if(renderer.getLandTime() > 0f && !renderer.isLaunching()){
                float alpha = Mathf.clamp(
                    (renderer.getLandTimeIn() - 0.96f) / 0.04f
                );

                Draw.alpha(alpha);
                super.draw();
                Draw.reset();
            }else{
                super.draw();
            }
        }

        @Override
        public void endLaunch(){
            landingPlanet = null;
            thrusterTime = 0f;
        }
    }
}
