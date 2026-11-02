package GierJava.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
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

        private FrameBuffer landingBuffer;
        private Planet landingPlanet;
        private final PlanetParams landingParams = new PlanetParams();
        private final Vec3 landingDirection = new Vec3();

        private int bufferSize;

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction : 0f;
        }

        @Override
        public void beginLaunch(boolean launching){
            super.beginLaunch(launching);

            if(headless || launching) return;

            if(landingBuffer != null){
                landingBuffer.dispose();
                landingBuffer = null;
            }

            Sector sector = state.rules.sector;

            if(sector != null && sector.planet != null){
                landingPlanet = sector.planet;

                if(sector.tile != null){
                    landingDirection.set(sector.tile.v).nor();
                }else{
                    randomDirection();
                }
            }else{
                landingPlanet = state.rules.planetBackground != null
                    ? state.rules.planetBackground.planet
                    : state.rules.planet;

                randomDirection();
            }

            if(landingPlanet == null) return;

            landingParams.planet = landingPlanet;
            landingParams.drawSkybox = true;
            landingParams.drawUi = false;
            landingParams.alwaysDrawAtmosphere = true;
            landingParams.uiAlpha = 0f;

            bufferSize = Math.max(Core.graphics.getWidth(), Core.graphics.getHeight());

            landingParams.viewW = bufferSize;
            landingParams.viewH = bufferSize;

            landingBuffer = new FrameBuffer(bufferSize, bufferSize);
        }

        private void randomDirection(){
            landingDirection.set(
                Mathf.random(-1f, 1f),
                Mathf.random(-1f, 1f),
                Mathf.random(-1f, 1f)
            ).nor();
        }

        @Override
        public void drawLaunch(){
            if(landingBuffer != null && landingPlanet != null){

                float fin = Mathf.clamp(renderer.getLandTimeIn());
                float zoom = Interp.pow3In.apply(fin);

                landingParams.camPos.set(landingDirection);
                landingParams.zoom = Mathf.lerp(1f, -0.45f, zoom);

                float fade = Mathf.clamp((fin - 0.25f) / 0.25f);
                fade = Interp.pow2In.apply(fade);

                int size = Math.max(
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
                );

                if(size != bufferSize){
                    landingBuffer.dispose();

                    bufferSize = size;
                    landingBuffer = new FrameBuffer(size, size);

                    landingParams.viewW = size;
                    landingParams.viewH = size;
                }

                landingBuffer.begin(Color.clear);
                renderer.planets.render(landingParams);
                landingBuffer.end();

                float drawSize = Math.max(
                    Core.camera.width,
                    Core.camera.height
                );

                Draw.color(Color.white);
                Draw.alpha(1f - fade);

                Draw.rect(
                    Draw.wrap(landingBuffer.getTexture()),
                    Core.camera.position.x,
                    Core.camera.position.y,
                    drawSize,
                    -drawSize
                );

                Draw.reset();
            }
            
            super.drawLaunch();
        }

        @Override
        public void endLaunch(){
            if(landingBuffer != null){
                landingBuffer.dispose();
                landingBuffer = null;
            }

            landingPlanet = null;
        }
    }
}
